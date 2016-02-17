package com.example.bcloud;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

/**
 * Created by Administrator on 2016/2/15 0015.
 */
public class MainApplication extends Application {
    public static final String SEND_PATH = "/mnt/sdcard/send/";
    private String SHARED_APP = "app";
    SharedPreferences mShared = null;
    private static MainApplication instance = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mShared = getSharedPreferences(SHARED_APP, Context.MODE_PRIVATE);
        instance = this;
    }


    public void setShared(String key, String value){
        SharedPreferences.Editor editor = mShared.edit();
        if (value == null){
            editor.remove(key);
        }
        else{
            editor.putString(key,value);
        }
        editor.commit();
    }
    public String getShared(String key){
        return mShared.getString(key, null);
    }
    public static MainApplication getInstance(){
        return instance;
    }
}

