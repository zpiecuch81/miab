package pl.com.ezap.miab.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

import pl.com.ezap.miab.CloudEndpointUtils;
import pl.com.ezap.miab.miabendpoint.Miabendpoint;
import pl.com.ezap.miab.miabendpoint.model.GeoPt;
import pl.com.ezap.miab.miabendpoint.model.MIAB;
import pl.com.ezap.miab.shared.GeoIndex;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.widget.Toast;

public class SenderService extends IntentService {

	final static public String MESSAGE_KEY = "pl.com.ezap.miab.services.Message2Send";
	final static public String IS_BURRIED_KEY = "pl.com.ezap.miab.services.IsBurried";
	final static public String IS_FLOWING_KEY = "pl.com.ezap.miab.services.IsFlowing";

	public SenderService() {
		super("MIABSenderService");
	}

	Handler handler;

	@Override
	public void onCreate() {
		// Handler will get associated with the current thread, which is the main thread.
		handler = new Handler();
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
		MIAB miabMessage = new MIAB();
		miabMessage.setMessage( intent.getStringExtra( MESSAGE_KEY ) );
		miabMessage.setBurried( intent.getBooleanExtra( IS_BURRIED_KEY, false ) );
		miabMessage.setFlowing( intent.getBooleanExtra( IS_FLOWING_KEY, false ) );
		miabMessage.setTimeStamp( new java.util.Date().getTime() );
		//ID can't be 0/null, app engine will assign a number to it anyway
		miabMessage.setId( miabMessage.getTimeStamp() );

		showToast( "Retriving GPS location" );
		miabMessage.setLocation( getCurrentLocation() );
		GeoIndex geoIndex = new GeoIndex();
		miabMessage.setGeoIndex( geoIndex.getIndex( 
				miabMessage.getLocation().getLatitude(),
				miabMessage.getLocation().getLongitude() ) );
		miabMessage.setDeltaLocation( null );

		showToast( "Sending message" );
		Miabendpoint.Builder endpointBuilder = new Miabendpoint.Builder(
				AndroidHttp.newCompatibleTransport(),
				new JacksonFactory(),
				new HttpRequestInitializer() {
					public void initialize( HttpRequest httpRequest ) {}
				});
		endpointBuilder.setApplicationName("message-in-bottle");
		Miabendpoint endpoint = CloudEndpointUtils.updateBuilder( endpointBuilder ).build();
		endpoint.insertMIAB(miabMessage).execute();

		showToast( "Message send correctly" );
		} catch ( Exception e ) {
			showToast( "Error sending message" );
			//TODO:: save message for later use
		}
	}

	private GeoPt getCurrentLocation() throws Exception {
		CurrentLocation currentLocation = new CurrentLocation( (LocationManager)this.getApplicationContext().getSystemService( Context.LOCATION_SERVICE ) );
		Location location = currentLocation.get();
		currentLocation.close();
		if( location == null ) {
			throw new Exception();
		}
		GeoPt geoPt = new GeoPt();
		geoPt.setLatitude( (float)location.getLatitude() );
		geoPt.setLongitude( (float)location.getLongitude() );
		return geoPt;
	}

	public void showToast(final String toast)
	{
		runOnUiThread(new Runnable() {
			public void run()
			{
				Toast.makeText( getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void runOnUiThread(Runnable runnable) {
		handler.post(runnable);
	}
}
