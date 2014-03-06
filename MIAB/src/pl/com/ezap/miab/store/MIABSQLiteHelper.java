package pl.com.ezap.miab.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MIABSQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MIABS = "miabs";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_WAS_FLOWING = "wasFlowing";
	public static final String COLUMN_WAS_DIG = "wasDig";
	public static final String COLUMN_DROP_TIME_STAMP = "dropTimeStamp";
	public static final String COLUMN_FOUND_TIME_STAMP = "foundTimeStamp";
	public static final String COLUMN_LOCATION = "location";

	private static final String DATABASE_NAME = "miabs.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MIABS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MESSAGE + " blob not null, "
			+ COLUMN_WAS_FLOWING + " int not null, "
			+ COLUMN_WAS_DIG + " int not null, "
			+ COLUMN_DROP_TIME_STAMP + " int not null, "
			+ COLUMN_FOUND_TIME_STAMP + " int not null, "
			+ COLUMN_LOCATION + " blob not null, "
			+ ");";
	
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

}
