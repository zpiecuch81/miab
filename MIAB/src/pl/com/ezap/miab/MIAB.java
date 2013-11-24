package pl.com.ezap.miab;

import android.location.Location;

public class MIAB {
	public static MIAB getInstance() {
		if( m_instance == null ) {
			m_instance = new MIAB();
		}
		return m_instance;
	}
	public String m_message;
	public Location m_location;
	public boolean m_isFlowing;
	private static MIAB m_instance;
}
