package pl.com.ezap.miab;

import pl.com.ezap.miab.shared.GeneralMenuHelper;
import pl.com.ezap.miab.store.MIABContentProvider;
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * TodoDetailActivity allows to enter a new todo item 
 * or to change an existing
 */
public class MessageViewActivity extends Activity {

	private GeneralMenuHelper menuHelper;
	private TextView messageView;
	private TextView detailsView;
	private Uri todoUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_message_view);

		menuHelper = new GeneralMenuHelper( this );

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		messageView = (TextView) findViewById(R.id.message_view);
		detailsView = (TextView) findViewById(R.id.message_details);

		Bundle extras = getIntent().getExtras();

		// check from the saved Instance
		todoUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(MIABContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			todoUri = extras
					.getParcelable(MIABContentProvider.CONTENT_ITEM_TYPE);

			processMIAB(todoUri);
		}

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

	private void processMIAB( Uri uri )
	{
		Cursor cursor = getCursorToMIAB( uri );
		if( cursor != null ) {
			cursor.moveToFirst();
			setBackground( cursor );
			fillData( cursor );
			cursor.close();
		}
	}

	private Cursor getCursorToMIAB(Uri uri) 
	{
		String[] projection = {
				MIABSQLiteHelper.COLUMN_MESSAGE,
				MIABSQLiteHelper.COLUMN_MESSAGE_FLAG,
				MIABSQLiteHelper.COLUMN_DROP_TIME_STAMP,
				MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP,
				MIABSQLiteHelper.COLUMN_LONGITUDE,
				MIABSQLiteHelper.COLUMN_LATITUDE,
				MIABSQLiteHelper.COLUMN_ID };
		return getContentResolver().query(uri, projection, null, null, null);
	}

	private void setBackground(Cursor cursor) {
		int flags = cursor.getInt(cursor.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_MESSAGE_FLAG));
		if( flags == MIABSQLiteHelper.MIAB_FLAG_WAS_FLOWING ) {
			LinearLayout layout= (LinearLayout)findViewById(R.id.viewMessageLayout);
			layout.setBackgroundResource(R.drawable.bkg_throw);
		} else if( flags == MIABSQLiteHelper.MIAB_FLAG_WAS_DIG ) {
			LinearLayout layout= (LinearLayout)findViewById(R.id.viewMessageLayout);
			layout.setBackgroundResource(R.drawable.bkg_dig);
		}
	}

	private void fillData(Cursor cursor) {

		messageView.setText( cursor.getString(
				cursor.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_MESSAGE)) );

		int flags = cursor.getInt(cursor.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_MESSAGE_FLAG));
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder
			.append( getString(R.string.msgMessageLeft) )
			.append(
				new java.util.Date(
					cursor.getLong(
						cursor.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_DROP_TIME_STAMP))
					).toString() )
			.append("\n" + getString(R.string.msgMessageFound) )
			.append(
				new java.util.Date(
					cursor.getLong(
						cursor.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP))
					).toString() )
			.append("\n" + getString(R.string.msgMessageFoundLocation) )
			.append(
				cursor.getLong(
					cursor.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_LONGITUDE)))
			.append(",")
			.append(
				cursor.getLong(
					cursor.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_LATITUDE)));
		if( flags == MIABSQLiteHelper.MIAB_FLAG_WAS_FLOWING ) {
			messageBuilder.append("\n" + getString(R.string.msgMIABwasFlowing) );
		} else if( flags == MIABSQLiteHelper.MIAB_FLAG_WAS_DIG ) {
			messageBuilder.append("\n" + getString(R.string.msgMIABwasDig) );
		}

		detailsView.setText(messageBuilder);
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(MIABContentProvider.CONTENT_ITEM_TYPE, todoUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
	}

} 