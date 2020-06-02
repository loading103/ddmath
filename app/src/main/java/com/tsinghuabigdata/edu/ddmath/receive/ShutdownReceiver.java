package com.tsinghuabigdata.edu.ddmath.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;


public class ShutdownReceiver {

    private static final BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            AppLog.i("shutdown", "onReceive: action = " + action);
            if (Intent.ACTION_SHUTDOWN.equals(action)) {
                AppLog.i("正在关机");
                //BehaviorUtil.quitApp();
            }
        }
    };

    public static void register(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SHUTDOWN);
        context.registerReceiver(mShutdownReceiver, intentFilter);
    }

    public static void unRegister(Context context) {
        context.unregisterReceiver(mShutdownReceiver);
    }
}
