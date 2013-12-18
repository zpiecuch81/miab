package pl.com.ezap.miab;

import java.io.IOException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;

import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.GeoPoint;
import pl.com.ezap.miab.miabendpoint.model.MIAB;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageCreate extends Activity {

	private class SendMessage extends AsyncTask<Context, Integer, Long> {

		@Override
		protected Long doInBackground(Context... contects) {
			Miabendpoint.Builder endpointBuilder = new Miabendpoint.Builder(
					AndroidHttp.newCompatibleTransport(),
					new JacksonFactory(),
					new HttpRequestInitializer() {
						public void initialize( HttpRequest httpRequest ) {}
					});
			endpointBuilder.setApplicationName("message-in-bottle");
			Miabendpoint endpoint = CloudEndpointUtils.updateBuilder( endpointBuilder ).build();
			Message message = Message.getInstance();
			MIAB miab = new MIAB();
			miab.setDeviceRegistrationID( Long.toString(new java.util.Date().getTime()) );
			miab.setMessage( message.m_message );
			miab.setIsFlowing( message.m_isFlowing );
			miab.setTimeStamp( new java.util.Date().getTime() );
//			GeoPoint point = new GeoPoint();
//			if( message.m_location == null ) {
//				point.setLatitude( 356.0 );
//				point.setLongitude( 123.0 );
//			} else {
//				point.setLatitude( message.m_location.getLatitude() );
//				point.setLongitude( message.m_location.getLongitude() );
//			}
			miab.setLocation( null );
			try{
				endpoint.insertMIAB(miab).execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return (long)0;
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_create);
		EditText text = (EditText)findViewById(R.id.editTextMessage);
		text.setFilters( new InputFilter[] { new InputFilter.LengthFilter(1000)} );

		findViewById(R.id.button_leave).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						leaveTheMessage();
					}
				});

		m_gps = new GPSDealer( (LocationManager)this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE) );
		m_gps.setSimpleStatusListener( new GPSDealer.GPSLocationProvider() {
			
			@Override
			public void GPSLocationChanged(Location location) {
				m_location = location;
			}

			@Override
			public void GPSAvailabilityChanged(boolean available) {
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Message miab = Message.getInstance();
		EditText text = (EditText)findViewById(R.id.editTextMessage);
		text.setText(miab.m_message);
		if( miab.m_isFlowing ) {
			findViewById(R.id.editTextMessage)
				.setBackgroundResource(R.drawable.background_see);
			Button buttonLeave = (Button)(findViewById(R.id.button_leave));
			buttonLeave.setText(R.string.button_throwMsg);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		EditText text = (EditText)findViewById(R.id.editTextMessage);
		Message.getInstance().m_message = text.getText().toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.message_create, menu);
		return true;
	}

	public void leaveTheMessage()
	{
		EditText text = (EditText)findViewById(R.id.editTextMessage);
		Message message = Message.getInstance();
		message.m_message = text.getText().toString();
		if( m_location == null ) {
			//TODO: different behavior needed when gps signal not found
			m_location = m_gps.getLastLocation();
		}
		message.m_location = m_location;
		new SendMessage().execute( getApplicationContext() );
	}

	private GPSDealer m_gps;
	private Location m_location;
}
