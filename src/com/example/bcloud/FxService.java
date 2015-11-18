package com.example.bcloud;

import android.app.Service;

/**
 * Created by shanlihou on 15-11-9.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FxService extends Service
{

    //定义浮动窗口布局
    LinearLayout mFloatLayout;
    LinearLayout mFloatButtonLayout;
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    Handler mHandler;

    Button mFloatView;
    Button mBtSearch;
    Button mBtClose;

    EditText mEditSearch;

    ListView mListView;

    Runnable searchRun;
    /*****/
    String code;
    Context mContext;
    SimpleAdapter simpleAdapter;
    /*****/
    private static final String TAG = "FxService";

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i(TAG, "oncreat");
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void createFloatView()
    {
        mContext = this;
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "mWindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

         /*// 设置悬浮窗口长宽数据
        wmParams.width = 200;
        wmParams.height = 80;*/

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        //添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        //浮动窗口按钮
        mFloatView = (Button)mFloatLayout.findViewById(R.id.float_id);
        mFloatButtonLayout = (LinearLayout)mFloatLayout.findViewById(R.id.floatLine);
        mEditSearch = (EditText)mFloatLayout.findViewById(R.id.searchEdit);
        mEditSearch.setFocusable(true);
        mEditSearch.setFocusableInTouchMode(true);
        mEditSearch.setText("china");
        mEditSearch.setEnabled(true);
        mEditSearch.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        mBtSearch = (Button)mFloatLayout.findViewById(R.id.btFloatSearch);
        mBtClose = (Button)mFloatLayout.findViewById(R.id.btFloatClose);
        mListView = (ListView)mFloatLayout.findViewById(R.id.floatListView);

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);
        //设置监听浮动窗口的触摸移动
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 1:
                        List<Map<String, String>> magList = (List<Map<String, String>>)msg.obj;
                        if (magList.size() == 0){
                            Map<String, String> map = new HashMap<>();
                            map.put("magUrl", "nothing");
                            map.put("magSize", "nothing");
                            map.put("magTitle", "nothing");
                            magList.add(map);
                        }
                        simpleAdapter = new SimpleAdapter(mContext, magList, R.layout.mag_item,
                                new String[]{"magUrl", "magSize", "magTitle"},
                                new int[]{R.id.magUrl, R.id.magSize, R.id.magTitle});
                        mListView.setAdapter(simpleAdapter);
                        mBtSearch.setEnabled(true);
                        break;
                    default:
                        break;
                }
            }
        };
        mFloatView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) event.getRawX() - mFloatView.getMeasuredWidth() / 2;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                //减25为状态栏的高度
                wmParams.y = (int) event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                //刷新
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;  //此处必须返回false，否则OnClickListener获取不到监听
            }
        });



        mFloatView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(FxService.this, "onClick", Toast.LENGTH_SHORT).show();
                mFloatView.setVisibility(View.GONE);
                mFloatButtonLayout.setVisibility(View.VISIBLE);
                mEditSearch.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.VISIBLE);

                //请求获得焦点
                forceFocusOnView(mEditSearch);
                /*
                InputMethodManager inputManager = (InputMethodManager) mEditSearch
                        .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(mEditSearch, 0);*/

            }
        });

        mBtClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatView.setVisibility(View.VISIBLE);
                mFloatButtonLayout.setVisibility(View.GONE);
                mEditSearch.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
            }
        });
        searchRun = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = MagnetManager.getInstance().getHomePage(code);
                mHandler.sendMessage(message);
            }
        };
        mBtSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                code = mEditSearch.getText().toString();
                new Thread(searchRun).start();
                mBtSearch.setEnabled(false);
            }
        });
    }
    public static void forceFocusOnView(final View view) {
        if (view == null)
            return;
        view.post(new Runnable() {
            @Override
            public void run() {
                view.clearFocus();
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.requestFocus();
                    }
                });
            }
        });
    }


    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mFloatLayout != null)
        {
            //移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
    }

}
