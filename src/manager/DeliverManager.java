package manager;

import android.util.Log;
import com.example.bcloud.MainApplication;
import com.example.bcloud.ServerHelper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-28.
 */
public class DeliverManager {
    private static DeliverManager instance = null;

    private DeliverManager() {
        init();
    }

    public static Map<String, String> cookie = new HashMap<>();
    public static Map<String, String> tokens = new HashMap<>();
    public static Map<String, String> preference = new HashMap<>();

    public static DeliverManager getInstance() {
        if (instance == null) {
            instance = new DeliverManager();
            return instance;
        } else
            return instance;
    }

    private void init() {
        loadPreference();
    }

    private void loadPreference() {
        String first = MainApplication.getInstance().getShared("first");
        if (first != null) {
            preference.put("searchServer", MainApplication.getInstance().getShared("searchServer"));
        } else {
            preference.put("searchServer", "www.btaia.com");
            MainApplication.getInstance().setShared("first", "1");
            MainApplication.getInstance().setShared("searchServer", "www.btaia.com");
        }
    }
    public void setPreference(){
        MainApplication.getInstance().setShared("searchServer", preference.get("searchServer"));
    }

    public Map<String, String> getPreference() {
        String ret = ServerHelper.getInstance().getData("preference");
        Map<String, String> map = new HashMap<>();
        if (ret != null) {
            Log.d("shanlihou", ret);
            JSONTokener jsonTokener = new JSONTokener(ret);
            try {
                JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                JSONArray jsonResult = (JSONArray) jsonObject.get("results");
                JSONObject jsonPrefer = jsonResult.getJSONObject(0);
                String temp;
                if (jsonPrefer.has("searchServer")){
                    temp = jsonPrefer.getString("searchServer");
                    preference.put("searchServer", temp);
                }
                if (jsonPrefer.has("versionCode")){
                    temp = jsonPrefer.getString("versionCode");
                    map.put("versionCode", temp);
                }
                if (jsonPrefer.has("apkUrl")){
                    temp = jsonPrefer.getString("apkUrl");
                    map.put("apkUrl", temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
