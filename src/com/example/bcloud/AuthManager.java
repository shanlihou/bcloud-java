package com.example.bcloud;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.crypto.Cipher;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.security.PublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shanlihou on 15-4-22.
 */
public class AuthManager {
    private static final String PASSPORT_BASE = "https://passport.baidu.com/";
    private static final String PASSPORT_URL = PASSPORT_BASE + "v2/api/";
    private static final String REFERER = PASSPORT_BASE + "v2/?login";
    private static final String PASSPORT_LOGIN = PASSPORT_BASE + "v2/api/?login";
    private static final String ACCEPT_HTML = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
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
    public List<String> getBaiduId(){
        Long tsLong = System.currentTimeMillis();
        String url = PASSPORT_URL + "?getapi&tpl=mn&apiver=v3" + "&tt=" + tsLong.toString() +
                "&class=login&logintype=basicLogin";
        Map<String, String> map = new HashMap<>();
        map.put("Referer", "");
        Log.d("shanlihou", "get baidu id ," + url);
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        Log.d("shanlihou", req.getHeader().get("Set-Cookie").toString());

        return req.getHeader().get("Set-Cookie");
    }
    public int getToken(Cookie cookie, Map<String, String> tokens){
        Long tsLong = System.currentTimeMillis();
        String url = PASSPORT_URL + "?getapi&tpl=pp&apiver=v3" + "&tt=" + tsLong.toString() +
                "&class=login&logintype=basicLogin";

        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        map.put("Accept", ACCEPT_HTML);
        map.put("Cache-control", "max-age=0" );
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        Log.d("shanlihou", req.getHeader().get("Set-Cookie" ).toString());
        cookie.loadList(req.getHeader().get("Set-Cookie" ));
        try {
            JSONTokener jsonParser = new JSONTokener(req.getContent());
            JSONObject json = (JSONObject)jsonParser.nextValue();
            Log.d("shanlihou", ((JSONObject)json.get("data")).get("token").toString());
            tokens.put("token", ((JSONObject)json.get("data")).get("token").toString());
        }catch (JSONException ex){
            ex.printStackTrace();
        }

        return 0;
    }
    public int getUbi(Cookie cookie, Map<String, String> tokens){
        String url = PASSPORT_URL + "?loginhistory" + "&token=" + tokens.get("token" ) +
                "&tpl=pp&apiver=v3" + "&tt=" + System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        map.put("Referer", REFERER);
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        Log.d("shanlihou", req.getHeader().get("Set-Cookie" ).toString());
        cookie.loadList(req.getHeader().get("Set-Cookie" ));
        cookie.logCookie();
        return 0;
    }

    public int checkLogin(Cookie cookie, Map<String, String> tokens, String username,
                          Map<String, String> code){
        String url = PASSPORT_URL + "?logincheck" + "&token=" + tokens.get("token") +
                "&tpl=mm&apiver=v3" + "&tt=" + System.currentTimeMillis() + "&username=" +
                URLEncoder.encode(username) + "&isphone=false";
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        map.put("Referer", REFERER);
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        cookie.loadList(req.getHeader().get("Set-Cookie" ));
        Log.d("shanlihou", req.getHeader().get("Set-Cookie" ).toString());
        Log.d("shanlihou", url);
        try {
            JSONTokener jsonParser = new JSONTokener(req.getContent());
            JSONObject json = (JSONObject)jsonParser.nextValue();
            code.put("codeString", ((JSONObject) json.get("data")).get("codeString").toString());
            code.put("vcodeType", ((JSONObject) json.get("data")).get("vCodeType").toString());
        }catch (JSONException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    public int getPublicKeyString(Cookie cookie, Map<String, String> tokens, Map<String, String> code){
        String url = PASSPORT_BASE + "v2/getpublickey" + "?token=" + tokens.get("token") +
                "&tpl=pp&apiver=v3&tt=" + System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        map.put("Referer", REFERER);
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        try {
            JSONTokener jsonParser = new JSONTokener(req.getContent());
            JSONObject json = (JSONObject)jsonParser.nextValue();
            code.put("pubkey", json.get("pubkey" ).toString());
            code.put("key", json.get("key" ).toString());
        }catch (JSONException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    public int postLogin(Cookie cookie, Map<String, String> tokens, Map<String, String> code, String username,
                         String password){
        String url = PASSPORT_LOGIN;
        byte[] encPass = null;
        String basePass = null;
        try
        {
            int index = code.get("pubkey").indexOf("-----END PUBLIC KEY-----");
            String tmpStr = code.get("pubkey").substring(26, index);
            Log.d("shanlihou", code.get("pubkey"));
            Log.d("shanlihou", tmpStr);
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(tmpStr));
            Log.d("shanlihou", new BASE64Decoder().decodeBuffer(tmpStr).toString().length() + "");
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encPass = cipher.doFinal(password.getBytes());
            basePass = new BASE64Encoder().encodeBuffer(encPass).toString();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d("shanlihou", username);
        Log.d("shanlihou", new String(encPass));

        String data = "staticpage=https%3A%2F%2Fpassport.baidu.com%2Fstatic%2Fpasspc-account%2Fhtml%2Fv3Jump.html" +
                "&charset=UTF-8" + "&token=" + tokens.get("token") + "&tpl=pp&subpro=&apiver=v3" +
                "&tt=" + System.currentTimeMillis() + "&codestring=" + code.get("codeString") +
                "&safeflg=0&u=http%3A%2F%2Fpassport.baidu.com%2F" + "&isPhone=" +
                "&quick_user=0&logintype=basicLogin&logLoginType=pc_loginBasic&idc=" + "&loginmerge=true" +
                "&username=" + URLEncoder.encode(username) +
//                "&password=" + URLEncoder.encode(new String(encPass)) + "&verifycode=";
                "&password=" + URLEncoder.encode(basePass) + "&verifycode=";
        if (code.get("verifyCode") != null){
            data += code.get("verifyCode");
        }
        data += "&mem_pass=on" + "&rsakey=" + code.get("key") + "&crypttype=12" + "&ppui_logintime=" +
                (int)(Math.random() * 6536 + 52000) + "&callback=parent.bd__pcbs__28g1kg";
        Log.d("shanlihou", data + "\n");
        Map<String, String> map = new HashMap<>();
        List<String> subList = new ArrayList<>();
        subList.add("BAIDUID" );
        subList.add("HOSUPPORT" );
        subList.add("UBI" );
        map.put("Cookie", cookie.getSubHeader(subList));
        map.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" );
        map.put("Referer", REFERER);
        map.put("Connection", "Keep-Alive" );
        HttpContent req = UrlOpener.getInstance().urlPost(url, map, data);
        req.getInfo();
        return 0;
    }
    public int getSignVcode(){
        return 0;
    }
}
