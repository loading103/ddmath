package com.tsinghuabigdata.edu.ddmath.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.session.AppSession;
import com.tsinghuabigdata.edu.utils.RestfulUtils;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/20.
 * </p>
 *
 * 监听网络变化
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.receive
 * @createTime: 2015/11/20 15:38
 */
public class NetworkStateRecevie extends BroadcastReceiver {


    public static final int WIFI = 1;
    public static final int TRAFFIC = 2;
    public static final int NONETWORK = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 监听到网络变化后需要重置DNS
        RestfulUtils.useProxy(null, 0);
        NetworkStateRecevie.setNetWorkState(context);
    }

    public static void setNetWorkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        String type = null;
        try {
            type = activeInfo != null ? activeInfo.getTypeName().toLowerCase() : "";
        } catch (Exception e) {
            AppLog.i("err", e);
        }
        if("wifi".equals(type) && wifiInfo != null && wifiInfo.isConnected()){
            AlertManager.toastForForeground(context, "wifi环境", true);
            AppSession.getInstance().put(AppConst.SESSION_NETWORK_STSTE, NetworkStateRecevie.WIFI);
        } else if(mobileInfo != null && mobileInfo.isConnected()){
            AlertManager.toastForForeground(context, "当前使用2G/3G/4G网络，接听电话会导致网络中断！", true);
            AppSession.getInstance().put(AppConst.SESSION_NETWORK_STSTE, NetworkStateRecevie.TRAFFIC);
        } else {
            AlertManager.toastForForeground(context, "网络已断开", true);
            AppSession.getInstance().put(AppConst.SESSION_NETWORK_STSTE, NetworkStateRecevie.NONETWORK);
        }
    }
}
