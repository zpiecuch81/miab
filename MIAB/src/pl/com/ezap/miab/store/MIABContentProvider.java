
package pl.com.ezap.miab.store;

import java.util.Arrays;
import java.util.HashSet;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class MIABContentProvider extends ContentProvider
{
  // database
  private MIABSQLiteHelper database;
  // used for the UriMacher
  private static final int MIABS = 10;
  private static final int MIAB_ID = 20;
  private static final String AUTHORITY = "pl.com.ezap.miab.store.contentProvider";
  private static final String BASE_PATH = "miabs";
  public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/" + BASE_PATH );
  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/miabs";
  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/miab";
  private static final UriMatcher sURIMatcher = new UriMatcher( UriMatcher.NO_MATCH );
  static {
    sURIMatcher.addURI( AUTHORITY, BASE_PATH, MIABS );
    sURIMatcher.addURI( AUTHORITY, BASE_PATH + "/#", MIAB_ID );
  }

  @Override
  public boolean onCreate()
  {
    database = new MIABSQLiteHelper( getContext() );
    return false;
  }

  @Override
  public Cursor query(
      Uri uri,
      String[] projection,
      String selection,
      String[] selectionArgs,
      String sortOrder )
  {
    // Using SQLiteQueryBuilder instead of query() method
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
    // check if the caller has requested a column which does not exists
    checkColumns( projection );
    // Set the table
    queryBuilder.setTables( MIABSQLiteHelper.TABLE_MIABS );
    int uriType = sURIMatcher.match( uri );
    switch( uriType ) {
      case MIABS:
        break;
      case MIAB_ID:
        // adding the ID to the original query
        queryBuilder.appendWhere( MIABSQLiteHelper.COLUMN_ID + "=" + uri.getLastPathSegment() );
        break;
      default:
        throw new IllegalArgumentException( "Unknown URI: " + uri );
    }
    if( sortOrder == null ) {
      sortOrder = MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP + " desc";
    }
    SQLiteDatabase db = database.getWritableDatabase();
    Cursor cursor =
        queryBuilder.query(
            db,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder );
    // make sure that potential listeners are getting notified
    cursor.setNotificationUri( getContext().getContentResolver(), uri );
    return cursor;
  }

  @Override
  public String getType( Uri uri )
  {
    return null;
  }

  @Override
  public Uri insert( Uri uri, ContentValues values )
  {
    int uriType = sURIMatcher.match( uri );
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    long id = 0;
    switch( uriType ) {
      case MIABS:
        id = sqlDB.insert( MIABSQLiteHelper.TABLE_MIABS, null, values );
        break;
      default:
        throw new IllegalArgumentException( "Unknown URI: " + uri );
    }
    getContext().getContentResolver().notifyChange( uri, null );
    return Uri.parse( BASE_PATH + "/" + id );
  }

  @Override
  public int delete( Uri uri, String selection, String[] selectionArgs )
  {
    int uriType = sURIMatcher.match( uri );
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsDeleted = 0;
    switch( uriType ) {
      case MIABS:
        rowsDeleted =
            sqlDB.delete(
                MIABSQLiteHelper.TABLE_MIABS,
                selection,
                selectionArgs );
        break;
      case MIAB_ID:
        String id = uri.getLastPathSegment();
        if( TextUtils.isEmpty( selection ) ) {
          rowsDeleted =
              sqlDB.delete(
                  MIABSQLiteHelper.TABLE_MIABS,
                  MIABSQLiteHelper.COLUMN_ID + "=" + id,
                  null );
        } else {
          rowsDeleted =
              sqlDB.delete(
                  MIABSQLiteHelper.TABLE_MIABS,
                  MIABSQLiteHelper.COLUMN_ID + "=" + id + " and " + selection,
                  selectionArgs );
        }
        break;
      default:
        throw new IllegalArgumentException( "Unknown URI: " + uri );
    }
    getContext().getContentResolver().notifyChange( uri, null );
    return rowsDeleted;
  }

  @Override
  public int update(
      Uri uri,
      ContentValues values,
      String selection,
      String[] selectionArgs )
  {
    int uriType = sURIMatcher.match( uri );
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsUpdated = 0;
    switch( uriType ) {
      case MIABS:
        rowsUpdated =
            sqlDB.update(
                MIABSQLiteHelper.TABLE_MIABS,
                values,
                selection,
                selectionArgs );
        break;
      case MIAB_ID:
        String id = uri.getLastPathSegment();
        if( TextUtils.isEmpty( selection ) ) {
          rowsUpdated =
              sqlDB.update(
                  MIABSQLiteHelper.TABLE_MIABS,
                  values,
                  MIABSQLiteHelper.COLUMN_ID + "=" + id,
                  null );
        } else {
          rowsUpdated =
              sqlDB.update(
                  MIABSQLiteHelper.TABLE_MIABS,
                  values,
                  MIABSQLiteHelper.COLUMN_ID + "=" + id + " and " + selection,
                  selectionArgs );
        }
        break;
      default:
        throw new IllegalArgumentException( "Unknown URI: " + uri );
    }
    getContext().getContentResolver().notifyChange( uri, null );
    return rowsUpdated;
  }

  private void checkColumns( String[] projection )
  {
    String[] available =
        {
            MIABSQLiteHelper.COLUMN_MESSAGE,
            MIABSQLiteHelper.COLUMN_HEAD,
            MIABSQLiteHelper.COLUMN_MESSAGE_FLAG,
            MIABSQLiteHelper.COLUMN_DROP_TIME_STAMP,
            MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP,
            MIABSQLiteHelper.COLUMN_LONGITUDE,
            MIABSQLiteHelper.COLUMN_LATITUDE,
            MIABSQLiteHelper.COLUMN_NOT_READ,
            MIABSQLiteHelper.COLUMN_ID };
    if( projection != null ) {
      HashSet<String> requestedColumns =
          new HashSet<String>( Arrays.asList( projection ) );
      HashSet<String> availableColumns =
          new HashSet<String>( Arrays.asList( available ) );
      // check if all columns which are requested are available
      if( !availableColumns.containsAll( requestedColumns ) ) {
        throw new IllegalArgumentException( "Unknown columns in projection" );
      }
    }
  }

}
