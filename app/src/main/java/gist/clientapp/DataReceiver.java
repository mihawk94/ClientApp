package gist.clientapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

public class DataReceiver extends BroadcastReceiver{

    private Context mContext;
    private AdapterLANDevice mDevices;
    private ArrayList<LANDevice> mDeviceArrayList;
    private HashSet<String> mLANDeviceHashSet;
    private FragmentManager mFragmentManager;

    public DataReceiver(Context context){
        mContext = context;
    }

    public DataReceiver(Context context, AdapterLANDevice adapterLANDevice, ArrayList<LANDevice> deviceArrayList, HashSet<String> lanDeviceHashSet){
        mContext = context;
        mDevices = adapterLANDevice;
        mDeviceArrayList = deviceArrayList;
        mLANDeviceHashSet = lanDeviceHashSet;
        mFragmentManager = ((ClientActivity)mContext).getSupportFragmentManager();
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Logging", "Receiver has received a new message");

        switch(intent.getAction()){
            case "ENABLE_APPBUTTON":
                enable_appButton(context, intent);
                break;
            case "LAN_RECEIVEDMSG":
                lan_receivedMsg(context, intent);
                break;
            case "NETWORK_ERROR":
                network_error(context, intent);
                break;
            case "STATUS":
                status(context, intent);
                break;
            default:
                break;
        }

    }

    private void enable_appButton(Context context, Intent intent){

        if(!(mContext instanceof MainActivity)) return;

        Log.d("Logging", "Enabling main buttons..");

        Button appButton = (Button)((Activity)mContext).findViewById(R.id.app_btn);

        appButton.setBackgroundResource(R.drawable.custom_button);

        ((MainActivity)mContext).setConnection(false);

    }

    private void lan_receivedMsg(Context context, Intent intent){

        if(!(mContext instanceof ClientActivity)) return;

        String data = intent.getStringExtra("message");

        String command = data.substring(0, data.indexOf(" "));
        String subcommand = data.substring(data.indexOf(" ") + 1);

        String value = subcommand.substring(0, data.indexOf(" "));
        String address = value.substring(data.indexOf(" ") + 1);

        switch(command){
            case "PRESS:":
                press(value, address);
                break;
            case "RELEASE:":
                release(value, address);
                break;
            case "NAME:":
                Log.d("Logging", "New connection..");
                if(mLANDeviceHashSet.contains(address)) return;
                mLANDeviceHashSet.add(address);
                mDeviceArrayList.add(new LANDevice(value, address));
                mDevices.notifyDataSetChanged();


                Log.d("Logging", "New fragment..");

                ControlFragment fragment = ControlFragment.newInstance(value);
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment, address)
                        .addToBackStack(null)
                        .commit();

                Toast.makeText(mContext, "'" + value + "' is connected", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    private void press(String value, String address){
        if(mFragmentManager.findFragmentByTag(address) == null) return;
        switch (value){
            case "CH_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_up)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            case "VOL_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_down)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            case "VOL_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_up)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            case "CH_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_down)
                        .getBackground()).setColor(Color.parseColor("#FF4A148C"));
                break;
            default:
                break;
        }
    }

    private void release(String value, String address){
        if(mFragmentManager.findFragmentByTag(address) == null) return;
        switch (value){
            case "CH_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_up)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "VOL_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_down)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "VOL_UP":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_up)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "CH_DOWN":
                ((GradientDrawable)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_down)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            default:
                break;
        }
    }

    private void network_error(Context context, Intent intent){

    }

    private void status(Context context, Intent intent){

        if(!(mContext instanceof ClientActivity)) return;

        Log.d("Logging", "Changing status..");

        TextView status = (TextView) ((Activity)mContext).findViewById(R.id.status);

        status.setText(intent.getStringExtra("message"));
    }
}