package pl.com.ezap.miab;

import android.location.Location;

public class Message {
	public static Message getInstance() {
		if( m_instance == null ) {
			m_instance = new Message();
		}
		return m_instance;
	}
	public String m_message;
	public Location m_location;
	public boolean m_isFlowing;
	private static Message m_instance;
}
