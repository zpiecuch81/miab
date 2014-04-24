package pl.com.ezap.miab.shared;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class GPSHelper
{
  public interface GPSListener
  {
    void onLocationFound(Location foundLocation);
    void onGPSFailure();
  }

  private int interval;
  private int maxFoundTrials;
  private int foundTrials;
  private LocationManager manager;
  private LocationListener listener;
  private GPSListener client;
  private Context context;

  public GPSHelper( Context context, GPSListener client, int interval, int maxFoundTrials )
  {
    this.context = context;
    this.client = client;
    this.interval = interval;
    this.maxFoundTrials = maxFoundTrials;
  }

  public void start()
  {
    startGPSListening();
    //client.onLocationFound( new Location(LocationManager.GPS_PROVIDER) );
  }

  public void stop()
  {
    stopGPSListening();
  }

  private void startGPSListening()
  {
    initialize();
    manager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER,
        interval,
        0,
        listener );
  }

  private void stopGPSListening()
  {
    if( manager != null && listener != null ) {
      manager.removeUpdates( listener );
    }
    listener = null;
    manager = null;
  }

  private void initialize()
  {
    if( manager == null ) {
      manager = (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
    }
    foundTrials = maxFoundTrials;
    if( listener == null ) {
      listener = new LocationListener() {
        @Override
        public void onLocationChanged( Location location )
        {
          if( LocationHelper.isAccuracyEnough( location ) || --foundTrials < 0 ) {
            client.onLocationFound( location );
            foundTrials = maxFoundTrials;
          }
        }

        @Override
        public void onProviderDisabled( String provider )
        {
          if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
            client.onGPSFailure();
          }
        }

        @Override
        public void onProviderEnabled( String provider )
        { }

        @Override
        public void onStatusChanged( String provider, int status, Bundle extras )
        {
          if( provider.equals( LocationManager.GPS_PROVIDER ) ) {
            switch( status ) {
              case LocationProvider.OUT_OF_SERVICE:
                client.onGPSFailure();
                break;
              case LocationProvider.TEMPORARILY_UNAVAILABLE:
                client.onGPSFailure();
                break;
              case LocationProvider.AVAILABLE:
                break;
            }
          }
        }
      };
    }
  }
}
