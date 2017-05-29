package gist.clientapp;

import java.net.Socket;
import java.util.HashMap;

public class LANDevice{

    String mName;
    String mAddress;
    Socket mSocket;

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

    public void setSocket(Socket socket){
        mSocket = socket;
    }

    public Socket getSocket(){
        return mSocket;
    }
}