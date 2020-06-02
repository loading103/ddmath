package com.tsinghuabigdata.edu.ddmath.module.ddwork;

import android.app.Activity;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 下载进度动画
 */
public class DownloadProgressManager {

    private static final int MAX_POINT = 4;

    private TextView textView;

    private Timer mTimer;
    private Activity mActivity;

    //显示.的个数
    private int count = 0;

    public DownloadProgressManager() {

    }

    /**
     * 启动网络状态监测
     * @param activity ac
     */
    public void start(@NonNull Activity activity, @NonNull TextView textView){
        this.mActivity = activity;
        this.textView  = textView;

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
                showStatus();
            }
        }, 1000, 1000);     //延时1000ms, 间隔 1000ms
    }

    /**
     * 停止监测
     */
    public void stop(){
        if( mTimer!=null ) {
            mTimer.cancel();
            mTimer = null;
        }
    }


    //-------------------------------------------------------------------------------------------

    private void showStatus(){
        if( mActivity==null || textView==null) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                count = count % MAX_POINT;

                StringBuilder sb = new StringBuilder();
                sb.append("下载中");

                for( int i=0; i<count; i++ )
                    sb.append(".");
                for( int i=0; i<MAX_POINT-count-1; i++ )
                    sb.append(" ");

                textView.setText( sb.toString() );
            }
        });
    }
}
