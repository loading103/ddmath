package com.tsinghuabigdata.edu.ddmath.module.login.model;

import android.content.Context;

import com.tsinghuabigdata.edu.commons.codec.MD5Utils;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;

/**
 * Created by 28205 on 2017/2/17.
 */
public class LoginSuccessHandler {
    /**
     * 保存用户名和密码
     */
    public static void loginSuccessHandler(Context context, String userName, String password) {
        PreferencesUtils.putString(context, AppConst.LOGIN_NAME, userName);
        PreferencesUtils.putString(context, AppConst.LOGIN_PASS, MD5Utils.stringToMD5(password));
    }
//    public static void loginSuccessHandlerMd5(Context context, String userName, String passwordmd5) {
//        PreferencesUtils.putString(context, AppConst.LOGIN_NAME, userName);
//        PreferencesUtils.putString(context, AppConst.LOGIN_PASS,passwordmd5 );
//    }

    public static void cacheParentHandler(Context context, String userName, String password) {
        PreferencesUtils.putString(context, AppConst.LOGIN_PARENTNAME, userName);
        PreferencesUtils.putString(context, AppConst.LOGIN_PARENTPASS, MD5Utils.stringToMD5(password));
    }

}
