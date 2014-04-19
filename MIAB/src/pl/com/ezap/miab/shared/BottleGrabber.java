package pl.com.ezap.miab.shared;

import android.location.Location;
import android.os.AsyncTask;

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

  public BottleGrabber( BottleListener listener ) {
    this.listener = listener;
  }

  public void search( Location location )
  {
    searchHidden = false;
    this.location = location;
    this.geoIndex =
        new GeoIndex().getIndex( location.getLatitude(), location.getLongitude() );
    doSearch( );
  }

  public void dig( Location location )
  {
    searchHidden = true;
    this.location = location;
    this.geoIndex =
        new GeoIndex().getIndex( location.getLatitude(), location.getLongitude() );
    doSearch( );
  }

  public void doSearch()
  {
    new AsyncTask<Void, Integer, Integer>() {
      @Override
      protected Integer doInBackground( Void... params )
      {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }
}
