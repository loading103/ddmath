package com.tsinghuabigdata.edu.ddmath.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.inter.CartoomViewNameListener;
import com.umeng.analytics.MobclickAgent;

public class RoboActivity extends AppCompatActivity implements CartoomViewNameListener {

    public static final String ACTION = "FINISH_ALL";

    private FinishReceiver mFinishReceiver;
    private SystemBarTintManager tintManager;
    protected boolean transparent = true;
    protected boolean bFinishroadcast = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Custom_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //设置全屏显示
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initSystemBar(this);
        mFinishReceiver = new FinishReceiver();
        startMonitoring();

        String umstr = getUmEventName();
        if( !TextUtils.isEmpty(umstr) ){
            MobclickAgent.onEvent( this, umstr);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume( this );
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause( this );
    }

    public void setFinishroadcast( boolean b ){
        bFinishroadcast = b;
    }

    protected void startMonitoring() {
        IntentFilter intentFilter = new IntentFilter(ACTION);
        registerReceiver(mFinishReceiver, intentFilter);
    }

    public String getUmEventName() {
        return null;
    }
    protected void stopMonitoring() {
        unregisterReceiver(mFinishReceiver);
    }

    /**
     * 关闭所有的Activity
     */
    public void finishAll() {
        sendBroadcast(new Intent(ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMonitoring();
    }

    private void initSystemBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);

            tintManager = new SystemBarTintManager(activity);

            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            // 使用颜色资源
            if (transparent) {
                tintManager.setStatusBarTintColor(Color.TRANSPARENT);
                tintManager.setNavigationBarTintColor(Color.TRANSPARENT);
            } else {
                tintManager.setStatusBarTintColor(Color.BLACK);
                tintManager.setNavigationBarTintColor(Color.BLACK);
            }
        }

    }


    public void goActivity(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void goActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @TargetApi(19)
    private void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public String getViewName() {
        return "all";
    }

    /**
     * 退出监听
     */
    class FinishReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听退出消息
            if (ACTION.equals(intent.getAction()) && bFinishroadcast) {
                finish();
            }
        }
    }

//    public void setStatusbarBg(int colorid) {
//        if (tintManager != null) {
//            tintManager.setStatusBarTintResource(colorid);
//        }
//    }

}
