
package pl.com.ezap.miab;

import pl.com.ezap.miab.services.SenderService;
import pl.com.ezap.miab.shared.GeneralMenuHelper;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CreateMessageActivity extends Activity
{
  private static final int MIN_MESSAGE_LENGTH = 10;
  private static final int MAX_MESSAGE_LENGTH = 1500;
  private static final String MESSAGE_SHARED_PREF_KEY = "message2Send"; 

  private GeneralMenuHelper menuHelper;
  private boolean isFlowing;
  private boolean isHidden;

  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_create_message );
    menuHelper = new GeneralMenuHelper( this );

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled( true );

    isHidden = getIntent().getBooleanExtra( SenderService.IS_HIDDEN_KEY, false );
    isFlowing = getIntent().getBooleanExtra( SenderService.IS_FLOWING_KEY, false );

    EditText text = (EditText)findViewById( R.id.editMessageText );
    text.setFilters( new InputFilter[] { new InputFilter.LengthFilter( MAX_MESSAGE_LENGTH ) } );
    findViewById( R.id.buttonMessageReady ).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick( View v )
          {
            messageReady();
          }
        } );
  }

  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    getMenuInflater().inflate( R.menu.general, menu );
    return true;
  }

  @Override
  public boolean onOptionsItemSelected( MenuItem item )
  {
    return menuHelper.onOptionsItemSelected( item ) ? true :
      super.onOptionsItemSelected( item );
  }

  @Override
  public boolean onPrepareOptionsMenu( Menu menu )
  {
    menuHelper.onPrepareOptionsMenu( menu );
    return super.onPrepareOptionsMenu( menu );
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    menuHelper.updateMenuState();
    EditText text = (EditText)findViewById( R.id.editMessageText );
    String message = getMessage2Send();
    text.setText( message );
    if( isFlowing ) {
      LinearLayout layout = (LinearLayout)findViewById( R.id.createMessageLayout );
      layout.setBackgroundResource( R.drawable.bkg_throw );
      Button buttonLeave = (Button)( findViewById( R.id.buttonMessageReady ) );
      buttonLeave.setText( R.string.button_throwMsg );
    } else if( isHidden ) {
      LinearLayout layout = (LinearLayout)findViewById( R.id.createMessageLayout );
      layout.setBackgroundResource( R.drawable.bkg_dig );
      Button buttonLeave = (Button)( findViewById( R.id.buttonMessageReady ) );
      buttonLeave.setText( R.string.button_digMsg );
    }
  }

  @Override
  protected void onPause()
  {
    super.onPause();
    EditText text = (EditText)findViewById( R.id.editMessageText );
    storeMessage2Send( text.getText().toString() );
    LinearLayout layout = (LinearLayout)findViewById( R.id.createMessageLayout );
    layout.setBackgroundResource( 0 );
  }

  private void messageReady()
  {
    if( !checkDeviceSendRequirements() )
    {
      return;
    }
    EditText text = (EditText)findViewById( R.id.editMessageText );
    if( text.getText().toString().trim().length() < MIN_MESSAGE_LENGTH ) {
      Toast.makeText(
          getApplicationContext(),
          R.string.msgMessageTooShort,
          Toast.LENGTH_LONG ).show();
      return;
    }
    Intent sendMessageIntent = new Intent( getApplicationContext(), SenderService.class );
    sendMessageIntent.putExtra( SenderService.IS_FLOWING_KEY, isFlowing );
    sendMessageIntent.putExtra( SenderService.IS_HIDDEN_KEY, isHidden );
    sendMessageIntent.putExtra( SenderService.MESSAGE_KEY, text.getText().toString() );
    startService( sendMessageIntent );
    text.setText("");
    onBackPressed();
  }

  private void storeMessage2Send( String message )
  {
    SharedPreferences settings = getSharedPreferences( GeneralMenuHelper.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    SharedPreferences.Editor settingsEditor = settings.edit();
    settingsEditor.putString( MESSAGE_SHARED_PREF_KEY, message );
    settingsEditor.commit();
  }

  private String getMessage2Send()
  {
    SharedPreferences settings = getSharedPreferences( GeneralMenuHelper.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );
    return settings.getString( MESSAGE_SHARED_PREF_KEY, "" );
  }

  private boolean checkDeviceSendRequirements()
  {
    final LocationManager manager =
        (LocationManager)getSystemService( Context.LOCATION_SERVICE );
    if( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
      Toast.makeText(
          getApplicationContext(),
          R.string.msgEnableGPSToast,
          Toast.LENGTH_LONG ).show();
      return false;
    }
    final ConnectivityManager cm =
        (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE );
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    if( netInfo == null || !netInfo.isConnected() ) {
      Toast.makeText(
          getApplicationContext(),
          R.string.msgEnableNetToast,
          Toast.LENGTH_LONG ).show();
      return false;
    }
    return true;
  }

}
