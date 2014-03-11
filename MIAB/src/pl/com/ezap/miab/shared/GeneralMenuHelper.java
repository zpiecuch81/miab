package pl.com.ezap.miab.shared;

import pl.com.ezap.miab.R;
import pl.com.ezap.miab.services.MIABService;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

public class GeneralMenuHelper {

	private Activity activity;

	public GeneralMenuHelper(Activity activity) {
		this.activity = activity;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
//		case R.id.action_settings:
//			Intent intent = new Intent(this, SettingsActivity.class);
//			startActivity(intent);
//			return true;
		case R.id.action_scanning:
			switchMIABService();
			activity.invalidateOptionsMenu();
			return true;
		}
		return false;
	}

	public void onPrepareOptionsMenu (Menu menu) {
		SharedPreferences settings = activity.getPreferences(0);
		if(settings.getBoolean("MIABServiceOn", true)) {
			menu.getItem(0).setIcon(R.drawable.action_scanning);
		} else {
			menu.getItem(0).setIcon(R.drawable.action_notscanning);
		}
		
	}

	private void switchMIABService()
	{
		SharedPreferences settings = activity.getPreferences(0);
		SharedPreferences.Editor settingsEditor = settings.edit();
		boolean enableNow = !settings.getBoolean("MIABServiceOn", true);
		settingsEditor.putBoolean("MIABServiceOn", enableNow);
		settingsEditor.commit();
		if( enableNow ) {
			activity.startService(new Intent( activity.getApplicationContext(), MIABService.class ));
		} else {
			activity.stopService(new Intent( activity.getApplicationContext(), MIABService.class ));
		}
	}
}
