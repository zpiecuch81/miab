package pl.com.ezap.miab.shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pl.com.ezap.miab.messagev1endpoint.Messagev1endpoint;
import pl.com.ezap.miab.messagev1endpoint.model.MessageV1;
import pl.com.ezap.miab.store.MIABSQLiteHelper;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

public class BottleGrabber
{
  public interface BottleListener
  {
    public void onGrabFinished( int foundBottlesNumber );
    public void onGrabFailure();
  }

  private BottleListener listener;
  private boolean searchHidden;
  private Location location;
  private long geoIndex;
  private Context context;
  public List<MessageV1> bottlesCache;

  public BottleGrabber( Context context, BottleListener listener ) {
    this.context = context;
    this.listener = listener;
    this.geoIndex = 0;
    bottlesCache = new ArrayList<MessageV1>();
  }

  public void search( Location location )
  {
    searchHidden = false;
    doSearch( location );
  }

  public void dig( Location location )
  {
    searchHidden = true;
    doSearch( location );
  }

  private void doSearch( Location location )
  {
    this.location = location;
    prepareData2Search();
    startAsynchronousSearch();
  }

  private void prepareData2Search()
  {
    long newGeoIndex = new GeoIndex().getIndex( location.getLatitude(), location.getLongitude() );
    if( newGeoIndex != geoIndex || searchHidden ){
      bottlesCache = new ArrayList<MessageV1>();
      geoIndex = newGeoIndex;
    }
  }

  private void startAsynchronousSearch()
  {
    new AsyncTask<Void, Integer, Integer>() {
      @Override
      protected Integer doInBackground( Void... params )
      {
        List<MessageV1> bottles2Check = getMIABsWithCurrentGeoIndex();
        List<MessageV1> bottles2Download = selectCurrentLocationMIABS( bottles2Check );
        List<MessageV1> downloadedBottles = downloadBottles( bottles2Download );
        List<MessageV1> storedBottles = storeDownloadedBottles( downloadedBottles );
        removeStoredMessages( storedBottles );
        return storedBottles.size();
      }

      @Override
      protected void onPostExecute( Integer foundMessages )
      {
        if( foundMessages >= 0 ) {
          listener.onGrabFinished( foundMessages );
        } else {
          listener.onGrabFailure();
        }
      }

      private List<MessageV1> getMIABsWithCurrentGeoIndex()
      {
        if( isCacheEnough() ) {
          return bottlesCache;
        }
        List<MessageV1> bottles = getFromDataStore();
        if( !searchHidden ) {
          bottlesCache = bottles;
        }
        return bottles;
      }

      private boolean isCacheEnough()
      {
        if( !searchHidden ) {
          if( !bottlesCache.isEmpty() ) {
            return true;
          }
        }
        return false;
      }

      private List<MessageV1> getFromDataStore()
      {
        Messagev1endpoint endpoint = MessageV1EndPoint.get();
        List<MessageV1> bottles = null;
        try {
          bottles = endpoint.listMessageV1( geoIndex, searchHidden ).execute().getItems();
        }
        catch( IOException e ) {
          e.printStackTrace();
        }
        if( bottles == null ) {
          bottles = new ArrayList<MessageV1>();
        }
        Log.d( "BottleGrabber", "Received list of "
            + bottles.size()
            + " MIABs with index "
            + geoIndex );
        return bottles;
      }

      private List<MessageV1> selectCurrentLocationMIABS( List<MessageV1> miabs2Check )
      {
        List<MessageV1> foundBottles = new ArrayList<MessageV1>();
        for( MessageV1 bottle: miabs2Check ) {
          if( LocationHelper.isSamePoint( location, bottle.getLocation() ) ) {
            foundBottles.add( bottle );
          }
        }
        return foundBottles;
      }

      private List<MessageV1> downloadBottles( List<MessageV1> bottles2Download )
      {
        List<MessageV1> downloadedBottles = new ArrayList<MessageV1>();
        if( !bottles2Download.isEmpty() ) {
          Messagev1endpoint endpoint = MessageV1EndPoint.get();
          for( MessageV1 miab: bottles2Download ) {
            try {
              MessageV1 gotBottle = endpoint.getMessageV1( miab.getId() ).execute();
              if( gotBottle != null ) {
                downloadedBottles.add( gotBottle );
                Log.d(
                    "BottleGrabber",
                    "Downloaded MessageV1, id =  " + gotBottle.getId() );
              }
            }
            catch( IOException e ) {
              Log.e( "BottleGrabber", e.getMessage() );
            }
          }
        }
        return downloadedBottles;
      }

      private List<MessageV1> storeDownloadedBottles( List<MessageV1> downloadedBottles )
      {
        MIABSQLiteHelper sqlHelper = new MIABSQLiteHelper( context );
        List<MessageV1> storedBottles = new ArrayList<MessageV1>();
        for( MessageV1 bottle: downloadedBottles ) {
          long storeID =
              sqlHelper.storeMessage(
                  bottle.getMessage(),
                  bottle.getTimeStamp(),
                  bottle.getHidden(),
                  bottle.getFlowing(),
                  location );
          if( storeID != -1 ) {
            storedBottles.add( bottle );
          }
          Log.d( "BottleGrabber", "Message id "
              + bottle.getId()
              + ", store ID = "
              + storeID );
        }
        return storedBottles;
      }

      private void removeStoredMessages( List<MessageV1> downloadedBottles )
      {
        if( downloadedBottles.isEmpty() ) {
          return;
        }
        Messagev1endpoint endpoint = MessageV1EndPoint.get();
        for( MessageV1 bottle: downloadedBottles ) {
          // remove stored MIABS from Datastore
          try {
            endpoint.removeMessageV1( bottle.getId() ).execute();
          }
          catch( IOException e ) {
            Log.e( "BottleGrabber", e.getMessage() );
          }
          // clean also from cache
          if( bottlesCache != null ) {
            for( MessageV1 cachedBottle: bottlesCache ) {
              if( cachedBottle.getId() == bottle.getId() ) {
                bottlesCache.remove( cachedBottle );
                break;
              }
            }
          }
        }
      }

    }.execute( (Void)null );

  }

}
