package com.example.bcloud;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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
    private Map<String, String> mBtRet;
    private Map<String, String> mBtAdd;
    private Bitmap codeBmp;
    private String vCodeDialog;
    private TextView statText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnet_layout);
        listView = (ListView)findViewById(R.id.magnetList);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        code = bundle.getString("code");
        mBtAdd = new HashMap<>();
        statText = (TextView)findViewById(R.id.statText);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> item = (HashMap<String, String>)adapterView.getItemAtPosition(i);
                addUrl = item.get("magUrl");
                statText.setText("start");
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
                }else if(msg.what == 2){
                    mBtRet = (Map<String, String>)msg.obj;
                    Log.d("shanlihou", "has toast");
                    statText.setText(mBtRet.get("msg"));
                    Toast.makeText(mContext, mBtRet.get("msg"), Toast.LENGTH_SHORT);
                    if (mBtRet.containsKey("errorCode")){
                        if (mBtRet.get("errorCode").equals("-19")){
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
                        }
                    }

                }else if(msg.what == 3){
                    getVCode(mContext, codeBmp);
                }else if(msg.what == 4){
                    mBtAdd.clear();
                    mBtAdd.put("ids", mBtRet.get("ids"));
                    mBtAdd.put("vcode", mBtRet.get("vcode"));
                    mBtAdd.put("code", vCodeDialog);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
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

    private Map<String, String> addBtTask(Map<String, String> mapArg){
        Map<String, String> map = new HashMap<>();
        Log.d("shanlihou", "addurl:" + addUrl);
        String addRet;
        if(mapArg == null){
            AuthManager.getInstance().printBytes(addUrl.getBytes());
            List<String> selectIds = PcsManager.getInstance().queryMagnetInfo(Cookie.getInstance(), DeliverManager.tokens,
                    addUrl, "/");
            if (selectIds.size() == 0){
                map.put("msg", "not find movie");
                map.put("errorCode", "100001");
                return map;
            }
            if (selectIds.get(0).equals("error")){
                map.put("msg", selectIds.get(2));
                map.put("errorCode", selectIds.get(1));
                return map;
            }
            String strIds = "";
            for (int i = 0; i < selectIds.size(); i++){
                if (i != 0){
                    strIds += ",";
                }
                strIds += selectIds.get(i);
            }
            addRet = PcsManager.getInstance().addBtTask(Cookie.getInstance(), DeliverManager.tokens,
                    addUrl, "/", strIds, "", "", "");
            map.put("ids", strIds);

        }else{
            addRet = PcsManager.getInstance().addBtTask(Cookie.getInstance(), DeliverManager.tokens,
                    addUrl, "/", mapArg.get("ids"), "", mapArg.get("vcode"), mapArg.get("code"));
            map.put("ids", mapArg.get("ids"));
        }
        try {
            JSONTokener jsonTokener = new JSONTokener(addRet);
            JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
            if (!jsonObject.isNull("error_code")) {
                map.put("errorCode", jsonObject.getInt("error_code") + "");
                map.put("msg", jsonObject.getString("error_msg"));
                if (jsonObject.getInt("error_code") == -19){
                    map.put("vcode", jsonObject.getString("vcode"));
                    map.put("url", jsonObject.getString("img"));
                }
            }else{
                map.put("msg", "succeed");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
    public void getVCode(Context context, Bitmap bmp){
        vCodeDialog = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        final View vCodeView = factory.inflate(R.layout.vcode, null);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("验证码");
        builder.setView(vCodeView);
        ImageView vCodeImg = (ImageView)vCodeView.findViewById(R.id.vcodeBmp);
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
