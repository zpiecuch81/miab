package pl.com.ezap.miab;

import android.os.Bundle;
import android.app.Activity;
import android.text.InputFilter;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;

public class MessageCreate extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_create);
		EditText text = (EditText)findViewById(R.id.editTextMessage);
		text.setFilters( new InputFilter[] { new InputFilter.LengthFilter(10)} );
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MIAB miab = MIAB.getInstance();
		EditText text = (EditText)findViewById(R.id.editTextMessage);
		text.setText(miab.m_message);
		if( miab.m_isFlowing ) {
			findViewById(R.id.editTextMessage)
				.setBackgroundResource(R.drawable.background_see);
			Button buttonLeave = (Button)(findViewById(R.id.button_leave));
			buttonLeave.setText(R.string.button_throwMsg);
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		EditText text = (EditText)findViewById(R.id.editTextMessage);
		MIAB.getInstance().m_message = text.getText().toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.message_create, menu);
		return true;
	}

}
