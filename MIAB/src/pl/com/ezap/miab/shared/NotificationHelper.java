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

public class NotificationHelper {

	private int FOUND_NOTIFICATION_ID = 101;
	//private int SENDING_NOTIFICATION_ID = 131;

	private Context context;
	private int unreadMessagesCount;
	private String contentText;
	private PendingIntent pendingIntent;

	public NotificationHelper( Context context ) {
		this.context = context;
	}

	public void updateFoundBottles() {
		Cursor cursor = getUnreadMessages();
		unreadMessagesCount = cursor.getCount();
		if( unreadMessagesCount == 0 ) {
			return;
		}
		cursor.moveToFirst();
		createContentText( cursor );
		createPendingIntent( cursor );
		createNotification( FOUND_NOTIFICATION_ID );
	}

	private Cursor getUnreadMessages() {
		String[] projection = {
				MIABSQLiteHelper.COLUMN_HEAD,
				MIABSQLiteHelper.COLUMN_ID };
		MIABSQLiteHelper dbHelper = new MIABSQLiteHelper( context );
		return dbHelper.getReadableDatabase().query(
				MIABSQLiteHelper.TABLE_MIABS,
				projection,
				MIABSQLiteHelper.COLUMN_NOT_READ +"=1",
				null,
				null,
				null,
				null );
	}

	private void createContentText( Cursor cursor ) {
		if( unreadMessagesCount == 1 ) {
			contentText = cursor.getString(
					cursor.getColumnIndexOrThrow( MIABSQLiteHelper.COLUMN_HEAD ) );
		} else {
			contentText = context.getString( R.string.msgNotificationManyBottleContent )
					+ Integer.toString( unreadMessagesCount );
		}
	}

	private void createPendingIntent( Cursor cursor ) {
		Intent resultIntent;
		if( unreadMessagesCount == 1 ) {
			resultIntent = new Intent( context, MessageViewActivity.class );
			Uri todoUri = Uri.parse(MIABContentProvider.CONTENT_URI + "/"
					+ Integer.toString( cursor.getInt(
							cursor.getColumnIndexOrThrow( MIABSQLiteHelper.COLUMN_ID ) ) ) );
			resultIntent.putExtra(MIABContentProvider.CONTENT_ITEM_TYPE, todoUri);
		} else {
			resultIntent = new Intent(context, MessageListActivity.class);
		}
		pendingIntent = PendingIntent.getActivity( 
			context,
			0,
			resultIntent,
			PendingIntent.FLAG_UPDATE_CURRENT );
	}

	private void createNotification( int notificationID) {
		Bitmap largeIcon = BitmapFactory.decodeResource( context.getResources(), R.drawable.ic_launcher );
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder( context )
				.setSmallIcon(android.R.drawable.ic_dialog_email, unreadMessagesCount )
				.setContentTitle( context.getString( R.string.msgNotificationFoundBottleTitle ) )
				.setContentText( contentText )
				.setLargeIcon( largeIcon )
				.setAutoCancel( true )
				.setContentIntent( pendingIntent );
		NotificationManager mNotifyMgr = 
				( NotificationManager ) context.getSystemService( Context.NOTIFICATION_SERVICE );
		mNotifyMgr.notify( notificationID, notificationBuilder.build() );
	}
}
