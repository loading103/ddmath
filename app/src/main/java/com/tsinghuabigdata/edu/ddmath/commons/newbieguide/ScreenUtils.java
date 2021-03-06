package com.tsinghuabigdata.edu.ddmath.commons.newbieguide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.ScreenUtil;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.lang.reflect.Field;

public class ScreenUtils {

    private ScreenUtils() {
        throw new AssertionError();
    }

    public static float dpToPx(Context context, float dp) {
        if(context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

    /**
     * 获取状态栏的高
     */
    public static int getStatusBarHeight(Activity context) {
        Rect frame = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if(0 == statusBarHeight) {
            statusBarHeight = getStatusBarHeightByReflection(context);
        }
        return statusBarHeight;
    }

    /**
     * 获取屏幕内容高度
     */
    public static int getScreenContentHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenContentHeight = dm.heightPixels- ScreenUtil.getNavBarHeight(context);
        return screenContentHeight;
    }

    /**
     * 获取屏幕内容高度
     */
    public static int getPhoneScreenContentHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenContentHeight = dm.heightPixels;
        return screenContentHeight;
    }

    public static int getStatusBarHeightByReflection(Context context) {
        Class<?> c;
        Object obj;
        Field field;
        // 默认为38，貌似大部分是这样的
        int x, statusBarHeight = 38;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            AppLog.i( "", e1 );
        }
        return statusBarHeight;
    }
}
