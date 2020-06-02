package com.tsinghuabigdata.edu.ddmath.module.floatwindow;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.tsinghuabigdata.edu.ddmath.module.floatwindow.view.CartoonBubbleView;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.view.CartoonDouDouView;


/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:悬浮窗统一管理，与悬浮窗交互的真正实现
 */
public class FloatWindowManager {
    /**
     * 悬浮窗
     */
    public static CartoonDouDouView mFloatLayout;
    public static CartoonBubbleView bubbleView;
    private static View backgroundView;
    private static WindowManager mWindowManager;

    private static WindowManager.LayoutParams toolbarParams;
    private static WindowManager.LayoutParams bubbleParams;
    private static WindowManager.LayoutParams backgroudParams;

    private static boolean mHasShown;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void createFloatWindow(Context context) {

        WindowManager windowManager = getWindowManager(context);
        //背景
        backgroundView = new View( context );
        backgroundView.setBackgroundColor(Color.argb( 0x80, 0,0,0 ));
        backgroundView.setVisibility( View.GONE );              //默认不显示
        backgroudParams = createWindowLayoutParams( context,WindowManager.LayoutParams.MATCH_PARENT );
        windowManager.addView(backgroundView, backgroudParams);

        //豆豆工具
        mFloatLayout = new CartoonDouDouView(context);
        mFloatLayout.setBackgroundView( backgroundView );
        toolbarParams = createWindowLayoutParams( context,WindowManager.LayoutParams.WRAP_CONTENT );
        windowManager.addView(mFloatLayout, toolbarParams);
        mFloatLayout.setParams(toolbarParams);

        //豆豆气泡
        bubbleView = new CartoonBubbleView( context );
        bubbleView.setVisibility( View.GONE );              //默认不显示
        bubbleParams = createWindowLayoutParams( context,WindowManager.LayoutParams.WRAP_CONTENT );
        windowManager.addView(bubbleView, bubbleParams);
        bubbleView.setParams( bubbleParams, toolbarParams );
        mFloatLayout.setBubbleView( bubbleView );

        mHasShown = true;

    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mFloatLayout.isAttachedToWindow();
        }
        if (mHasShown && isAttach && mWindowManager != null){
            mWindowManager.removeView(mFloatLayout);
            mWindowManager.removeView(bubbleView);
            mWindowManager.removeView(backgroundView);
        }
    }

    /**
     * 隐藏对话栏
     */
    public static void hide() {
        if (mHasShown){
            mWindowManager.removeViewImmediate(mFloatLayout);
            mWindowManager.removeViewImmediate(bubbleView);
            mWindowManager.removeViewImmediate( backgroundView );
        }
        mHasShown = false;
    }

    public static void show() {
        if (!mHasShown){
            mWindowManager.addView(backgroundView, backgroudParams );
            mWindowManager.addView(mFloatLayout, toolbarParams);
            mWindowManager.addView(bubbleView, bubbleParams);
        }
        mHasShown = true;
    }

    public static boolean hasShow(){
        return mHasShown;
    }
    //-------------------------------------------------------------------------------------------
    /**
     * 返回当前已创建的WindowManager。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    //
    private static WindowManager.LayoutParams createWindowLayoutParams( Context context, int wtype ){
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packname = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packname));
            if (permission) {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |  WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.START | Gravity.TOP;

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = screenWidth;
        wmParams.y = screenHeight;

        //设置悬浮窗口长宽数据
        wmParams.width = wtype;
        wmParams.height = wtype;

        return wmParams;
    }
}
