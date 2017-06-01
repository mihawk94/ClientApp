package com.example.clientapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

public class ConnectionListener implements AdapterView.OnItemClickListener{

    private Context mContext;
    private AdapterLANDevice mAdapterLANDevice;
    private FragmentManager mFragmentManager;

    public ConnectionListener(Context context, AdapterLANDevice adapterLANDevice){
        mContext = context;
        mAdapterLANDevice = adapterLANDevice;
        mFragmentManager = ((ClientActivity)mContext).getSupportFragmentManager();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.d("Logging", "Fragment chosen");

        Fragment fragment = mFragmentManager.findFragmentByTag(mAdapterLANDevice.getItem(position).getAddress());

        Log.d("Logging", "Replacing fragment..");

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        for (Fragment currentFragment : mFragmentManager.getFragments()) {
            transaction.hide(currentFragment);
        }

        transaction.show(fragment);
        transaction.commit();
    }
}