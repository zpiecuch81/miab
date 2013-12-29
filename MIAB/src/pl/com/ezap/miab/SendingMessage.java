package pl.com.ezap.miab;

import java.io.IOException;

import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.GeoPt;
import pl.com.ezap.miab.miabendpoint.model.MIAB;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class SendingMessage extends Activity {

	private GPSDealer m_gps = null;

	private class SendMessage extends AsyncTask<Context, Integer, Long> {

		@Override
		protected Long doInBackground(Context... contects) {
			Message message = Message.getInstance();
			assert(message.m_location != null);

			MIAB miab = new MIAB();
			miab.setMessage( message.m_message );
			miab.setFlowing( message.m_isFlowing );
			miab.setTimeStamp( new java.util.Date().getTime() );
			//ID just can't be 0/null, app engine will assign a number to it anyway
			miab.setId( miab.getTimeStamp() );
			GeoPt geoPt = new GeoPt();
			geoPt.setLatitude( (float)message.m_location.getLatitude() );
			geoPt.setLongitude( (float)message.m_location.getLongitude() );
			GeoIndex geoIndex = new GeoIndex();
			miab.setGeoIndex( geoIndex.getIndex( 
					message.m_location.getLatitude(),
					message.m_location.getLongitude() ) );
			miab.setLocation( geoPt );
			miab.setDeltaLocation( null );

			Miabendpoint.Builder endpointBuilder = new Miabendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(),
					new HttpRequestInitializer() {
						public void initialize( HttpRequest httpRequest ) {}
					});
			endpointBuilder.setApplicationName("message-in-bottle");
			Miabendpoint endpoint = CloudEndpointUtils.updateBuilder( endpointBuilder ).build();

			try{
				endpoint.insertMIAB(miab).execute();
			} catch (IOException e) {
				e.printStackTrace();
				return (long)1;
			}
			return (long)0;
		}

		@Override
		protected void onPostExecute(Long result) {
			if( result == 0 ) {
				Message.resetInstance();
			}
			displayMessage(R.string.msgSendingDone);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sending_message);

		getGPSposition();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.sending_message, menu);
		return true;
	}

	private void getGPSposition() {
		displayMessage(R.string.msgRetrivingCurrentLocation);
		m_gps = new GPSDealer( (LocationManager)this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE) );
		m_gps.setSimpleLocationListener( new GPSDealer.GPSLocationProvider() {
			@Override
			public void GPSLocationChanged(Location location) {
				Message message = Message.getInstance();
				message.m_location = location;

				if( message.m_isBurried ) {
					displayMessage(R.string.msgBurryingMessage);
				} else if( message.m_isFlowing ) {
					displayMessage(R.string.msgThrowingMessage);
				} else {
					displayMessage(R.string.msgLeavingMessage);
				}

				new SendMessage().execute( getApplicationContext() );
			}
			@Override
			public void GPSAvailabilityChanged(boolean available) {
			}
		});
	}

	private void displayMessage( int messageID ) {
		TextView textView = (TextView)(findViewById(R.id.editSendText));
		textView.setText( messageID );
	}
}
