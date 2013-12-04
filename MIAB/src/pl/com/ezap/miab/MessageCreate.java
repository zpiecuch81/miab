package pl.com.ezap.miab;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MessageCreate extends Activity {

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
		MIAB miab = MIAB.getInstance();
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
		MIAB.getInstance().m_message = text.getText().toString();
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
		MIAB message = MIAB.getInstance();
		message.m_message = text.getText().toString();
		if( m_location == null ) {
			//TODO: different behavior needed when gps signal not found
			m_location = m_gps.getLastLocation();
		}
		message.m_location = m_location;
		//TODO: send message to server!!!
	}

	private GPSDealer m_gps;
	private Location m_location;
}
