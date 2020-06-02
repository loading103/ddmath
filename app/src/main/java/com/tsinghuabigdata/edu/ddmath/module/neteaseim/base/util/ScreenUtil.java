package com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;


import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.lang.reflect.Field;

public class ScreenUtil {
	private static final String TAG = "Demo.ScreenUtil";
	
	private static final double RATIO = 0.85;
	
	//private static int screenWidth;
	//private static int screenHeight;
	//public static int screenMin;// 宽高中，小的一边
	//public static int screenMax;// 宽高中，较大的值

	//public static float density;
	//public static float scaleDensity;
	//public static float xdpi;
	//public static float ydpi;
	//public static int densityDpi;
	
//	public static int dialogWidth;
	//public static int statusbarheight;
	//public static int navbarheight;

	
//	public static int dip2px(float dipValue) {
//		return (int) (dipValue * density + 0.5f);
//	}
//
//	public static int px2dip(float pxValue) {
//		return (int) (pxValue / density + 0.5f);
//	}
//
//	public static int sp2px(float spValue) {
//		return (int) (spValue * scaleDensity + 0.5f);
//	}

//	public static int getDialogWidth() {
//		dialogWidth = (int) (screenMin * RATIO);
//		return dialogWidth;
//	}

    public static void init(Context context) {
        if (null == context) {
            return;
        }
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
        int screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
        float density = dm.density;
		float scaleDensity = dm.scaledDensity;
		float xdpi = dm.xdpi;
		float ydpi = dm.ydpi;
		float densityDpi = dm.densityDpi;

        Log.d(TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density);
    }

//	public static int getDisplayHeight() {
//		if(screenHeight == 0){
//			GetInfo(NimCache.getContext());
//		}
//		return screenHeight;
//	}

	public static void GetInfo(Context context) {
		if (null == context) {
			return;
		}
		DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		int screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
		int screenMax = (screenWidth < screenHeight) ? screenHeight : screenWidth;
		float density = dm.density;
		float scaleDensity = dm.scaledDensity;
		float xdpi = dm.xdpi;
		float ydpi = dm.ydpi;
		int densityDpi = dm.densityDpi;
		int statusbarheight = getStatusBarHeight(context);
		int navbarheight = getNavBarHeight(context);
		Log.d(TAG, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density);
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			AppLog.i("",e);
		}
		return sbar;
	}

	public static int getNavBarHeight(Context context){
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}
}
