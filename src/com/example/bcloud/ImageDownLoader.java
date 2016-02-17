package com.example.bcloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import manager.ImageManager;
import manager.SowManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownLoader {
	/**
	 * ����Image���࣬���洢Image�Ĵ�С����LruCache�趨��ֵ��ϵͳ�Զ��ͷ��ڴ�
	 */
	private LruCache<String, Bitmap> mMemoryCache;
	/**
	 * �����ļ��������������
	 */
	/**
	 * ����Image���̳߳�
	 */
	private ExecutorService mImageThreadPool = null;
	
	
	public ImageDownLoader(Context context){
		//��ȡϵͳ�����ÿ��Ӧ�ó��������ڴ棬ÿ��Ӧ��ϵͳ����32M
		int maxMemory = (int) Runtime.getRuntime().maxMemory();  
        int mCacheSize = maxMemory / 16;
        //��LruCache����1/8 4M
		mMemoryCache = new LruCache<String, Bitmap>(mCacheSize){

			//������д�˷�����������Bitmap�Ĵ�С
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
			
		};
		
	}
	
	
	/**
	 * ��ȡ�̳߳صķ�������Ϊ�漰�����������⣬���Ǽ���ͬ����
	 * @return
	 */
	public ExecutorService getThreadPool(){
		if(mImageThreadPool == null){
			synchronized(ExecutorService.class){
				if(mImageThreadPool == null){
					//Ϊ������ͼƬ���ӵ���������������2���߳�������ͼƬ
					mImageThreadPool = Executors.newFixedThreadPool(2);
				}
			}
		}
		
		return mImageThreadPool;
		
	}
	
	/**
	 * ���Bitmap���ڴ滺��
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {  
	    if (getBitmapFromMemCache(key) == null && bitmap != null) {
			Log.d("shanlihou", "height:" + bitmap.getHeight());
			Log.d("shanlihou", "width:" + bitmap.getWidth());
	        mMemoryCache.put(key, bitmap);
	    }
	}  
	 
	/**
	 * ���ڴ滺���л�ȡһ��Bitmap
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {  
	    return mMemoryCache.get(key);  
	} 
	
	/**
	 * �ȴ��ڴ滺���л�ȡBitmap,���û�оʹ�SD�������ֻ������л�ȡ��SD�������ֻ�����
	 * û�о�ȥ����
	 * @param url
	 * @param listener
	 * @return
	 */
	public Bitmap downloadImage(final String url, final onImageLoaderListener listener){
		String codePat = "https://www.javbus.me/";
		final String code = url.substring(codePat.length());
		Bitmap bitmap = showCacheBitmap(code);
		if(bitmap != null){
			return bitmap;
		}else{
			final Handler handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					listener.onImageLoader((Bitmap)msg.obj, url);
				}
			};
			
			getThreadPool().execute(new Runnable() {
				
				@Override
				public void run() {
					Bitmap bitmap = SowManager.getInstance().getBigImage(url);
					Bitmap newBM = ImageManager.getInstance().bitmapScale(bitmap);
					Message msg = handler.obtainMessage();
					msg.obj = newBM;
					handler.sendMessage(msg);
                    ImageManager.getInstance().saveBmpToSd(bitmap, code + ".jpeg");
					addBitmapToMemoryCache(code, newBM);
					ImageManager.getInstance().bitmapRecycle(bitmap);
				}
			});
		}
		
		return null;
	}

	public Bitmap showCacheBitmap(String code){
		if(getBitmapFromMemCache(code) != null){
			return getBitmapFromMemCache(code);
		}else{
			Bitmap bitmap = ImageManager.getInstance().getLoacalBitmap(code + ".jpeg");
			if (bitmap != null){
				addBitmapToMemoryCache(code, bitmap);
			}
			return bitmap;
		}
	}
	

	/**
	 * ��Url�л�ȡBitmap
	 * @param url
	 * @return
	 */

	
	/**
	 * ȡ���������ص�����
	 */
	public synchronized void cancelTask() {
		if(mImageThreadPool != null){
			mImageThreadPool.shutdownNow();
			mImageThreadPool = null;
		}
	}
	
	
	/**
	 * �첽����ͼƬ�Ļص��ӿ�
	 * @author len
	 *
	 */
	public interface onImageLoaderListener{
		void onImageLoader(Bitmap bitmap, String url);
	}
	
}
