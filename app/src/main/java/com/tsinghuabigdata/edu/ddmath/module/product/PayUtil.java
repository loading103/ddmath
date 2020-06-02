package com.tsinghuabigdata.edu.ddmath.module.product;

import android.content.Context;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.MVPModel.PayModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */

public class PayUtil {

    //DIRECT(1,"直接付款") SUBSTITUTE(2,"代付款")
    public static final String DIRECT     = "DIRECT";
    public static final String SUBSTITUTE = "SUBSTITUTE";

    //套餐
    public static Map<String, String> getAliPaySuiteSignParams(ProductBean productBean, int bean, String payType) {
        return getAliPaySignParams(false, bean, null, payType,productBean.getProductSuiteId(),productBean.getName(),productBean.getChargeDdAmt());
    }

    //精品套题
    public static Map<String,String> getAliPayPracticeSignParams(PracticeBean practiceBean, int bean, String payType){
        return getAliPaySignParams(true,(practiceBean.getXuedou()-bean), null, payType, null, null, 0 );
    }

    //充值学豆
    public static Map<String, String> getAliPayBeanSignParams(RewardBean rewardBean, String payType) {
        return getAliPaySignParams( true, rewardBean.getRechargeMoney(), rewardBean.getReturnSettingId(), payType,null,null,0 );
    }

    private static Map<String, String> getAliPaySignParams( boolean isExcharge, int bean, String returnSettingId, String payType, String productId, String productName, int exchangeDdAmt ) {
        Map<String, String> params = new LinkedHashMap<>();
        LoginInfo loginUser = AccountUtils.getLoginUser();
        if( loginUser == null ){    //家长端
            loginUser = AccountUtils.getLoginParent();
        }
        if (loginUser != null) {
            params.put("accountId", loginUser.getAccountId());
        }
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ){
            detailinfo = AccountUtils.getParentUserDetailinfo();
        }
        if (detailinfo != null) {
            params.put("studentId", detailinfo.getStudentId());
            params.put("studentName", detailinfo.getReallyName());
            params.put("loginName", detailinfo.getLoginName());
        }
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( classInfo == null ){
            classInfo = AccountUtils.getCurrentClassInfoForParent();
        }
        if ( classInfo != null) {
            params.put("classId", classInfo.getClassId());
        }
        if ( isExcharge) {
            //充值学豆
            params.put("rechargeMoney", getPrice(bean));
            params.put("costDdamt", "0");
        } else {
            //购买套餐
            params.put("productSuiteId", productId);
            params.put("productSuiteName", productName);
            params.put("rechargeMoney", getPrice(exchangeDdAmt - bean));
            params.put("costDdamt", String.valueOf(bean));
        }
        if (!TextUtils.isEmpty(returnSettingId)) {
            params.put("returnSettingId", returnSettingId);
        }
        params.put("paymentMethod", payType);
        return params;
    }

    //根据学豆折算价格
    public static String getPrice(int bean) {
        if (bean % 10 == 0) {
            return bean / 10 + "";
        }
        float pr = (float) bean / 10;
        return pr + "";
    }

    public static void updateStatusbefore(String tradeNo) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("tradeNo", tradeNo);
        params.put("status", "2");
        //params.put("remark", "");
        new PayModel().updateTrade(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                LogUtils.i("updateStatusbefore onSuccess");
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                LogUtils.i("updateStatusbefore onFail ex:" + ex.getMessage());
            }
        });
    }

    public static void updateStatusAfter(String tradeNo, String remark) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("tradeNo", tradeNo);
        params.put("status", "4");
        params.put("remark", remark);
        new PayModel().updateTrade(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                LogUtils.i("updateStatus onSuccess");
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                LogUtils.i("updateStatus onFail ex:" + ex.getMessage());
            }
        });
    }

    //检查支付宝是否已安装
    public static boolean checkAliPayExist(Context context) {
        return AppUtils.checkPackageExist(context, "com.eg.android.AlipayGphone");
    }

    //检查微信是否已安装
    public static boolean checkWxExist(Context context) {
        return AppUtils.checkPackageExist(context, "com.tencent.mm");
    }

    //兑换精品套题商品,不区分用免费使用权 还是 学豆，服务器后台处理
    //购买与 兑换一起
    public static void exchangePracticeProduct( final PracticeBean practiceBean, RequestListener successListener ){

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( detailinfo == null || classInfo == null ) return;

        //开始兑换
        new ProductModel().exchangePracticeProduct( detailinfo.getStudentId(), classInfo.getClassId(), practiceBean.getProductBean().getProductId(),practiceBean.getProductBean().getPrivilegeId(), practiceBean.getExcluId(), successListener );
    }
}
