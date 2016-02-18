package com.example.bcloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import manager.DeliverManager;
import manager.MagnetManager;
import manager.PcsManager;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-30.
 */
public class MagnetActivity extends Activity {
    private Thread thread;
    private Handler mHandler;
    private ListView listView;
    private Context mContext;
    private SimpleAdapter simpleAdapter;
    private String code;
    private String addUrl = null;
    private int curIndex = -1;
    private Map<String, String> mBtRet;
    private Map<String, String> mBtAdd;
    private Bitmap codeBmp;
    private String vCodeDialog;
    private TextView statText;
    private EditText searchText;
    private Button searchButton;
    private Runnable searchRun = null;
    private boolean bSearch = false;
    private ImageView imReturn;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnet_layout);
        listView = (ListView) findViewById(R.id.magnetList);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString("code");
        mBtAdd = new HashMap<>();

        searchText = (EditText) findViewById(R.id.searchCode);
        statText = (TextView) findViewById(R.id.statText);
        imReturn = (ImageView)findViewById(R.id.returnBack);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> item = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                curIndex = i;
                addUrl = item.get("magUrl");
                statText.setText("start:" + curIndex);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> ret = addBtTask(null);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = ret;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    bSearch = false;
                    statText.setText("search finished!");
                    Log.d("shanlihou", "end dialog");
                    if (mProgressDialog != null){
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    List<Map<String, String>> magList = (List<Map<String, String>>) msg.obj;
                    if (magList.size() == 0) {
                        Map<String, String> map = new HashMap<>();
                        map.put("magUrl", "nothing");
                        map.put("magSize", "nothing");
                        map.put("magTitle", "nothing");
                        magList.add(map);
                    }
                    simpleAdapter = new SimpleAdapter(mContext, magList, R.layout.mag_item,
                            new String[]{"magUrl", "magSize", "magTitle"},
                            new int[]{R.id.magUrl, R.id.magSize, R.id.magTitle});
                    listView.setAdapter(simpleAdapter);
                } else if (msg.what == 2) {
                    mBtRet = (Map<String, String>) msg.obj;
                    Log.d("shanlihou", "has toast");
                    statText.setText(mBtRet.get("msg"));
                    Toast.makeText(mContext, mBtRet.get("msg"), Toast.LENGTH_SHORT);
                    if (mBtRet.containsKey("errorCode")) {
                        if (mBtRet.get("errorCode").equals("-19")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
//                            codeBmp = PcsManager.getInstance().getVcodeBmp(mBtRet.get("url"), Cookie.getInstance());
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("Cookie", Cookie.getInstance().getHeader());
                                    codeBmp = UrlOpener.getInstance().getVCode(mBtRet.get("url"), map);
                                    Message message = new Message();
                                    message.what = 3;
                                    mHandler.sendMessage(message);
                                }
                            }).start();
                        } else {
                            curIndex++;
                            if (curIndex < listView.getCount()) {
                                HashMap<String, String> item = (HashMap<String, String>) listView.getItemAtPosition(curIndex);
                                addUrl = item.get("magUrl");
                                statText.setText("start:" + curIndex);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Map<String, String> ret = addBtTask(null);
                                        Message message = new Message();
                                        message.what = 2;
                                        message.obj = ret;
                                        mHandler.sendMessage(message);
                                    }
                                }).start();
                            }
                        }
                    }

                } else if (msg.what == 3) {
                    getVCode(mContext, codeBmp);
                } else if (msg.what == 4) {
                    mBtAdd.clear();
                    mBtAdd.put("ids", mBtRet.get("ids"));
                    mBtAdd.put("vcode", mBtRet.get("vcode"));
                    mBtAdd.put("code", vCodeDialog);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            bSearch = true;
                            Map<String, String> ret = addBtTask(mBtAdd);
                            Message message = new Message();
                            message.what = 2;
                            message.obj = ret;
                            mHandler.sendMessage(message);
                        }
                    }).start();
                }
            }

        };
        init();
        statText.setText("idle");
        if (!code.equals("")){
            search();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            return true;
        }*/
        return super.onKeyDown(keyCode, event);

    }

    private void search(){
        statText.setText("search code:" + code);
        Log.d("shanlihou", "start dialog");
        if (mProgressDialog == null){
            mProgressDialog = ProgressDialog.show(MagnetActivity.this, "请稍等...", "获取数据中...", true);
            mProgressDialog.setCancelable(true);
        }
        new Thread(searchRun).start();
    }

    private void init() {
        runInit();
        viewInit();
    }

    private void viewInit(){
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("shanlihou", KeyEvent.KEYCODE_ENTER + "");
                Log.d("shanlihou", keyCode + "");
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    code = searchText.getText().toString();
                    Log.d("shanlihou", code);
                    if (!bSearch) {
                        search();
                    }
                }
                return false;
            }
        });
        imReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MagnetActivity.this.finish();
            }
        });
    }

    private void runInit() {
        searchRun = new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = MagnetManager.getInstance().getHomePage(code);
                mHandler.sendMessage(message);
            }
        };
    }

    private Map<String, String> addBtTask(Map<String, String> mapArg) {
        Map<String, String> map = new HashMap<>();
        Log.d("shanlihou", "addurl:" + addUrl);
        String magUrl = MagnetManager.getInstance().getMagnet(addUrl);
        String addRet;
        if (mapArg == null) {
            AuthManager.getInstance().printBytes(magUrl.getBytes());
            List<String> selectIds = PcsManager.getInstance().queryMagnetInfo(Cookie.getInstance(), DeliverManager.tokens,
                    magUrl, "/");
            if (selectIds.size() == 0) {
                map.put("msg", "not find movie");
                map.put("errorCode", "100001");
                return map;
            }
            if (selectIds.get(0).equals("error")) {
                map.put("errorCode", selectIds.get(1));
                if (map.get("errorCode").equals("36004")){
                    map.put("msg", "请先登录，否则无法添加视频到百度云");
                }else {
                    map.put("msg", selectIds.get(2));
                }
                return map;
            }
            String strIds = "";
            for (int i = 0; i < selectIds.size(); i++) {
                if (i != 0) {
                    strIds += ",";
                }
                strIds += selectIds.get(i);
            }
            addRet = PcsManager.getInstance().addBtTask(Cookie.getInstance(), DeliverManager.tokens,
                    magUrl, "/", strIds, "", "", "");
            map.put("ids", strIds);

        } else {
            addRet = PcsManager.getInstance().addBtTask(Cookie.getInstance(), DeliverManager.tokens,
                    magUrl, "/", mapArg.get("ids"), "", mapArg.get("vcode"), mapArg.get("code"));
            map.put("ids", mapArg.get("ids"));
        }
        try {
            JSONTokener jsonTokener = new JSONTokener(addRet);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            if (!jsonObject.isNull("error_code")) {
                map.put("errorCode", jsonObject.getInt("error_code") + "");
                map.put("msg", jsonObject.getString("error_msg"));
                if (jsonObject.getInt("error_code") == -19) {
                    map.put("vcode", jsonObject.getString("vcode"));
                    map.put("url", jsonObject.getString("img"));
                }
            } else {
                int rapid = jsonObject.getInt("rapid_download");
                if (rapid == 0){
                    map.put("msg", "添加成功,但任务未下载完成,可在任务列表查看任务");
                }else {
                    map.put("msg", "添加成功,请到百度云中观看");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public void getVCode(Context context, Bitmap bmp) {
        vCodeDialog = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        final View vCodeView = factory.inflate(R.layout.vcode, null);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("验证码");
        builder.setView(vCodeView);
        ImageView vCodeImg = (ImageView) vCodeView.findViewById(R.id.vcodeBmp);
        vCodeImg.setImageBitmap(bmp);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                EditText vcode = (EditText) vCodeView.findViewById(R.id.vcodeEdit);
                vCodeDialog = vcode.getText().toString();
                Message message = new Message();
                message.what = 4;
                mHandler.sendMessage(message);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.create().show();
    }
}

