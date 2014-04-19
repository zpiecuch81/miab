
package pl.com.ezap.miab.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import pl.com.ezap.miab.shared.GPSHelper;

public class DigService extends Service
    implements
    NetworkBroadcastReceiver.NetworkStateListener,
    GPSHelper.GPSListener
{
  private NetworkBroadcastReceiver networkReceiver;
  private GPSHelper gpsHelper;

  @Override
  public void onCreate()
  {
    super.onCreate();
    gpsHelper = null;
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    if( gpsHelper != null ) {
      gpsHelper.stop();
      gpsHelper = null;
    }
    if( networkReceiver != null ) {
      unregisterReceiver( networkReceiver );
      networkReceiver = null;
    }
  }

  @Override
  public int onStartCommand( Intent intent, int flags, int startId )
  {
    if( gpsHelper != null ) {
      return START_NOT_STICKY;
    }

    gpsHelper = new GPSHelper( 
        this.getApplication().getApplicationContext(),
        this,
        10000,
        3 );
    gpsHelper.start();

    if( networkReceiver == null ) {
      networkReceiver = new NetworkBroadcastReceiver( this );
      IntentFilter filter =
          new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION );
      registerReceiver( networkReceiver, filter );
    }

    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind( Intent arg0 )
  {
    return null;
  }

  @Override
  public void onNetwoorkAvailable()
  { }

  @Override
  public void onNetwoorkUnavailable()
  { }

  @Override
  public void onLocationFound( Location foundLocation )
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void onGPSFailure()
  {
    // TODO Auto-generated method stub
  }

  private void searchMessage( Location location )
  {
    Log.i( "MIABService", "searchMessage on location "
        + location.getLatitude()
        + ","
        + location.getLongitude() );
    BottleSearcher.digAtLocation( location, this.getApplicationContext() );
  }

}
