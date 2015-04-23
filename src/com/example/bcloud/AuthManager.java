package com.example.bcloud;

import android.util.Log;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-22.
 */
public class AuthManager {
    private static final String PASSPORT_BASE = "https://passport.baidu.com/";
    private static final String PASSPORT_URL = PASSPORT_BASE + "v2/api";
    private AuthManager(){

    }
    private static AuthManager instance = null;
    public static AuthManager getInstance(){
        if (instance == null) {
            instance = new AuthManager();
            return new AuthManager();
        }
        else
            return instance;
    }
    public int getBaiduId(){
        Long tsLong = System.currentTimeMillis();
        String url = PASSPORT_URL + "?getapi&tpl=mn&apiver=v3" + "&tt=" + tsLong.toString() +
                "&class=login&logintype=basicLogin";
        Map<String, String> map = new HashMap<>();
        map.put("Referer", "");
        Log.d("shanlihou", "get baidu id");
        UrlOpener.getInstance().urlOpen(url, map);

        return 0;
    }
}
