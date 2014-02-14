package pl.com.ezap.miab;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.GeoPt;

@Entity
public class MIAB {

	private String message;
	private GeoPt location;
	private GeoPt deltaLocation;
	private boolean isFlowing;
	private boolean isBurried;
	private long timeStamp;
	private long geoIndex;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long ID;

	public MIAB() {
		setMessage("");
		setLocation(null);
		setFlowing(false);
		setBurried(false);
		setTimeStamp(0);
		setGeoIndex(0);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public GeoPt getLocation() {
		return location;
	}

	public void setLocation(GeoPt location) {
		this.location = location;
	}

	public GeoPt getDeltaLocation() {
		return deltaLocation;
	}

	public void setDeltaLocation(GeoPt deltaLocation) {
		this.deltaLocation = deltaLocation;
	}

	public boolean isFlowing() {
		return isFlowing;
	}

	public void setFlowing(boolean isFlowing) {
		this.isFlowing = isFlowing;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getGeoIndex() {
		return geoIndex;
	}

	public void setGeoIndex(long geoIndex) {
		this.geoIndex = geoIndex;
	}

	public Long getID() {
		return ID;
	}

	public void setID(Long ID) {
		this.ID = ID;
	}

	public boolean isBurried() {
		return isBurried;
	}

	public void setBurried(boolean isBurried) {
		this.isBurried = isBurried;
	}
}
