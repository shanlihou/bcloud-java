package com.example.bcloud;

import android.graphics.Bitmap;
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
            return instance;
        }
        else
            return instance;
    }
    public List<Map<String, Object>> getPage(int num){
        List<Map<String, Object>> ret = new ArrayList<>();
        int index = 0, start, end;
        String url = "http://www.javmoo.info/cn/currentPage/" + num;
        HttpContent req = UrlOpener.getInstance().urlOpen(url, null);
        if (req == null || req.getContent() == null){
            return null;
        }
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
            String imgUrl = req.getContent().substring(start, end);
            start = req.getContent().indexOf(codePat, end);
            if(start == -1){
                break;
            }
            start += codePat.length();
            end = req.getContent().indexOf('<', start);
            String code = req.getContent().substring(start, end);
            imgUrl = imgUrl.replace("ps.jpg", "pl.jpg");
            map.put("code", code);
            map.put("bitmap", getImage(imgUrl, code));
            index = end;
            ret.add(map);
        }
        return ret;
    }

    private Bitmap getImage(String url, String code){
        Bitmap ret;
        String filename = code + ".jpeg";
        ret = ImageManager.getInstance().getLoacalBitmap(filename);
        if (ret == null){
            ret = UrlOpener.getInstance().getImageBmp(url);
            ImageManager.getInstance().saveBmpToSd(ret, filename);
        }
        return ret;
    }
    public List<Map<String, Object>> getPageBigBmp(int num){
        List<Map<String, Object>> ret = new ArrayList<>();
        int index = 0, start, end;
        String url = "http://www.javfee.com/cn/currentPage/" + num;
        HttpContent req = UrlOpener.getInstance().urlOpen(url, null);
        if (req == null || req.getContent() == null){
            return null;
        }
        String codeUrl = " <div class=\"item pull-left\"> <a href=\"";
        String codePat = "<br> <span style=\"color:#CC0000;font-size:12px;\">";
        int count = 0;
        while(true) {
            Map<String, Object> map = new HashMap<>();
            start = req.getContent().indexOf(codeUrl, index);
            if (start == -1){
                break;
            }
            count ++;
            Log.d("shanlihou", "count:" + count);
            start += codeUrl.length();
            end = req.getContent().indexOf('\"', start);
            Log.d("shanlihou", req.getContent().substring(start, end));
            String imgUrl = req.getContent().substring(start, end);

            start = req.getContent().indexOf(codePat, end);
            if(start == -1){
                break;
            }
            start += codePat.length();
            end = req.getContent().indexOf('<', start);
            String code = req.getContent().substring(start, end);
            map.put("code", code);
            map.put("bitmap", getBigImage(imgUrl, code));
            index = end;
            ret.add(map);
        }
        return ret;
    }
    private Bitmap getBigImage(String url, String code){
        Bitmap ret;
        String filename = code + ".jpeg";
        ret = ImageManager.getInstance().getLoacalBitmap(filename);
        String imgUrlPat = "<a class=\"bigImage\" href=\"";
        if (ret == null){
            HttpContent req = UrlOpener.getInstance().urlOpen(url, null);
            if (req == null || req.getContent() == null){
                return null;
            }
            int start = req.getContent().indexOf(imgUrlPat);
            start += imgUrlPat.length();
            int end = req.getContent().indexOf('\"', start);
            ret = UrlOpener.getInstance().getImageBmp(req.getContent().substring(start, end));
            ImageManager.getInstance().saveBmpToSd(ret, filename);
        }
        return ret;
    }
}
