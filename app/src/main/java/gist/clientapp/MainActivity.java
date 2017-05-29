package gist.clientapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private ButtonListener mButtonListener;
    private Button mAppButton;
    private boolean mRegisteredReceiver;
    private boolean mConnected;

    public static final int REQUEST_APP = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setFonts();

        setReceiver();

        setButtons();
    }

    private void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    private void setReceiver(){

        mConnectionFilter = new IntentFilter();

        mConnectionFilter.addAction("ENABLE_APPBUTTON");

        mReceiver = new DataReceiver(this);
    }

    private void setButtons(){

        mButtonListener = new ButtonListener(this);

        mAppButton = (Button)findViewById(R.id.app_btn);

        mAppButton.setOnClickListener(mButtonListener);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != RESULT_OK) {
            return;
        }
        if(requestCode == REQUEST_APP){
            disableButtonColor();
            Intent i = new Intent(this, ConnectionService.class);
            i.setAction("StopConnection");
            startService(i);
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        if (mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }
    }

    protected void onResume(){
        super.onResume();
        if (!mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mConnectionFilter);
            mRegisteredReceiver = true;
        }
    }

    protected void onPause(){
        super.onPause();
        if (mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }
    }

    protected void onStart(){
        super.onStart();
        if (!mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mConnectionFilter);
            mRegisteredReceiver = true;
        }
    }

    protected void onStop(){
        super.onStop();
        if (mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }
    }


    public void disableButtonColor(){
        mAppButton.setBackgroundResource(R.drawable.disabled_custom_button);
    }

    public boolean isConnected(){
        return mConnected;
    }

    public void setConnection(boolean status){
        mConnected = status;
    }

}