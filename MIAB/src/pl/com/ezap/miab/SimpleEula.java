package pl.com.ezap.miab;

import pl.com.ezap.miab.shared.GeneralMenuHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.KeyEvent;

public class SimpleEula
{
  private static final String EULA_KEY = "MIAB_EULA_KEY";
  private Activity mActivity;

  public SimpleEula( Activity context )
  {
    mActivity = context;
  }

  private PackageInfo getPackageInfo()
  {
    PackageInfo packageInfo = null;
    try {
      packageInfo =
          mActivity.getPackageManager().getPackageInfo(
              mActivity.getPackageName(),
              PackageManager.GET_ACTIVITIES );
    }
    catch( PackageManager.NameNotFoundException e ) {
      e.printStackTrace();
    }
    return packageInfo;
  }

  public void show()
  {
    final PackageInfo versionInfo = getPackageInfo();
    final SharedPreferences prefs = mActivity.getSharedPreferences(
        GeneralMenuHelper.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    if( versionInfo.versionCode != prefs.getInt( EULA_KEY, 0 ) ) {
      String title =
          mActivity.getString( R.string.app_name )
              + " v"
              + versionInfo.versionName;
      AlertDialog.Builder builder =
        new AlertDialog.Builder( mActivity )
          .setTitle( title )
          .setMessage( R.string.EULAString )
          .setPositiveButton(
              android.R.string.ok,
              new Dialog.OnClickListener() {
                @Override
                public
                    void
                    onClick( DialogInterface dialogInterface, int i )
                {
                  // Mark this version as read.
                  SharedPreferences.Editor editor = prefs.edit();
                  editor.putInt( EULA_KEY, versionInfo.versionCode );
                  editor.commit();
                  dialogInterface.dismiss();
                }
              } )
          .setNegativeButton(
              android.R.string.cancel,
              new Dialog.OnClickListener() {
                @Override
                public void onClick( DialogInterface dialog, int which )
                {
                  // Close the activity as they have declined the EULA
                  mActivity.finish();
                }
              } )
          .setOnKeyListener( new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(
                DialogInterface dialoginterface,
                int keyCode,
                KeyEvent event )
            {
              if( ( keyCode == KeyEvent.KEYCODE_HOME ) ) {
                return false;
              } else {
                return true;
              }
            }
          } )
          .setCancelable( false );
      builder.create().show();
    }
  }

}