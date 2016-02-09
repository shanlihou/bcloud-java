package com.example.bcloud;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.*;

/**
 * Created by shanlihou on 15-5-2.
 */
public class ImageManager {
    private static ImageManager instance;
    private static String dir = null;
    private ImageManager(){
    }
    public static ImageManager getInstance(){
        if (instance == null) {
            instance = new ImageManager();
            return instance;
        }
        else
            return instance;
    }
    public void mkdir(String dirName){
        File dir = new File(dirName);
        try{
            if (!dir.exists()){
                dir.mkdirs();
            }

        }catch (Exception e){
            Log.d("shanlihou", "io except");
            e.printStackTrace();
        }
        if (dir.exists()){
            Log.d("shanlihou", "img exist");
            this.dir = dirName;
        }else{
            Log.d("shanlihou", "img not exist");
        }
    }
    public void saveBmpToSd(Bitmap bm, String filename) {
        if (bm == null) {
            Log.w("shanlihou", " trying to savenull bitmap");
            return;
        }
        //判断sdcard上的空间
        File file = new File(this.dir +"/" + filename);
        if (file.exists()){
            return;
        }
        try {
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i("shanlihou", "Image saved tosd");
        } catch (FileNotFoundException e) {
            Log.w("shanlihou", "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("shanlihou", "IOException");
        }
    }
    public Bitmap getLoacalBitmap(String filename) {
        File file = new File(this.dir + "/" + filename);
        if(!file.exists()){
            Log.d("shanlihou", "not exist");
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(this.dir + "/" + filename);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
