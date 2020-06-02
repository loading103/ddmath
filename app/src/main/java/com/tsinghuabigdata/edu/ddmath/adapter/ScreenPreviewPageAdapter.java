package com.tsinghuabigdata.edu.ddmath.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Callback;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.ScreenPreviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.CorrectImageUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2017/2/17.
 */

public class ScreenPreviewPageAdapter extends PagerAdapter {
    private Context           context;
    private ArrayList<String> mList;
    private String accessToken;
    private CorrectImageUtil correctImageUtil;
    private ScreenPreviewActivity activity;
    private ArrayList<String> correctInfos;

    public ScreenPreviewPageAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.mList = list;
        correctImageUtil = new CorrectImageUtil( context );
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo!=null ){
            try {
                accessToken = URLEncoder.encode( loginInfo.getAccessToken(), "utf-8");
            }catch (Exception e){
                AppLog.i("", e);
            }
        }
    }

    public void setActivity( ScreenPreviewActivity activity ){
        this.activity = activity;
    }

    public void setCorrectInfo( ArrayList<String> infos ){
        correctInfos = infos;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        final PhotoView photoView = new PhotoView(context);
        String url = mList.get(position);
        if (url.startsWith("http")) {
            final String comUrl = BitmapUtils.getUrlQuality( url/*, 80*/, accessToken );
            final String result = correctInfos==null?"":correctInfos.get(position);
            if( TextUtils.isEmpty(result) || "[]".equals(result) || "null".equals( result ) ){
                PicassoUtil.getPicasso(context).load(comUrl).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(photoView);
            }else{
                PicassoUtil.getPicasso(context).load(comUrl).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(photoView,new Callback() {
                    @Override
                    public void onSuccess() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap bitmap = PicassoUtil.getPicasso(context).load( comUrl ).get();
                                    if( correctInfos != null && correctInfos.size() > position ){
                                        final Bitmap dstBitmap = correctImageUtil.getCorrectBitmap( comUrl, bitmap, result, null );
                                        if(activity!=null)activity.updateImage( photoView, dstBitmap );
                                    }
                                }catch (Exception e){
                                    AppLog.i("", e);
                                }
                            }
                        }).start();
                    }
                    @Override
                    public void onError() {
                        AppLog.d("dfdfdfssss  get error url=" + comUrl );
                    }
                });
            }

        } else {
            try {
                PicassoUtil.getPicasso( context ).load(new File(url)).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(photoView);
            } catch (Exception e) {
                AppLog.i("", e);
            }
        }
        container.addView(photoView);
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                Activity activity = (Activity) ScreenPreviewPageAdapter.this.context;
                activity.finish();
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
