package pl.com.ezap.miab;

import pl.com.ezap.miab.shared.GeneralMenuHelper;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity{

	private GeneralMenuHelper menuHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkGooglePlayServicesAvailability();

		setContentView(R.layout.activity_main);

		menuHelper = new GeneralMenuHelper( this );

		findViewById(R.id.button_leaveMsg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startMessageCreation(false,false);
					}
				});

		findViewById(R.id.button_throwMsg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startMessageCreation(true,false);
					}
				});

		findViewById(R.id.button_digMsg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						startMessageCreation(false,true);
					}
				});

		findViewById(R.id.button_searchDigMsg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});

		findViewById(R.id.button_foundMsg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent viewMIABs = new Intent(getMainActivity(), MessageListActivity.class);
						startActivity(viewMIABs);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.general, menu);
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
		return menuHelper.onOptionsItemSelected( item )
				? true
				: super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		menuHelper.onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	private MainActivity getMainActivity() {
		return this;
	}

	private void startMessageCreation(boolean isFlowing, boolean isDig) {
		Message.getInstance().m_isFlowing = isFlowing;
		Message.getInstance().m_isBurried = isDig;

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
}
