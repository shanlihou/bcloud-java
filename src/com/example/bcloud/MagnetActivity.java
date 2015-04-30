package com.example.bcloud;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-30.
 */
public class MagnetActivity extends Activity{
    private Thread thread;
    private Handler mHandler;
    private ListView listView;
    private Context mContext;
    private SimpleAdapter simpleAdapter;
    private String code;
    private String addUrl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnet_layout);
        listView = (ListView)findViewById(R.id.magnetList);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString("code");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> item = (HashMap<String, String>)adapterView.getItemAtPosition(i);
                addUrl = item.get("magUrl");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        addBtTask();
                    }
                }).start();
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1)
                {
                    List<Map<String, String>> magList = (List<Map<String, String>>)msg.obj;
                    if (magList.size() == 0){
                        Map<String, String> map = new HashMap<>();
                        map.put("magUrl", "nothing");
                        map.put("magSize", "nothing");
                        magList.add(map);
                    }
                    simpleAdapter = new SimpleAdapter(mContext, magList, R.layout.mag_item,
                            new String[]{"magUrl", "magSize"},
                            new int[]{R.id.magUrl, R.id.magSize});
                    listView.setAdapter(simpleAdapter);
                }
            }

        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = MagnetManager.getInstance().getHomePage(code);
                mHandler.sendMessage(message);
            }
        });
        thread.start();

    }

    private void addBtTask(){
        Log.d("shanlihou", "addurl:" + addUrl);
        AuthManager.getInstance().printBytes(addUrl.getBytes());
        /*
        List<String> selectIds = PcsManager.getInstance().queryMagnetInfo(Cookie.getInstance(), DeliverManager.tokens,
                addUrl, "/");
        PcsManager.getInstance().addBtTask(Cookie.getInstance(), DeliverManager.tokens,
                addUrl, "/", selectIds, "", "", "");*/
    }
}
