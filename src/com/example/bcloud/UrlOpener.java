package com.example.bcloud;


import android.util.Log;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-22.
 */
public class UrlOpener {
    private static UrlOpener instance = null;
    private static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64; rv:31.0) Gecko/20100101 Firefox/31.0 Iceweasel/31.2.0";
    private static final String PAN_REFERER =
            "http://pan.baidu.com/disk/home";
    private static final String ACCEPT_JSON =
            "application/json, text/javascript, */*; q=0.8";
    private static Map<String, String> map = new HashMap<String, String>();
    private UrlOpener(){
        map.put("User-agent", USER_AGENT);
        map.put("Referer", PAN_REFERER);
        map.put("Accept", ACCEPT_JSON);
        map.put("Accept-language", "zh-cn, zh;q=0.5");
        map.put("Accept-encoding", "gzip, deflate");
        map.put("Pragma", "no-cache");
        map.put("Cache-control", "no-cache");

    }
    public static UrlOpener getInstance(){
        if (instance == null) {
            instance = new UrlOpener();
            return new UrlOpener();
        }
        else
            return instance;
    }
    public int urlOpen(String strUrl, Map<String, String> map){
        try {
            URL url = new URL(strUrl);
            Map<String, String> newMap = new HashMap<String, String>();
            newMap.putAll(this.map);
            newMap.putAll(map);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setDoOutput(false);
            urlConn.setDoInput(true);
        //    urlConn.setUseCaches(false);
            for (Map.Entry<String, String>entry : newMap.entrySet()) {
                urlConn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            Log.d("shanlihou", "urlopen");
            urlConn.connect();
            Log.d("shanlihou", "urlopen" + urlConn.getResponseCode());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            Log.d("shanlihou", "urlopen");
            String result = "";
            String readLine = null;
            while((readLine = bufferedReader.readLine()) != null){
                result = readLine;
            }
            Log.d("shanlihou", "urlopen");
            bufferedReader.close();
            Log.d("shanlihou", "urlopen");
            urlConn.disconnect();
            Log.d("shanlihou", "urlopen");


        }catch(Exception e){
            Log.d("shanlihou", "print start\n");
            e.printStackTrace();

            Log.d("shanlihou", "print end\n" + e.getMessage());
        }
        return 0;
    }

}
