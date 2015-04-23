package com.example.bcloud;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d("shanlihou", "hello");
        new Thread(new Runnable() {
            @Override
            public void run() {
                AuthManager.getInstance().getBaiduId();
            }
        }).start();
    }

}
