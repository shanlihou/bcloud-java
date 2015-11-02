package com.example.bcloud;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

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

    private EditText editUser, editPass;
    private String mUserName;
    private String mPassWord;
    private Cookie cookie;
    private Map<String, String> tokens;
    private Activity mActivity;
    private Button btnMag;
    private Runnable loginRun = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mActivity = this;
        cookie = new Cookie(mActivity);
        tokens = new HashMap<String, String>();

        cookie.loadAll(tokens);
        cookie.getMap(DeliverManager.getInstance().cookie);
        DeliverManager.getInstance().tokens.clear();
        DeliverManager.getInstance().tokens.putAll(tokens);

        btnLogin = (Button)findViewById(R.id.loginButton);
        btnList = (Button)findViewById(R.id.listButton);
        btnSow = (Button)findViewById(R.id.sowGrid);
        btnMag = (Button)findViewById(R.id.magTest);

        editUser = (EditText)findViewById(R.id.userEdit);
        editPass = (EditText)findViewById(R.id.passEdit);
        editUser.setText("分是否收费");
        editPass.setText("410015216");
        editPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ImageManager.getInstance().mkdir("/sdcard/bcloud/sowImage");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = editUser.getText().toString();
                mPassWord = editPass.getText().toString();
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
                mBundle.putString("code", "wanz-213");//压入数据
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
        Log.d("shanlihou", "hello");
        Log.d("shanlihou",  getApplicationContext().getFilesDir().getAbsolutePath());
        /*test*/
//        String pubKey = "-----BEGIN PUBLIC KEY-----\nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwfczbrS0ZW5r+yParkgkxOrPG\ncpQnZ2Th4HzDXwoH/9O/fw7Hsr459QlEuhK6iro2e1a7OD+Si1Lq+gYr7DZ2g3WR\n6XKUBnwNgXn6aflOLpqawgrVH/j8JENvsgnwzVGbCY8vLaEgC9fRJyK5AcH9X5OO\nfPnnHmxbfoS6uBpcCwIDAQAB\n-----END PUBLIC KEY-----";
//        AuthManager.getInstance().encrypt(pubKey, "410015216");
        /*test*/

        loginRun = new Runnable() {
            @Override
            public void run() {
                cookie.clear();
                tokens.clear();
                Map<String, String> code = new HashMap<>();
                List baiduid = AuthManager.getInstance().getBaiduId();
                cookie.loadList(baiduid);
                AuthManager.getInstance().getToken(cookie, tokens);
                Log.d("shanlihou", tokens.toString());
                cookie.add("cflag=65535%3A1;");
                cookie.add("PANWEB=1;");
                Log.d("shanlihou", "get ubi");
                AuthManager.getInstance().getUbi(cookie, tokens);
                Log.d("shanlihou", "checklogin");
                AuthManager.getInstance().checkLogin(cookie, tokens, mUserName, code);
                Log.d("shanlihou", "get public");
                AuthManager.getInstance().getPublicKeyString(cookie, tokens, code);
                AuthManager.getInstance().postLogin(cookie, tokens, code, mUserName, mPassWord);
                AuthManager.getInstance().get_bdsToken(cookie, tokens);
                cookie.saveAll(tokens);
                cookie.getMap(DeliverManager.getInstance().cookie);
                DeliverManager.getInstance().tokens.clear();
                DeliverManager.getInstance().tokens.putAll(tokens);
            }
        };
    }



}
