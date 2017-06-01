package com.example.clientapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

public class LANConnectionThread extends Thread{

    private Context mContext;
    private Socket mSocket;
    private boolean mFinish;
    private String mAppName;
    private HashMap<String, String> mStringHashMap;


    public LANConnectionThread(Context context, String appName){
        mContext = context;
        mAppName = appName;
        //cambiar esto por una pila.
        mStringHashMap = new HashMap<String,String>();
    }

    public void run(){
        Intent intent;

        Log.d("Logging", "Connecting socket");
        try {
            mSocket = new Socket();
            mSocket.setReuseAddress(true);
            mSocket.bind(new InetSocketAddress(LANConnectionThread.getMainAddress(LANConnectionThread.getMainInterface().getInetAddresses()), 48185));
        } catch (IOException e) {
            Log.d("Logging", "Creating socket error");
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "CONNECT: Error while creating socket after an error");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

        mFinish = false;

        while(!mFinish) {
            try {
                Log.d("Logging", "Trying to connect..");
                mSocket.connect(new InetSocketAddress(LANConnectionThread.getMainAddress(LANConnectionThread.getMainInterface().getInetAddresses()), 48186));
                Log.d("Logging", "Connected succesfully!");
                break;
            }  catch (ConnectException cex) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    continue;
                }
                continue;
            } catch (IOException e) {
                try {
                    if (!mSocket.isClosed()) mSocket.close();
                } catch (IOException e1) {
                    //Information about the error
                    intent = new Intent("NETWORK_ERROR");
                    intent.putExtra("message", "CONNECT: Error closing socket after an error");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                Log.d("Logging", e.toString());
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "CONNECT: Error while connecting socket to broker");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

        }

        //Information about the error
        intent = new Intent("STATUS");
        intent.putExtra("message", "STATUS: Connected");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

        String data = "NAME: " + mAppName;

        InputStream tmpIn;
        OutputStream tmpOut;

        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
            tmpOut.write(data.getBytes());
        } catch (IOException e) {
            try{
                if(!mSocket.isClosed()) mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_SERVER: Error while closing socket after an error");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
            //Information about the error
            intent = new Intent("NETWORK_ERROR");
            intent.putExtra("message", "EXCHANGE_SERVER: Error while creating socket input/output or writing output");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }

        byte [] reply = new byte[1024];
        int bytes = 0;
        byte [] word;

        while(!mFinish){
            try{
                bytes = tmpIn.read(reply);
                Log.d("Logging", "" + bytes);
            } catch(IOException ioe){
                finish();
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_SERVER: Error while reading input bytes");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            if(bytes != -1){
                word = Arrays.copyOfRange(reply, 0, bytes);
                Log.d("Logging", new String(word));
            }
            else{
                finish();
                //Information about the error
                intent = new Intent("NETWORK_ERROR");
                intent.putExtra("message", "EXCHANGE_SERVER: The broker has been disconnected");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }

            String message = new String(word);

            String command = message.substring(0, data.indexOf("_"));
            String address = message.substring(data.indexOf(":") + 1, data.indexOf("/"));
            String name = message.substring(data.indexOf(" ") + 1);

            if(command.equals("CONNECTION")){
                intent = new Intent("LAN_RECEIVEDMSG");

                intent.putExtra("command", command);
                intent.putExtra("address", address);
                intent.putExtra("name", name);

                mStringHashMap.put(address, name);
            }
            else if(command.equals("DISCONNECTION")){
                intent = new Intent("NETWORK_ERROR");

                intent.putExtra("message", "EXCHANGE_CLIENT: Device " + address + " has been disconnected");
                mStringHashMap.remove(address);
            }

            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

    }

    public static NetworkInterface getMainInterface(){
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for(NetworkInterface interf : Collections.list(interfaces)){
                if(getMainAddress(interf.getInetAddresses()) != null &&
                        !getMainAddress(interf.getInetAddresses()).getHostAddress().equals("127.0.0.1"))
                    return interf;
            }
        } catch (SocketException se) {
            return null;
        }
        return null;
    }

    public static InetAddress getMainAddress(Enumeration<InetAddress> addresses){
        for(InetAddress address : Collections.list(addresses)){
            if(address instanceof Inet4Address){
                return address;
            }
        }
        return null;
    }

    public HashMap<String, String> getStringHashMap(){
        return mStringHashMap;
    }

    public void finish(){

        mFinish = true;
        Intent intent = new Intent("NETWORK_ERROR");

        if(!mSocket.isClosed()){
            try{
                Log.d("Logging", "Closing socket");
                mSocket.close();
            }
            catch(IOException ioe){
                //Information about the error
                intent.putExtra("message", "CONNECT: Error while closing socket at exit");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                return;
            }
        }
    }
}