package com.example.bcloud;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import manager.DeliverManager;
import manager.PcsManager;

import java.text.DecimalFormat;
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
                            String fileSize = btList.get("fileSize").get(i);
                            String finishSize = btList.get("finishSize").get(i);
                            HashMap<String, Object> tmp = new HashMap<>();
                            tmp.put("fileName", btList.get("fileName").get(i));
                            tmp.put("fileSize", getSize(fileSize));
                            tmp.put("filePercent", getPercent(finishSize, fileSize));
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
    private String getSize(String num){
        long n = Long.parseLong(num);
        long mod = 0;
        int radix = 0;
        String ret = "";
        while (n >= 1024){
            radix ++;
            mod = n % 1024;
            n /= 1024;
        }
        switch (radix){
            case 0:
                ret += n + "bytes";
                break;
            case 1:
                ret += n + "." + mod + "Kb";
                break;
            case 2:
                ret += n + "." + mod + "Mb";
                break;
            case 3:
                ret += n + "." + mod + "Gb";
                break;
            case 4:
                ret += n + "." + mod + "Tb";
                break;
        }
        return ret;
    }

    private String getPercent(String num1, String num2){
        double n1 = Double.parseDouble(num1);
        double n2 = Double.parseDouble(num2);
        double result = n1 * 100 / n2;
        DecimalFormat df = new DecimalFormat(".##");
        return df.format(result) + "%";
    }
}
