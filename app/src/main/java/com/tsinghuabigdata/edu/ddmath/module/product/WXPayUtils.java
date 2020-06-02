package com.tsinghuabigdata.edu.ddmath.module.product;

import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.string.MD5;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2017/12/12.
 */

public class WXPayUtils {
    //JSAPI（微信小程序支付），APP（微信app支付），NATIVE（扫码支付）
    public static final String APPPAY  = "APP";
    public static final String SCANPAY = "NATIVE";

    /**
     * 地址拼接
     */
    public static String getSignParams(Map<String, String> params) {
        // 添加url参数
        String param = null;
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            param = sb.toString();
        }
        return param;
    }

    public static String makeSign(String signParams) {
        String stringSignTemp = signParams + "&key=ASJKVndJNDJF2349320452QWER2QR5E3";//注：key为商户平台设置的密钥key
        //        String sign = MD5(stringSignTemp).toUpperCase() = "9A0A8659F005D6984697E2CA0A9CF3B7" //注：MD5签名方式;
        LogUtils.i("makeSign =" + stringSignTemp);
        return MD5.getStringMD5(stringSignTemp).toUpperCase();
    }

    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    //套餐
    public static Map<String, String> getUnifiedorderSuiteParams(ProductBean productBean, int bean, String payType) {
        return getUnifiedorderParams(false, bean, null, payType, productBean.getProductSuiteId(), productBean.getName(), productBean.getChargeDdAmt());
    }

    //套题
    public static Map<String, String> getUnifiedorderPracticeParams(PracticeBean practiceBean, int bean, String payType) {
        return getUnifiedorderParams(true, (practiceBean.getXuedou()-bean), null, payType, null, null, 0);
    }

    //充值学豆
    public static Map<String, String> getUnifiedorderBeanParams(RewardBean rewardBean, String payType) {
        return getUnifiedorderParams(true, rewardBean.getRechargeMoney(), rewardBean.getReturnSettingId(), payType, null, null, 0);
    }

    private static Map<String, String> getUnifiedorderParams(boolean isExcharge, int bean, String returnSettingId, String payType, String productId, String productName, int exchangeDdAmt ) {
        Map<String, String> params = new LinkedHashMap<>();

        params.put("tradeType", payType);
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( classInfo == null ){
            //家长端
            classInfo = AccountUtils.getCurrentClassInfoForParent();
            LoginInfo loginInfo = AccountUtils.getLoginParent();
            if( loginInfo != null ) params.put("accountId", loginInfo.getAccountId() );

            UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
            if( detailinfo!=null )     params.put("studentId",detailinfo.getStudentId() );
        }
        if ( classInfo != null) {
            params.put("classId", classInfo.getClassId());
        }
        params.put("appId", AppConst.APP_ID);
        if (isExcharge) {
            //充值学豆 单位分
            params.put("rechargeMoney", getPrice(bean));
            params.put("costDdamt", "0");
        } else {
            //购买套餐 单位分
            params.put("productSuiteId", productId);
            params.put("productSuiteName", productName);
            params.put("rechargeMoney", getPrice(exchangeDdAmt - bean));
            params.put("costDdamt", String.valueOf(bean));
        }
        if (!TextUtils.isEmpty(returnSettingId)) {
            params.put("returnSettingId", returnSettingId);
        }
        return params;
    }

    //根据学豆折算价格 单位分
    public static String getPrice(int bean) {
        return String.valueOf(bean * 10);
    }
}
