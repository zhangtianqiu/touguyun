package com.touguyun.utils;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.touguyun.MainApplication;
import com.touguyun.R;
import com.touguyun.view.ImageProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by zhengyonghui on 15/8/25.
 */

public class ImageLoader {


    private volatile static ImageLoader instance;
    /** Returns singleton class instance */
    public static ImageLoader getInstance() {
        if (instance == null) {
            synchronized (ImageLoader.class) {
                if (instance == null) {
                    instance = new ImageLoader(MainApplication.getInstance());
                }
            }
        }
        return instance;
    }

    protected ImageLoader() {
    }

    private Map<String,List<ImageView>> OriginalImageMap;
    private Map<String,View> OriginalProgressMap;               //
    private ImageLoadingProgressListener originalProgressListener;  //下载图片加载进度条
    private OnImageLoadListener onImageLoadListener;    //图片下载回调
    private SimpleImageLoadingListener loadingListener;
    private ImageLoadingProgressListener progressListener;
    private Context mContext;
    public ImageLoader(Context mContext){
        this.mContext = mContext;
        if(mContext instanceof OnImageLoadListener){
            this.onImageLoadListener = (OnImageLoadListener)mContext;
        }
    }

    private DisplayImageOptions filletPicOption;
    public void showFilletImage(String url,ImageView imageView){
        showFilletImage(url,imageView,10);
    }
    public void showFilletImage(String url,ImageView imageView,int fillet){
        if(StringUtils.startWithHttp(url) && imageView!=null){
            if(filletPicOption == null){
                filletPicOption = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.white_pic)
                        .showImageOnFail(R.drawable.white_pic)
                        .showImageOnLoading(R.drawable.white_pic)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .displayer(new RoundedBitmapDisplayer(fillet))
                        .build();
            }
            showImage(url,imageView,true,filletPicOption,mContext.getResources(),null);
        }
    }

    private DisplayImageOptions picOption;
    public void showImage(String url,ImageView imageView){
        showImage(url,imageView,null);
    }
    public void showImage(String url, ImageView imageView, View progressBar){
        if(StringUtils.startWithHttp(url) && imageView!=null){
            if(picOption == null){
                picOption = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.white_pic)
                        .showImageOnFail(R.drawable.white_pic)
                        .showImageOnLoading(R.drawable.white_pic)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .considerExifParams(true)
                        .build();
            }
            showImage(url,imageView,true,picOption,mContext.getResources(),progressBar);
        }
    }


    private DisplayImageOptions OriginalOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.white_pic)
            .showImageOnFail(R.drawable.white_pic)
            .showImageOnLoading(R.drawable.white_pic)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();
    /**
     * 下载并显示图片
     * @param url
     * @param imageView
     * @param fromNetwork
     * @param option
     * @param res
     * @param progressBar
     */
    public void showImage(String url,ImageView imageView,boolean fromNetwork,DisplayImageOptions option,Resources res,final View progressBar){
        if(imageView == null){
            return;
        }
        if(StringUtils.startWithHttp(url)){
            if(progressListener == null && progressBar!=null && progressBar instanceof ImageProgressBar){
                final ImageProgressBar progress = (ImageProgressBar)progressBar;
                progressListener = new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        progress.setProgress(Math.round(100.0f * current / total));
                    }
                };
            }
            if(loadingListener == null) {
                 loadingListener = new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri,view);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri,view,failReason);
                        if (progressBar != null) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "图片加载失败";
                                    break;
                                case DECODING_ERROR:
                                    message = "图片加载失败";
                                    break;
                                case NETWORK_DENIED:
                                    message = "下载失败";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "内存空间不足";
                                    break;
                                case UNKNOWN:
                                    message = "未知错误";
                                    break;
                            }
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        if (onImageLoadListener != null) {
                            onImageLoadListener.onLoadError(imageUri);
                        }

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri,view,loadedImage);
                        if(onImageLoadListener!=null){
                            onImageLoadListener.onImageLoad((ImageView) view, loadedImage, imageUri);
                        }
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        super.onLoadingCancelled(imageUri, view);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                };
            }
            if(fromNetwork || !hasCatchFile(url)){
                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url,imageView,option,loadingListener,progressListener);
            }else{
                imageView.setImageDrawable(option.getImageForEmptyUri(res));
            }
        }
    }
    /**
     * 下载原图
     * @param url
     * @param imageView
     * @param progressBar
     * @param isShowImage
     */
    public void showOriginalImage(String url, ImageView imageView, final View progressBar, final boolean isShowImage){
        if(imageView == null || !StringUtils.startWithHttp(url)){
            return;
        }
        if(OriginalImageMap == null){
            OriginalImageMap = new HashMap<String,List<ImageView>>();
        }
        if(OriginalProgressMap == null){
            OriginalProgressMap = new HashMap<String,View>();
        }
        if(OriginalImageMap.get(url) == null){
            List<ImageView> imageViewList = new ArrayList<ImageView>();
            imageViewList.add(imageView);
            OriginalImageMap.put(url,imageViewList);
        }else{
            OriginalImageMap.get(url).add(imageView);
        }
        OriginalProgressMap.put(url,progressBar);
        if(originalProgressListener == null){
            originalProgressListener = new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    if(OriginalProgressMap!=null && OriginalProgressMap.get(imageUri)!=null && OriginalProgressMap.get(imageUri) instanceof ImageProgressBar){
                        ((ImageProgressBar)(OriginalProgressMap.get(imageUri))).setProgress(Math.round(100.0f * current / total));
                    }
                }
            };
        }
        ImageLoadingListener loadingListener = new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String s, View view) {
                if(OriginalProgressMap!=null && OriginalProgressMap.get(s)!=null){
                    OriginalProgressMap.get(s).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "图片加载失败";
                        break;
                    case DECODING_ERROR:
                        message = "图片加载失败";
                        break;
                    case NETWORK_DENIED:
                        message = "下载失败";
                        break;
                    case OUT_OF_MEMORY:
                        message = "内存空间不足";
                        break;
                    case UNKNOWN:
                        message = "未知错误";
                        break;
                }
                UIShowUtil.toast(mContext, message);
                if(OriginalProgressMap!=null && OriginalProgressMap.get(s)!=null){
                    OriginalProgressMap.get(s).setVisibility(View.GONE);
                }
                if (onImageLoadListener != null) {
                    onImageLoadListener.onLoadError(s);
                }
            }
            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (OriginalImageMap!=null && OriginalImageMap.get(s)!=null) {
                    List<ImageView> viewList = OriginalImageMap.get(s);
                    for (int i=0;i<viewList.size();i++){
                        if(isShowImage){
                            viewList.get(i).setImageBitmap(bitmap);
                        }
                        if (onImageLoadListener != null) {
                            onImageLoadListener.onImageLoad(viewList.get(i), bitmap, s);
                        }
                    }
                    OriginalImageMap.remove(s);
                }
                if(OriginalProgressMap!=null && OriginalProgressMap.get(s)!=null){
                    OriginalProgressMap.get(s).setVisibility(View.GONE);
                    OriginalImageMap.remove(s);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                if(OriginalProgressMap!=null && OriginalProgressMap.get(s)!=null){
                    OriginalProgressMap.get(s).setVisibility(View.GONE);
                }
                if (onImageLoadListener != null) {
                    onImageLoadListener.onLoadError(s);
                }
            }
        };
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().loadImage(url, null, OriginalOption, loadingListener, originalProgressListener);
    }

    public void showImage(int rid,ImageView imageView){
        String drawableUrl = "drawable://" + rid;
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(drawableUrl,imageView);
    }


    public interface OnImageLoadListener {
        public void onImageLoad(ImageView view, Bitmap bitmat, String imageUrl);
        public void onLoadError(String imageUrl);
    }


    public boolean hasCatchFile(String url){
        File file = DiskCacheUtils.findInCache(url, com.nostra13.universalimageloader.core.ImageLoader.getInstance().getDiskCache());
        return file !=null  && file.exists() && file.length() > 0L;
    }

    public File getCatchFile(String url){
        return  DiskCacheUtils.findInCache(url, com.nostra13.universalimageloader.core.ImageLoader.getInstance().getDiskCache());
    }

    public void clean(){
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().destroy();
    }
}
