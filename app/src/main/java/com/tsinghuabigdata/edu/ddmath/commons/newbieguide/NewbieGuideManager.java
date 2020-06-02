package com.tsinghuabigdata.edu.ddmath.commons.newbieguide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;

import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

/**
 * 浮层引导页管理
 */
public class NewbieGuideManager {

    private static final String TAG = "newbie_guide";

    private Activity mActivity;
    private NewbieGuide mNewbieGuide;
    private SharedPreferences sp;
    private String activityname;

    public NewbieGuideManager(Activity activity, String classname, String version ) {
        mNewbieGuide = new NewbieGuide(activity);
        mNewbieGuide.setEveryWhereTouchable( false );
        sp = activity.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        mActivity = activity;
        activityname = classname + version;
    }

    public NewbieGuide getNewbieGuide(){
        return mNewbieGuide;
    }

//    public NewbieGuideManager addView(View view, int shape, String tips, String btntext ) {
//
//        //高亮区域
//        mNewbieGuide.addHighLightView(view, shape);
//
//        //文本提示
//        mNewbieGuide.addMessage( tips, ScreenUtils.dpToPx(mActivity, 150), 400 );
//
//        //按钮
//        mNewbieGuide.addBtnTv( btntext, 600, 700 );
//
//        return this;
//    }

    /**
     * 第一步时调用
     */
    public void show() {

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ){
            loginInfo = AccountUtils.getLoginParent();
            if( loginInfo == null ){
                AppLog.d("guide manager loginInfo = null");
                return;
            }
        }

        mNewbieGuide.show();
        //
        String rid = activityname + loginInfo.getAccountId();

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean( rid, false);
        editor.apply();
    }

    /**
     * 其他步调用
     */

    public void show(int delayTime) {

        if( delayTime < 50 )
            delayTime = 50;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mNewbieGuide.show();
            }
        }, delayTime);
    }

    public void showWithListener(NewbieGuide.OnGuideChangedListener onGuideChangedListener) {
        mNewbieGuide.setOnGuideChangedListener(onGuideChangedListener);
    }

    /**
     * 判断新手引导也是否已经显示了
     */
    public static boolean isNeverShowed(Activity activity, String classname, String version  ) {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ){
            loginInfo = AccountUtils.getLoginParent();
            if( loginInfo == null ){
                return false;
            }
        }
        if( activity == null ) return false;
        //
        String rid = classname + version + loginInfo.getAccountId();
        return activity.getSharedPreferences(TAG, Activity.MODE_PRIVATE).getBoolean(rid, true);
    }

}
