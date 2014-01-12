package pl.com.ezap.miab;

import pl.com.ezap.miab.service.MIABService;
import pl.com.ezap.miab.shared.Message;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends Activity{

	private GPSDealer m_GPS;
	private boolean m_GPS_OK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkGooglePlayServicesAvailability();

		setContentView(R.layout.activity_main);

		findViewById(R.id.button_leaveMsg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startMessageCreation(false);
					}
				});

		findViewById(R.id.button_throwMsg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startMessageCreation(true);
					}
				});

		startService(new Intent( getApplicationContext(), MIABService.class ));

		m_GPS = new GPSDealer( (LocationManager)this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE) );
		m_GPS.setSimpleStatusListener( new GPSDealer.GPSSimpleStatus() {
			@Override
			public void GPSAvailabilityChanged(boolean available) {
				m_GPS_OK = available;
				updateStatuses();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onStart()
	{
		super.onStart();
		m_GPS_OK = m_GPS.isGPSAvailable();
		updateStatuses();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		stopService(new Intent( getApplicationContext(), MIABService.class ));
	}

	private void startMessageCreation(boolean isFlowing) {
		Message.getInstance().m_isFlowing = isFlowing;

		Intent intent = new Intent(this, CreateMessage.class);
		startActivity(intent);
	}

	private void checkGooglePlayServicesAvailability()
	{
		if( GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setMessage(getString(R.string.playServicesNotAvailable));
			ad.setButton( DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {  
					dialog.dismiss();
				}
			});
			ad.show();
			this.finish();
		}
	}

	private void updateStatuses()
	{
		//update GPS checkbox
		CheckBox checkBox = (CheckBox)(findViewById(R.id.checkBox_GPS));
		checkBox.setChecked(m_GPS_OK);
	}

}
