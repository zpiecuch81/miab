package pl.com.ezap.miab.services;

import java.io.IOException;
import java.util.ArrayList;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

import pl.com.ezap.miab.CloudEndpointUtils;
import pl.com.ezap.miab.R;
import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.GeoPt;
import pl.com.ezap.miab.miabendpoint.model.MIAB;
import pl.com.ezap.miab.shared.GeoIndex;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SenderService extends Service {

	final static public String MESSAGE_KEY = "pl.com.ezap.miab.services.Message2Send";
	final static public String IS_BURRIED_KEY = "pl.com.ezap.miab.services.IsBurried";
	final static public String IS_FLOWING_KEY = "pl.com.ezap.miab.services.IsFlowing";

	private ArrayList<MIAB> messages2send;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private int maxGPSFoundTrials;
	private boolean isSending;

	private class SendMessageTask extends AsyncTask<MIAB, Integer, Long> {

		@Override
		protected Long doInBackground(MIAB... miabMsg) {
			Miabendpoint.Builder endpointBuilder = new Miabendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(),
					new HttpRequestInitializer() {
						public void initialize( HttpRequest httpRequest ) {}
					});
			endpointBuilder.setApplicationName("message-in-bottle");
			Miabendpoint endpoint = CloudEndpointUtils.updateBuilder( endpointBuilder ).build();

			try{
				endpoint.insertMIAB(miabMsg[0]).execute();
			} catch (IOException e) {
				e.printStackTrace();
				return (long)1;
			}
			return (long)0;
		}

		@Override
		protected void onPostExecute(Long result) {
			Log.d( "SendMessageTask", "onPostExecute, result " + result);
			if( result == 0 ) {
				messages2send.remove( 0 );
				Toast.makeText( getApplicationContext(), R.string.msgSendingDone, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText( getApplicationContext(), R.string.msgSendingError, Toast.LENGTH_LONG).show();
			}
			isSending = false;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		locationManager = null;
		locationListener = null;
		isSending = false;
		messages2send = new ArrayList<MIAB>();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( intent == null ) {
			return START_STICKY;
		}

		//fill basic MIAB data 
		MIAB miabMessage = new MIAB();
		miabMessage.setMessage( intent.getStringExtra( MESSAGE_KEY ) );
		miabMessage.setBurried( intent.getBooleanExtra( IS_BURRIED_KEY, false ) );
		miabMessage.setFlowing( intent.getBooleanExtra( IS_FLOWING_KEY, false ) );
		miabMessage.setTimeStamp( new java.util.Date().getTime() );
		//ID can't be 0/null, app engine will assign a number to it anyway
		miabMessage.setId( miabMessage.getTimeStamp() );

		int displayMessageID = miabMessage.getBurried() ? R.string.msgBurryingMessage :
								miabMessage.getFlowing() ? R.string.msgThrowingMessage :
								R.string.msgLeavingMessage;
		Toast.makeText( getApplicationContext(), displayMessageID, Toast.LENGTH_LONG).show();

		//add message to send it when position will be known
		Log.i( "SenderService", "Adding message to queue" );
		messages2send.add( miabMessage );

		//start searching location if not searching already
		startLocationListener();
		//sendNextMessage( locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) );
		return START_STICKY;
	}

	private void startLocationListener() {
		if( locationManager != null ) {
			return;		//already started
		}

		//TODO: make correct notifications
//		NotificationCompat.Builder mBuilder =
//				new NotificationCompat.Builder(this)
//					.setSmallIcon(R.drawable.ic_launcher)
//					.setContentTitle("Sending message")
//					.setContentText("Retrieving current location...");
//		NotificationManager mNotificationManager =
//				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		mNotificationManager.notify(1, mBuilder.build());

		maxGPSFoundTrials = 5;
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				if( isAccuracyEnough( location ) || --maxGPSFoundTrials < 0 ) {
					sendNextMessage( location );
					maxGPSFoundTrials = 5;
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				if( provider.equals(LocationManager.GPS_PROVIDER) ) {
					//gpsDisabled = true;
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
				if( provider.equals(LocationManager.GPS_PROVIDER) ) {
					//gpsDisabled = false;
				}
			}

			@Override
			public void onStatusChanged(String provider,
					int status,
					Bundle extras) {
				if( provider.equals(LocationManager.GPS_PROVIDER) ) {
					switch( status ) {
					case LocationProvider.OUT_OF_SERVICE:
						//gpsDisabled = true;
						break;
					case LocationProvider.TEMPORARILY_UNAVAILABLE:
						//gpsDisabled = true;
						break;
					case LocationProvider.AVAILABLE:
						//gpsDisabled = false;
						break;
					}
				}
			}

		};

		locationManager = (LocationManager)this.getApplicationContext().getSystemService( Context.LOCATION_SERVICE );
		locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 100, 0, locationListener );
	}

	private boolean isAccuracyEnough( Location location) {
		if( location != null ) {
			return location.getAccuracy() <= 5.0;
		}
		return false;
	}

	private void sendNextMessage( Location location ) {
		if( isSending ) {
			return;
		}
		if( messages2send.isEmpty() ) {
			finishService();
			return;
		}
		isSending = true;
		addGeoDataAndSend( messages2send.get( 0 ), location );
	}

	private void finishService() {
		Log.i( "SenderService", "stopping service");
		if( locationManager != null && locationListener != null ) {
			locationManager.removeUpdates( locationListener );
		}
		locationManager = null;
		stopSelf();
	}

	private void addGeoDataAndSend( MIAB miabMsg, Location location ) {
		GeoPt geoPt = new GeoPt();
		geoPt.setLatitude( (float)location.getLatitude() );
		geoPt.setLongitude( (float)location.getLongitude() );
		miabMsg.setLocation( geoPt );
		GeoIndex geoIndex = new GeoIndex();
		miabMsg.setGeoIndex( geoIndex.getIndex( 
				geoPt.getLatitude(),
				geoPt.getLongitude() ) );
		miabMsg.setDeltaLocation( null );

		new SendMessageTask().execute( miabMsg );
	}
}
