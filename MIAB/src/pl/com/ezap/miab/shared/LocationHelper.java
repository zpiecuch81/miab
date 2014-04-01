
package pl.com.ezap.miab.shared;

import pl.com.ezap.miab.miabendpoint.model.GeoPt;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class LocationHelper
{
  private final static float SAME_POINT_ACCURACY = 7.0f;
  private final static float GPS_ACCURACY = 6.0f;

  public static Location geoPt2Location( GeoPt geoPt )
  {
    Location location = new Location( LocationManager.GPS_PROVIDER );
    location.setLongitude( geoPt.getLongitude() );
    location.setLatitude( geoPt.getLatitude() );
    return location;
  }

  public static boolean isSamePoint( Location loc1, Location loc2 )
  {
    return ( loc1.distanceTo( loc2 ) <= SAME_POINT_ACCURACY );
  }

  public static boolean isSamePoint( Location loc1, GeoPt loc2 )
  {
    return isSamePoint( loc1, geoPt2Location( loc2 ) );
  }

  public static boolean isAccuracyEnough( Location location )
  {
    if( location != null ) {
      Log.d(
          "MIABService",
          "isAccuracyEnough, accuracy = " + location.getAccuracy() );
      return location.getAccuracy() <= GPS_ACCURACY;
    }
    Log.e( "MIABService", "isAccuracyEnough - null parameter" );
    return false;
  }
}
