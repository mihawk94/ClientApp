package com.example.clientapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterLANDevice extends ArrayAdapter<LANDevice>{


    public AdapterLANDevice(Context context, ArrayList<LANDevice> deviceList){
        super(context, 0, deviceList);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        LANDevice device = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.landevicetext, parent, false);
        }

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.deviceName);
        TextView address = (TextView) convertView.findViewById(R.id.deviceAddress);

        // Populate the data into the template view using the data object
        if(device.getName() == null || device.getName().equals("")){
            name.setText("No name");
        }
        else{
            name.setText(device.getName());
        }
        address.setText(device.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }
}