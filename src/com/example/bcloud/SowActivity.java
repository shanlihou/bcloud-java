package com.example.bcloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-29.
 */
public class SowActivity extends Activity {
    private GridView gridView;
    private Thread thread;
    private Handler mHandler;
    private SimpleAdapter simpleAdapter;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sow_layout);
        gridView = (GridView)findViewById(R.id.sowGridView);
        mContext = this;
        gridView.setOnItemClickListener(new ItemClickListener());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1)
                {
                    List<Map<String, Object>> grid = (List<Map<String, Object>>)msg.obj;
                    simpleAdapter = new SimpleAdapter(mContext, grid, R.layout.sow_item,
                            new String[]{"bitmap", "code"},
                            new int[]{R.id.sowImage, R.id.sowText});
                    simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object o, String s) {
                            if ((view instanceof ImageView) & (o instanceof Bitmap)){
                                ImageView iv = (ImageView)view;
                                Bitmap bm = (Bitmap)o;
                                iv.setImageBitmap(bm);
                                return true;
                            }
                            return false;
                        }
                    });
                    gridView.setAdapter(simpleAdapter);
                }
            }

        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = SowManager.getInstance().getPage(1);
                mHandler.sendMessage(message);
            }
        });
        thread.start();
    }
    class  ItemClickListener implements AdapterView.OnItemClickListener {
    public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened
                            View arg1,//The view within the AdapterView that was clicked
                            int arg2,//The position of the view in the adapter
                            long arg3//The row id of the item that was clicked
    ) {
        HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
        Intent intent = new Intent();
        intent.setClass(SowActivity.this, MagnetActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("code", (String)item.get("code"));//压入数据
        intent.putExtras(mBundle);
        startActivity(intent);
    }
 }
}

