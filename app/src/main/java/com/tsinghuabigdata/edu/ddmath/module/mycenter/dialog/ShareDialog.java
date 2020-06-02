package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.myscore.UserScoreUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.QRCodeUtil;
import com.tsinghuabigdata.edu.ddmath.util.ShareUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Locale;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;


/**
 * Created by Administrator on 2018/4/10.
 * 分享图片Dialog
 */

public class ShareDialog extends Dialog implements View.OnClickListener, PlatformActionListener {

    public static ShareDialog showShaerDialog(Context context, String title, String url, long createTime){
        ShareDialog shareDialog = new ShareDialog(context);
        shareDialog.setSharText( title, url, createTime );
        shareDialog.show();
        return shareDialog;
    }

    private Context mContext;
    private String  mUrl;

    private ImageView mIvClose;
//    private ImageView mIvWechatCircle;
//    private ImageView mIvWechatFriend;
//    private ImageView mIvQqZone;
//    private ImageView mIvQqFriend;

    private final static int TYPE_PIC = 0;
    private final static int TYPE_TEXT = 1;
    private int shareType = TYPE_TEXT;

    private String shareTitle;
    private String shareUrl;
    private String shareText;

    private String mScoreEventId;
    private String mContentId;

    public ShareDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;

        initView();
    }

    public void setSharPicture( String url ){
        if (!TextUtils.isEmpty(url) && !url.startsWith("http:")) {
            mUrl = AccountUtils.getFileServer() + url;
        } else {
            mUrl = url;
        }
        shareType = TYPE_PIC;
    }

    public void setSharText( String title, String url, String text ){
        shareTitle = title;
        shareUrl = url;
        shareText = text;

        shareType = TYPE_TEXT;
    }

    public ShareDialog setScoreEventId( String eventId ){
        mScoreEventId = eventId;
        return this;
    }

    public ShareDialog setContentId( String contentId ){
        mContentId = contentId;
        return this;
    }


    private void setSharText( String title, String url, long createTime ){
        UserDetailinfo loginInfo = AccountUtils.getUserdetailInfo();
        if( loginInfo != null ){
            shareTitle = String.format(Locale.getDefault(), "%s的%s", loginInfo.getReallyName(), title );
        }else{
            shareTitle = title;
        }
        shareUrl = url;
        shareText = String.format(Locale.getDefault(), "%s生成", DateUtils.format( createTime, DateUtils.FORMAT_DATA_TIME_MIN ) );
        shareType = TYPE_TEXT;
    }


    @Override
    public void onClick(View v) {
        AppLog.d("fsdfsdfsdfsdffffff v.id="+v.getId() + ",,, wechat id= "+R.id.iv_wechat_circle);
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_wechat_circle:
                share(ShareUtils.PLAT_WXCIRCLE);
                MobclickAgent.onEvent(getContext(), "share_wxcircle");
                AppLog.d("fsdfsdfsdfsdffffff --");
                break;
            case R.id.iv_wechat_friend:
                share(ShareUtils.PLAT_WEIXIN);
                MobclickAgent.onEvent(getContext(), "share_weixin");
                break;
            case R.id.iv_qq_zone:
                share(ShareUtils.PLAT_QZONE);
                MobclickAgent.onEvent(getContext(), "share_qzone");
                break;
            case R.id.iv_qq_friend:
                share(ShareUtils.PLAT_QQ);
                MobclickAgent.onEvent(getContext(), "share_qq");
                break;
            default:
                break;
        }
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        ToastUtils.show(getContext(), "分享成功");
        if( !TextUtils.isEmpty(mScoreEventId) ) UserScoreUtil.addUserScore( mScoreEventId, mContentId );
        dismiss();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        if (platform.getName().contains("Wechat")) {
            mIvClose.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show(getContext(), "分享失败，请检查是否安装了微信。");
                }
            });
        } else if (platform.getName().contains("QZone") || platform.getName().contains("QQ")) {
            ToastUtils.show(getContext(), "分享失败，请检查是否安装了QQ。");
            mIvClose.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show(getContext(), "分享失败，请检查是否安装了QQ。");
                }
            });
        }
    }

    @Override
    public void onCancel(Platform platform, int i) {
    }

    //----------------------------------------------------------------------------------------------
    private void initView() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_common_share);
        } else {
            setContentView(R.layout.dialog_common_share_phone);
        }
        mIvClose =  findViewById(R.id.iv_close);
        ImageView mIvWechatCircle =  findViewById(R.id.iv_wechat_circle);
        ImageView mIvWechatFriend =  findViewById(R.id.iv_wechat_friend);
        ImageView mIvQqZone =  findViewById(R.id.iv_qq_zone);
        ImageView mIvQqFriend =  findViewById(R.id.iv_qq_friend);

        mIvClose.setOnClickListener(this);
        mIvWechatCircle.setOnClickListener(this);
        mIvWechatFriend.setOnClickListener(this);
        mIvQqZone.setOnClickListener(this);
        mIvQqFriend.setOnClickListener(this);

        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.recharge_dialog_anim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void share(String plat) {
        //分享图片
        if( TYPE_PIC == shareType ){
            loadBitmap(plat);
        }else{      //分享文本
//            ToastUtils.show(mContext, "开始分享。");
            new ShareUtils().shareLink( mContext, plat, shareTitle, shareUrl, shareText, this );
        }
    }


    private void loadBitmap(final String plat) {
        PicassoUtil.getPicasso(getContext()).load(mUrl).fetch(new Callback() {
            @Override
            public void onSuccess() {
                LogUtils.i("loadBitmap onSuccess");
                getBitmapMain(plat);
            }

            @Override
            public void onError() {
                LogUtils.i("loadBitmap onError");
                ToastUtils.show(mContext, "加载图片失败，请重试.");
            }
        });
    }

    private void getBitmapMain(final String plat) {
        LogUtils.i("getBitmapMain");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap netBitmap = PicassoUtil.getPicasso(getContext()).load(mUrl).get();
                    String url = ShareUtils.getsharePicUrl( getContext() );
                    int codeWidth = (int) (netBitmap.getWidth() * AppConst.WIDTH_RATIO);
                    int oriCodeWidth = (int) (WindowUtils.getScreenHeight(mContext) * AppConst.WIDTH_RATIO);
                    Bitmap logoBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);
                    Bitmap oriCodeBitmap = QRCodeUtil.createQRCodeBitmap(url, oriCodeWidth, logoBitmap, 0.3f);
                    Bitmap codeBitmap = BitmapUtils.zoomImage(oriCodeBitmap, codeWidth, codeWidth);
                    Bitmap bitmap = BitmapUtils.toConformBitmap(netBitmap, codeBitmap);
                    creatShareBitmap(bitmap, plat);
                } catch (Exception e) {
                    AppLog.i("", e);
                }
            }


        }).start();
    }

    //生成要分享的图片
    private void creatShareBitmap(Bitmap dstBitmap, final String plat) {
        String filePath = Environment.getExternalStorageDirectory().toString() + AppConst.IMAGE_DIR;
        final String fpath = BitmapUtils.saveImage(filePath, dstBitmap);
        dstBitmap.recycle();
        mIvClose.post(new Runnable() {
            @Override
            public void run() {
                startShare(fpath, plat);
            }
        });
    }

    private void startShare(String path, String plat) {
        //
//        ToastUtils.show(mContext, "开始分享。");
        if (TextUtils.isEmpty(path)) {
            ToastUtils.show(mContext, "分享失败，请重试.");
            return;
        }
        //new ShareUtils().sharePic(getContext(), plat, getShareBitmap(), this);
        new ShareUtils().sharePic(getContext(), plat, path, this);
    }



}
