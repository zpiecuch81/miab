package pl.com.ezap.miab;

import pl.com.ezap.miab.services.MIABService;
import pl.com.ezap.miab.shared.Message;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
		checkIsOnline();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_scanning:
			switchMIABService();
			invalidateOptionsMenu();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		SharedPreferences settings = getPreferences(0);
		if(settings.getBoolean("MIABServiceOn", true)) {
			menu.getItem(0).setIcon(R.drawable.action_scanning);
		} else {
			menu.getItem(0).setIcon(R.drawable.action_notscanning);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private void startMessageCreation(boolean isFlowing) {
		Message.getInstance().m_isFlowing = isFlowing;

		Intent intent = new Intent(this, CreateMessageActivity.class);
		startActivity(intent);
	}

	private void checkGooglePlayServicesAvailability()
	{
		if( GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
			Intent intent = new Intent(this, GooglePlayServicesMissingActivity.class);
			startActivity(intent);
		}
	}

	private void checkGPSEnabled()
	{
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			Toast.makeText( getApplicationContext(), R.string.msgEnableGPSToast, Toast.LENGTH_LONG ).show();
		}
	}

	private void checkIsOnline()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
			Toast.makeText( getApplicationContext(), R.string.msgEnableNetToast, Toast.LENGTH_LONG ).show();
		}
	}

	private void switchMIABService()
	{
		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor settingsEditor = settings.edit();
		boolean enableNow = !settings.getBoolean("MIABServiceOn", true);
		settingsEditor.putBoolean("MIABServiceOn", enableNow);
		settingsEditor.commit();
		if( enableNow ) {
			startService(new Intent( getApplicationContext(), MIABService.class ));
		} else {
			stopService(new Intent( getApplicationContext(), MIABService.class ));
		}
	}
}
