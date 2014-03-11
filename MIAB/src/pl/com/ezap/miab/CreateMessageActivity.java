package pl.com.ezap.miab;

import pl.com.ezap.miab.services.SenderService;
import pl.com.ezap.miab.shared.GeneralMenuHelper;
import pl.com.ezap.miab.shared.Message;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CreateMessageActivity extends Activity {

	private GeneralMenuHelper menuHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);

		menuHelper = new GeneralMenuHelper( this );

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		EditText text = (EditText)findViewById(R.id.editMessageText);
		text.setFilters( new InputFilter[] { new InputFilter.LengthFilter(3000)} );

		findViewById(R.id.buttonMessageReady).setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					messageReady();
				}
			});
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
	protected void onResume()
	{
		super.onResume();
		Message miab = Message.getInstance();
		EditText text = (EditText)findViewById(R.id.editMessageText);
		text.setText(miab.m_message);
		if( miab.m_isFlowing ) {
			LinearLayout layout= (LinearLayout)findViewById(R.id.createMessageLayout);
			layout.setBackgroundResource(R.drawable.bkg_throw);
			Button buttonLeave = (Button)(findViewById(R.id.buttonMessageReady));
			buttonLeave.setText(R.string.button_throwMsg);
		} else if( miab.m_isBurried ) {
			LinearLayout layout= (LinearLayout)findViewById(R.id.createMessageLayout);
			layout.setBackgroundResource(R.drawable.bkg_dig);
			Button buttonLeave = (Button)(findViewById(R.id.buttonMessageReady));
			buttonLeave.setText(R.string.button_digMsg);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		EditText text = (EditText)findViewById(R.id.editMessageText);
		Message.getInstance().m_message = text.getText().toString();
	}

	private void messageReady() {
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			Toast.makeText( getApplicationContext(), R.string.msgEnableGPSToast, Toast.LENGTH_LONG ).show();
			return;
		}
		final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnected()) {
			Toast.makeText( getApplicationContext(), R.string.msgEnableNetToast, Toast.LENGTH_LONG ).show();
			return;
		}

		EditText text = (EditText)findViewById(R.id.editMessageText);
		Message message = Message.getInstance();
		message.m_message = text.getText().toString();

		Intent sendMessageIntent = new Intent( getApplicationContext(), SenderService.class );
		sendMessageIntent.putExtra( SenderService.IS_FLOWING_KEY, message.m_isFlowing );
		sendMessageIntent.putExtra( SenderService.IS_BURRIED_KEY, message.m_isBurried );
		sendMessageIntent.putExtra( SenderService.MESSAGE_KEY, message.m_message );
		startService( sendMessageIntent );
		Message.resetInstance();
		onBackPressed();
	}
}
