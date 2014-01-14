package pl.com.ezap.miab.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GPSDealerService extends Service {
	public GPSDealerService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
