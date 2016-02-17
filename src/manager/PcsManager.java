package manager;

import android.graphics.Bitmap;
import android.util.Log;
import com.example.bcloud.Cookie;
import com.example.bcloud.HttpContent;
import com.example.bcloud.UrlOpener;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;
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
            return instance;
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

    public List<String> queryMagnetInfo(Cookie cookie, Map<String, String> tokens, String sourceUrl, String savePath){
        String url = PAN_URL + "rest/2.0/services/cloud_dl?channel=chunlei&clienttype=0&web=1" +
                "&bdstoken=" + tokens.get("bdstoken");
        String data = "";
        data = "method=query_magnetinfo&app_id=250528" + "&source_url=" + URLEncoder.encode(sourceUrl) +
                "&save_path=" + URLEncoder.encode(savePath) + "&type=4";
        Log.d("shanlihou", data);
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        HttpContent req = UrlOpener.getInstance().urlPost(url, map, data);
        List<String> selectList = new ArrayList<>();
        try {
            JSONTokener jsonTokener = new JSONTokener(req.getContent());
            JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
            if (!jsonObject.isNull("error_code")){
                selectList.add("error");
                selectList.add("" + jsonObject.getInt("error_code"));
                selectList.add(jsonObject.getString("error_msg"));
                return selectList;
            }
            JSONArray jsonArray = (JSONArray)jsonObject.get("magnet_info");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject tmp = (JSONObject)jsonArray.get(i);
                String filename = tmp.getString("file_name");
                if (filename.endsWith(".mp4")
                        || filename.endsWith(".wmv")
                        || filename.endsWith(".avi")
                        || filename.endsWith(".rmvb")
                        || filename.endsWith(".flv")
                        || filename.endsWith(".mkv")) {
                    selectList.add((i + 1) + "");
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return selectList;
    }
    public String addBtTask(Cookie cookie, Map<String, String> tokens, String sourceUrl,
                          String savePath, String strIds, String file_sha1,
                          String vcode, String vcodeInput){
        String url = PAN_URL + "rest/2.0/services/cloud_dl?channel=chunlei&clienttype=0&web=1" +
                "&bdstoken=" + tokens.get("bdstoken");

        String data = "method=add_task&app_id=250528" + "&file_sha1=" + file_sha1 +
                "&save_path=" + URLEncoder.encode(savePath) + "&selected_idx=" + strIds +
                "&task_from=1" + "&t=" + System.currentTimeMillis() + "&" + "source_url" +
                "=" + URLEncoder.encode(sourceUrl) + "&type=4";
        if (!vcode.equals("")){
            data += "&input=" + vcodeInput + "&vcode=" + vcode;
        }
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        HttpContent req = UrlOpener.getInstance().urlPost(url, map, data);
        return req.getContent();
    }
    public Bitmap getVcodeBmp(String url, Cookie cookie){
        Map<String, String> map = new HashMap<>();
        map.put("Cookie", cookie.getHeader());
        HttpContent req = UrlOpener.getInstance().urlOpen(url, map);
        return UrlOpener.getInstance().convertStringToIcon(req.getContent());
    }
}
