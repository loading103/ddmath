package com.tsinghuabigdata.edu.ddmath.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tsinghuabigdata.edu.ddmath.inter.CartoomViewNameListener;
import com.umeng.analytics.MobclickAgent;


public class RoboBaseActivity extends AppCompatActivity implements CartoomViewNameListener {

    public static final String ACTION = "FINISH_ALL";

    private FinishReceiver mFinishReceiver;
    protected boolean bFinishroadcast = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFinishReceiver = new FinishReceiver();
        startMonitoring();

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

    protected void startMonitoring() {
        IntentFilter intentFilter = new IntentFilter(ACTION);
        registerReceiver(mFinishReceiver, intentFilter);
    }

    protected void stopMonitoring() {
        unregisterReceiver(mFinishReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMonitoring();
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


}
