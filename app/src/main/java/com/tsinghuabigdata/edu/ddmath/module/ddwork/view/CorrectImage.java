package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

/*
 * 带批改标志的图片
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.SinglePicturePreviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.CorrectImageUtil;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.ScreenUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.ContextUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.DeviceUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.io.File;

public class CorrectImage extends ImageView implements View.OnClickListener {

    private CorrectImageUtil correctImageUtil;
    private boolean mAttached = false;

    private String   mUrl;
    private String   mCorrectData;
    private String   mEKPoints;
    private Activity mActivity;

    public CorrectImage(Context context) {
        super(context);
        init(context);
    }

    public CorrectImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CorrectImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        mAttached = false;
        super.onDetachedFromWindow();
    }

    @Override
    public void onClick(View view) {
        //ArrayList<String> list = new ArrayList<>();
        //list.add(mUrl);
        //        PicturePreviewActivity.startPicturePreviewActivity( getContext(), list, mCorrectData, 0 );
        if (mActivity == null) {
            SinglePicturePreviewActivity.startSinglePicturePreviewActivity(getContext(), mUrl, mCorrectData, mEKPoints);
        } else {
            SinglePicturePreviewActivity.startSinglePicturePreviewActivity(mActivity, mUrl, mCorrectData, mEKPoints);
        }
    }

    //    public void loadResourceImage( int resid ){
    //        setScaleType( ScaleType.CENTER_INSIDE );
    //        mUrl = null;
    //        mCorrectData = null;
    //        PicassoUtil.getPicasso( getContext() ).load( resid ).error( R.drawable.ic_broken_image ).placeholder(R.drawable.ic_temporary_image).into( this );
    //    }
    public void loadLocalIamge(final String path) {
        setScaleType(ScaleType.FIT_XY);
        mUrl = path;
        mCorrectData = null;
        mEKPoints = null;
        PicassoUtil.getPicasso(getContext()).load(new File(path)).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(this);
    }

    public void loadImage(final String url, final String correctdata, final String ekpoint ) {

        setScaleType(ScaleType.FIT_XY);

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null)
            return;

        final String turl = BitmapUtils.getUrlQuality(loginInfo.getFileServer() + url, loginInfo.getAccessToken());
        mUrl = turl;
        mCorrectData = correctdata;
        mEKPoints = ekpoint;

        //AppLog.d("fdfdfd mUrl = " + mUrl);

        if (TextUtils.isEmpty(correctdata) || "[]".equals(correctdata) || "null".equals(correctdata)) {
            mCorrectData = null;
            PicassoUtil.getPicasso(getContext()).load(turl).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(this);
        } else {
            PicassoUtil.getPicasso(getContext()).load(turl).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(this, new Callback() {
                @Override
                public void onSuccess() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Bitmap bitmap = PicassoUtil.getPicasso(getContext()).load(turl).get();
                                final Bitmap dstBitmap = correctImageUtil.getCorrectBitmap(turl, bitmap, correctdata, ekpoint );

                                int index = 0;
                                while (!mAttached) {
                                    index++;
                                    SystemClock.sleep(50);
                                    if (index > 200)
                                        break;        //10S自动退出
                                }
                                updateImage(dstBitmap);
                            } catch (Exception e) {
                                AppLog.i("", e);
                            }
                        }
                    }).start();
                }

                @Override
                public void onError() {
                    AppLog.d("dfdfdfssss  get error url=" + turl);
                }
            });
        }
    }

    //------------------------------------------------------------------------------------
    private void init(Context context) {
        correctImageUtil = new CorrectImageUtil(context);
        setOnClickListener(this);

        int screenWidth = WindowUtils.getScreenWidth( getContext() );
        setMaxWidth(screenWidth);
        setMaxHeight(screenWidth * 5);// 这里其实可以根据需求而定，我这里测试为最大宽度的1.5倍

    }

    private void updateImage(final Bitmap dstBitmap) {
        post(new Runnable() {
            @Override
            public void run() {
                setImageBitmap(dstBitmap);
            }
        });
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }
}