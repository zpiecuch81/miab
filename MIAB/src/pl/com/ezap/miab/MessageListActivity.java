package pl.com.ezap.miab;

import pl.com.ezap.miab.shared.GeneralMenuHelper;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MessageListActivity extends ListActivity implements LoaderCallbacks<Cursor>
{

	private GeneralMenuHelper menuHelper;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private SimpleCursorAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miab_list);
		this.getListView().setDividerHeight(2);

		menuHelper = new GeneralMenuHelper( this );

		MIABSQLiteHelper helper = new MIABSQLiteHelper( getApplicationContext() );
		helper.recreateTable();

		fillData();
		registerForContextMenu(getListView());
	}

	@Override
	public void onResume()
	{
		super.onResume();
		menuHelper.updateMenuState();
		getLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		//getLoaderManager().destroyLoader( 0 );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.general, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return menuHelper.onOptionsItemSelected( item )
				? true
				: super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		menuHelper.onPrepareOptionsMenu(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
			Uri uri = Uri.parse(MIABContentProvider.CONTENT_URI + "/"
					+ info.id);
			getContentResolver().delete(uri, null, null);
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	// Opens the second activity if an entry is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, MessageViewActivity.class);
		Uri todoUri = Uri.parse(MIABContentProvider.CONTENT_URI + "/" + id);
		i.putExtra(MIABContentProvider.CONTENT_ITEM_TYPE, todoUri);

		startActivity(i);
	}

	private void fillData() {
		String[] from = new String[] {
				MIABSQLiteHelper.COLUMN_HEAD,
				MIABSQLiteHelper.COLUMN_ID,
				MIABSQLiteHelper.COLUMN_NOT_READ
				};
		// Fields on the UI to which we map
		int[] to = new int[] { R.id.label };
		adapter = new SimpleCursorAdapter(this, R.layout.miab_row, null, from, to, 0);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (view.getId() == R.id.label)
				{
					int isNotRead = cursor.getInt(
							cursor.getColumnIndexOrThrow( MIABSQLiteHelper.COLUMN_NOT_READ ) );
					TextView tv = (TextView)view;
					if( isNotRead != 0 ) {
						tv.setTypeface( null, Typeface.BOLD );
					} else {
						tv.setTypeface( null, Typeface.NORMAL );
					}
					tv.setText( cursor.getString( columnIndex ) );
				}
				return true;
			}
		});

		setListAdapter(adapter);
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	// creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = {
				MIABSQLiteHelper.COLUMN_ID,
				MIABSQLiteHelper.COLUMN_HEAD,
				MIABSQLiteHelper.COLUMN_NOT_READ };
		CursorLoader cursorLoader = new CursorLoader(this,
				MIABContentProvider.CONTENT_URI, projection, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		adapter.swapCursor(null);
	}

}
