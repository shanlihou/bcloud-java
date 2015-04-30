package com.example.bcloud;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shanlihou on 15-4-29.
 */
public class SowManager {
    private static SowManager instance = null;
    private SowManager(){
    }
    public static SowManager getInstance(){
        if (instance == null) {
            instance = new SowManager();
            return new SowManager();
        }
        else
            return instance;
    }
    public List<Map<String, Object>> getPage(int num){
        List<Map<String, Object>> ret = new ArrayList<>();
        int index = 0, start, end;
        String url = "http://www.avsow.com/cn/currentPage/" + num;
        HttpContent req = UrlOpener.getInstance().urlOpen(url, null);
        String imgPat = "_blank\"><img src=\"";
        String codePat = "<br> <span style=\"color:#CC0000;font-size:12px;\">";
        int count = 0;
        while(true) {
            Map<String, Object> map = new HashMap<>();
            start = req.getContent().indexOf(imgPat, index);
            if (start == -1){
                break;
            }
            count ++;
            Log.d("shanlihou", "count:" + count);
            Log.d("shanlihou", "start:" + start);
            start += imgPat.length();
            end = req.getContent().indexOf('\"', start);
            Log.d("shanlihou", req.getContent().substring(start, end));
            map.put("bitmap", UrlOpener.getInstance().getImageBmp(req.getContent().substring(start, end)));
            start = req.getContent().indexOf(codePat, end);
            if(start == -1){
                break;
            }
            start += codePat.length();
            end = req.getContent().indexOf('<', start);
            map.put("code", req.getContent().substring(start, end));
            index = end;
            ret.add(map);
        }
        return ret;
    }
}
