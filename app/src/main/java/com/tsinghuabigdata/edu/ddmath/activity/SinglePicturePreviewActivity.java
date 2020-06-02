package com.tsinghuabigdata.edu.ddmath.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.squareup.picasso.Callback;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.CorrectImageUtil;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.io.File;
import java.io.IOException;

import uk.co.senab.photoview.MyOnDoubleTapListener;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 单张大图预览界面
 * Created by Administrator on 2017/2/17.
 */

public class SinglePicturePreviewActivity extends RoboBaseActivity {

    //多个图片地址，用","分割
    public static final String PARMA_IMAGE   = "imagespath";
    public static final String PARMA_CORRECT = "correctinfos";
    public static final String PARMA_ERRKP   = "errkpoints";

    private PhotoView mPhotoView;
    private Context   mContext;
    private String    url;
    private String    corrects;
    private String    ekpoints;

    public static void startSinglePicturePreviewActivity(Context context, String image, String corrects, String ekpoint) {
        Intent intent = new Intent(context, SinglePicturePreviewActivity.class);
        intent.putExtra(PARMA_IMAGE, image);
        intent.putExtra(PARMA_CORRECT, corrects);
        intent.putExtra(PARMA_ERRKP, ekpoint);
        context.startActivity(intent);
        /*if (context instanceof Activity){
            Activity activity = (Activity) context;
            activity.overridePendingTransition(R.anim.picture_in, 0);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_picture_preview);
        initView();
        initData();
    }


    private void initView() {
        mPhotoView = (PhotoView) findViewById(R.id.photoView);
    }

    private void initData() {
        mContext = this;
        url = getIntent().getStringExtra(PARMA_IMAGE);
        corrects = getIntent().getStringExtra(PARMA_CORRECT);
        ekpoints = getIntent().getStringExtra(PARMA_ERRKP);

        mPhotoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                finish();
                //                overridePendingTransition(0, R.anim.picture_out);
                //                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onOutsidePhotoTap() {
                finish();
                //                overridePendingTransition(0, R.anim.picture_out);
                //                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        PhotoViewAttacher photoViewAttacher = (PhotoViewAttacher) mPhotoView.getIPhotoViewImplementation();
        mPhotoView.setOnDoubleTapListener(new MyOnDoubleTapListener(photoViewAttacher));
        if (url.startsWith("http")) {
            if (TextUtils.isEmpty(corrects) || "[]".equals(corrects) || "null".equals(corrects)) {
                PicassoUtil.getPicasso(mContext).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(mPhotoView);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = PicassoUtil.getPicasso(mContext).load(url).get();
                            if (bitmap == null) {
                                getBitmapFromNextWork();
                            } else {
                                setImage(bitmap, ekpoints);
                            }
                        } catch (IOException e) {
                            AppLog.e( "", "", e );
                        }
                    }
                }).start();
            }
        } else {
            PicassoUtil.getPicasso(mContext).load(new File(url)).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(mPhotoView);
        }


    }

    private void setImage(Bitmap bitmap, String ekpoints) {
        CorrectImageUtil correctImageUtil = new CorrectImageUtil(mContext);
        final Bitmap dstBitmap = correctImageUtil.getCorrectBitmap(url, bitmap, corrects, ekpoints);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPhotoView.setImageBitmap(dstBitmap);
            }
        });
    }

    private void getBitmapFromNextWork() {
        PicassoUtil.getPicasso(this).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(mPhotoView, new Callback() {
            @Override
            public void onSuccess() {
                try {
                    Bitmap bitmap = PicassoUtil.getPicasso(mContext).load(url).get();
                    setImage(bitmap, ekpoints);
                } catch (Exception e) {
                    AppLog.i("", e);
                }
            }

            @Override
            public void onError() {
                AppLog.d("dfdfdfssss  get error url=" + url);
            }
        });
    }


}
