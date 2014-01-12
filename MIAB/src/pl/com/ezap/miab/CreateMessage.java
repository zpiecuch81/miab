package pl.com.ezap.miab;

import pl.com.ezap.miab.shared.Message;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateMessage extends Activity {

	private GPSDealer m_gps = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		EditText text = (EditText)findViewById(R.id.editMessageText);
		text.setFilters( new InputFilter[] { new InputFilter.LengthFilter(3000)} );

		findViewById(R.id.buttonMessageReady).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					messageReady();
				}
			});

		//GPS created just to make sure GPS signal is still searched for
		m_gps = new GPSDealer( (LocationManager)this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE) );
		m_gps.setSimpleStatusListener( new GPSDealer.GPSLocationProvider() {
			@Override
			public void GPSLocationChanged(Location location) {
			}
			@Override
			public void GPSAvailabilityChanged(boolean available) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.create_message, menu);
		return true;
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Message miab = Message.getInstance();
		EditText text = (EditText)findViewById(R.id.editMessageText);
		text.setText(miab.m_message);
		if( miab.m_isFlowing ) {
			text.setBackgroundResource(R.drawable.background_see);
			Button buttonLeave = (Button)(findViewById(R.id.buttonMessageReady));
			buttonLeave.setText(R.string.button_throwMsg);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		EditText text = (EditText)findViewById(R.id.editMessageText);
		Message.getInstance().m_message = text.getText().toString();
	}

	private void messageReady() {
		EditText text = (EditText)findViewById(R.id.editMessageText);
		Message message = Message.getInstance();
		message.m_message = text.getText().toString();

		Intent intent = new Intent(this, SendingMessage.class);
		startActivity(intent);
	}
}
