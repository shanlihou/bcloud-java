package com.example.bcloud;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-28.r
 */
public class BTActivity extends Activity{
    private ListView btListView;
    private ArrayList<Map<String, Object>> arrayList;
    private SimpleAdapter simpleAdapter;
    private Thread thread;
    private Map<String, List<String>> btList = null;
    private Handler mHandler;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_task_list);
        mContext = this;
        btListView = (ListView)findViewById(R.id.btListView);

        btList = new HashMap<>();
        btList.put("fileName", new ArrayList<>());
        btList.put("fileSize", new ArrayList<>());
        btList.put("finishSize", new ArrayList<>());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1)
                {
                    arrayList = new ArrayList<>();
                    if (btList != null) {
                        for (int i = 0; i < btList.get("fileName").size(); i++) {
                            HashMap<String, Object> tmp = new HashMap<>();
                            tmp.put("fileName", btList.get("fileName").get(i));
                            tmp.put("fileSize", btList.get("fileSize").get(i));
                            tmp.put("filePercent", btList.get("finishSize").get(i));
                            arrayList.add(tmp);
                        }
                    }
                    simpleAdapter = new SimpleAdapter(mContext, arrayList, R.layout.line_lay_out,
                            new String[]{"fileName", "fileSize", "filePercent"},
                            new int[]{R.id.fileName, R.id.fileSize, R.id.filePercent});
                    btListView.setAdapter(simpleAdapter);
                }
            }

        };


        for (Map.Entry<String, String> entry: DeliverManager.getInstance().cookie.entrySet()){
            Log.d("shanlihou", entry.getKey() + ":" + entry.getValue());
        }
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> ids = PcsManager.getInstance().listTask(Cookie.getInstance(), DeliverManager.tokens);
                PcsManager.getInstance().queryTask(Cookie.getInstance(), DeliverManager.tokens, ids, btList);
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });
        thread.start();
    }


}
