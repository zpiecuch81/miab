package pl.com.ezap.miab;

import pl.com.ezap.miab.services.SenderService;
import pl.com.ezap.miab.shared.Message;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.InputFilter;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CreateMessageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_message);

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
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.create_message, menu);
		return true;
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
