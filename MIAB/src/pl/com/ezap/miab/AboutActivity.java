
package pl.com.ezap.miab;

import pl.com.ezap.miab.shared.Message;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends Activity
{
  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_about );

    fillAboutInfo();
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    // getMenuInflater().inflate(R.menu.about, menu);
    return true;
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    ImageView logo = (ImageView)findViewById( R.id.logoImage );
    logo.setImageResource( R.drawable.logo_h );
  }

  @Override
  protected void onPause()
  {
    ImageView logo = (ImageView)findViewById( R.id.logoImage );
    logo.setImageResource( 0 );
    super.onPause();
  }

  private void fillAboutInfo()
  {
    String version;
    try {
      version = getPackageManager().getPackageInfo( this.getPackageName(), 0 ).versionName;
    }
    catch( NameNotFoundException e ) {
      e.printStackTrace();
      version = new String( "?.?" );
    }
    StringBuilder aboutInfo = new StringBuilder()
      .append( getString( R.string.msgGraphics ) )
      .append( "\nL@KI\n\n" )
      .append( getString( R.string.msgVersion ) )
      .append( "\n" )
      .append( version );
    TextView aboutInfoText = (TextView)findViewById( R.id.aboutInfo );
    aboutInfoText.setText( aboutInfo );
  }

}
