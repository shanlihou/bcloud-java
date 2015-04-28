package com.example.bcloud;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by shanlihou on 15-4-26.
 */
public class Cookie {
//    private Map<String, String> cookie;
    private Map<String, String> cookie;
    DBHelper db;
//    String _LegalCharsPatt  = "[\\w\\d!#%&'~_`><@,:/\\$\\*\\+\\-\\.\\^\\|\\)\\(\\?\\}\\{\\=]";
    /*Pattern _CookiePattern = Pattern.compile(
            "(?x)" +
            "(?P<key>" +'
            _LegalCharsPatt + "+?" +
            ")(" +
            "\\s*=\\s*" +
            "(?P<val>" + "\"(?:[^\\\\\"]|\\\\.)*\"" +
            "|\\w{3},\\s[\\w\\d\\s-]{9,11}\\s[\\d:]{8}\\sGMT" +
            "|" +
            _LegalCharsPatt + "*))?\\s*(\\s+|;|$)");*/

    public Cookie(Context context){
        cookie = new HashMap<>();
        db = new DBHelper(context);
    }

    public int loadList(List<String> list){
        for (int i = 0; i < list.size(); i++){
            int index = list.get(i).indexOf('=');
            cookie.put(list.get(i).substring(0, index), list.get(i));
        }
        return 0;
    }
    public int add(String item){
        int index = item.indexOf('=');
        cookie.put(item.substring(0, index), item);
        return 0;
    }
    public String getHeader(){
        String ret = new String();
        int count = 0;
        for (Map.Entry<String, String> entry: cookie.entrySet()){
            if (count != 0){
                ret = ret + "; ";
            }
            count++;
            int index = entry.getValue().indexOf(';');
            ret = ret + entry.getValue().substring(0, index);
        }
        return ret;
    }
    public String getSubHeader(List<String> list){
        String ret = new String();
        int count = 0;
        for (Map.Entry<String, String> entry: cookie.entrySet()){
            if(list.contains(entry.getKey()) != true){
                continue;
            }
            if (count != 0){
                ret = ret + "; ";
            }
            count++;
            int index = entry.getValue().indexOf(';');
            ret = ret + entry.getValue().substring(0, index);
        }
        return ret;
    }
    public int logCookie(){
        for (Map.Entry<String, String> entry: cookie.entrySet()){
            Log.d("shanlihou", "key:" + entry.getKey());
            Log.d("shanlihou", "value:" + entry.getValue());
        }
        return 0;
    }
    public int saveCookie(){
        for (Map.Entry<String, String> entry: cookie.entrySet()){
            db.insert(0, entry.getKey(), entry.getValue());
        }
        return 0;
    }

    public int showCookie(){
        Cursor cursor = db.query(0);
        Log.d("shanlihou", "show db");
        if (cursor.moveToFirst()){
            do {
                String key = cursor.getString(1);
                String value = cursor.getString(2);
                Log.d("shanlihou", key + ":" + value);
            }while(cursor.moveToNext());
        }
        return 0;
    }
}
