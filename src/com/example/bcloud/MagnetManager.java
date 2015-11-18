package com.example.bcloud;

import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-29.
 */
public class MagnetManager {
    private static MagnetManager instance = null;
    private MagnetManager(){
    }
    public static MagnetManager getInstance(){
        if (instance == null) {
            instance = new MagnetManager();
            return new MagnetManager();
        }
        else
            return instance;
    }
    public List<Map<String, String>> getHomePage(String code){
        List<Map<String , String>> ret = new ArrayList<>();
        int index = 0, start, end;
        int flag = 0;
        String url = "http://www.bt2mag.com/search/" + URLEncoder.encode(code);
        HttpContent req = UrlOpener.getInstance().urlOpen(url, null);
        String pat = "http://www.bt2mag.com/magnet/detail/hash/";
        String sizePat = ">Size:";
        String titlePat = " title=\"";
        if (req == null || req.getContent() == null) {
            return ret;
        }
        int count = 0;
        while(true){
            start = req.getContent().indexOf(pat, index);
            if (start == -1){
                break;
            }
            end = req.getContent().indexOf('\"', start);
            if (flag == 1){
                flag = 0;
                index = end;
                continue;
            }
            Map<String, String>map = new HashMap<>();
            map.put("magUrl", getMagnet(req.getContent().substring(start, end)));

            start = req.getContent().indexOf(titlePat, end);
            start += titlePat.length();
            end = req.getContent().indexOf('"', start);
            count ++;
            map.put("magTitle", count + req.getContent().substring(start, end));
            Log.d("shanlihou", count + req.getContent().substring(start, end));

            start = req.getContent().indexOf(sizePat, end);
            start += sizePat.length();
            end = req.getContent().indexOf(' ', start);
            map.put("magSize", req.getContent().substring(start, end));

            index = end;
            //flag = 1;
            ret.add(map);
        }
        return ret;
    }

    public String getMagnet(String url){
        HttpContent req = UrlOpener.getInstance().urlOpen(url, null);
        String pat = "href=\"magnet:?xt=urn:btih:";
        int start = req.getContent().indexOf(pat, 0);
        start += 6;
        int end = req.getContent().indexOf('\"', start);

        return req.getContent().substring(start, end);
    }
}
