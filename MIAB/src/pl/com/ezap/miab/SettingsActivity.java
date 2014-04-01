
package pl.com.ezap.miab;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import java.util.List;

public class SettingsActivity extends PreferenceActivity
{
  private static final boolean ALWAYS_SIMPLE_PREFS = false;

  @Override
  protected void onPostCreate( Bundle savedInstanceState )
  {
    super.onPostCreate( savedInstanceState );
    setupSimplePreferencesScreen();
  }

  @SuppressWarnings( "deprecation" )
  private void setupSimplePreferencesScreen()
  {
    if( !isSimplePreferences( this ) ) {
      return;
    }
    // In the simplified UI, fragments are not used at all and we instead
    // use the older PreferenceActivity APIs.
    // Add 'general' preferences.
    addPreferencesFromResource( R.xml.pref_general );
  }

  @Override
  public boolean onIsMultiPane()
  {
    return isXLargeTablet( this ) && !isSimplePreferences( this );
  }

  /** Helper method to determine if the device has an extra-large screen. For
   * example, 10" tablets are extra-large. */
  private static boolean isXLargeTablet( Context context )
  {
    return ( context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK ) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  private static boolean isSimplePreferences( Context context )
  {
    return ALWAYS_SIMPLE_PREFS
        || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
        || !isXLargeTablet( context );
  }

  /** {@inheritDoc} */
  @Override
  public void onBuildHeaders( List<Header> target )
  {
    if( !isSimplePreferences( this ) ) {
      loadHeadersFromResource( R.xml.pref_headers, target );
    }
  }

  /** This fragment shows general preferences only. It is used when the activity
   * is showing a two-pane settings UI. */
  @TargetApi( Build.VERSION_CODES.HONEYCOMB )
  public static class GeneralPreferenceFragment extends PreferenceFragment
  {
    @Override
    public void onCreate( Bundle savedInstanceState )
    {
      super.onCreate( savedInstanceState );
      addPreferencesFromResource( R.xml.pref_general );
    }
  }
}
