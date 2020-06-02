package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/10/17.
 */

public class DeviceUtils {

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public static boolean hasSimCard(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
        return false;
    }

    public boolean checkPhoneExist(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            int st = telephonyManager.getPhoneType();
            return TelephonyManager.PHONE_TYPE_NONE != st;
        }
        return false;
    }

    /**
     * 发送邮件
     */
    public static void sendMail(Context context, String email, String title, String data) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, data);
        context.startActivity(intent);
    }

    /**
     * 动态设置手机端首页导航栏
     */
    public static void setMainPhoneParams(Context context, LinearLayout llTeam) {
        int phoneRemainDistance = DataUtils.getPhoneMianDistance(context);
        int phoneNavDistance = DensityUtils.dp2px(context, 244);
        LogUtils.i("phoneRemainDistance=" + phoneRemainDistance + " phoneNavDistance=" + phoneNavDistance);
        if (phoneRemainDistance < phoneNavDistance) {
            int imageHeight = (phoneRemainDistance - DensityUtils.dp2px(context, 11 * 4 + 16 + 6)) / 4;
            LogUtils.i("imageHeight=" + imageHeight);
            for (int i = 0; i < llTeam.getChildCount(); i++) {
                LinearLayout child = (LinearLayout) llTeam.getChildAt(i);
                ImageView imageView = (ImageView) child.getChildAt(0);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.weight = imageHeight;
                layoutParams.height = imageHeight;
                imageView.setLayoutParams(layoutParams);
            }
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llTeam.getLayoutParams();
            params.height = phoneRemainDistance - DensityUtils.dp2px(context, 6);
            llTeam.setLayoutParams(params);
        }
    }


}
