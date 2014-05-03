
package pl.com.ezap.miab.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import pl.com.ezap.miab.R;
import pl.com.ezap.miab.shared.BottleGrabber;
import pl.com.ezap.miab.shared.GPSHelper;
import pl.com.ezap.miab.shared.NotificationHelper_v2;

public class DigService extends Service
    implements
    GPSHelper.GPSListener,
    BottleGrabber.BottleListener
{
  private NetworkBroadcastReceiver networkReceiver;
  private GPSHelper gpsHelper;
  private BottleGrabber bottleGrabber;
  private NotificationHelper_v2 notifications; 

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

    notifications = new NotificationHelper_v2(
        getApplicationContext(),
        NotificationHelper_v2.DIG_SERVICE_NOTIFICATION_ID,
        getString( R.string.msgNotificationSearchingHidden ) );
    notifications.createUpdateNotification( getString( R.string.msgAcquireCurrentLocation ) );

    gpsHelper = new GPSHelper( 
        this.getApplication().getApplicationContext(),
        this,
        10000,
        3 );
    gpsHelper.start();

    return START_NOT_STICKY;
  }

  @Override
  public IBinder onBind( Intent arg0 )
  {
    return null;
  }

  @Override
  public void onLocationFound( Location foundLocation )
  {
    notifications.createUpdateNotification( getString( R.string.msgNotificationSearchingHidden ) );
    if( bottleGrabber == null ) {
      bottleGrabber = new BottleGrabber( this.getApplication().getApplicationContext(), this );
    }
    gpsHelper.stop();
    bottleGrabber.dig( foundLocation );
  }

  @Override
  public void onGPSFailure()
  {
    notifications.finalNotification( getString( R.string.msgNotificationGPSError ) );
    stopSelf();
  }

  @Override
  public void onGrabFinished( int foundBottlesNumber )
  {
    if( foundBottlesNumber > 0 ) {
      notifications.updateFoundBottles( true );
    } else {
      notifications.finalNotification( getString( R.string.msgNotificationNoBottleFound ) );
    }
    stopSelf();
  }

  @Override
  public void onGrabFailure()
  {
    notifications.finalNotification( getString( R.string.msgNotificationGrabError ) );
    stopSelf();
  }

}
