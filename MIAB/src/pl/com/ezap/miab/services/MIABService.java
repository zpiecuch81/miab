package pl.com.ezap.miab.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class MIABService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Toast.makeText( getApplicationContext(), "MIAB Service stopped", Toast.LENGTH_LONG ).show();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText( getApplicationContext(), "MIAB Service started", Toast.LENGTH_LONG ).show();

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
