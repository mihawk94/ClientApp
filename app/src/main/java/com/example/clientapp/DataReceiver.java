package com.example.clientapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.Touch;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static android.R.attr.bitmap;

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

        Log.d("Logging", "Receiver has received a new message: " + intent.getAction());

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
            case "UPDATE_LIST":
                update_list(context, intent);
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

        String command = intent.getStringExtra("command");
        String address = intent.getStringExtra("address");
        String name = intent.getStringExtra("name");

        Log.d("Logging", "Received command: " + command);

        switch(command){
            case "PRESS":
                press(name, address);
                break;
            case "RELEASE":
                release(name, address);
                break;
            case "CONNECTION":
                Log.d("Logging", "New connection..");
                if(mLANDeviceHashSet.contains(address)) return;
                mLANDeviceHashSet.add(address);
                mDeviceArrayList.add(new LANDevice(name, address));
                mDevices.notifyDataSetChanged();


                Log.d("Logging", "New fragment..");

                ControlFragment fragment = ControlFragment.newInstance(name);
                mFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment, address)
                        .addToBackStack(null)
                        .commit();

                Toast.makeText(mContext, "'" + name + "' is connected", Toast.LENGTH_SHORT).show();
                break;
            case "TOUCH":
                touch(name, address);
                break;
            case "TOUCHRELEASE":
                touchRelease(name, address);
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

    private void release(String value, String address) {
        if (mFragmentManager.findFragmentByTag(address) == null) return;
        switch (value) {
            case "CH_UP":
                ((GradientDrawable) mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_up)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "VOL_DOWN":
                ((GradientDrawable) mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_down)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "VOL_UP":
                ((GradientDrawable) mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.vol_up)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            case "CH_DOWN":
                ((GradientDrawable) mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.ch_down)
                        .getBackground()).setColor(Color.parseColor("#616161"));
                break;
            default:
                break;
        }
    }

    private void touch(String value, String address){
        if (mFragmentManager.findFragmentByTag(address) == null) return;

        TouchView touch = (TouchView)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.touch);

        Bitmap bitmap = ((BitmapDrawable)touch.getDrawable()).getBitmap();
        bitmap.setHasAlpha(true);
        Canvas canvas = new Canvas(bitmap);

        int touchWidth = touch.getWidth();
        int touchHeight = touch.getHeight();

        String x_str = value.substring(0, value.indexOf(" "));
        Log.d("Logging", "X string: " + x_str);
        String y_str = value.substring(value.indexOf(" ") + 1);
        Log.d("Logging", "Y string: " + y_str);

        float xPercentage = Float.parseFloat(x_str.substring(x_str.indexOf(":") + 1));
        float yPercentage = Float.parseFloat(y_str.substring(y_str.indexOf(":") + 1));

        float x = touchWidth * xPercentage;
        float y = touchHeight * yPercentage;

        Log.d("Logging", "Touch width: " + touchWidth + " Touch height: " + touchHeight);

        Paint p = new Paint();
        p.setColor(Color.BLACK);
        canvas.drawCircle(x, y, touchWidth/10, p);

        touch.setImageBitmap(bitmap);


    }

    private void touchRelease(String value, String address){
        if (mFragmentManager.findFragmentByTag(address) == null) return;

        TouchView touch = (TouchView)mFragmentManager.findFragmentByTag(address).getView().findViewById(R.id.touch);

        Bitmap bitmap = ((BitmapDrawable)touch.getDrawable()).getBitmap();

        bitmap.eraseColor(Color.TRANSPARENT);

        touch.setImageBitmap(bitmap);
    }

    private void network_error(Context context, Intent intent){

        String data = intent.getStringExtra("message");

        Log.d("Logging", "Network error received: " + data);

        String command = data.substring(0, data.indexOf(" "));
        String address = data.substring(data.indexOf(" ") + 1);

        switch(command){
            case "CONNECT:":
                Toast.makeText(mContext, "Error on broker connection: " + address, Toast.LENGTH_SHORT).show();
                break;
            case "EXCHANGE_SERVER:":
                if(mContext instanceof ClientActivity){
                    Toast.makeText(mContext, "Error on broker connection: " + address, Toast.LENGTH_SHORT).show();
                    ((ClientActivity)mContext).finish();
                }
                break;
            case "EXCHANGE_CLIENT:":
                Log.d("Logging", "EXCHANGE_CLIENT error called!");
                if(!(mContext instanceof ClientActivity)) return;
                if(!mLANDeviceHashSet.contains(address)){
                    Log.d("Logging", "Device isn't connected");
                    return;
                }

                Log.d("Logging", "Proceeding to remove the fragment and the item");

                Fragment fragment = mFragmentManager.findFragmentByTag(address);
                if(fragment != null){
                    Log.d("Logging", "Removing fragment");
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

                mLANDeviceHashSet.remove(address);

                for(int i = 0; i < mDeviceArrayList.size(); i++){
                    if(mDeviceArrayList.get(i).getAddress().equals(address)){
                        mDeviceArrayList.remove(i);
                        break;
                    }
                }

                Fragment fragment1;

                FragmentTransaction transaction = mFragmentManager.beginTransaction();

                for (Fragment currentFragment : mFragmentManager.getFragments()) {
                    transaction.hide(currentFragment);
                }

                if(!mDeviceArrayList.isEmpty()){
                    fragment1 = mFragmentManager.findFragmentByTag(mDeviceArrayList.get(mDeviceArrayList.size()-1).getAddress());
                    transaction.show(fragment1);
                }
                transaction.addToBackStack(null);
                transaction.commit();


                mDevices.notifyDataSetChanged();
                Toast.makeText(mContext, "Disconnected: " + address, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void status(Context context, Intent intent){

        if(!(mContext instanceof ClientActivity)) return;

        Log.d("Logging", "Changing status..");

        TextView status = (TextView) ((Activity)mContext).findViewById(R.id.status);

        status.setText(intent.getStringExtra("message"));
    }

    private void update_list(Context context, Intent intent){



        HashMap<String, String> addresses = (HashMap<String, String>) intent.getSerializableExtra("addresses");
        Log.d("Logging", "" + addresses.size());
        Log.d("Logging", "" + mDeviceArrayList.size());

        for(int i = 0; i < mDeviceArrayList.size(); i++){
            Log.d("Logging", mDeviceArrayList.get(i).getAddress());
            if(!addresses.containsKey(mDeviceArrayList.get(i).getAddress())){
                delete_fragment(mDeviceArrayList.get(i).getAddress(), i);
                i -= 1;
            }
        }

        Iterator it = addresses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry device = (Map.Entry)it.next();
            if(!mLANDeviceHashSet.contains(device.getKey())){
                add_fragment((String)device.getValue(), (String)device.getKey());
            }
            it.remove();
        }

    }

    private void delete_fragment(String address, int i){

        Fragment fragment = mFragmentManager.findFragmentByTag(address);
        if(fragment != null){
            Log.d("Logging", "Removing fragment");
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        mLANDeviceHashSet.remove(address);

        mDeviceArrayList.remove(i);

        Fragment fragment1;

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        for (Fragment currentFragment : mFragmentManager.getFragments()) {
            transaction.hide(currentFragment);
        }

        if(!mDeviceArrayList.isEmpty()){
            fragment1 = mFragmentManager.findFragmentByTag(mDeviceArrayList.get(mDeviceArrayList.size()-1).getAddress());
            transaction.show(fragment1);
        }
        transaction.addToBackStack(null);
        transaction.commit();


        mDevices.notifyDataSetChanged();

    }

    private void add_fragment(String name, String address){

        mLANDeviceHashSet.add(address);
        mDeviceArrayList.add(new LANDevice(name, address));

        Log.d("Logging", "New fragment. Name: " + name + ", address: " + address);

        ControlFragment fragment = ControlFragment.newInstance(name);
        mFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, address)
                .addToBackStack(null)
                .commit();

        mDevices.notifyDataSetChanged();

    }
}