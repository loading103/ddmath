package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.StatFs;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class PicassoUtil {

    private static Picasso.Builder mBuilder;
    private static Picasso         mPicasso;


    public static Picasso getPicasso() {
        return getPicasso(ZxApplication.getApplication());
    }

    public static Picasso getPicasso(Context context) {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo != null && loginInfo.getAccessToken() != null) {
            if (mPicasso == null) {
                if (mBuilder == null)
                    mBuilder = new Picasso.Builder(context).downloader(new OkHttpDownloader(new PicassoUtil().getOkHttpClient()));
                mPicasso = mBuilder.defaultBitmapConfig(Bitmap.Config.RGB_565).build();
            }
            return mPicasso;
        } else {
            return Picasso.with(context);
        }
    }

    public static void init() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo != null && loginInfo.getAccessToken() != null) {
            if (mBuilder == null) {
                mBuilder = new Picasso.Builder(ZxApplication.getApplication()).downloader(new OkHttpDownloader(new PicassoUtil().getOkHttpClient()));
            }
            mPicasso = mBuilder.defaultBitmapConfig(Bitmap.Config.RGB_565).build();
        }
    }

    public static void displayImage(String urL, ImageView imageView) {
        //        if (TextUtils.isEmpty(urL)) {
        //            PicassoUtil.getPicasso().load(R.drawable.ic_broken_image).into(imageView);
        //        } else {
        //            PicassoUtil.displayImageComUrl(AccountUtils.getFileServer() + urL, imageView);
        //        }
        displayImageIndetUrl(urL, imageView);
    }


    /**
     * 加载图片（url不确定是否已经加前缀）
     */
    public static void displayImageIndetUrl(String urL, ImageView imageView) {
        if (TextUtils.isEmpty(urL)) {
            PicassoUtil.getPicasso().load(R.drawable.ic_broken_image).into(imageView);
        } else if (urL.startsWith("http:")) {
            PicassoUtil.displayImageComUrl(urL, imageView);
        } else {
            PicassoUtil.displayImageComUrl(AccountUtils.getFileServer() + urL, imageView);
        }
    }

    public static void displayImageComUrl(String urL, ImageView imageView) {
        PicassoUtil.getPicasso().load(urL).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(imageView);
    }

    /**
     * 加载图片（地址为空或者加载失败 用指定图片占位）
     */
    public static void displayWithSpecImage(String urL, ImageView imageView,int res) {
        if (TextUtils.isEmpty(urL)) {
            PicassoUtil.getPicasso().load(res).into(imageView);
        } else /*if (urL.startsWith("http:"))*/ {

//            PicassoUtil.getPicasso().load(urL).error(res).placeholder(res).into(imageView);
//        } else {
            PicassoUtil.getPicasso().load( BitmapUtils.getUrlWithToken(urL) ).error(res).placeholder(res).into(imageView);
        }
    }

    /**
     * 加载图片（带回调）
     */
    public static void displayUrlWithCallback(String urL, ImageView imageView, Callback callback) {
        if (TextUtils.isEmpty(urL)) {
            PicassoUtil.getPicasso().load(R.drawable.ic_broken_image).into(imageView);
        } else if (urL.startsWith("http:")) {
            PicassoUtil.displayComUrlWithCallback(urL, imageView, callback);
        } else {
            PicassoUtil.displayComUrlWithCallback(AccountUtils.getFileServer() + urL, imageView, callback);
        }
    }

    private static void displayComUrlWithCallback(String urL, ImageView imageView, Callback callback) {
        PicassoUtil.getPicasso().load(urL).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(imageView, callback);
    }

    public static void displaySpecificImage(String urL, ImageView imageView, int resId) {
        if (TextUtils.isEmpty(urL)) {
            PicassoUtil.getPicasso(ZxApplication.getApplication()).load(resId).into(imageView);
        } else {
            PicassoUtil.getPicasso().load(AccountUtils.getFileServer() + urL).error(resId).placeholder(resId).into(imageView);
        }
    }

    public static void displayEmptyImage(ImageView imageView) {
        PicassoUtil.getPicasso().load(R.drawable.ic_broken_image).into(imageView);
    }


    private class TokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder request = chain.request().newBuilder();
            try {
                LoginInfo loginInfo = AccountUtils.getLoginUser();
                if (loginInfo != null && loginInfo.getAccessToken() != null) {
                    request.addHeader("access_token", loginInfo.getAccessToken());
                }
            } catch (Exception ex) {
                AppLog.i("set token fault", ex);
            }
            return chain.proceed(request.build());
        }
    }

    private PicassoUtil() {
    }

    private OkHttpClient getOkHttpClient() {

        OkHttpClient okHttpClient = new OkHttpClient();
        File cachedir = createDefaultCacheDir();
        try {
            okHttpClient.setCache(new com.squareup.okhttp.Cache(cachedir, calculateDiskCacheSize(cachedir)));
        } catch (Exception e) {
            AppLog.d("", e);
        }
        okHttpClient.interceptors().add(new TokenInterceptor());
        return okHttpClient;
    }

    private static final String PICASSO_CACHE       = "cache_image";
    //private static final int KEY_PADDING = 50; // Determined by exact science.
    private static final int    MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int    MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB


    private static File createDefaultCacheDir() {
        File cache = new File(ZxApplication.getApplication().getApplicationContext().getCacheDir(), PICASSO_CACHE);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return cache;
    }

    private static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (Exception e) {
            AppLog.d("", e);
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }

}
