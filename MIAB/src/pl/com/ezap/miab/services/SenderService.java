
package pl.com.ezap.miab.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import pl.com.ezap.miab.CreateMessageActivity;
import pl.com.ezap.miab.R;
import pl.com.ezap.miab.messagev1endpoint.Messagev1endpoint;
import pl.com.ezap.miab.messagev1endpoint.model.GeoPt;
import pl.com.ezap.miab.messagev1endpoint.model.MessageV1;
import pl.com.ezap.miab.shared.GPSHelper;
import pl.com.ezap.miab.shared.GeneralMenuHelper;
import pl.com.ezap.miab.shared.GeoIndex;
import pl.com.ezap.miab.shared.MessageV1EndPoint;
import pl.com.ezap.miab.shared.NotificationHelper_v2;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SenderService extends Service
    implements GPSHelper.GPSListener
{
  final static public String MESSAGE_KEY = "pl.com.ezap.miab.services.Message2Send";
  final static public String IS_HIDDEN_KEY = "pl.com.ezap.miab.services.IsHidden";
  final static public String IS_FLOWING_KEY = "pl.com.ezap.miab.services.IsFlowing";
  private ArrayList<MessageV1> messages2send;
  private GPSHelper gpsHelper;
  private NotificationHelper_v2 notificationHelper;

  private class SendMessageTask extends AsyncTask<MessageV1, Integer, Long>
  {
    @Override
    protected Long doInBackground( MessageV1... miabMsg )
    {
      Messagev1endpoint endpoint = MessageV1EndPoint.get();
      try {
        endpoint.insertMessageV1( miabMsg[ 0 ] ).execute();
      }
      catch( IOException e ) {
        e.printStackTrace();
        return (long)1;
      }
      return (long)0;
    }

    @Override
    protected void onPostExecute( Long result )
    {
      Log.d( "SendMessageTask", "onPostExecute, result " + result );
      if( result != 0 ) {
        SharedPreferences settings = getSharedPreferences( GeneralMenuHelper.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
        SharedPreferences.Editor settingsEditor = settings.edit();
        settingsEditor.putString( CreateMessageActivity.MESSAGE_SHARED_PREF_KEY, messages2send.get( 0 ).getMessage() );
        settingsEditor.commit();
      }
      //if( messages2send.isEmpty() ) {
        finishService( result == 0 );
      //} else {
        //gpsHelper.start();
      //}
    }
  }

  static public boolean isHardwareReady( Context context, boolean displayToasts )
  {
    final LocationManager manager =
        (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
    if( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
      if( displayToasts ) {
        Toast.makeText(
            context.getApplicationContext(),
            R.string.msgEnableGPSToast,
            Toast.LENGTH_LONG ).show();
      }
      return false;
    }
    final ConnectivityManager cm =
        (ConnectivityManager)context.getSystemService( Context.CONNECTIVITY_SERVICE );
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if( netInfo == null || !netInfo.isConnected() ) {
      if( displayToasts ) {
        Toast.makeText(
            context.getApplicationContext(),
            R.string.msgEnableNetToast,
            Toast.LENGTH_LONG ).show();
      }
      return false;
    }
    return true;
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    gpsHelper = null;
    messages2send = new ArrayList<MessageV1>();
  }

  @Override
  public IBinder onBind( Intent arg0 )
  {
    return null;
  }

  @Override
  public int onStartCommand( Intent intent, int flags, int startId )
  {
    if( intent == null ) {
      return START_STICKY;
    }
    MessageV1 miabMessage = intent2message( intent );
    Log.i( "SenderService", "Adding message to queue" );
    messages2send.add( miabMessage );

    startSendingBottles();
    return START_STICKY;
  }


  @Override
  public void onLocationFound( Location foundLocation )
  {
    Log.d( "SenderService", "onLocationFound called, location = " + foundLocation.toString() );
    sendNextMessage( foundLocation );
  }

  @Override
  public void onGPSFailure()
  {
    Log.d( "SenderService", "onGPSFailure called" );
  }

  private MessageV1 intent2message( Intent intent ){
    MessageV1 miabMessage = new MessageV1();
    miabMessage.setMessage( intent.getStringExtra( MESSAGE_KEY ) );
    miabMessage.setHidden( intent.getBooleanExtra( IS_HIDDEN_KEY, false ) );
    miabMessage.setFlowing( intent.getBooleanExtra( IS_FLOWING_KEY, false ) );
    miabMessage.setTimeStamp( new java.util.Date().getTime() );
    setFloatingData( miabMessage );
    // ID can't be 0/null, app engine will assign a number to it anyway
    miabMessage.setId( miabMessage.getTimeStamp() );
    return miabMessage;
  }

  private void setFloatingData( MessageV1 miab )
  {
    if( miab.getFlowing() ) {
      Random r = new Random();
      float longitudeSpeed = (float)( r.nextInt( 90 ) - 45 ) / 10000.0f;
      float latitudeSpeed = (float)( r.nextInt( 90 ) - 45 ) / 10000.0f;
      GeoPt deltaGeoPt = new GeoPt();
      deltaGeoPt.setLatitude( latitudeSpeed );
      deltaGeoPt.setLongitude( longitudeSpeed );
      miab.setDeltaLocation( deltaGeoPt );
      miab.setFlowStamp( miab.getTimeStamp() );
    } else {
      miab.setDeltaLocation( null );
    }
  }

  private void startSendingBottles()
  {
    if( gpsHelper != null ) {
      return; // already started
    }

    notificationHelper = new NotificationHelper_v2(
        getApplicationContext(),
        NotificationHelper_v2.SENDING_SERVICE_NOTIFICATION_ID,
        getSendText( messages2send.get( 0 ) ) );
    notificationHelper.createUpdateNotification( getString( R.string.msgAcquireCurrentLocation ) );

    gpsHelper = new GPSHelper( 
        this.getApplication().getApplicationContext(),
        this,
        1000,
        10 );
    gpsHelper.start();

  }

  private void sendNextMessage( Location location )
  {
    gpsHelper.stop();
    if( messages2send.isEmpty() ) {
      finishService(true);
      return;
    }
    addGeoData( messages2send.get( 0 ), location );
    new SendMessageTask().execute( messages2send.get( 0 ) );
  }

  private void finishService(boolean success)
  {
    Log.i( "SenderService", "stopping service" );
    if( notificationHelper != null ) {
      notificationHelper.finalNotification(
          getString( success ? R.string.msgSendingDone : R.string.msgSendingError ) );
    }
    if( gpsHelper != null ) {
      gpsHelper.stop();
      gpsHelper = null;
    }
    stopSelf();
  }

  private void addGeoData( MessageV1 miab, Location location )
  {
    notificationHelper.createUpdateNotification( getSendText( miab ) );
    GeoPt geoPt = new GeoPt();
    if( miab.getFlowing() ) {
      geoPt.setLatitude( (float)location.getLatitude() + miab.getDeltaLocation().getLatitude() );
      geoPt.setLongitude( (float)location.getLongitude() + miab.getDeltaLocation().getLongitude() );
    } else {
      geoPt.setLatitude( (float)location.getLatitude() );
      geoPt.setLongitude( (float)location.getLongitude() );
    }
    miab.setLocation( geoPt );
    GeoIndex geoIndex = new GeoIndex();
    miab.setGeoIndex( geoIndex.getIndex( geoPt.getLatitude(), geoPt.getLongitude() ) );
  }

  private String getSendText( MessageV1 message )
  {
    int displayMessageID =
        message.getHidden()
            ? R.string.msgBurryingMessage : message .getFlowing()
                ? R.string.msgThrowingMessage : R.string.msgLeavingMessage;
    return getString( displayMessageID );
  }

}
