package com.example.bcloud;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import com.example.bcloud.ImageDownLoader.onImageLoaderListener;

import java.util.List;
import java.util.Map;


public class ImageAdapter extends BaseAdapter implements OnScrollListener {
    private Context mContext;
    private List<Map<String, String>> mGridList;
    private GridView mGridView;

    private ImageDownLoader mImageDownLoader;

    private boolean isFirstEnter = true;
    private LayoutInflater mInflater;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;


    public ImageAdapter(Context context, GridView mGridView, List<Map<String, String>> grid) {
        this.mContext = context;
        this.mGridView = mGridView;
        this.mGridList = grid;
        mImageDownLoader = new ImageDownLoader(context);
        mInflater = LayoutInflater.from(context);
        mGridView.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //����GridView��ֹʱ��ȥ����ͼƬ��GridView����ʱȡ�������������ص�����
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            showImage(mFirstVisibleItem, mVisibleItemCount);
        } else {
            cancelTask();
        }

    }


    /**
     * GridView������ʱ����õķ������տ�ʼ��ʾGridViewҲ����ô˷���
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        // ���������Ϊ�״ν����������������
        if (isFirstEnter && visibleItemCount > 0) {
            showImage(mFirstVisibleItem, mVisibleItemCount);
            isFirstEnter = false;
        }
    }


    @Override
    public int getCount() {
        return mGridList.size();
    }

    @Override
    public Object getItem(int position) {
        return mGridList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, String> map = mGridList.get(position);
        ImageView imageView;
        TextView textView;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.sow_item, null);
        }
        imageView = (ImageView) convertView.findViewById(R.id.sowImage);
        textView = (TextView) convertView.findViewById(R.id.sowText);
        textView.setText(map.get("code"));
        imageView.setTag(map.get("code"));
        Bitmap bitmap = mImageDownLoader.showCacheBitmap(map.get("code"));
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.not_use));
        }

        return convertView;
    }

    private void showImage(int firstVisibleItem, int visibleItemCount) {
        Bitmap bitmap = null;
        Log.d("shanlihou", "show image");
        for (int i = 0; i < visibleItemCount; i++) {
            //	for(int i=firstVisibleItem; i<firstVisibleItem + visibleItemCount; i++){
            Map<String, String> map = mGridList.get(i + firstVisibleItem);
            View view = (View)mGridView.getChildAt(i);
            //final ImageView imageView = (ImageView) mGridView.findViewWithTag(map.get("code"));
            final ImageView imageView = (ImageView)view.findViewById(R.id.sowImage);
            TextView textView = (TextView)view.findViewById(R.id.sowText);
            textView.setText(map.get("code"));
            bitmap = mImageDownLoader.downloadImage(map.get("url"), new onImageLoaderListener() {
                @Override
                public void onImageLoader(Bitmap bitmap, String url) {
                    if (imageView != null && bitmap != null) {
                        String text = textView.getText().toString();
                        Log.d("shanlihou", "url:" + url);
                        Log.d("shanlihou", "text:" + text);
                        if (url.contains(text)){
                            Log.d("shanlihou", "contain");
                            imageView.setImageBitmap(bitmap);
                        }else{
                            Log.d("shanlihou", "not contain");
                        }
                        Log.d("shanlihou", "index:" +  url.indexOf(text));
                    }

                }
            });

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.not_use));
            }
        }
    }

    /**
     * ȡ����������
     */
    public void cancelTask() {
        mImageDownLoader.cancelTask();
    }

}
