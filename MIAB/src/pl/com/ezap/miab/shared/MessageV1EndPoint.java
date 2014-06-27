package pl.com.ezap.miab.shared;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import pl.com.ezap.miab.messagev1endpoint.Messagev1endpoint;

public class MessageV1EndPoint
{
  public static Messagev1endpoint get()
  {
    Messagev1endpoint.Builder builder =
        new Messagev1endpoint.Builder(
            AndroidHttp.newCompatibleTransport(),
            new GsonFactory(),
            null );
    builder.setApplicationName( "message-ina-bottle" );
    return builder.build();
  }
}
