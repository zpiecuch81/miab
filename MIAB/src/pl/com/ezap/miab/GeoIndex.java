package pl.com.ezap.miab;

final class DeltaLatLong
{
	public DeltaLatLong( double minL, double deltaL ) {
		minLongitude = minL;
		deltaLongitude = deltaL;
	}
	public double minLongitude;
	public double deltaLongitude;
}

/**
 * 
 * @author zap
 *
 *This class is used to generate geometric index based on latitude and longitude.
 *The GeoIndex is index of location area on earth's surface. All locations that are in the same
 *area will have the same index, thus it's easier to search near by locations.
 *
 *First earth is cut in slices from south to north. These virtual cuts are done every DELTA_LATITUDE
 *angle value, this way cut perimeter's length is about 0.5km.
 *Then the slices are further cut based on the longitude value.
 */
public class GeoIndex {

	/**
	 * This array holds ranges in which longitude has specific delta value.
	 * ( 75, infinity ) - delta is 4.5 degree
	 * [ 60, 75 ) - delta is 0.045 degree
	 * [-60, 60 ) - delta is 0.0045 degree
	 * [-75, -60 ) - delta is 0.045 degree
	 * [-91, -75 ) - delta is 4.5 degree
	 * 
	 * -91 is actually invalid (min. can be -90 on south pole) but that's just to be sure
	 */
	static final private DeltaLatLong lat2deltaLong[] = {
		new DeltaLatLong( 75.0, 4.5 ),
		new DeltaLatLong( 60.0, 0.045 ),
		new DeltaLatLong( -60.0, 0.0045 ),
		new DeltaLatLong( -75.0, 0.045 ),
		new DeltaLatLong( -91.0, 4.5 )
	};

	static final double DELTA_LATITUDE = 0.0045;
	static final double MIN_DELTA_LONGITUDE = 0.0045;
	static final long LATITUDE_INDEX_SHIFT = (long)( 360.0 / MIN_DELTA_LONGITUDE ) + 1;

	/** 
	 * @param latitude in degrees, valid range [-90, 90]
	 * @param longitude in degrees, valid range [-180,180]
	 * @return geographical location index that is used to geographically makes locations closer
	 */
	public long getIndex( double latitude, double longitude ) {
		assert( latitude >= -90.0 && latitude <= 90.0 );
		assert( longitude >= -180.0 && longitude <= 180.0 );
		return getLatIndex( latitude ) + getLongIndex( latitude, longitude );
	}


	private long getLatIndex( double latitude ) {
		return (long)( (latitude + 90.0) / DELTA_LATITUDE ) + LATITUDE_INDEX_SHIFT;
	}

	private long getLongIndex( double latitude, double longitude ) {
		double deltaLongitude = getDeltaLongitude( latitude );
		return (long)( (longitude + 180.0) / deltaLongitude );
	}

	private double getDeltaLongitude( double latitude )
	{
		for( DeltaLatLong latLong : lat2deltaLong ) {
			if( latitude > latLong.minLongitude ) {
				return latLong.deltaLongitude;
			}
		}
		assert(false);
		return MIN_DELTA_LONGITUDE;
	}

}
