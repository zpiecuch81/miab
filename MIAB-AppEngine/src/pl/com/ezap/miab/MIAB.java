package pl.com.ezap.miab;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.datanucleus.api.jpa.annotations.Extension;

import com.google.appengine.api.datastore.GeoPt;

@Entity
public class MIAB {

	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String message;

	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private GeoPt location;

	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private GeoPt deltaLocation;
	private boolean isFlowing;
	private boolean isBurried;
	private Long timeStamp;

	private Long geoIndex;

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long ID;

	public MIAB() {
		setMessage("");
		setLocation(null);
		setFlowing(false);
		setBurried(false);
		setTimeStamp(0L);
		setGeoIndex(0L);
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

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Long getGeoIndex() {
		return geoIndex;
	}

	public void setGeoIndex(Long geoIndex) {
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
