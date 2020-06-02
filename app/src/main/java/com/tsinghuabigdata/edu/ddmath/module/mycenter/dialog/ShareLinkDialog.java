package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.myscore.UserScoreUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ShareUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;


/**
 * Created by Administrator on 2018/4/10.
 * 分享链接Dialog
 */

public class ShareLinkDialog extends Dialog implements View.OnClickListener, PlatformActionListener {


    private Context mContext;


    private ImageView mIvClose;
    private ImageView mIvWechatCircle;
    private ImageView mIvWechatFriend;
    private ImageView mIvQqZone;
    private ImageView mIvQqFriend;

    private String  mShareUrl;
    private String  mTitle;
    private String  mText;
    private boolean mFromReport;

    private String mScoreEventId;
    private String mContentId;

    public ShareLinkDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
        initView();
        initData();
    }

    public ShareLinkDialog setScoreEventId(String mScoreEventId) {
        this.mScoreEventId = mScoreEventId;
        return this;
    }
    public ShareLinkDialog setContentId(String contentId) {
        this.mContentId = contentId;
        return this;
    }
    public void setShareUrl(String shareUrl) {
        mShareUrl = shareUrl;
    }

    public void setShareInfo(String shareUrl, String title, String text, boolean isReport) {
        mShareUrl = shareUrl;
        mTitle = title;
        mText = text;
        mFromReport = isReport;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_wechat_circle:
                share(ShareUtils.PLAT_WXCIRCLE);
                MobclickAgent.onEvent(getContext(), "share_wxcircle");
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

    //---------------------------------------------------------------------------------------------
    private void initView() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_common_share);
        } else {
            setContentView(R.layout.dialog_common_share_phone);
        }
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvWechatCircle = (ImageView) findViewById(R.id.iv_wechat_circle);
        mIvWechatFriend = (ImageView) findViewById(R.id.iv_wechat_friend);
        mIvQqZone = (ImageView) findViewById(R.id.iv_qq_zone);
        mIvQqFriend = (ImageView) findViewById(R.id.iv_qq_friend);


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

    private void initData() {

    }

    private void share(String plat) {
//        ToastUtils.show(mContext, "开始分享。");
        String url = TextUtils.isEmpty(mShareUrl) ? ShareUtils.getshareLinkUrl( getContext() ) : mShareUrl;
        String title = TextUtils.isEmpty(mTitle) ? "豆豆数学邀你诊断作业" : mTitle;
        //String url = "http://wwww.baidu.com";
        String text;
        if (mFromReport) {
            text = mText;
        } else {
            text = TextUtils.isEmpty(mText) ? "数学作业整页拍，智能诊断当日改，科学记忆好提分。现在加入可获得学豆奖励！" : mText;
        }
        new ShareUtils().shareLink(mContext, plat, title, url, text, this);
    }

}
