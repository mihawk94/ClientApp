package gist.clientapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ConnectionService extends Service {

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("Logging", "Service called");

        return START_NOT_STICKY;
    }


    public IBinder onBind(Intent intent){
        return null;
    }

}