package com.example.clientapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class ConnectionService extends Service {

    LANConnectionThread mLANConnectionThread;
    CharSequence mStatus = "STATUS: Trying to connect..";

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Logging", "Service called: " + intent.getAction());
        Intent currentIntent;

        if(mLANConnectionThread != null) Log.d("Logging", "" + mLANConnectionThread.getStringHashMap().size());

        switch(intent.getAction()) {
            case "Connect":
                mLANConnectionThread = new LANConnectionThread(this, intent.getStringExtra("name"));
                mLANConnectionThread.start();
                break;
            case "StopConnection":
                if(mLANConnectionThread != null){
                    mLANConnectionThread.finish();
                }

                currentIntent = new Intent("ENABLE_APPBUTTON");
                LocalBroadcastManager.getInstance(this).sendBroadcast(currentIntent);
                stopSelf();

                break;
            case "UpdateUI":

                currentIntent = new Intent("STATUS");
                currentIntent.putExtra("message", mStatus);
                LocalBroadcastManager.getInstance(this).sendBroadcast(currentIntent);

                HashMap<String, String> map;

                if(mLANConnectionThread.isFinished()){
                    currentIntent = new Intent("NETWORK_ERROR");
                    currentIntent.putExtra("message", "EXCHANGE_SERVER: Broker is disconnected");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(currentIntent);
                }
                else{
                    currentIntent = new Intent("UPDATE_LIST");
                    File file = new File(getDir("data", MODE_PRIVATE), "map");
                    try{
                        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                        map = (HashMap<String, String>) inputStream.readObject();
                        inputStream.close();
                    }
                    catch(Exception e){
                        return START_NOT_STICKY;
                    }
                    currentIntent.putExtra("addresses", map);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(currentIntent);
                }
                break;
            default:
                break;
        }

        return START_NOT_STICKY;
    }

    public void setStatus(CharSequence status){
        mStatus = status;
    }

    public IBinder onBind(Intent intent){
        return null;
    }

}