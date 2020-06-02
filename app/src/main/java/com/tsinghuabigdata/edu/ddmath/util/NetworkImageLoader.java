package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.tsinghuabigdata.edu.commons.cache.CacheManager;

//import roboguice.util.Strings;

/**
 * Created by 28205 on 2016/12/2.
 */
public class NetworkImageLoader {
    private static class InstanceHolder{
        private static final NetworkImageLoader INSTANCE = new NetworkImageLoader();
    }
    public NetworkImageLoader getInstance(){
        return InstanceHolder.INSTANCE;
    }
    private NetworkImageLoader(){}


    private int defaultImgResid;
    private int errImgResid;

    private static ImageLoader.ImageCache mCache = CacheManager.getBitmapLruCache();
    private ImageLoader imageLoader;

    /**
     * 加载网络图片
     */
    public void loadImage(ImageView view, String url) throws Exception {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (defaultImgResid == 0 || errImgResid == 0){
            throw new Exception("please set attribute defaultImageResid or errImgResid");
        }
        imageLoader.get(url, getImageListener(view));
    }
    /**
     * 加载网络图片
     */
    public void loadImage(ImageView view, String url, int defaultImgResid, int errImgResid) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        imageLoader.get(url, getImageListener(view, defaultImgResid, errImgResid));
    }

    public void initImageLoader(Context context){
        imageLoader = new ImageLoader(Volley.newRequestQueue(context), mCache);
    }

    private ImageLoader.ImageListener getImageListener(ImageView imageView){
        return getImageListener(imageView, defaultImgResid, errImgResid);
    }


    private ImageLoader.ImageListener getImageListener(ImageView imageView, int defaultResid, int errResid){
        return ImageLoader.getImageListener(imageView, defaultResid, errResid);
    }

    public int getDefaultImgResid() {
        return defaultImgResid;
    }

    public void setDefaultImgResid(int defaultImgResid) {
        this.defaultImgResid = defaultImgResid;
    }

    public int getErrImgResid() {
        return errImgResid;
    }

    public void setErrImgResid(int errImgResid) {
        this.errImgResid = errImgResid;
    }
}
