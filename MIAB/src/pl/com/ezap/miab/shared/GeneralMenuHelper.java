
package pl.com.ezap.miab.shared;

import pl.com.ezap.miab.AboutActivity;
import pl.com.ezap.miab.R;
import pl.com.ezap.miab.services.SearchService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

public class GeneralMenuHelper
{
  public static final String SHARED_PREFERENCES_NAME = "sharedPreferences";
  private Activity activity;
  private Menu menu;

  public GeneralMenuHelper( Activity activity )
  {
    this.activity = activity;
    this.menu = null;
  }

  public boolean onOptionsItemSelected( MenuItem item )
  {
    // Handle presses on the action bar items
    switch( item.getItemId() ) {
    // case R.id.action_settings:
    // Intent intent = new Intent(this, SettingsActivity.class);
    // startActivity(intent);
    // return true;
      case R.id.action_scanning:
        switchMIABService();
        activity.invalidateOptionsMenu();
        return true;
      case R.id.action_help:
        Intent helpIntent = new Intent();
        helpIntent.setAction(Intent.ACTION_VIEW);
        helpIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        helpIntent.setData(Uri.parse(activity.getString( R.string.action_help_url )));
        activity.startActivity(helpIntent);
        return true;
      case R.id.action_about:
        Intent intent = new Intent( activity, AboutActivity.class );
        activity.startActivity( intent );
        return true;
    }
    return false;
  }

  public void onPrepareOptionsMenu( Menu menu )
  {
    this.menu = menu;
    updateMenuState();
  }

  public void updateMenuState()
  {
    if( menu == null ) {
      return;
    }
    SharedPreferences settings =
        activity.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    if( settings.getBoolean( "MIABServiceOn", false ) ) {
      menu.getItem( 0 ).setIcon( R.drawable.action_scanning );
    } else {
      menu.getItem( 0 ).setIcon( R.drawable.action_notscanning );
    }
  }

  private void switchMIABService()
  {
    SharedPreferences settings =
        activity.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    SharedPreferences.Editor settingsEditor = settings.edit();
    boolean enableNow = !settings.getBoolean( "MIABServiceOn", false );
    settingsEditor.putBoolean( "MIABServiceOn", enableNow );
    settingsEditor.commit();
    if( enableNow ) {
      activity.startService( new Intent( activity.getApplicationContext(), SearchService.class ) );
    } else {
      activity.stopService( new Intent( activity.getApplicationContext(), SearchService.class ) );
    }
  }
}
