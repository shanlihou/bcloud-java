package com.example.bcloud;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-28.
 */
public class PcsManager {
    private static final String PAN_URL = "http://pan.baidu.com/";
    private static PcsManager instance = null;
    private PcsManager(){
    }
    public static PcsManager getInstance(){
        if (instance == null) {
            instance = new PcsManager();
            return new PcsManager();
        }
        else
            return instance;
    }
    public List<String> listTask(Cookie cookie, Map<String, String> tokens){
        String url = PAN_URL + "rest/2.0/services/cloud_dl?channel=chunlei&clienttype=0&web=1" +
                "&bdstoken=" + tokens.get("bdstoken") + "&need_task_info=1&status=255" + "&start=" +
                0 + "&limit=50&method=list_task&app_id=250528" + "&t=" + System.currentTimeMillis();
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        Log.d("shanlihou", req.getContent());
        List<String> ids = new ArrayList<>();
        try {
            JSONTokener jsonTokener = new JSONTokener(req.getContent());
            JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
            JSONArray taskList = jsonObject.getJSONArray("task_info");
            for (int i = 0; i < taskList.length(); i++){
                ids.add(((JSONObject) taskList.get(i)).get("task_id").toString());
                Log.d("shanlihou", ((JSONObject) taskList.get(i)).get("task_id").toString());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    public void queryTask(Cookie cookie, Map<String, String> tokens, List<String> list, Map<String, List<String>> taskList){
        String idList = "";
        for (int i = 0; i < list.size(); i++){
            if (i != 0){
                idList += ",";
            }
            idList += list.get(i);
        }
        String url = PAN_URL + "rest/2.0/services/cloud_dl?method=query_task&app_id=250528" +
                "&bdstoken=" + tokens.get("bdstoken") + "&task_ids=" + idList + "&t=" +
                System.currentTimeMillis() + "&channel=chunlei&clienttype=0&web=1";
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        try {
            JSONTokener jsonTokener = new JSONTokener(req.getContent());
            JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
            for (String i:list){
                JSONObject jsonTmp = (JSONObject)((JSONObject) jsonObject.get("task_info")).get(i);
                taskList.get("fileName").add(jsonTmp.get("task_name").toString());
                taskList.get("fileSize").add(jsonTmp.get("file_size").toString());
                taskList.get("finishSize").add(jsonTmp.get("finished_size").toString());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
