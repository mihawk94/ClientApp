package com.example.clientapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ConnectionService extends Service {

    LANConnectionThread mLANConnectionThread;

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Logging", "Service called: " + intent.getAction());
        Intent currentIntent;

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
                currentIntent = new Intent("UPDATE_LIST");
                currentIntent.putExtra("addresses", mLANConnectionThread.getStringHashMap());
                LocalBroadcastManager.getInstance(this).sendBroadcast(currentIntent);
                break;
            default:
                break;
        }

        return START_NOT_STICKY;
    }


    public IBinder onBind(Intent intent){
        return null;
    }

}