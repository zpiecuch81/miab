
package pl.com.ezap.miab.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import pl.com.ezap.miab.CloudEndpointUtils;
import pl.com.ezap.miab.R;
import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.GeoPt;
import pl.com.ezap.miab.miabendpoint.model.MessageV1;
import pl.com.ezap.miab.shared.GeoIndex;
import pl.com.ezap.miab.shared.LocationHelper;
import pl.com.ezap.miab.shared.NotificationHelper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SenderService extends Service
{
  final static public String MESSAGE_KEY = "pl.com.ezap.miab.services.Message2Send";
  final static public String IS_HIDDEN_KEY = "pl.com.ezap.miab.services.IsHidden";
  final static public String IS_FLOWING_KEY = "pl.com.ezap.miab.services.IsFlowing";
  private ArrayList<MessageV1> messages2send;
  private LocationManager locationManager;
  private LocationListener locationListener;
  private NotificationHelper notificationHelper;
  private int maxGPSFoundTrials;
  private boolean isSending;

  private class SendMessageTask extends AsyncTask<MessageV1, Integer, Long>
  {
    @Override
    protected Long doInBackground( MessageV1... miabMsg )
    {
      Miabendpoint.Builder endpointBuilder =
          new Miabendpoint.Builder(
              AndroidHttp.newCompatibleTransport(),
              new JacksonFactory(),
              new HttpRequestInitializer() {
                public void initialize( HttpRequest httpRequest )
                {}
              } );
      endpointBuilder.setApplicationName( "message-in-bottle" );
      Miabendpoint endpoint = CloudEndpointUtils.updateBuilder( endpointBuilder ).build();
      try {
        endpoint.insertMIAB( miabMsg[ 0 ] ).execute();
      }
      catch( IOException e ) {
        e.printStackTrace();
        return (long)1;
      }
      return (long)0;
    }

    @Override
    protected void onPostExecute( Long result )
    {
      Log.d( "SendMessageTask", "onPostExecute, result " + result );
      if( result == 0 ) {
        messages2send.remove( 0 );
      }
      isSending = false;
    }
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    locationManager = null;
    locationListener = null;
    isSending = false;
    messages2send = new ArrayList<MessageV1>();
  }

  @Override
  public IBinder onBind( Intent arg0 )
  {
    return null;
  }

  @Override
  public int onStartCommand( Intent intent, int flags, int startId )
  {
    if( intent == null ) {
      return START_STICKY;
    }
    MessageV1 miabMessage = intent2message( intent );
    Log.i( "SenderService", "Adding message to queue" );
    messages2send.add( miabMessage );

    startSendingBottles();
    return START_STICKY;
  }

  private MessageV1 intent2message( Intent intent ){
    MessageV1 miabMessage = new MessageV1();
    miabMessage.setMessage( intent.getStringExtra( MESSAGE_KEY ) );
    miabMessage.setHidden( intent.getBooleanExtra( IS_HIDDEN_KEY, false ) );
    miabMessage.setFlowing( intent.getBooleanExtra( IS_FLOWING_KEY, false ) );
    miabMessage.setTimeStamp( new java.util.Date().getTime() );
    setFloatingData( miabMessage );
    // ID can't be 0/null, app engine will assign a number to it anyway
    miabMessage.setId( miabMessage.getTimeStamp() );
    return miabMessage;
  }

  private void setFloatingData( MessageV1 miab )
  {
    if( miab.getFlowing() ) {
      Random r = new Random();
      float longitudeSpeed = (float)( r.nextInt( 90 ) - 45 ) / 10000.0f;
      float latitudeSpeed = (float)( r.nextInt( 90 ) - 45 ) / 10000.0f;
      GeoPt deltaGeoPt = new GeoPt();
      deltaGeoPt.setLatitude( latitudeSpeed );
      deltaGeoPt.setLongitude( longitudeSpeed );
      miab.setDeltaLocation( deltaGeoPt );
      miab.setFlowStamp( miab.getTimeStamp() );
    } else {
      miab.setDeltaLocation( null );
    }
  }

  private void startSendingBottles()
  {
    if( locationManager != null ) {
      return; // already started
    }

    maxGPSFoundTrials = 5;
    locationListener = new LocationListener() {
      @Override
      public void onLocationChanged( Location location )
      {
        if( LocationHelper.isAccuracyEnough( location )
            || --maxGPSFoundTrials < 0 ) {
          sendNextMessage( location );
          maxGPSFoundTrials = 5;
        }
      }

      @Override
      public void onProviderDisabled( String provider )
      {
        //if( provider.equals( LocationManager.GPS_PROVIDER ) ) {}
      }

      @Override
      public void onProviderEnabled( String provider )
      {
        //if( provider.equals( LocationManager.GPS_PROVIDER ) ) {}
      }

      @Override
      public void onStatusChanged( String provider, int status, Bundle extras )
      {
//        if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
//          switch( status ) {
//            case LocationProvider.OUT_OF_SERVICE:
//              break;
//            case LocationProvider.TEMPORARILY_UNAVAILABLE:
//              break;
//            case LocationProvider.AVAILABLE:
//              break;
//          }
//        }
      }
    };

    notificationHelper = new NotificationHelper( this );
    notificationHelper.gpsSearchingStarted( getSendText( messages2send.get( 0 ) ) );

    locationManager =
        (LocationManager)this.getApplicationContext().getSystemService( Context.LOCATION_SERVICE );
    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        100,
        0,
        locationListener );
  }

  private void sendNextMessage( Location location )
  {
    if( isSending ) {
      return;
    }
    if( messages2send.isEmpty() ) {
      finishService();
      return;
    }
    isSending = true;
    addGeoData( messages2send.get( 0 ), location );
    new SendMessageTask().execute( messages2send.get( 0 ) );
  }

  private void finishService()
  {
    Log.i( "SenderService", "stopping service" );
    if( notificationHelper != null ) {
      notificationHelper.sendingFinished( getString( R.string.msgSendingDone ) );
    }
    if( locationManager != null && locationListener != null ) {
      locationManager.removeUpdates( locationListener );
    }
    locationManager = null;
    stopSelf();
  }

  private void addGeoData( MessageV1 miab, Location location )
  {
    notificationHelper.messageSending( getSendText( miab ) );
    GeoPt geoPt = new GeoPt();
    if( miab.getFlowing() ) {
      geoPt.setLatitude( (float)location.getLatitude() + miab.getDeltaLocation().getLatitude() );
      geoPt.setLongitude( (float)location.getLongitude() + miab.getDeltaLocation().getLongitude() );
    } else {
      geoPt.setLatitude( (float)location.getLatitude() );
      geoPt.setLongitude( (float)location.getLongitude() );
    }
    miab.setLocation( geoPt );
    GeoIndex geoIndex = new GeoIndex();
    miab.setGeoIndex( geoIndex.getIndex( geoPt.getLatitude(), geoPt.getLongitude() ) );
  }

  private String getSendText( MessageV1 message )
  {
    int displayMessageID =
        message.getHidden()
            ? R.string.msgBurryingMessage : message .getFlowing()
            ? R.string.msgThrowingMessage : R.string.msgLeavingMessage;
    return getString( displayMessageID );
  }

}
