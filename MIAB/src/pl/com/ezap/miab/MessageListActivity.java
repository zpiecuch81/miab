
package pl.com.ezap.miab;

import pl.com.ezap.miab.services.SenderService;
import pl.com.ezap.miab.shared.GeneralMenuHelper;
import pl.com.ezap.miab.shared.NotificationHelper_v2;
import pl.com.ezap.miab.store.MIABContentProvider;
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MessageListActivity extends ListActivity
    implements
    LoaderCallbacks<Cursor>
{
  private GeneralMenuHelper menuHelper;
  private static final int DELETE_ID = Menu.FIRST + 1;
  private static final int LEAVE_ID = Menu.FIRST + 2;
  private static final int THROW_ID = Menu.FIRST + 3;
  private static final int HIDE_ID = Menu.FIRST + 4;
  private SimpleCursorAdapter adapter;

  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_miab_list );
    this.getListView().setDividerHeight( 2 );
    menuHelper = new GeneralMenuHelper( this );
    MIABSQLiteHelper helper = new MIABSQLiteHelper( getApplicationContext() );
    helper.recreateTable();
    fillData();
    registerForContextMenu( getListView() );
  }

  @Override
  public void onResume()
  {
    super.onResume();
    menuHelper.updateMenuState();
    getLoaderManager().restartLoader( 0, null, this );
  }

  @Override
  public void onPause()
  {
    super.onPause();
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    getMenuInflater().inflate( R.menu.general, menu );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected( MenuItem item )
  {
    return menuHelper.onOptionsItemSelected( item ) ? true :
      super.onOptionsItemSelected( item );
  }

  @Override
  public boolean onPrepareOptionsMenu( Menu menu )
  {
    menuHelper.onPrepareOptionsMenu( menu );
    return super.onPrepareOptionsMenu( menu );
  }

  @Override
  public boolean onContextItemSelected( MenuItem item )
  {
    switch( item.getItemId() ) {
      case DELETE_ID:
        AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        Uri uri = Uri.parse( MIABContentProvider.CONTENT_URI + "/" + info.id );
        getContentResolver().delete( uri, null, null );
        fillData();
        return true;
      case LEAVE_ID:
        leaveMessage( item, false, false );
        return true;
      case THROW_ID:
        leaveMessage( item, true, false );
        return true;
      case HIDE_ID:
        leaveMessage( item, false, true );
        return true;
    }
    return super.onContextItemSelected( item );
  }

  // Opens the second activity if an entry is clicked
  @Override
  protected void onListItemClick( ListView l, View v, int position, long id )
  {
    super.onListItemClick( l, v, position, id );
    Intent i = new Intent( this, MessageViewActivity.class );
    Uri todoUri = Uri.parse( MIABContentProvider.CONTENT_URI + "/" + id );
    i.putExtra( MIABContentProvider.CONTENT_ITEM_TYPE, todoUri );
    startActivity( i );
  }

  private void fillData()
  {
    showEmptyText( View.VISIBLE );
    String[] from =
        new String[] {
            MIABSQLiteHelper.COLUMN_MESSAGE,
            MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP,
            MIABSQLiteHelper.COLUMN_ID,
            MIABSQLiteHelper.COLUMN_NOT_READ };
    int[] to = new int[] { R.id.bottleRowHeader, R.id.bottleRowDate };

    adapter = new SimpleCursorAdapter( this, R.layout.miab_row, null, from, to, 0 );
    adapter.setViewBinder( new SimpleCursorAdapter.ViewBinder() {
      public boolean setViewValue( View view, Cursor cursor, int columnIndex )
      {
        showEmptyText( View.INVISIBLE );
        if( view.getId() == R.id.bottleRowHeader ) {
          TextView tv = (TextView)view;
          tv.setTypeface( null, getTypeFaceStyle( cursor ) );
          String message = NotificationHelper_v2.createMessageHead( cursor.getString( columnIndex ) );
          tv.setText( message );
        } else if( view.getId() == R.id.bottleRowDate ) {
          TextView tv = (TextView)view;
          tv.setTypeface( null, getTypeFaceStyle( cursor ) );
          tv.setText( getString( R.string.msgMessageFound ) + " " +
              DateFormat.getDateFormat( getApplicationContext() ).
                format( new java.util.Date( cursor.getLong( columnIndex ) ) ) );
        }
        return true;
      }
    } );

    setListAdapter( adapter );
    getLoaderManager().initLoader( 0, null, this );
  }

  @Override
  public void onCreateContextMenu(
      ContextMenu menu,
      View v,
      ContextMenuInfo menuInfo )
  {
    super.onCreateContextMenu( menu, v, menuInfo );
    menu.add( 0, DELETE_ID, 0, R.string.menu_delete );
    menu.add( 0, LEAVE_ID, 0, R.string.menu_leave );
    menu.add( 0, THROW_ID, 0, R.string.menu_throw );
    menu.add( 0, HIDE_ID, 0, R.string.menu_hide );
  }

  // creates a new loader after the initLoader () call
  @Override
  public Loader<Cursor> onCreateLoader( int id, Bundle args )
  {
    String[] projection =
        {
            MIABSQLiteHelper.COLUMN_ID,
            MIABSQLiteHelper.COLUMN_MESSAGE,
            MIABSQLiteHelper.COLUMN_NOT_READ,
            MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP };
    CursorLoader cursorLoader =
        new CursorLoader(
            this,
            MIABContentProvider.CONTENT_URI,
            projection,
            null,
            null,
            null );
    return cursorLoader;
  }

  @Override
  public void onLoadFinished( Loader<Cursor> loader, Cursor data )
  {
    adapter.swapCursor( data );
  }

  @Override
  public void onLoaderReset( Loader<Cursor> loader )
  {
    // data is not available anymore, delete reference
    adapter.swapCursor( null );
  }

  private int getTypeFaceStyle( Cursor cursor )
  {
    if( isNewBottle( cursor ) ) {
      return Typeface.BOLD;
    } else {
      return Typeface.NORMAL;
    }
  }

  private boolean isNewBottle( Cursor cursor )
  {
    return cursor.getInt( cursor
            .getColumnIndexOrThrow( MIABSQLiteHelper.COLUMN_NOT_READ ) ) != 0;
  }

  private void showEmptyText( int show )
  {
    findViewById( R.id.emptyText ).setVisibility( show );
  }

  private void leaveMessage( MenuItem item, boolean isFlowing, boolean isHidden )
  {
    if( SenderService.isHardwareReady( this, true ) ) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
      Uri uri = Uri.parse( MIABContentProvider.CONTENT_URI + "/" + info.id );
      Cursor cursor = 
          getContentResolver().query(
              uri, new String[]{
                  MIABSQLiteHelper.COLUMN_ID,
                  MIABSQLiteHelper.COLUMN_MESSAGE},
              null, null, null );
      if( cursor != null ) {
        cursor.moveToFirst();
        int index = cursor.getColumnIndexOrThrow( MIABSQLiteHelper.COLUMN_MESSAGE );
        String message = cursor.getString( index );
        Intent sendMessageIntent = new Intent( getApplicationContext(), SenderService.class );
        sendMessageIntent.putExtra( SenderService.IS_FLOWING_KEY, isFlowing );
        sendMessageIntent.putExtra( SenderService.IS_HIDDEN_KEY, isHidden );
        sendMessageIntent.putExtra( SenderService.MESSAGE_KEY, message );
        startService( sendMessageIntent );
        cursor.close();
        getContentResolver().delete( uri, null, null );
        fillData();
      }
    }
  }
}
