package com.example.clientapp;

import java.net.Socket;

public class LANDevice{

    String mName;
    String mAddress;

    public LANDevice(String name, String address){
        mName = name;
        mAddress = address;
    }

    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }
}