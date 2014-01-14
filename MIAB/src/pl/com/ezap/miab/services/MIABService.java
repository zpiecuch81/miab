package pl.com.ezap.miab.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class MIABService extends Service {

	static final int MSG_TURN_OFF_SERVICE = 1;

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_TURN_OFF_SERVICE:
				stopSelf();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

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
