package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;

/**
 * 学校作业 基类
 */
public class ScWorkBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startAddImageReceiver();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopAddImageReceiver();
    }

    //--------------------------------------------------------------------------------------------------------
    private CloseReceiver mReceiver;
    private void startAddImageReceiver(){
        mReceiver = new CloseReceiver();
        IntentFilter intentFilter = new IntentFilter( AppConst.ACTION_CLOSE_ACTIVITY );
        registerReceiver(mReceiver, intentFilter);
    }
    private void stopAddImageReceiver(){
        unregisterReceiver(mReceiver);
    }
    class CloseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听消息
            if ( AppConst.ACTION_CLOSE_ACTIVITY.equals( intent.getAction() ) ){
                finish();
            }
        }
    }

}
