package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.tsinghuabigdata.edu.ddmath.R.id.tv_monitor;

/**
 * 网络监控View
 * 每3秒轮询请求一次http post的请求，每次请求发送固定32kb大小的数据包，然后通过发送大小除以响应时长得到每秒的上速度
 * Created by HP on 2016/6/30.
 */
public class NetworkMonitorView extends LinearLayout {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private boolean startMonitor;

    private ImageView netstatusImage;
    private TextView uploadSpeedView;

    private OkHttpClient client = new OkHttpClient();

    private int speed;
    private long threshold;

    public NetworkMonitorView(Context context) {
        super(context);
        init();
    }

    public NetworkMonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NetworkMonitorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 开始监控
     */
    public synchronized void startMonitor() {
        AppLog.i("startMonitor 001");
        setVisibility( View.VISIBLE );

        AppLog.i("startMonitor 002");
        if (startMonitor) {
            //AppLog.i("不能重复启动监控");
            return;
        }
        AppLog.i("startMonitor 003");
        startMonitor = true;
        new UploadSpeedTask().start();
    }

    /**
     * 停止监控
     */
    public synchronized void stopMonitor() {
        setVisibility( View.GONE );
        startMonitor = false;
    }

    public boolean getSpeedPoor() {
        return speed < (threshold == 0 ? AppConst.MIN_SPEED :threshold);
    }

    protected void onReceiverPacket(long startTime, long endTime, long packetSize, long threshold) {
        if (!AppUtils.isNetworkConnected(getContext())) {
            setSpeedState( 0 );
        } else {
            long time = endTime - startTime;
            float speedByte = 1f * packetSize / time * 1000;
            speed = Math.round(speedByte / 1024);
            this.threshold = threshold;
            setSpeedState( speed );
        }
    }

    //------------------------------------------------------------------------------------
    private void init() {
        inflate(getContext(), GlobalData.isPad()?R.layout.layout_network_monitor:R.layout.layout_network_monitor_phone, this);
        netstatusImage  = (ImageView) findViewById( R.id.network_sttaus );
        uploadSpeedView = (TextView) this.findViewById(tv_monitor);

        // 10秒连接超时
        client.setConnectTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        client.setReadTimeout(10 * 1000, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void setNetState( int state ) {
        switch ( state ){
            case 0:{
                netstatusImage.setImageResource( R.drawable.ic_signal1 );
                break;
            }
            case 1:{
                netstatusImage.setImageResource( R.drawable.ic_signal2 );
                break;
            }
            case 2:{
                netstatusImage.setImageResource( R.drawable.ic_signal3 );
                break;
            }
            case 3:{
                netstatusImage.setImageResource( R.drawable.ic_signal4 );
                break;
            }
            case 4:{
                netstatusImage.setImageResource( R.drawable.ic_signal5 );
                break;
            }
            default:
                break;
        }
    }

    private void setSpeedState( final int speed ) {

        //<5K: 0格；<10K: 1格；<30K: 2格; <50K: 3格; >=50K:4格
        int netstate;
        if( speed < 5 ){
            netstate = 0;
        }else if( speed < 10 ){
            netstate = 1;
        }else if( speed < 30 ){
            netstate = 2;
        }else if( speed < 50 ){
            netstate = 3;
        }else{
            netstate = 4;
        }
        setNetState( netstate );
        uploadSpeedView.setText( String.valueOf(speed) );
        uploadSpeedView.append(" KB/s");
    }

//    private void clearState() {
//        netstatusImage.setVisibility(GONE);
//        uploadSpeedView.setVisibility(GONE);
//    }

//    private void collectNetInfo(){
//
//        if( !NetWorkUtil.IsNetWorkEnable(getContext()) )return;
//
//        StringBuilder netinfo = new StringBuilder();
//        netinfo.append("NetType:").append( NetWorkUtil.getCurrentNetworkType( getContext() ) );
//
//        if( NetWorkUtil.isWifiAvailable( getContext() ) ){
//            netinfo.append(",mac:").append( NetWorkUtil.getMacAddress(getContext()));
//            netinfo.append(",rssi:").append( NetWorkUtil.getWifiRssi(getContext()));
//            netinfo.append(",ssid:").append( NetWorkUtil.getWifiSsid(getContext()));
//        }else{
//            netinfo.append(",运营商:").append( NetWorkUtil.getProvider(getContext()));
//            netinfo.append(",IMEI:").append( NetWorkUtil.getImei(getContext()));
//            netinfo.append(",IMSI:").append( NetWorkUtil.getPhoneImsi(getContext()));
//        }
//        AppLog.i( ErrTag.TAG_NET, netinfo.toString() );
//    }

    //-----------------------------------------------------------------------------------
    private class UploadSpeedTask extends Thread {
        @Override
        public void run() {
            while (startMonitor) {
                //AppLog.i("请求网络监控接口");
                if (!AppUtils.isNetworkConnected(getContext())) {
                    notifyMainThread(0, 1, 0, 0);
                } else {
                    try {
                        long[] res = sendMonitorPacket();
                        notifyMainThread(res[0], res[1], res[2], res[3]);
                    } catch (Exception ex) {
                        AppLog.i("检查网络异常", ex);
                        notifyMainThread(0, 1, 0, 1);
                    }
                }
                SystemClock.sleep(AppConst.NET_MONITOR_SLEEP);
            }
            //AppLog.i("停止监控线程,ID " + getId());
        }

        /**
         * 发送测试数据包
         */
        private long[] sendMonitorPacket() throws JSONException, IOException {
            final long startTime = System.currentTimeMillis();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", getContent());

            RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder().post(requestBody).url(AppRequestConst.RESTFUL_ADDRESS + AppRequestConst.SPEED_TEST).build();
            Response response = client.newCall(request).execute();
            long endTime = System.currentTimeMillis();

            if (response.isSuccessful()) {
                JSONObject object = new JSONObject(response.body().string()).getJSONObject("data");
                long size = object.getLong("networkSpeed");
                long threshold = object.getInt("threshold");
                return new long[] {startTime, endTime, size, threshold};
            } else {
                return new long[] {startTime, endTime, 0, 0};
            }
        }

        private void notifyMainThread(final long startTime, final long endTime, final long packetSize, final long threshold) {
            post(new Runnable() {
                @Override
                public void run() {
                    onReceiverPacket(startTime, endTime, packetSize, threshold);
                }
            });
        }

        private String getContent() {
            int size = AppConst.MONITOR_SEND_SIZE * 1024;
            StringBuilder content = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                // 随机产生A-Z
                char c = (char) (random.nextInt(91-65) + 65);
                content.append(c);
            }
            return content.toString();
        }
    }

}
