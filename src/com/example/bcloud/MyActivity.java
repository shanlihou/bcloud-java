package com.example.bcloud;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import manager.DeliverManager;
import manager.ImageManager;
import manager.UpdateAppManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private Handler mHandler;
    private final int USERNAME = 0;
    private final int PASSWORD = 1;
    private Button btnLogin;
    private Button btnList;
    private Button btnSow;
    private Button btnFloat;

    private EditText editUser, editPass;
    private TextView tLoginState;
    private View vUser, vPass;
    private String mUserName;
    private String mPassWord;
    private Map<String, String> tokens;
    private Button btnMag;
    private Runnable loginRun = null;
    private Runnable getLoginRun = null;
    private Runnable initRun = null;
    private Context mContext;
    private int versionCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tokens = new HashMap<String, String>();
        mContext = this;

        Cookie.getInstance().loadAll(tokens);
        Cookie.getInstance().getMap(DeliverManager.getInstance().cookie);
        DeliverManager.getInstance().tokens.clear();
        DeliverManager.getInstance().tokens.putAll(tokens);

        btnLogin = (Button)findViewById(R.id.loginButton);
        btnList = (Button)findViewById(R.id.listButton);
        btnSow = (Button)findViewById(R.id.sowGrid);
        btnMag = (Button)findViewById(R.id.magTest);
        btnFloat = (Button)findViewById(R.id.floatButton);

        editUser = (EditText)findViewById(R.id.userEdit);
        editPass = (EditText)findViewById(R.id.passEdit);

        tLoginState = (TextView)findViewById(R.id.tLoginState);
        editPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        vUser = findViewById(R.id.vUser);
        vPass = findViewById(R.id.vPass);

        editUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ViewGroup.LayoutParams params = vUser.getLayoutParams();
                if (hasFocus == true){
                    params.height *= 4;
                }else{
                    params.height /= 4;
                }
                vUser.setLayoutParams(params);
            }
        });
        editPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ViewGroup.LayoutParams params = vPass.getLayoutParams();
                if (hasFocus == true){
                    params.height *= 4;
                }else{
                    params.height /= 4;
                }
                vPass.setLayoutParams(params);
            }
        });

        ImageManager.getInstance().mkdir("/sdcard/bcloud/sowImage");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = editUser.getText().toString();
                mPassWord = editPass.getText().toString();
                tLoginState.setText("登录中...");
                new Thread(loginRun).start();
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
                Intent intent = new Intent(MyActivity.this, BTActivity.class);
                startActivity(intent);
            }
        });
        btnList.setEnabled(false);

        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyActivity.this, FxService.class);
                startService(intent);
                finish();
            }
        });

        btnSow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyActivity.this, SowActivity.class);
                startActivity(intent);
            }
        });

        btnMag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MyActivity.this, MagnetActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("code", "");//压入数据
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
        init();
        Log.d("shanlihou", "hello");
        Log.d("shanlihou",  getApplicationContext().getFilesDir().getAbsolutePath());
        tLoginState.setText("获取登录状态中...");
        /*test*/
//        String pubKey = "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwfczbrS0ZW5r+yParkgkxOrPG\ncpQnZ2Th4HzDXwoH/9O/fw7Hsr459QlEuhK6iro2e1a7OD+Si1Lq+gYr7DZ2g3WR\n6XKUBnwNgXn6aflOLpqawgrVH/j8JENvsgnwzVGbCY8vLaEgC9fRJyK5AcH9X5OO\nfPnnHmxbfoS6uBpcCwIDAQAB\n-----END PUBLIC KEY-----";
//        AuthManager.getInstance().encrypt(pubKey, "410015216");
        /*test*/

    }

    private void init(){
        handlerInit();
        runInit();
        new Thread(getLoginRun).start();
        new Thread(initRun).start();
    }
    private void handlerInit(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        String username = (String)msg.obj;
                        if (username == null)
                            tLoginState.setText("未登录");
                        else {
                            tLoginState.setText(username);
                            btnList.setEnabled(true);
                        }
                        break;
                    case 1:
                        Map<String, String> map = (Map<String, String>)msg.obj;
                        DeliverManager.getInstance().setPreference();
                        if (map != null){
                            if (map.containsKey("versionCode")){
                                String temp = map.get("versionCode");
                                int code = Integer.parseInt(temp);
                                if (code > getVersionCode()){
                                    UpdateAppManager up = new UpdateAppManager(mContext);
                                    up.setSpec(map.get("apkUrl"));
                                    up.checkUpdateInfo();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void runInit(){
        loginRun = new Runnable() {
            @Override
            public void run() {
                Cookie.getInstance().clear();
                tokens.clear();
                Map<String, String> code = new HashMap<>();
                List baiduid = AuthManager.getInstance().getBaiduId();
                Cookie.getInstance().loadList(baiduid);
                AuthManager.getInstance().getToken(Cookie.getInstance(), tokens);
                Log.d("shanlihou", tokens.toString());
                Cookie.getInstance().add("cflag=65535%3A1;");
                Cookie.getInstance().add("PANWEB=1;");
                Log.d("shanlihou", "get ubi");
                AuthManager.getInstance().getUbi(Cookie.getInstance(), tokens);
                Log.d("shanlihou", "checklogin");
                AuthManager.getInstance().checkLogin(Cookie.getInstance(), tokens, mUserName, code);
                Log.d("shanlihou", "get public");
                AuthManager.getInstance().getPublicKeyString(Cookie.getInstance(), tokens, code);
                AuthManager.getInstance().postLogin(Cookie.getInstance(), tokens, code, mUserName, mPassWord);
                AuthManager.getInstance().get_bdsToken(Cookie.getInstance(), tokens);
                Cookie.getInstance().saveAll(tokens);
                Cookie.getInstance().getMap(DeliverManager.getInstance().cookie);
                DeliverManager.getInstance().tokens.clear();
                DeliverManager.getInstance().tokens.putAll(tokens);
                String runInfo = AuthManager.getInstance().getBaiduLogin(Cookie.getInstance());
                Log.d("shanlihou", runInfo);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = runInfo;
                mHandler.sendMessage(msg);
            }
        };
        getLoginRun = new Runnable() {
            @Override
            public void run() {
                String runInfo = AuthManager.getInstance().getBaiduLogin(Cookie.getInstance());
                if (runInfo != null)
                    Log.d("shanlihou", runInfo);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = runInfo;
                mHandler.sendMessage(msg);
            }
        };
        initRun = new Runnable() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage();
                message.obj = DeliverManager.getInstance().getPreference();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        };
    }

    private int getVersionCode(){
        int code = 0;
        try {
            code = this.getPackageManager().getPackageInfo("com.example.bcloud", 0).versionCode;

        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return code;
    }

}
