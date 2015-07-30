package com.example.bcloud;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
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
        map.put("Cache-control", "max-age=0");
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        Log.d("shanlihou", req.getHeader().get("Set-Cookie" ).toString());
        cookie.loadList(req.getHeader().get("Set-Cookie"));
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
        String url = PASSPORT_URL + "?loginhistory" + "&token=" + tokens.get("token") +
                "&tpl=pp&apiver=v3" + "&tt=" + System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        map.put("Referer", REFERER);
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        Log.d("shanlihou", req.getHeader().get("Set-Cookie" ).toString());
        cookie.loadList(req.getHeader().get("Set-Cookie"));
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
        cookie.loadList(req.getHeader().get("Set-Cookie"));
        Log.d("shanlihou", req.getHeader().get("Set-Cookie").toString());
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
            code.put("pubkey", json.get("pubkey").toString());
            code.put("key", json.get("key").toString());
        }catch (JSONException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    public int postLogin(Cookie cookie, Map<String, String> tokens, Map<String, String> code, String username,
                         String password){
        String url = PASSPORT_LOGIN;
        String data = "staticpage=https%3A%2F%2Fpassport.baidu.com%2Fstatic%2Fpasspc-account%2Fhtml%2Fv3Jump.html" +
                "&charset=UTF-8" + "&token=" + tokens.get("token") + "&tpl=pp&subpro=&apiver=v3" +
                "&tt=" + System.currentTimeMillis() + "&codestring=" + code.get("codeString") +
                "&safeflg=0&u=http%3A%2F%2Fpassport.baidu.com%2F" + "&isPhone=" +
                "&quick_user=0&logintype=basicLogin&logLoginType=pc_loginBasic&idc=" + "&loginmerge=true" +
                "&username=" + URLEncoder.encode(username) +
//                "&password=" + URLEncoder.encode(new String(encPass)) + "&verifycode=";
                "&password=" + URLEncoder.encode(encrypt(code.get("pubkey"), password)) + "&verifycode=";
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
        Map<String, String> info = req.getInfo();
        cookie.loadList(req.getHeader().get("Set-Cookie"));
        return 0;
    }

    public int get_bdsToken(Cookie cookie, Map<String, String> tokens){
        String url = "http://pan.baidu.com/disk/home";
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        int index = req.getContent().indexOf("BDSTOKEN");
        Log.d("shanlihou", index + "");
        int start = req.getContent().indexOf('\"', index);
        int end = req.getContent().indexOf('\"', start + 1);
        String bdstoken = req.getContent().substring(start + 1, end);
        Log.d("shanlihou", bdstoken);
        tokens.put("bdstoken", bdstoken);
        return 0;
    }



    public String encrypt(String pubKey, String strToEnc){
        byte[] encPass = null;
        String basePass = null;
        try
        {
            int index = pubKey.indexOf("-----END PUBLIC KEY-----");
            String tmpStr = pubKey.substring(26, index);
            Log.d("shanlihou", pubKey);
            Log.d("shanlihou", tmpStr);
            String tmpFinal = tmpStr.replaceAll("\n", "");
            Log.d("shanlihou", tmpFinal);

            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    new BASE64Decoder().decodeBuffer(tmpFinal));
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    new  BASE64Decoder().decodeBuffer(tmpFinal));
            Log.d("shanlihou", new BASE64Decoder().decodeBuffer(tmpFinal).toString().length() + "");
            printBytes(new BASE64Decoder().decodeBuffer(tmpFinal));
            printBytes(new BASE64Decoder().decodeBuffer(tmpStr));
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PublicKey publicKey = keyFactory.generatePublic(bobPubKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            Log.d("shanlihou", strToEnc.getBytes().length + strToEnc);
            encPass = cipher.doFinal(strToEnc.getBytes());
            printBytes(encPass);
            basePass = new BASE64Encoder().encodeBuffer(encPass).toString();

            Log.d("shanlihou", basePass);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return basePass;
    }
    public String decrypt(String privateKey, String strToEnc){
        byte[] encPass = null;
        String basePass = null;
        try
        {
            Log.d("shanlihou", privateKey);

            int index = privateKey.indexOf("-----END PUBLIC KEY-----");
            String tmpStr = privateKey.substring(26, index);
            Log.d("shanlihou", tmpStr);
            String tmpFinal = tmpStr.replaceAll("\n", "");
            Log.d("shanlihou", tmpFinal);

            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                    new  BASE64Decoder().decodeBuffer(tmpFinal));
            Log.d("shanlihou", new BASE64Decoder().decodeBuffer(tmpFinal).toString().length() + "");
            printBytes(new BASE64Decoder().decodeBuffer(tmpFinal));
            printBytes(new BASE64Decoder().decodeBuffer(tmpStr));
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance("RSA");
            // 取公钥匙对象
            PrivateKey privateKey1 = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey1);
            encPass = cipher.doFinal(new BASE64Decoder().decodeBuffer(strToEnc));
            printBytes(encPass);
            Log.d("shanlihou", new String(encPass));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    public void printBytes(byte[] encPass){
        String print = new String();
        for (int i:encPass){
            print = print + i + " ";
        }
        Log.d("shanlihou", encPass.length + "");
        Log.d("shanlihou", print);
    }
    public int getSignVcode(){
        return 0;
    }

}
