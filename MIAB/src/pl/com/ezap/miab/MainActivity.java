package pl.com.ezap.miab;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button1).setOnClickListener(
        		new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						startMessageCreation(false);
					}
				});
        
        findViewById(R.id.button2).setOnClickListener(
        		new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						startMessageCreation(true);
					}
				});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void startMessageCreation(boolean isFlowing) {
    	MIAB newMessage = new MIAB();
    	newMessage.m_isFlowing = isFlowing;
    	//newMessage.m_location = ???;
    }

}
