package pl.com.ezap.miab;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class GPSDealer {

	public interface GPSSimpleStatus {
		public void GPSAvailabilityChanged( boolean available );
	}

	private class MyLocationListener implements LocationListener
	{
		private GPSSimpleStatus m_listener;
		public MyLocationListener( GPSSimpleStatus listener ) {
			m_listener = listener;
		}

		@Override
		public void onLocationChanged(Location location) {
		}

		@Override
		public void onProviderDisabled(String provider) {
			m_listener.GPSAvailabilityChanged(false);
		}

		@Override
		public void onProviderEnabled(String provider) {
			m_listener.GPSAvailabilityChanged(true);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if( provider.compareToIgnoreCase( LocationManager.GPS_PROVIDER ) == 0 ) {
				return;
			}
			switch( status ) {
			case LocationProvider.OUT_OF_SERVICE:
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				m_listener.GPSAvailabilityChanged(false);
				break;
			case LocationProvider.AVAILABLE:
				m_listener.GPSAvailabilityChanged(false);
				break;
			}
		}
		
	}

	MyLocationListener m_locationListener;

	public GPSDealer( LocationManager manager ) {
		assert( manager != null );
		m_locationManager = manager;
	}

	public void setSimpleStatusListener( GPSSimpleStatus listener )
	{
		assert( listener != null );
		if( m_locationListener != null ) {
			m_locationManager.removeUpdates(m_locationListener);
		}
		m_locationListener = new MyLocationListener( listener );
		m_locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				0, 0,
				m_locationListener );
	}

	public boolean isGPSAvailable()
	{
		return m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private LocationManager m_locationManager;
}
