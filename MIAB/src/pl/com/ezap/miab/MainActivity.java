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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity{

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
		checkGPSEnabled();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		stopService(new Intent( getApplicationContext(), MIABService.class ));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void startMessageCreation(boolean isFlowing) {
		Message.getInstance().m_isFlowing = isFlowing;

		Intent intent = new Intent(this, CreateMessageActivity.class);
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

	private void checkGPSEnabled()
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			Toast.makeText( getApplicationContext(), R.string.msgEnableGPSToast, Toast.LENGTH_LONG ).show();
		}
	}
}
