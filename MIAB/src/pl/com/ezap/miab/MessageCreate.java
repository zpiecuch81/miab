package pl.com.ezap.miab;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MessageCreate extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_create);
		if( MIAB.getInstance().m_isFlowing ) {
			findViewById(R.id.editTextMessage)
				.setBackgroundResource(R.drawable.background_see);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.message_create, menu);
		return true;
	}

}
