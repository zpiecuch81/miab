package pl.com.ezap.miab.shared;

import android.location.Location;

public class Message {
	public static Message getInstance() {
		if( m_instance == null ) {
			resetInstance();
		}
		return m_instance;
	}
	public static Message resetInstance() {
		m_instance = new Message();
		return m_instance;
	}
	private Message() {
		m_isBurried = false;
		m_isFlowing = false;
		m_location = null;
		m_message = "";
	}
	public String m_message;
	public Location m_location;
	public boolean m_isFlowing;
	public boolean m_isBurried;
	private static Message m_instance;
}
