package gist.clientapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class ClientActivity extends FragmentActivity {

    private IntentFilter mConnectionFilter;
    private DataReceiver mReceiver;
    private boolean mRegisteredReceiver;
    private ListView mDevices;
    private AdapterLANDevice mLANDeviceAdapter;
    private ConnectionListener mConnectionListener;
    private ArrayList<LANDevice> mDeviceArrayList;
    private HashSet<String> mLANDeviceHashSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_client);

        setFonts();

        setListing();

        setReceiver();

        setListeners();

        ((TextView)(findViewById(R.id.main_title))).setText(getIntent().getStringExtra("name") + ": attached devices");

        Intent i = new Intent(this, ConnectionService.class);
        i.setAction("Connect");
        i.putExtra("name", getIntent().getStringExtra("name"));
        startService(i);

        setResult(RESULT_OK);

    }

    private void setFonts(){
        TextView tv=(TextView)findViewById(R.id.main_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/orange_juice_2.ttf");
        tv.setTypeface(face);
    }

    private void setListing(){

        mDevices = (ListView) findViewById(R.id.lanDevicesListView);

        mDeviceArrayList = new ArrayList<LANDevice>();

        mLANDeviceHashSet = new HashSet<String>();

        mLANDeviceAdapter = new AdapterLANDevice(this, mDeviceArrayList);

        mDevices.setAdapter(mLANDeviceAdapter);

    }

    private void setReceiver(){

        mConnectionFilter = new IntentFilter();

        mConnectionFilter.addAction("LAN_RECEIVEDMSG");
        mConnectionFilter.addAction("NETWORK_ERROR");

        mReceiver = new DataReceiver(this, mLANDeviceAdapter, mDeviceArrayList, mLANDeviceHashSet);
    }

    private void setListeners(){

        mConnectionListener = new ConnectionListener(this, mLANDeviceAdapter);

        mDevices.setOnItemClickListener(mConnectionListener);
    }

    protected void onDestroy(){
        super.onDestroy();
        if(mRegisteredReceiver){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mRegisteredReceiver = false;
        }
    }

    protected void onPause(){
        super.onPause();
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

        Intent i = new Intent(this, ConnectionService.class);
        i.setAction("UpdateServerUI");
        startService(i);
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

    public void onBackPressed(){
        finish();
    }
}