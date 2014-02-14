package pl.com.ezap.miab.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

	public interface NetworkStateListener {
		void onNetwoorkAvailable();
		void onNetwoorkUnavailable();
	}

	NetworkStateListener m_listener;
	public NetworkBroadcastReceiver(NetworkStateListener listener) {
		m_listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent arg1) {
		if( m_listener == null ) {
			return;
		}

		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
			m_listener.onNetwoorkUnavailable();
		} else {
			m_listener.onNetwoorkAvailable();
		}
	}

}
