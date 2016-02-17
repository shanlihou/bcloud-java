package com.example.bcloud;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import manager.SowManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-29.
 */
public class SowActivity extends Activity {
    private GridView gridView;
    private Runnable getImageAble;
    private Handler mHandler;
    private ImageAdapter mImageAdapter;
    private Context mContext;
    private Button btnPrev;
    private Button btnNext;
    private int page;
    private TextView statText;
    private ProgressDialog mProgressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sow_layout);
        gridView = (GridView)findViewById(R.id.sowGridView);
        mContext = this;
        btnPrev = (Button)findViewById(R.id.prevButton);
        btnNext = (Button)findViewById(R.id.nextButton);
        page = Cookie.getInstance().getDb().getPage();
        statText = (TextView)findViewById(R.id.sowStatText);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page > 1){
                    page--;
                }
                Cookie.getInstance().getDb().modifyPage(page);
                startGetPage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                Cookie.getInstance().getDb().modifyPage(page);
                startGetPage();
            }
        });
        gridView.setOnItemClickListener(new ItemClickListener());
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1)
                {
                    mProgressDialog.dismiss();
                    List<Map<String, String>> grid = (List<Map<String, String>>)msg.obj;
                    if (grid == null){
                        statText.setText("解析失败");
                        return;
                    }
                    statText.setText("解析成功,图片正在加载:第" + page + "页");
                    /*
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
                    });*/
                    mImageAdapter = new ImageAdapter(mContext, gridView, grid);
                    gridView.setAdapter(mImageAdapter);
                }
            }

        };
        getImageAble = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = SowManager.getInstance().getPage(page);
                mHandler.sendMessage(message);
                return;
            }
        };
        startGetPage();
    }
    private void startGetPage(){
        statText.setText("正在解析列表,请耐心等待");
        mProgressDialog = ProgressDialog.show(SowActivity.this, "请稍等...", "获取数据中...", true);
        new Thread(getImageAble).start();
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

