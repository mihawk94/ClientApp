package com.example.clientapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ButtonListener implements View.OnClickListener{

    private Context mContext;

    public ButtonListener(Context context){
        mContext = context;
    }
    @Override
    public void onClick(View v) {

        if(((MainActivity)mContext).isConnected()){
            Log.d("Logging", "Socket connected in MainActivity");
            return;
        }

        Intent i;

        switch (v.getId()){
            case R.id.app_btn:

                if(((EditText)(((Activity)mContext).findViewById(R.id.app_name))).getText().toString().equals("") ||
                        ((EditText)(((Activity)mContext).findViewById(R.id.app_name))).getText().toString() == null){
                    Toast.makeText(mContext.getApplicationContext(), "Insert your device name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(((EditText)(((Activity)mContext).findViewById(R.id.app_name))).getText().toString().toCharArray().length > 16){
                    Toast.makeText(mContext.getApplicationContext(), "Maximum size of name: 16 symbols", Toast.LENGTH_SHORT).show();
                    return;
                }

                ((MainActivity)mContext).setConnection(true);
                i = new Intent(mContext, ClientActivity.class);
                i.putExtra("name", ((EditText)(((Activity)mContext).findViewById(R.id.app_name))).getText().toString());
                ((Activity)mContext).startActivityForResult(i, ((MainActivity) mContext).REQUEST_APP);
                break;
        }
    }
}