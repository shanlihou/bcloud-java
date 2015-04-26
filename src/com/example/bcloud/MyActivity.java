package com.example.bcloud;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
    private EditText editUser, editPass;
    private Thread thread;
    private String mUserName;
    private String mPassWord;
    private Cookie cookie;
    private Map<String, String> tokens;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnLogin = (Button)findViewById(R.id.loginButton);
        editUser = (EditText)findViewById(R.id.userEdit);
        editPass = (EditText)findViewById(R.id.passEdit);
        editUser.setText("分是否收费");
        editPass.setText("410015216");
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserName = editUser.getText().toString();
                mPassWord = editPass.getText().toString();
                thread.start();
            }
        });
        Log.d("shanlihou", "hello" );
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                cookie = new Cookie();
                tokens = new HashMap<String, String>();
                Map<String, String> code = new HashMap<>();
                List baiduid = AuthManager.getInstance().getBaiduId();
                cookie.loadList(baiduid);
                AuthManager.getInstance().getToken(cookie, tokens);
                Log.d("shanlihou", tokens.toString());
                cookie.add("cflag=65535%3A1;" );
                cookie.add("PANWEB=1;" );
                Log.d("shanlihou", "get ubi" );
                AuthManager.getInstance().getUbi(cookie, tokens);
                Log.d("shanlihou", "checklogin" );
                AuthManager.getInstance().checkLogin(cookie, tokens, mUserName, code);
                Log.d("shanlihou", "get public" );
                AuthManager.getInstance().getPublicKeyString(cookie, tokens, code);
                AuthManager.getInstance().postLogin(cookie, tokens, code, mUserName, mPassWord);
            }
        });
    }

}
