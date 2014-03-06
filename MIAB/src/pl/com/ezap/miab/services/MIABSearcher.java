package pl.com.ezap.miab.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.MIAB;
import pl.com.ezap.miab.shared.GeoIndex;
import pl.com.ezap.miab.shared.LocationHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class MIABSearcher extends AsyncTask<Void, Integer, Long> {

	private class MessageCache {
		public MessageCache() {
			miabs = null;
			geoIndex = 0;
		}
		public List<MIAB> miabs;
		public long geoIndex;
	}

	public MIABSearcher( Location location2search, boolean doDig ) {
		this.location2search = location2search;
		this.doDig = doDig;
		this.geoIndex = new GeoIndex().getIndex(
				location2search.getLatitude(),
				location2search.getLongitude() );
	}

	@Override
	protected Long doInBackground(Void... unused)
	{
		List<MIAB> miab2Check;
		if( isCacheEnough() ) {
			miab2Check = cache.miabs;
		} else {
			miab2Check = getFromDataStore();
			if( !doDig ) {
				cache.miabs = miab2Check;
				cache.geoIndex = geoIndex;
			}
		}
		checkMiabs( miab2Check );
		return 0L;
	}

	private boolean isCacheEnough()
	{
		if( doDig ) {
			return false;
		}
		return ( cache != null && cache.geoIndex == geoIndex );
	}

	private List<MIAB> getFromDataStore()
	{
		Miabendpoint.Builder builder = new Miabendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		builder.setApplicationName("message-in-bottle");
		Miabendpoint endpoint = builder.build();
		List<MIAB> miabs = new ArrayList<MIAB>();
		try{
			miabs = endpoint.listMIAB( geoIndex, doDig ).execute().getItems();
			Log.d( "SearchMessageTask", "Received " + miabs.size() + " MIABs");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return miabs;
	}

	private List<MIAB> checkMiabs( List<MIAB> miabs2Check )
	{
		List<MIAB> foundMiabs = new ArrayList<MIAB>();
		for(MIAB miab : miabs2Check) {
			if( LocationHelper.isSamePoint( location2search, miab.getLocation() ) ){
				foundMiabs.add( miab );
			}
		}
		return foundMiabs;
	}

	static public void searchAtLocation( Location location ) {
		new MIABSearcher( location, false ).execute();
	}

	static public void digAtLocation( Location location ) {
		new MIABSearcher( location, true ).execute();
	}

	private Location location2search;
	private boolean doDig;
	private long geoIndex;
	static private MessageCache cache;

}
