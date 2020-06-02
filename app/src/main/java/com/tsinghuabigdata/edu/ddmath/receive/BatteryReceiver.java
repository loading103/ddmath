package com.tsinghuabigdata.edu.ddmath.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

/**
 * 获取电池信息的通知
 * Created by HP on 2016/8/3.
 */
public class BatteryReceiver {

    private static final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) ;
            {
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    AppLog.d("正在充电");
                } else {
                    AppLog.d("没有充电");
                }

                //获取当前电量
                int level = intent.getIntExtra("level", 0);
                //电量的总刻度
                int scale = intent.getIntExtra("scale", 100);

                AppLog.d("电池电量为"+((level*100)/scale)+"%");
            }
        }
    };

    public static void register(Context context) {
        context.registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public static void unRegister(Context context) {
        context.unregisterReceiver(mBatteryReceiver);
    }
}
