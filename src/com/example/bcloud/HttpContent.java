package com.example.bcloud;

import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-25.
 */
public class HttpContent {
    private Map<String, List<String>> headers;
    private String content;
    public HttpContent(Map<String, List<String>> headers, String content){
        this.headers = new HashMap<String, List<String>>(headers);
        this.content = content;
    }
    public Map<String, List<String>> getHeader(){
        return headers;
    }

    public String getContent(){
        return content;
    }

    public Map<String, String> getInfo(){
        int start = content.indexOf("\"err_no");
        int end = content.indexOf('"', start + 1);
        Map<String, String> map = new HashMap<>();
        Log.d("shanlihou", start + ":" + end);
        Log.d("shanlihou", content.substring(start + 1, end));
        String subStr = content.substring(start + 1, end);
        int st = 0, mid = -1, en = -1;
        String key, value;

        while(true){
            mid = subStr.indexOf('=', st);
            if (mid == -1){
                break;
            }
            key = subStr.substring(st, mid);
            en = subStr.indexOf('&', mid);
            if (en == -1){
                value = subStr.substring(mid + 1);
                map.put(key, value);
                break;
            }
            else{
                value = subStr.substring(mid + 1, en);
                map.put(key, value);
                st = en + 1;
            }
        }/*
        for (Map.Entry<String, String> entry: map.entrySet()){
            Log.d("shanlihou", "key:" + entry.getKey());
            Log.d("shanlihou", "value:" + entry.getValue());
        }*/

        return map;
    }
}
