package com.example.bcloud;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;

import javax.net.ssl.*;
import java.io.*;
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
            URL url = new URL(strUrl);
            Map<String, String> newMap = new HashMap<String, String>();
            newMap.putAll(this.map);
            if (map != null){
                newMap.putAll(map);
            }
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
    public Bitmap getImageBmp(String strUrl){
        Bitmap bitmap = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setDoOutput(false);
            urlConn.setDoInput(true);
            urlConn.connect();
            InputStream is = urlConn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            urlConn.disconnect();
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    /*
    public Uri getImageURI(String path, File cache) throws Exception {

        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == 200) {

                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }*/

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
