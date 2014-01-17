package pl.com.ezap.miab.services;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class CurrentLocation {

	private Location foundLocation;
	private boolean gpsDisabled;
	private LocationManager locationManager;
	private LocationListener locationListener;

	public CurrentLocation(LocationManager aLocationManager) {
		foundLocation = null;
		gpsDisabled = false;
		locationManager = aLocationManager;
		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				foundLocation = location;
			}

			@Override
			public void onProviderDisabled(String provider) {
				if( provider.equals(LocationManager.GPS_PROVIDER) ) {
					gpsDisabled = true;
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
				if( provider.equals(LocationManager.GPS_PROVIDER) ) {
					gpsDisabled = false;
				}
			}

			@Override
			public void onStatusChanged(String provider,
					int status,
					Bundle extras) {
				if( provider.equals(LocationManager.GPS_PROVIDER) ) {
					switch( status ) {
					case LocationProvider.OUT_OF_SERVICE:
						gpsDisabled = true;
						break;
					case LocationProvider.TEMPORARILY_UNAVAILABLE:
						gpsDisabled = true;
						break;
					case LocationProvider.AVAILABLE:
						gpsDisabled = false;
						break;
					}
				}
			}

		};
		requestUpdate();
	}

	public Location get() {
		int maxTrials = 5;
		while( true ) {
			if( foundLocation != null ) {
				Log.i( "CurrentLocation", "Found location");
				if( --maxTrials < 0 || isAccuracyEnough() ) {
					return foundLocation;
				} else {
					Log.i( "CurrentLocation", "Location inaccurate");
					foundLocation = null;
					requestUpdate();
				}
			}
			if( gpsDisabled ) {
				Log.e( "CurrentLocation", "GPS disabled during search");
				return null;
			}
			SystemClock.sleep(100);
		}
	}

	public void close() {
		locationManager.removeUpdates( locationListener );
	}

	private void requestUpdate() {
		try {
		locationManager.requestSingleUpdate(
			LocationManager.GPS_PROVIDER,
			locationListener,
			null );
		} catch( IllegalArgumentException e) {
			Log.e( "CurrentLocation", "IllegalArgumentException");
			gpsDisabled = true;
		} catch( SecurityException e ) {
			Log.e( "CurrentLocation", "SecurityException");
			gpsDisabled = true;
		}
	}

	//check if accuracy is less than or equal to 5 meters
	private boolean isAccuracyEnough() {
		if( foundLocation != null ) {
			return foundLocation.getAccuracy() <= 5.0;
		}
		return false;
	}
}
