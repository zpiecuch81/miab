
package pl.com.ezap.miab.services;

import pl.com.ezap.miab.R;
import pl.com.ezap.miab.shared.LocationHelper;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class SearchService extends Service implements NetworkBroadcastReceiver.NetworkStateListener
{
  static final int SERVICE_ICON_NOTIFICATION_ID = 123;
  static final int GPS_CHECK_TIME_INTERVAL = 10000;
  private LocationManager locationManager;
  private LocationListener locationListener;
  private NetworkBroadcastReceiver networkReceiver;
  private Bitmap largeIcon;
  private int maxGPSFoundTrials;

  @Override
  public void onCreate()
  {
    super.onCreate();
    locationManager = null;
    locationListener = null;
    maxGPSFoundTrials = 5;
    largeIcon = BitmapFactory.decodeResource(
        getApplicationContext().getResources(), R.drawable.icon_main );
    showServiceIcon();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    stopGPSListening();
    locationManager = null;
    locationListener = null;
    if( networkReceiver != null ) {
      unregisterReceiver( networkReceiver );
      networkReceiver = null;
    }
    removeServiceIcon();
  }

  @Override
  public int onStartCommand( Intent intent, int flags, int startId )
  {
    if( locationManager != null ) {
      return START_STICKY;
    }
    startGPSListening();
//    BottleSearcher.searchAtLocation(
//        new Location( LocationManager.GPS_PROVIDER ),
//        this.getApplicationContext() );
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
    startGPSListening();
  }

  @Override
  public void onNetwoorkUnavailable()
  {
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
          if( LocationHelper.isAccuracyEnough( location ) || --maxGPSFoundTrials < 0 ) {
            searchMessage( location );
            maxGPSFoundTrials = 3;
          }
        }

        @Override
        public void onProviderDisabled( String provider )
        {
          if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
          }
        }

        @Override
        public void onProviderEnabled( String provider )
        {
          if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
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
                break;
              case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
              case LocationProvider.AVAILABLE:
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
    BottleSearcher.searchAtLocation( location, this.getApplicationContext() );
  }

  private void showServiceIcon()
  {
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder( this.getApplicationContext() )
          .setSmallIcon( R.drawable.ic_stat_notify_searching )
          .setLargeIcon( largeIcon )
          .setContentTitle( getString( R.string.app_name ) )
          .setContentText( getString( R.string.msgNotificationSearchingBottles ) )
          .setAutoCancel( false )
          .setOngoing( true );
    NotificationManager notifier =
        (NotificationManager)this.getApplicationContext().
        getSystemService( Context.NOTIFICATION_SERVICE );
    notifier.notify( SERVICE_ICON_NOTIFICATION_ID, notificationBuilder.build() );
  }

  private void removeServiceIcon()
  {
    ((NotificationManager)this.getApplicationContext().
        getSystemService( Context.NOTIFICATION_SERVICE )).cancel( SERVICE_ICON_NOTIFICATION_ID );
  }
}
