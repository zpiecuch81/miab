package pl.com.ezap.miab.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.MessageV1;
import pl.com.ezap.miab.shared.GeoIndex;
import pl.com.ezap.miab.shared.LocationHelper;
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class MIABSearcher extends AsyncTask<Void, Integer, List<MessageV1> > {

	private class MessageCache {
		public List<MessageV1> miabs;
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
	protected List<MessageV1> doInBackground(Void... unused)
	{
		List<MessageV1> miabs2Check = getMIABsWithCurrentGeoIndex();
		List<MessageV1> miabs2Download = selectCurrentLocationMIABS( miabs2Check );
		List<MessageV1> downloadedMIABs = downloadMIABs( miabs2Download );
		storeDownloadedMIABs( downloadedMIABs );
		updateCache( downloadedMIABs );
		return downloadedMIABs;
	}

	@Override
	protected void onPostExecute(List<MessageV1> result)
	{
		if( !result.isEmpty() ) {
			Toast.makeText( context, "Found " + result.size() + " MIABs", Toast.LENGTH_LONG).show();
		}
	}

	private List<MessageV1> getMIABsWithCurrentGeoIndex()
	{
		if( isCacheEnough() ) {
			return cache.miabs;
		}
		List<MessageV1> miabs = getFromDataStore();
//		if( !doDig ) {
//			if( cache == null ) {
//				cache = new MessageCache();
//			}
//			cache.miabs = miabs;
//			cache.geoIndex = geoIndex;
//		}
		return miabs;
	}

	private boolean isCacheEnough()
	{
		if( doDig ) {
			return false;
		}
		return ( cache != null && cache.geoIndex == geoIndex );
	}

	private List<MessageV1> getFromDataStore()
	{
		Miabendpoint.Builder builder = new Miabendpoint.Builder(
				AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
		builder.setApplicationName("message-in-bottle");
		Miabendpoint endpoint = builder.build();
		List<MessageV1> miabs = null;
		try{
			miabs = endpoint.listMessages( geoIndex, doDig ).execute().getItems();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if( miabs == null ) {
			miabs = new ArrayList<MessageV1>();
		}
		Log.d( "MIABSearcher", "Received list of " + miabs.size() + " MIABs with index " + geoIndex);
		return miabs;
	}

	private List<MessageV1> selectCurrentLocationMIABS( List<MessageV1> miabs2Check )
	{
		List<MessageV1> foundMiabs = new ArrayList<MessageV1>();
		for(MessageV1 miab : miabs2Check) {
			if( LocationHelper.isSamePoint( location2search, miab.getLocation() ) ){
				foundMiabs.add( miab );
			}
		}
		return foundMiabs;
	}

	private List<MessageV1> downloadMIABs( List<MessageV1> miabs2Download )
	{
		List<MessageV1> downloadedMIABs = new ArrayList<MessageV1>();
		if( !miabs2Download.isEmpty() ) {
			Miabendpoint.Builder builder = new Miabendpoint.Builder(
					AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
			builder.setApplicationName("message-in-bottle");
			Miabendpoint endpoint = builder.build();
			for( MessageV1 miab : miabs2Download ) { 
				try{
					MessageV1 gotMIAB = endpoint.getMIAB( miab.getId() ).execute();
					if( gotMIAB != null ) {
						endpoint.removeMIAB( miab.getId() ).execute();
					}
					downloadedMIABs.add( gotMIAB );
					Log.d( "MIABSearcher", "Downloaded MessageV1, id =  " + gotMIAB.getId());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return downloadedMIABs;
	}

	private void storeDownloadedMIABs( List<MessageV1> downloadedMIABs )
	{
		MIABSQLiteHelper sqlHelper = new MIABSQLiteHelper( context );
		for( MessageV1 miab : downloadedMIABs ) {
			boolean stored = sqlHelper.storeMessage(
				miab.getMessage(),
				miab.getTimeStamp(),
				miab.getHidden(),
				miab.getFlowing(),
				location2search );
			Log.d( "MIABSearcher", "Message id " + miab.getId() + ", store status = " + stored );
		}
	}

	private void updateCache( List<MessageV1> downloadedMIABs )
	{
		if( cache == null || doDig ) {
			return;
		}
		for( MessageV1 miab : downloadedMIABs ) {
			for( MessageV1 cacheMiab : cache.miabs ) {
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
