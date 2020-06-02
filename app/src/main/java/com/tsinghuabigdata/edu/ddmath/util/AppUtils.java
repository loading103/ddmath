package com.tsinghuabigdata.edu.ddmath.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.BuildConfig;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.receive.NetworkStateRecevie;
import com.tsinghuabigdata.edu.session.AppSession;

import java.util.List;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/12.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.util
 * @createTime: 2015/11/12 19:19
 */
public class AppUtils {

    /**
     * 检测磁盘是否挂载
     */
    public static boolean isMounted(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    /**
     * 获取本包信息
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info;
        } catch (Exception e) {
            AppLog.i("err", e);
            return null;
        }
    }

    /**
     * 获取手机序列化
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            // 取AndroidId
            deviceId = getAndroidId(context);
        }
        if (TextUtils.isEmpty(deviceId)) {
            // 无效deviceId
            deviceId = "00000-00000";
        }
        return deviceId;
    }

    public static String getAndroidId(Context context) {
        return "id" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getChannelId(){
        Context context = ZxApplication.getApplication();
        if( context!=null ){
            return context.getResources().getString( R.string.channelId );
        }
        return "8R7S6";
    }
    /**
     * 是否无网络环境
     *
     * @return
     */
    /*public static boolean isNoNetwork() {
        Integer state = AppSession.getInstance().get(AppConst.SESSION_NETWORK_STSTE);
        return state != null && state == NetworkStateRecevie.NONETWORK;
    }*/

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 是否Wifi环境
     *
     * @return
     */
    public static boolean isWifi() {
        Integer state = AppSession.getInstance().get(AppConst.SESSION_NETWORK_STSTE);
        return state != null && state == NetworkStateRecevie.WIFI;
    }

    /**
     * 是否是Debug模式
     *
     * @return
     */
    public static boolean isDebug() {
        return "debug".equals(BuildConfig.BUILD_TYPE);
    }
//    public static boolean isBeta() {
//        return "beta".equals(BuildConfig.BUILD_TYPE);
//    }
//    /**
//     * 是否是release模式
//     *
//     * @return
//     */
//    public static boolean isRelease() {
//        return "release".equals(BuildConfig.BUILD_TYPE);
//    }

    /**
     * 是否前端运行
     *
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
            return true;
        }

        return false;
    }

    public static ActivityManager.RunningTaskInfo getTopTask(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = mActivityManager.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            return tasks.get(0);
        }
        return null;
    }

    /**
     * 判断目标页面是否运行在前端
     *
     * @param topTask
     * @param packageName
     * @param activityName
     * @return
     */
    public static boolean isTopActivity(
            ActivityManager.RunningTaskInfo topTask,
            String packageName,
            String activityName) {
        if (topTask != null) {
            ComponentName topActivity = topTask.topActivity;

            if (topActivity.getPackageName().equals(packageName) &&
                    topActivity.getClassName().equals(activityName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkPackageExist(Context context,String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 访问网页 增加时间戳 解决网页缓存问题
     *
     * @param url uu
     * @return url
     */
    public static String getUrlTimestamp(String url) {
        if( TextUtils.isEmpty(url) )
            return "";
        if( url.contains("phone.html#") ){
            url = url.replace("phone.html#","phone.html?t="+System.currentTimeMillis()+"#" );
        }
        return url;
    }
    /**
     * 检查下载管理器是否可以使用
     * @param context context
     * @return boolean
     */
    public static boolean isDownloadManagerAvailable(Context context) {

//        try {
            int status = context.getPackageManager().getApplicationEnabledSetting( "com.android.providers.downloads");
            return ( status == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
              || status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED );
//        } catch (Exception e) {
//            return false;
//        }

    }

    //发送邮件
    public static void startEmail( Context context ) {
        Uri uri = Uri.parse("mailto:"+ AppConst.SERVER_EMAIL);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }

    //拨打公司电话
    public static void dial( Context mContext ) {
        if (DeviceUtils.hasSimCard( mContext ) ) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4009928918"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            ToastUtils.showShort(mContext, R.string.cannot_dial);
        }
    }

    //动态获取App名称
    public static String getAppName(){
        return ZxApplication.getApplication().getBaseContext().getResources().getString(R.string.app_name);
    }
}
