
package pl.com.ezap.miab;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GooglePlayServicesMissingActivity extends Activity
{
  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_google_services_missing );
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    return true;
  }

  @Override
  public void onBackPressed()
  {}
}
