package com.example.bcloud;


import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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
    public HttpContent urlOpen(String strUrl, Map<String, String> map){
        HttpContent ret = null;
        Log.d("shanlihou", strUrl);
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
            URL url = new URL(strUrl);
            Map<String, String> newMap = new HashMap<String, String>();
            newMap.putAll(this.map);
            newMap.putAll(map);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
//            urlConn.setSSLSocketFactory(sslcontext.getSocketFactory());
            urlConn.setRequestMethod("GET");
            urlConn.setDoOutput(false);
            urlConn.setDoInput(true);
        //    urlConn.setUseCaches(false);
            for (Map.Entry<String, String>entry : newMap.entrySet()) {
                Log.d("shanlihou", entry.getKey() + ":" + entry.getValue());
                urlConn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            urlConn.connect();
            Log.d("shanlihou", "urlopen" + urlConn.getResponseCode());
            String encoding = urlConn.getHeaderField("Content-encoding");
            Log.d("shanlihou", "urlopen");
            String result = "";
            Log.d("shanlihou", "urlopen");
            if (encoding != null && encoding.equals("gzip")){
                int num;
                byte[] tmp = new byte[4096];
                ByteArrayBuffer bt = new ByteArrayBuffer(4096);
                GZIPInputStream gis = new GZIPInputStream(urlConn.getInputStream());
                while((num = gis.read(tmp)) != -1){
                    bt.append(tmp, 0, num);
                }
                result = new String(bt.toByteArray(), "utf-8");
                gis.close();
            }
            else{
                String readLine = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                while((readLine = bufferedReader.readLine()) != null){
                    result = readLine;
                }
                bufferedReader.close();
            }
            Log.d("shanlihou", "urlopen");

            ret = new HttpContent(urlConn.getHeaderFields(), result);
            urlConn.disconnect();
            Log.d("shanlihou", "urlopen:" + result);

        }catch(Exception e){
            Log.d("shanlihou", "print start\n");
            e.printStackTrace();

            Log.d("shanlihou", "print end\n" + e.getMessage());
        }
        return ret;
    }
public HttpContent urlPost(String strUrl, Map<String, String> map, String data){
        HttpContent ret = null;
        Log.d("shanlihou", strUrl);
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);
            URL url = new URL(strUrl);
            Map<String, String> newMap = new HashMap<String, String>();
            newMap.putAll(this.map);
            newMap.putAll(map);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
//            urlConn.setSSLSocketFactory(sslcontext.getSocketFactory());
            urlConn.setRequestMethod("POST");
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
        //    urlConn.setUseCaches(false);
            for (Map.Entry<String, String>entry : newMap.entrySet()) {
                Log.d("shanlihou", entry.getKey() + ":" + entry.getValue());
                urlConn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            urlConn.connect();
            DataOutputStream dop = new DataOutputStream(urlConn.getOutputStream());
            dop.writeBytes(data);
            dop.flush();
            dop.close();
            Log.d("shanlihou", "urlopen" + urlConn.getResponseCode());
            String encoding = urlConn.getHeaderField("Content-encoding");
            Log.d("shanlihou", "urlopen");
            String result = "";
            Log.d("shanlihou", "urlopen");
            if (encoding != null && encoding.equals("gzip")){
                int num;
                byte[] tmp = new byte[4096];
                ByteArrayBuffer bt = new ByteArrayBuffer(4096);
                GZIPInputStream gis = new GZIPInputStream(urlConn.getInputStream());
                while((num = gis.read(tmp)) != -1){
                    bt.append(tmp, 0, num);
                }
                result = new String(bt.toByteArray(), "utf-8");
                gis.close();
            }
            else{
                String readLine = null;
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                while((readLine = bufferedReader.readLine()) != null){
                    result = readLine;
                }
                bufferedReader.close();
            }
            Log.d("shanlihou", "urlopen");

            ret = new HttpContent(urlConn.getHeaderFields(), result);
            urlConn.disconnect();
            Log.d("shanlihou", "urlopen:" + result);

        }catch(Exception e){
            Log.d("shanlihou", "print start\n");
            e.printStackTrace();

            Log.d("shanlihou", "print end\n" + e.getMessage());
        }
        return ret;
    }
    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    };

}

/*
class myX509TrustManager implements X509TrustManager
{

    public void checkClientTrusted(X509Certificate[] chain, String authType)
    {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
    {
        System.out.println("cert: " + chain[0].toString() + ", authType: "
                + authType);
    }

    public X509Certificate[] getAcceptedIssuers()
    {
        return null;
    }
}*/
