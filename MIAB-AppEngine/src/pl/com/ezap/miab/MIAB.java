package pl.com.ezap.miab;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.search.GeoPoint;

@Entity
public class MIAB {

	private String m_message;
	private GeoPoint m_location;
	private boolean m_isFlowing;
	private long m_timeStamp;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String deviceRegistrationID;

	public MIAB() {
		setMessage("");
		setLocation(null);
		setIsFlowing(false);
		setTimeStamp(0);
	}

	public String getMessage() {
		return m_message;
	}
	public void setMessage(String m_message) {
		this.m_message = m_message;
	}
	public GeoPoint getLocation() {
		return m_location;
	}
	public void setLocation(GeoPoint m_location) {
		this.m_location = m_location;
	}
	public boolean isFlowing() {
		return m_isFlowing;
	}
	public void setIsFlowing(boolean m_isFlowing) {
		this.m_isFlowing = m_isFlowing;
	}
	public long getTimeStamp() {
		return m_timeStamp;
	}
	public void setTimeStamp(long m_timeStamp) {
		this.m_timeStamp = m_timeStamp;
	}
	public String getDeviceRegistrationID() {
		return deviceRegistrationID;
	}
	public void setDeviceRegistrationID(String deviceRegistrationID) {
		this.deviceRegistrationID = deviceRegistrationID;
	}
}
