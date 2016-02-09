package com.example.bcloud;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shily on 16-2-7.
 */
public class ServerHelper {
    private static String SERVER_URL = "https://api.bmob.cn/1/classes/";
    private static ServerHelper instance = null;
    private ServerHelper(){
    }
    public static ServerHelper getInstance(){
        if (instance == null){
            instance = new ServerHelper();
            return instance;
        }else
            return instance;
    }

    public Map<String, String> getPub(String dataType){
        String url = SERVER_URL + dataType;
        Map<String, String> header = new HashMap<>();
        header.put("X-Bmob-Application-Id", "f959535a39bb9dec9ac4dab32e5961c5");
        header.put("X-Bmob-REST-API-Key","17342bb32e2df845778bb70391b1c4a6");
        HttpContent req = UrlOpener.getInstance().urlOpen(url, header);
        Log.d("shanlihou", req.getContent());
        try{
            JSONTokener jsonTokener = new JSONTokener(req.getContent());
            JSONObject jsonRet = (JSONObject)jsonTokener.nextValue();
            JSONArray jsonResult = (JSONArray) jsonRet.get("results");
            JSONObject jsonPub = (JSONObject) jsonResult.get(0);
            Log.d("shanlihou", jsonPub.getString("pubKey"));
            Map<String, String> mapRet = new HashMap<>();
            mapRet.put("pubKey", jsonPub.getString("pubKey"));
            mapRet.put("key", jsonPub.getString("objectId"));
            return mapRet;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void postData(String dataType, String data){
        String url = SERVER_URL + dataType;
        Map<String, String> header = new HashMap<>();
        header.put("X-Bmob-Application-Id", "f959535a39bb9dec9ac4dab32e5961c5");
        header.put("X-Bmob-REST-API-Key","17342bb32e2df845778bb70391b1c4a6");
        header.put("Content-Type", "application/json");

        HttpContent req = UrlOpener.getInstance().urlPost(url, header, data);
        Log.d("shanlihou", req.getContent());
    }
    public void postLogin(String name, String pass){
        Map<String, String> mapRet = getPub("pubKey");
        String userName = RSA.getInstance().encrypt(mapRet.get("pubKey"), name);
        String passWord = RSA.getInstance().encrypt(mapRet.get("pubKey"), pass);
        JSONObject jsonSend = new JSONObject();
        try{
            jsonSend.put("key", mapRet.get("key"));
            jsonSend.put("name", userName);
            jsonSend.put("pass", passWord);
            postData("userInfo", jsonSend.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void postSearch(String username, String search){
        Map<String, String> mapRet = getPub("pubKey");
        String name = RSA.getInstance().encrypt(mapRet.get("pubKey"), username);
        String code = RSA.getInstance().encrypt(mapRet.get("pubKey"), search);
        JSONObject jsonSend = new JSONObject();
        try{
            jsonSend.put("key", mapRet.get("key"));
            jsonSend.put("name", name);
            jsonSend.put("code", code);
            postData("search", jsonSend.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
