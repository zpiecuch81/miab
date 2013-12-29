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

	public interface GPSLocationProvider extends GPSSimpleStatus {
		public void GPSLocationChanged( Location location );
	}

	private class MyLocationListener implements LocationListener
	{
		private GPSSimpleStatus m_statusListener;
		private GPSLocationProvider m_locationListener;

		public MyLocationListener( GPSSimpleStatus listener ) {
			m_statusListener = listener;
		}

		public MyLocationListener( GPSLocationProvider listener ) {
			m_locationListener = listener;
		}

		@Override
		public void onLocationChanged(Location location) {
			if( m_locationListener != null ) {
				m_locationListener.GPSLocationChanged( location );
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			if( provider.compareToIgnoreCase( LocationManager.GPS_PROVIDER ) == 0
					|| m_statusListener == null ) {
				return;
			}
			m_statusListener.GPSAvailabilityChanged(false);
		}

		@Override
		public void onProviderEnabled(String provider) {
			if( provider.compareToIgnoreCase( LocationManager.GPS_PROVIDER ) == 0
					|| m_statusListener == null ) {
				return;
			}
			m_statusListener.GPSAvailabilityChanged(true);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if( provider.compareToIgnoreCase( LocationManager.GPS_PROVIDER ) == 0
					|| m_statusListener == null ) {
				return;
			}
			switch( status ) {
			case LocationProvider.OUT_OF_SERVICE:
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				m_statusListener.GPSAvailabilityChanged(false);
				break;
			case LocationProvider.AVAILABLE:
				m_statusListener.GPSAvailabilityChanged(false);
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
//		m_locationManager.requestLocationUpdates(
//				LocationManager.GPS_PROVIDER,
//				100, 0,
//				m_locationListener );
		m_locationManager.requestSingleUpdate(
				LocationManager.GPS_PROVIDER,
				m_locationListener,
				null );
	}

	public void setSimpleLocationListener( GPSLocationProvider listener )
	{
		assert( listener != null );
		if( m_locationListener != null ) {
			m_locationManager.removeUpdates(m_locationListener);
		}
		m_locationListener = new MyLocationListener( listener );
//		m_locationManager.requestLocationUpdates(
//				LocationManager.GPS_PROVIDER,
//				100, 0,
//				m_locationListener );
		m_locationManager.requestSingleUpdate(
				LocationManager.GPS_PROVIDER,
				m_locationListener,
				null );
	}

	public boolean isGPSAvailable()
	{
		return m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public Location getLastLocation()
	{
		return m_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	private LocationManager m_locationManager;
}
