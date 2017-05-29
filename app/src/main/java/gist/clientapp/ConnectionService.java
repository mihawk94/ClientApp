package gist.clientapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ConnectionService extends Service {

    LANConnectionThread mLANConnectionThread;

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Logging", "Service called");

        switch(intent.getAction()) {
            case "Connect":
                mLANConnectionThread = new LANConnectionThread(this, intent.getStringExtra("name"));
                mLANConnectionThread.start();
                break;
            case "StopConnection":
                break;
            case "UpdateUI":
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