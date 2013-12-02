package pl.com.ezap.miab.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import android.test.AndroidTestCase;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import pl.com.ezap.miab.GPSDealer;

public class GPSDealer_ut extends AndroidTestCase{

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testisConnected() {
	}

	public void testSettingSimpleListener() {
//		GPSDealer dealer = new GPSDealer();
//		boolean availability = false;
//		dealer.setSimpleStatusListener( new GPSDealer.GPSSimpleStatus() {
//			
//			@Override
//			public void GPSAvailabilityChanged(boolean available) {
//				availability = available;
//			}
//		});
	}

	private class GPSSimpleStatus_mock implements GPSDealer.GPSSimpleStatus
	{
		public boolean m_available = false;
		public boolean m_availablityChangedCalled = false;

		@Override
		public void GPSAvailabilityChanged(boolean available) {
			m_availablityChangedCalled = true;
			m_available = available;
		}

	}
	public void testSimpleStatusChange() {
		LocationManager locationManager = (LocationManager)this.getContext().getSystemService(Context.LOCATION_SERVICE);
		locationManager.addTestProvider("Test", false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
		locationManager.setTestProviderEnabled("Test", true);

		Location location = new Location("Test");
		location.setLatitude(10.0);
		location.setLongitude(20.0);

		GPSSimpleStatus_mock statusMock = new GPSSimpleStatus_mock();

		GPSDealer dealer =new GPSDealer( locationManager );
		dealer.setSimpleStatusListener( statusMock );

		locationManager.setTestProviderLocation("Test", location);

		assertTrue( statusMock.m_availablityChangedCalled == true );
		assertTrue( statusMock.m_available == true );
	}
}
