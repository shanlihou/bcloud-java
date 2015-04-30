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
                List<String> selectIds = PcsManager.getInstance().queryMagnetInfo(Cookie.getInstance(), DeliverManager.tokens, "magnet:?xt=urn:btih:470729D8746976D21828EF6075F144B3D6EB151F&dn=WANZ-317",
                        "/");
                PcsManager.getInstance().addBtTask(Cookie.getInstance(), DeliverManager.tokens, "magnet:?xt=urn:btih:470729D8746976D21828EF6075F144B3D6EB151F&dn=WANZ-317",
                        "/", selectIds, "", "", "");

                selectIds.clear();
                String testUrl = "magnet:?xt=urn:btih:BC988FD3EE934AC32A348260C95A8351A77B52C4&dn=WANZ-213%E2%80%93%20Yuu%20Asakura%20%28%E9%BA%BB%E5%80%89%E6%86%82%29%20%E2%80%93%20%E7%94%9F%E5%BE%92%E3%81%AB%E8%87%AA%E5%AE%85%E3%82%92%E4%B9%97%E3%81%A3%E5%8F%96%E3%82%89%E3%82%8C%E3%81%9F%E8%8B%A5%E5%A6%BB%E5%A5%B3%E6%95%99%E5%B8%AB%20%E7%BE%8E%E4%BA%BA%E5%A6%BB%E3%81%8C%E5%A5%B4%E9%9A%B7%E3%83%9A%E3%83%83%E3%83%88%E3%81%A8%E5%8C%96%E3%81%993%E6%97%A5%E9%96%93%E3%81%AE%E5%87%8C%E8%BE%B1%E5%8A%87.mp4";
                selectIds = PcsManager.getInstance().queryMagnetInfo(Cookie.getInstance(), DeliverManager.tokens, testUrl,
                        "/");
                PcsManager.getInstance().addBtTask(Cookie.getInstance(), DeliverManager.tokens, testUrl,
                        "/", selectIds, "", "", "");
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        });
        thread.start();
    }


}
