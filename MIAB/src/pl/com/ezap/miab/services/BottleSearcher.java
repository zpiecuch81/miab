
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
import pl.com.ezap.miab.shared.NotificationHelper;
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

class DBMessage
{
  MessageV1 message;
  long dbID;
}

public class BottleSearcher extends AsyncTask<Void, Integer, List<DBMessage>>
{
  private class MessageCache
  {
    public List<MessageV1> miabs;
    public long geoIndex;
  }

  public BottleSearcher( Location location2search, Context context, boolean doDig )
  {
    this.location2search = location2search;
    this.context = context;
    this.doDig = doDig;
    this.geoIndex =
        new GeoIndex().getIndex(
            location2search.getLatitude(),
            location2search.getLongitude() );
  }

  @Override
  protected List<DBMessage> doInBackground( Void... unused )
  {
    List<MessageV1> miabs2Check = getMIABsWithCurrentGeoIndex();
    List<MessageV1> miabs2Download = selectCurrentLocationMIABS( miabs2Check );
    List<MessageV1> downloadedMIABs = downloadMIABs( miabs2Download );
    List<DBMessage> storedMIABS = storeDownloadedMIABs( downloadedMIABs );
    removeStoredMessages( storedMIABS );
    return storedMIABS;
  }

  @Override
  protected void onPostExecute( List<DBMessage> result )
  {
    if( !result.isEmpty() ) {
      new NotificationHelper( context ).updateFoundBottles();
    }
  }

  private List<MessageV1> getMIABsWithCurrentGeoIndex()
  {
    if( isCacheEnough() ) {
      return cache.miabs;
    }
    List<MessageV1> miabs = getFromDataStore();
    if( !doDig ) {
      // if( cache == null ) {
      // cache = new MessageCache();
      // }
      // cache.miabs = miabs;
      // cache.geoIndex = geoIndex;
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

  private List<MessageV1> getFromDataStore()
  {
    Miabendpoint.Builder builder =
        new Miabendpoint.Builder( AndroidHttp.newCompatibleTransport(), new GsonFactory(), null );
    builder.setApplicationName( "message-in-bottle" );
    Miabendpoint endpoint = builder.build();
    List<MessageV1> miabs = null;
    try {
      miabs = endpoint.listMessages( geoIndex, doDig ).execute().getItems();
    }
    catch( IOException e ) {
      e.printStackTrace();
    }
    if( miabs == null ) {
      miabs = new ArrayList<MessageV1>();
    }
    Log.d( "MIABSearcher", "Received list of "
        + miabs.size()
        + " MIABs with index "
        + geoIndex );
    return miabs;
  }

  private List<MessageV1> selectCurrentLocationMIABS(
      List<MessageV1> miabs2Check )
  {
    List<MessageV1> foundMiabs = new ArrayList<MessageV1>();
    for( MessageV1 miab: miabs2Check ) {
      if( LocationHelper.isSamePoint( location2search, miab.getLocation() ) ) {
        foundMiabs.add( miab );
      }
    }
    return foundMiabs;
  }

  private List<MessageV1> downloadMIABs( List<MessageV1> miabs2Download )
  {
    List<MessageV1> downloadedMIABs = new ArrayList<MessageV1>();
    if( !miabs2Download.isEmpty() ) {
      Miabendpoint endpoint = getEndPoint();
      for( MessageV1 miab: miabs2Download ) {
        try {
          MessageV1 gotMIAB = endpoint.getMIAB( miab.getId() ).execute();
          if( gotMIAB != null ) {
            downloadedMIABs.add( gotMIAB );
            Log.d(
                "MIABSearcher",
                "Downloaded MessageV1, id =  " + gotMIAB.getId() );
          }
        }
        catch( IOException e ) {
          e.printStackTrace();
        }
      }
    }
    return downloadedMIABs;
  }

  private Miabendpoint getEndPoint()
  {
    Miabendpoint.Builder builder =
        new Miabendpoint.Builder(
            AndroidHttp.newCompatibleTransport(),
            new GsonFactory(),
            null );
    builder.setApplicationName( "message-in-bottle" );
    return builder.build();
  }

  private List<DBMessage> storeDownloadedMIABs( List<MessageV1> downloadedMIABs )
  {
    MIABSQLiteHelper sqlHelper = new MIABSQLiteHelper( context );
    List<DBMessage> dbMessages = new ArrayList<DBMessage>();
    for( MessageV1 miab: downloadedMIABs ) {
      long storeID =
          sqlHelper.storeMessage(
              miab.getMessage(),
              miab.getTimeStamp(),
              miab.getHidden(),
              miab.getFlowing(),
              location2search );
      if( storeID != -1 ) {
        DBMessage dbMessage = new DBMessage();
        dbMessage.message = miab;
        dbMessage.dbID = storeID;
        dbMessages.add( dbMessage );
      }
      Log.d( "MIABSearcher", "Message id "
          + miab.getId()
          + ", store ID = "
          + storeID );
    }
    return dbMessages;
  }

  private void removeStoredMessages( List<DBMessage> downloadedMIABs )
  {
    if( downloadedMIABs.isEmpty() ) {
      return;
    }
    Miabendpoint endpoint = getEndPoint();
    for( DBMessage dbMessage: downloadedMIABs ) {
      // remove stored MIABS from Datastore
      // try {
      // endpoint.removeMIAB( dbMessage.message.getId() );
      // } catch (IOException e) {
      // e.printStackTrace();
      // continue;
      // }
      // clean also from cache
      if( cache != null && !doDig ) {
        for( MessageV1 cacheMiab: cache.miabs ) {
          if( cacheMiab.getId() == dbMessage.message.getId() ) {
            cache.miabs.remove( cacheMiab );
            break;
          }
        }
      }
    }
  }

  static public void searchAtLocation( Location location, Context context )
  {
    new BottleSearcher( location, context, false ).execute();
  }

  static public void digAtLocation( Location location, Context context )
  {
    new BottleSearcher( location, context, true ).execute();
  }

  private Context context;
  private Location location2search;
  private boolean doDig;
  private long geoIndex;
  static private MessageCache cache;
}
