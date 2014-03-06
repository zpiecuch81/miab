package pl.com.ezap.miab.store;

import pl.com.ezap.miab.miabendpoint.model.GeoPt;

public class StoredMIAB {
	private long id;
	private String message;
	private boolean wasFlowing;
	private boolean wasDig;
	private long dropTimeStamp;
	private long foundTimeStamp;
	private GeoPt location;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean wasFlowing() {
		return wasFlowing;
	}
	public void setWasFlowing( boolean wasFlowing ) {
		this.wasFlowing = wasFlowing;
	}

	public boolean wasDig() {
		return wasDig;
	}
	public void setWasDig( boolean wasDig ) {
		this.wasDig = wasDig;
	}

	public long getDropTimeStamp() {
		return dropTimeStamp;
	}
	public void setDropTimeStamp( long dropTimeStamp ) {
		this.dropTimeStamp = dropTimeStamp;
	}

	public long getFoundTimeStamp() {
		return foundTimeStamp;
	}
	public void setFoundTimeStamp( long foundTimeStamp ) {
		this.foundTimeStamp = foundTimeStamp;
	}

	public GeoPt getLocation() {
		return location;
	}
	public void setLocation(GeoPt location) {
		this.location = location;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return message;
	}
}
