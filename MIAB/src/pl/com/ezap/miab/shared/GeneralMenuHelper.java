package pl.com.ezap.miab.shared;

import pl.com.ezap.miab.R;
import pl.com.ezap.miab.services.MIABService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;

public class GeneralMenuHelper {

	static final String SHARED_PREFERENCES_NAME="sharedPreferences";
	private Activity activity;
	private Menu menu;

	public GeneralMenuHelper(Activity activity) {
		this.activity = activity;
		this.menu = null;
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

	public void onPrepareOptionsMenu(Menu menu)
	{
		this.menu = menu;
		updateMenuState();
	}

	public void updateMenuState()
	{
		if( menu == null ) {
			return;
		}
		SharedPreferences settings = activity.getSharedPreferences(
				SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		if(settings.getBoolean("MIABServiceOn", true)) {
			menu.getItem(0).setIcon(R.drawable.action_scanning);
		} else {
			menu.getItem(0).setIcon(R.drawable.action_notscanning);
		}
	}

	private void switchMIABService()
	{
		SharedPreferences settings = activity.getSharedPreferences(
				SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
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
