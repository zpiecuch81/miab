package pl.com.ezap.miab.store;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class MIABSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MIABS = "miabs";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_HEAD = "head";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_MESSAGE_FLAG = "flags";
	public static final String COLUMN_DROP_TIME_STAMP = "dropTimeStamp";
	public static final String COLUMN_FOUND_TIME_STAMP = "foundTimeStamp";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_LATITUDE = "latitude";

	public static final int MIAB_FLAG_DEFAULT = 0;
	public static final int MIAB_FLAG_WAS_FLOWING = 1;
	public static final int MIAB_FLAG_WAS_DIG = 2;

	private static final String DATABASE_NAME = "miabs.db";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MIABS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_HEAD + " text not null, "
			+ COLUMN_MESSAGE + " text not null, "
			+ COLUMN_MESSAGE_FLAG + " int not null, "
			+ COLUMN_DROP_TIME_STAMP + " int not null, "
			+ COLUMN_FOUND_TIME_STAMP + " int not null, "
			+ COLUMN_LONGITUDE + " long not null, "
			+ COLUMN_LATITUDE + " long not null "
			+ ");";
	//private static final String DATABASE_DROP = "drop table " + TABLE_MIABS;

	public MIABSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MIABSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MIABS);
		onCreate(db);
	}

	public boolean storeMessage(
			String message,
			long dropTimeStamp,
			boolean wasDig,
			boolean wasFlowing,
			Location foundLocation )
	{
		Log.i(MIABSQLiteHelper.class.getName(), "Storing message");
		long foundDate = new java.util.Date().getTime();
		int flags = wasDig ? MIAB_FLAG_WAS_DIG : wasFlowing ? MIAB_FLAG_WAS_FLOWING : MIAB_FLAG_DEFAULT;

		ContentValues values = new ContentValues();
		values.put( MIABSQLiteHelper.COLUMN_HEAD, createHead(foundDate, message) );
		values.put( MIABSQLiteHelper.COLUMN_MESSAGE, message);
		values.put( MIABSQLiteHelper.COLUMN_DROP_TIME_STAMP, dropTimeStamp);
		values.put( MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP, foundDate );
		values.put( MIABSQLiteHelper.COLUMN_MESSAGE_FLAG, flags);
		values.put( MIABSQLiteHelper.COLUMN_LONGITUDE, foundLocation.getLongitude() );
		values.put( MIABSQLiteHelper.COLUMN_LATITUDE, foundLocation.getLatitude() );

		boolean stored = false;
		try {
			stored = getWritableDatabase().insert( MIABSQLiteHelper.TABLE_MIABS, null, values) != -1;
		}
		catch( Exception e ) {
			Log.e( MIABSQLiteHelper.class.getName(), "Exception while storing message: " + e.getMessage() );
		}
		return stored;
	}

	private String createHead(long date, String message)
	{
		final int MAX_HEAD_MESSAGE_PART = 20;
		int end = message.length() <= MAX_HEAD_MESSAGE_PART ?
				message.length() - 1 : MAX_HEAD_MESSAGE_PART;
		int beforeSpaceEnd = message.trim().lastIndexOf(' ', end);
		return new java.util.Date(date).toString()
				+ ", "
				+ message.substring(0, beforeSpaceEnd == -1 ? end : beforeSpaceEnd)
				+ "...";
	}

	public void recreateTable() {
//		try{
//			getWritableDatabase().execSQL(DATABASE_DROP);
//			getWritableDatabase().execSQL(DATABASE_CREATE);
//		}
//		catch(Exception e)
//		{
//			Log.i(MIABSQLiteHelper.class.getName(), "table didn't exists" );
//			return;
//		}
//
//		//test data
//		storeMessage( "first test message", 1L, false, true, new Location(LocationManager.GPS_PROVIDER) );
//		storeMessage( "this is some quite long example of what could happen when long text was inputed to the line so stay" +
//						" tune for what is happenenning with this text, second test massage", 1L, true, false, new Location(LocationManager.GPS_PROVIDER) );
//		storeMessage( "third test massage", 1L, false, false, new Location(LocationManager.GPS_PROVIDER) );
	}

}
