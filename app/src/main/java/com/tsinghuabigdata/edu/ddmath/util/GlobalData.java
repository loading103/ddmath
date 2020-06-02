package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;


/**
 * 管理全局变量的类
 * Created by Administrator on 2017/3/15.
 */

public class GlobalData {



    private static boolean isPad = false;  //是否为平板
    private static String expandPrivilege;  //我要购买界面是否展开同步微课套餐

//    public static void clear() {
//
//    }

    public static boolean isPad() {
        return isPad;
    }

    public static String getCurrClassId() {
        ZxApplication application = ZxApplication.getApplication();
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( application!=null && loginInfo!=null ){
            return PreferencesUtils.getString( application, loginInfo.getAccountId(), "" );
        }
//        Object idstr = AppSessionCache.getInstance().get(AppConst.SESSION_CLASS_ID);
//        if (idstr instanceof String){
//            return (String)idstr;
//        }
        return "";
    }

    public static void setClassId(String classId) {
        ZxApplication application = ZxApplication.getApplication();
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( application!=null && loginInfo!=null && !TextUtils.isEmpty(classId)){
            PreferencesUtils.putString( application, loginInfo.getAccountId(), classId );
        }
//        AppSessionCache cache = AppSessionCache.getInstance();
//        if( cache != null ){
//            cache.remove(AppConst.SESSION_CLASS_ID);
//            if(!TextUtils.isEmpty(classId)) cache.put(AppConst.SESSION_CLASS_ID,classId);
//        }
    }
//
//    public static String getExpandPrivilege() {
//        return expandPrivilege;
//    }
//
//    public static void setExpandPrivilege(String expandPrivilege) {
//        GlobalData.expandPrivilege = expandPrivilege;
//    }

    /**
     * 判断是否为平板
     *
     */
    public static void judgePad(Context context) {
        //        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //        Display display = wm.getDefaultDisplay();
        //        // 屏幕宽度
        //        //float screenWidth = display.getWidth();
        //        // 屏幕高度
        //        //float screenHeight = display.getHeight();
        //        DisplayMetrics dm = new DisplayMetrics();
        //        display.getMetrics(dm);
        //        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        //        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        //        // 屏幕尺寸
        //        double screenInches = Math.sqrt(x + y);
        //        // 大于6尺寸则为Pad
        //        if (screenInches < 6.0) {
        //            isPad = false;
        //        }
        isPad = isPad(context);
    }

    private static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
