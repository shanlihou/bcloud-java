package manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.*;

/**
 * Created by shanlihou on 15-5-2.
 */
public class ImageManager {
    private static ImageManager instance;
    private static final int IMAGE_MAX_WIDTH = 200;
    private static final int IMAGE_MAX_HEIGHT = 200;
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
            Log.w("shanlihou", " trying to savenull bitmap:" + filename);
            return;
        }
        //判断sdcard上的空间
        File file = new File(this.dir +"/" + filename);
        if (file.exists()){
            Log.d("shanlihou", "file exist:" + filename);
            return;
        }
        try {
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Log.i("shanlihou", "Image saved tosd:" + filename);
        } catch (FileNotFoundException e) {
            Log.w("shanlihou", "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.w("shanlihou", "IOException");
        }
    }
    public Bitmap getLoacalBitmap(String filename) {
        String pathName = this.dir + "/" + filename;
        File file = new File(pathName);
        if(!file.exists()){
            Log.d("shanlihou", filename + "not exist");
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
//            FileInputStream fis = new FileInputStream(this.dir + "/" + filename);
            options.inSampleSize = getImageScale(pathName);
            return BitmapFactory.decodeFile(pathName, options);
//            return BitmapFactory.decodeStream(fis);
        } catch (OutOfMemoryError  e) {
            e.printStackTrace();
            return null;
        }
    }
    public void bitmapRecycle(Bitmap bitmap){
		if (bitmap != null && bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
			System.gc();
		}
	}

    public Bitmap bitmapScale(Bitmap bitmap){
        if (bitmap == null)
            return null;
        int widith = bitmap.getWidth();
        int height = bitmap.getHeight();
        int scale = 1;
        while (widith / scale > IMAGE_MAX_WIDTH || height / scale > IMAGE_MAX_HEIGHT) {
            scale *= 2;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(1f / scale, 1f / scale); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, widith, height, matrix, true);
        return resizeBmp;
    }
    private static int getImageScale(String imagePath) {
        BitmapFactory.Options option = new BitmapFactory.Options();
        // set inJustDecodeBounds to true, allowing the caller to query the bitmap info without having to allocate the
        // memory for its pixels.
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, option);

        int scale = 1;
        while (option.outWidth / scale > IMAGE_MAX_WIDTH || option.outHeight / scale > IMAGE_MAX_HEIGHT) {
            scale *= 2;
        }
        return scale;
    }
}
