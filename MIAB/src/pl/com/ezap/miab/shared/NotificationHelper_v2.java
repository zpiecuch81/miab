package pl.com.ezap.miab.shared;

import pl.com.ezap.miab.MessageListActivity;
import pl.com.ezap.miab.MessageViewActivity;
import pl.com.ezap.miab.R;
import pl.com.ezap.miab.store.MIABContentProvider;
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationHelper_v2
{
  public static int DIG_SERVICE_NOTIFICATION_ID = 102;
  public static int SEARCH_SERVICE_NOTIFICATION_ID = 103;
  public static int SENDING_SERVICE_NOTIFICATION_ID = 104;
  private static int FOUND_BOTTLES_NOTIFICAITON_ID = 105;

  private int notificationID;
  private Context context;
  private Bitmap largeIcon;
  private String title;
  private String message;
  private int bottlesCount;
  private Boolean autoCancel;
  private Boolean ongoing;
  private Boolean progress;
  private PendingIntent pendingIntent;

  public NotificationHelper_v2( Context context, int notificationID, String title )
  {
    this.context = context;
    this.notificationID = notificationID;
    this.title = title;
    largeIcon = BitmapFactory.decodeResource( context.getResources(), R.drawable.icon_main );
    bottlesCount = 0;
    autoCancel = false;
    ongoing = true;
    progress = true;
    pendingIntent = null;
  }

  public void createUpdateNotification( String message )
  {
    Log.d( getClass().getName(), "createUpdateNotification for ID = " + notificationID );
    this.message = message;
    showNotification();
  }

  public void finalNotification( String message )
  {
    Log.d( getClass().getName(), "finalNotification for ID = " + notificationID );
    this.message = message;
    autoCancel = true;
    progress = false;
    ongoing = false;
    showNotification();
  }

  public void closeNotification()
  {
    Log.d( getClass().getName(), "closeNotification for ID = " + notificationID );
    NotificationManager notifyMgr =
        (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
    notifyMgr.cancel( notificationID );
  }

  public void updateFoundBottles()
  {
    Log.d( getClass().getName(), "updateFoundBottles for ID = " + notificationID );
    closeNotification();
    Cursor cursor = getUnreadMessages();
    bottlesCount = cursor.getCount();
    if( bottlesCount == 0 ) {
      return;
    }
    cursor.moveToFirst();
    createFoundContentText( cursor );
    createPendingIntent( cursor );
    cursor.close();

    notificationID = FOUND_BOTTLES_NOTIFICAITON_ID;
    autoCancel = true;
    progress = false;
    ongoing = false;
    showNotification();
  }

  private Cursor getUnreadMessages()
  {
    String[] projection =
        { MIABSQLiteHelper.COLUMN_HEAD, MIABSQLiteHelper.COLUMN_ID };
    MIABSQLiteHelper dbHelper = new MIABSQLiteHelper( context );
    return dbHelper.getReadableDatabase().query(
        MIABSQLiteHelper.TABLE_MIABS,
        projection,
        MIABSQLiteHelper.COLUMN_NOT_READ + "=1",
        null,
        null,
        null,
        null );
  }

  private void createFoundContentText( Cursor cursor )
  {
    if( bottlesCount == 1 ) {
      message =
          cursor.getString( cursor
              .getColumnIndexOrThrow( MIABSQLiteHelper.COLUMN_HEAD ) );
    } else {
      message =
          context.getString( R.string.msgNotificationManyBottleContent )
              + Integer.toString( bottlesCount );
    }
  }

  private void createPendingIntent( Cursor cursor )
  {
    Intent resultIntent;
    if( bottlesCount == 1 ) {
      resultIntent = new Intent( context, MessageViewActivity.class );
      Uri todoUri =
          Uri.parse( MIABContentProvider.CONTENT_URI + "/"
              + Integer.toString( cursor.getInt(
                  cursor.getColumnIndexOrThrow( MIABSQLiteHelper.COLUMN_ID ) ) ) );
      resultIntent.putExtra( MIABContentProvider.CONTENT_ITEM_TYPE, todoUri );
    } else {
      resultIntent = new Intent( context, MessageListActivity.class );
    }
    pendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            resultIntent,
            PendingIntent.FLAG_CANCEL_CURRENT );
  }

  private void showNotification()
  {
    NotificationCompat.Builder notificationBuilder =
      new NotificationCompat.Builder( context )
        .setSmallIcon( android.R.drawable.ic_dialog_email )
        .setLargeIcon( largeIcon )
        .setContentTitle( title )
        .setContentText( message )
        .setWhen( new java.util.Date().getTime() )
        .setAutoCancel( autoCancel )
        .setOngoing( ongoing )
        .setProgress( 0, 0, progress );
    if( pendingIntent != null ) {
      notificationBuilder.setContentIntent( pendingIntent );
    }
    if( bottlesCount > 1 ) {
      notificationBuilder.setNumber( bottlesCount );
    }
    NotificationManager notifyMgr =
        (NotificationManager)context.getSystemService( Context.NOTIFICATION_SERVICE );
    notifyMgr.notify( notificationID, notificationBuilder.build() );
  }
}
