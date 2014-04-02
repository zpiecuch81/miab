
package pl.com.ezap.miab.services;

import pl.com.ezap.miab.shared.LocationHelper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SearchService extends Service implements NetworkBroadcastReceiver.NetworkStateListener
{
  static final int GPS_CHECK_TIME_INTERVAL = 2000;
  private LocationManager locationManager;
  private LocationListener locationListener;
  private NetworkBroadcastReceiver networkReceiver;
  private int maxGPSFoundTrials;

  @Override
  public void onCreate()
  {
    super.onCreate();
    locationManager = null;
    locationListener = null;
    maxGPSFoundTrials = 5;
  }

  @Override
  public void onDestroy()
  {
    Toast.makeText(
        getApplicationContext(),
        "MIAB Service stopped",
        Toast.LENGTH_LONG ).show();
    super.onDestroy();
    stopGPSListening();
    locationManager = null;
    locationListener = null;
    if( networkReceiver != null ) {
      unregisterReceiver( networkReceiver );
      networkReceiver = null;
    }
  }

  @Override
  public int onStartCommand( Intent intent, int flags, int startId )
  {
    Log.i( "MIABService", "onStartCommand called" );
    if( locationManager != null ) {
      return START_STICKY;
    }
    Toast.makeText(
        getApplicationContext(),
        "MIAB Service started",
        Toast.LENGTH_LONG ).show();
    //TODO: revert it back!!!
    // startGPSListening();
    BottleSearcher.searchAtLocation(
        new Location( LocationManager.GPS_PROVIDER ),
        this.getApplicationContext() );
    // If we get killed, after returning from here, restart
    return START_STICKY;
  }

  @Override
  public IBinder onBind( Intent arg0 )
  {
    return null;
  }

  @Override
  public void onNetwoorkAvailable()
  {
    Log.i( "MIABService", "onNetwoorkAvailable" );
    startGPSListening();
  }

  @Override
  public void onNetwoorkUnavailable()
  {
    Log.i( "MIABService", "onNetwoorkUnavailable" );
    stopGPSListening();
  }

  private void startGPSListening()
  {
    initializeListeners();
    maxGPSFoundTrials = 3;
    locationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        GPS_CHECK_TIME_INTERVAL,
        0,
        locationListener );
  }

  private void stopGPSListening()
  {
    if( locationManager != null && locationListener != null ) {
      locationManager.removeUpdates( locationListener );
    }
  }

  private void initializeListeners()
  {
    if( locationManager == null ) {
      locationManager =
          (LocationManager)this.getApplicationContext().getSystemService( Context.LOCATION_SERVICE );
    }
    if( locationListener == null ) {
      locationListener = new LocationListener() {
        @Override
        public void onLocationChanged( Location location )
        {
          Log.i( "MIABService", "onLocationChanged" );
          if( LocationHelper.isAccuracyEnough( location ) || --maxGPSFoundTrials < 0 ) {
            searchMessage( location );
            maxGPSFoundTrials = 3;
          }
        }

        @Override
        public void onProviderDisabled( String provider )
        {
          if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
            Log.i( "MIABService", "onProviderDisabled" );
          }
        }

        @Override
        public void onProviderEnabled( String provider )
        {
          if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
            Log.i( "MIABService", "onProviderEnabled" );
          }
        }

        @Override
        public
            void
            onStatusChanged( String provider, int status, Bundle extras )
        {
          if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
            switch( status ) {
              case LocationProvider.OUT_OF_SERVICE:
                Log.i(
                    "MIABService",
                    "onStatusChanged: LocationProvider.OUT_OF_SERVICE" );
                break;
              case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.i(
                    "MIABService",
                    "onStatusChanged: LocationProvider.TEMPORARILY_UNAVAILABLE" );
                break;
              case LocationProvider.AVAILABLE:
                Log.i(
                    "MIABService",
                    "onStatusChanged: LocationProvider.AVAILABLE" );
                break;
            }
          }
        }
      };
    }
    if( networkReceiver == null ) {
      networkReceiver = new NetworkBroadcastReceiver( this );
      IntentFilter filter = new IntentFilter( ConnectivityManager.CONNECTIVITY_ACTION );
      registerReceiver( networkReceiver, filter );
    }
  }

  private void searchMessage( Location location )
  {
    Log.i( "MIABService", "searchMessage on location "
        + location.getLatitude() + "," + location.getLongitude() );
    Toast
        .makeText( getApplicationContext(), "SEARCHING", Toast.LENGTH_SHORT )
        .show();
    BottleSearcher.searchAtLocation( location, this.getApplicationContext() );
  }

}
