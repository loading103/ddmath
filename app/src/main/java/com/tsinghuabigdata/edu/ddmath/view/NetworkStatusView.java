package com.tsinghuabigdata.edu.ddmath.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 网络状态变化 View
 */
@Deprecated
public class NetworkStatusView extends LinearLayout {

    private static final int ST_DICONNECT = 0;     //网络断开,无信号
    private static final int ST_CONNECT_1 = 1;      //信号强度 差
    private static final int ST_CONNECT_2 = 2;     //信号强度 一般
    private static final int ST_CONNECT_3 = 3;     //信号强度 较好
    private static final int ST_CONNECT_4 = 4;     //信号强度 最好


    private TextView textView;
    private ImageView imageView;

    private WifiManager wifiManager = null; //Wifi管理器
    private Timer mTimer;

    private Activity mActivity;


    public NetworkStatusView(Context context) {
        super(context);

        init();
    }

    public NetworkStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public NetworkStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    /**
     * 启动网络状态监测
     * @param activity ac
     */
    public void start(@NonNull Activity activity){
        mActivity = activity;

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //获得信号强度值
                int level = wifiInfo.getRssi();
                //根据获得的信号强度发送信息
                if (level <= 0 && level >= -50) {
                    showNetworkStatus( ST_CONNECT_4 );
                } else if (level < -50 && level >= -70) {
                    showNetworkStatus( ST_CONNECT_3 );
                } else if (level < -70 && level >= -80) {
                    showNetworkStatus( ST_CONNECT_2 );
                } else if (level < -80 && level >= -100) {
                    showNetworkStatus( ST_CONNECT_1 );
                } else {
                    showNetworkStatus( ST_DICONNECT );
                }
            }
        }, 1000, 5000);     //延时1000ms, 间隔 5000ms

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

    /**
     * 得到文本对象
     */
    public TextView getTextView(){
        return textView;
    }

    //-------------------------------------------------------------------------------------------
    @SuppressLint("WifiManagerLeak")
    private void init() {
        inflate( getContext(), R.layout.view_network_status, this );
        textView = (TextView)findViewById( R.id.view_network_textview );
        imageView= (ImageView)findViewById( R.id.view_network_imageview );
        imageView.setVisibility(INVISIBLE);     //默认不显示

        wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);

    }

    private void showNetworkStatus(final int status ){
        if( mActivity==null ) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setStatus( status );
            }
        });
    }

    private void setStatus(int status){

        //显示
        imageView.setVisibility(VISIBLE);

        switch ( status ){
            case ST_CONNECT_1:{
                imageView.setImageResource( R.drawable.ic_net_1);
                break;
            }
            case ST_CONNECT_2:{
                imageView.setImageResource( R.drawable.ic_net_2);
                break;
            }
            case ST_CONNECT_3:{
                imageView.setImageResource( R.drawable.ic_net_3);
                break;
            }
            case ST_CONNECT_4:{
                imageView.setImageResource( R.drawable.ic_net_4);
                break;
            }
            default:{
                imageView.setImageResource( R.drawable.ic_net_0);
                break;
            }
        }
    }

}
