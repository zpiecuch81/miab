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
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class MIABSearcher extends AsyncTask<Void, Integer, List<MIAB> > {

	private class MessageCache {
		public List<MIAB> miabs;
		public long geoIndex;
	}

	public MIABSearcher( Location location2search, Context context, boolean doDig ) {
		this.location2search = location2search;
		this.context = context;
		this.doDig = doDig;
		this.geoIndex = new GeoIndex().getIndex(
				location2search.getLatitude(),
				location2search.getLongitude() );
	}

	@Override
	protected List<MIAB> doInBackground(Void... unused)
	{
		List<MIAB> miabs2Check = getMIABsWithCurrentGeoIndex();
		List<MIAB> miabs2Download = selectCurrentLocationMIABS( miabs2Check );
		List<MIAB> downloadedMIABs = downloadMIABs( miabs2Download );
		storeDownloadedMIABs( downloadedMIABs );
		updateCache( downloadedMIABs );
		return downloadedMIABs;
	}

	@Override
	protected void onPostExecute(List<MIAB> result)
	{
		if( !result.isEmpty() ) {
			Toast.makeText( context, "Found " + result.size() + " MIABs", Toast.LENGTH_LONG).show();
		}
	}

	private List<MIAB> getMIABsWithCurrentGeoIndex()
	{
		if( isCacheEnough() ) {
			return cache.miabs;
		}
		List<MIAB> miabs = getFromDataStore();
		if( !doDig ) {
			if( cache == null ) {
				cache = new MessageCache();
			}
			cache.miabs = miabs;
			cache.geoIndex = geoIndex;
		}
		return miabs;
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
			Log.d( "MIABSearcher", "Received list of " + miabs.size() + " MIABs with index " + geoIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return miabs == null ? new ArrayList<MIAB>() : miabs;
	}

	private List<MIAB> selectCurrentLocationMIABS( List<MIAB> miabs2Check )
	{
		List<MIAB> foundMiabs = new ArrayList<MIAB>();
		for(MIAB miab : miabs2Check) {
			if( LocationHelper.isSamePoint( location2search, miab.getLocation() ) ){
				foundMiabs.add( miab );
			}
		}
		return foundMiabs;
	}

	private List<MIAB> downloadMIABs( List<MIAB> miabs2Download )
	{
		Miabendpoint.Builder builder = new Miabendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		builder.setApplicationName("message-in-bottle");
		Miabendpoint endpoint = builder.build();
		List<MIAB> downloadedMIABs = new ArrayList<MIAB>();
		for( MIAB miab : miabs2Download ) { 
			try{
				MIAB gotMIAB = endpoint.getMIAB( miab.getId() ).execute();
				if( gotMIAB != null ) {
					endpoint.removeMIAB( miab.getId() ).execute();
				}
				downloadedMIABs.add( gotMIAB );
				Log.d( "MIABSearcher", "Downloaded MIAB, id =  " + gotMIAB.getId());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return downloadedMIABs;
	}

	private void storeDownloadedMIABs( List<MIAB> downloadedMIABs )
	{
		MIABSQLiteHelper sqlHelper = new MIABSQLiteHelper( context );
		for( MIAB miab : downloadedMIABs ) {
			boolean stored = sqlHelper.storeMessage(
				miab.getMessage(),
				miab.getTimeStamp(),
				miab.getBurried(),
				miab.getFlowing(),
				location2search );
			Log.d( "MIABSearcher", "Message id " + miab.getId() + ", store status = " + stored );
		}
	}

	private void updateCache( List<MIAB> downloadedMIABs )
	{
		if( cache == null || doDig ) {
			return;
		}
		for( MIAB miab : downloadedMIABs ) {
			for( MIAB cacheMiab : cache.miabs ) {
				if( cacheMiab.getId() == miab.getId() ) {
					cache.miabs.remove( cacheMiab );
					break;
				}
			}
		}
	}

	static public void searchAtLocation( Location location, Context context ) {
		new MIABSearcher( location, context, false ).execute();
	}

	static public void digAtLocation( Location location, Context context ) {
		new MIABSearcher( location, context, true ).execute();
	}

	private Context context;
	private Location location2search;
	private boolean doDig;
	private long geoIndex;
	static private MessageCache cache;

}
