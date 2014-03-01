package pl.com.ezap.miab.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.MIAB;
import pl.com.ezap.miab.shared.GeoIndex;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
//import com.appspot.message-in-bottle.

public class MIABSearcher {

	private class SearchMessageTask extends AsyncTask<Location, Integer, Long> {

		@Override
		protected Long doInBackground(Location... location2Check) {
			Miabendpoint.Builder builder = new Miabendpoint.Builder(
					AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
			builder.setApplicationName("message-in-bottle");
			Miabendpoint endpoint = builder.build();

			List<MIAB> miabs = new ArrayList<MIAB>();
			try{
				miabs = endpoint.listMIAB().execute().getItems();
				Log.d( "SearchMessageTask", "Received " + miabs.size() + " MIABs");
			} catch (IOException e) {
				e.printStackTrace();
				return (long)1;
			}
			for( int i=0 ; i < miabs.size() ; ++i) {
				Log.d( "SearchMessageTask", "result "+i+" "+miabs.get(i).getMessage() );
			}
			return (long)0;

		}
	}

	public void searchAtLocation( Location location ) {
		new SearchMessageTask().execute( location );
	}
}
