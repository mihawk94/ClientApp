package gist.clientapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashSet;

public class DataReceiver extends BroadcastReceiver{

    private Context mContext;
    private AdapterLANDevice mLANDeviceAdapter;
    private ArrayList<LANDevice> mDeviceArrayList;
    private HashSet<String> mLANDeviceHashSet;

    public DataReceiver(Context context){
        mContext = context;
    }

    public DataReceiver(Context context, AdapterLANDevice adapterLANDevice, ArrayList<LANDevice> deviceArrayList, HashSet<String> lanDeviceHashSet){
        mContext = context;
        mLANDeviceAdapter = adapterLANDevice;
        mDeviceArrayList = deviceArrayList;
        mLANDeviceHashSet = lanDeviceHashSet;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}