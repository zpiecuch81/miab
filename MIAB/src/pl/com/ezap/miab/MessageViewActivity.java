package pl.com.ezap.miab;

import pl.com.ezap.miab.store.MIABContentProvider;
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

/*
 * TodoDetailActivity allows to enter a new todo item 
 * or to change an existing
 */
public class MessageViewActivity extends Activity {

	private EditText messageView;
	private Uri todoUri;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_message_view);

		messageView = (EditText) findViewById(R.id.message_view);

		Bundle extras = getIntent().getExtras();

		// check from the saved Instance
		todoUri = (bundle == null) ? null : (Uri) bundle
				.getParcelable(MIABContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
		if (extras != null) {
			todoUri = extras
					.getParcelable(MIABContentProvider.CONTENT_ITEM_TYPE);

			fillData(todoUri);
		}

	}

	private void fillData(Uri uri) {
		String[] projection = {
				MIABSQLiteHelper.COLUMN_MESSAGE,
				MIABSQLiteHelper.COLUMN_WAS_FLOWING,
				MIABSQLiteHelper.COLUMN_WAS_DIG,
				MIABSQLiteHelper.COLUMN_DROP_TIME_STAMP,
				MIABSQLiteHelper.COLUMN_FOUND_TIME_STAMP,
				MIABSQLiteHelper.COLUMN_LOCATION,
				MIABSQLiteHelper.COLUMN_ID };
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();

			messageView.setText(cursor.getString(cursor
					.getColumnIndexOrThrow(MIABSQLiteHelper.COLUMN_MESSAGE)));

			// always close the cursor
			cursor.close();
		}
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