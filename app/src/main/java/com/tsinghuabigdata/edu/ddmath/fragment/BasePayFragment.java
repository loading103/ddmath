package com.tsinghuabigdata.edu.ddmath.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tsinghuabigdata.edu.ddmath.MVPModel.PayModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.BuyPracticeEvent;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshPracticeEvent;
import com.tsinghuabigdata.edu.ddmath.event.WxPayResultEvent;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.BuySuccessDialog;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.ReplacePayActivity;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.AlipaySignBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.PayResult;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.WxSignBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.dialog.PayBeanDialog;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.dialog.PaySuiteDialog;
import com.tsinghuabigdata.edu.ddmath.module.product.PayUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.WXPayUtils;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import static com.tsinghuabigdata.edu.ddmath.util.AccountUtils.getStudyBean;


/**
 * 带支付功能的基类
 */

public abstract class BasePayFragment extends MyBaseFragment {

    private static final int SDK_PAY_FLAG = 1;
    private IWXAPI api;

    private Context mContext;

    protected String mRechargeNum;
    protected boolean isParent = false;

    private ProductBean mBuyingProductBean;
    private PracticeBean mPracticeBean;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。

                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    LogUtils.i("SDK_PAY_FLAG resultInfo" + resultInfo);
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        //购买精品套题
                        if( mPracticeBean!=null ){
                            exchangePracticeProduct(mPracticeBean);
                        }else if( mBuyingProductBean != null ){
                            BuySuccessDialog dialog = new BuySuccessDialog( mContext, R.style.FullTransparentDialog);
                            dialog.setData( mBuyingProductBean.getVipLevel(), mBuyingProductBean.getProductVoList().size());
                            dialog.show();
                            if( !TextUtils.isEmpty(mBuyingProductBean.getProductSuiteId()) ){
                                EventBus.getDefault().post( new BuySuiteEvent(mBuyingProductBean.getProductSuiteId()));
                            }
                        }else{
                            ToastUtils.showShort(mContext, "支付成功");
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(mContext, payResult.getMemo(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = getContext();
        initData();
    }

    // 弹出选择支付方式dialog
    public void toPay(Object mBean) {

        if( mBean instanceof RewardBean){
            RewardBean rewardBean = (RewardBean)mBean;
            PayBeanDialog payBeanDialog = new PayBeanDialog( getContext(), rewardBean, isParent);
            payBeanDialog.setMethodListener(new PayBeanDialog.SelectMethodListener<RewardBean>() {
                @Override
                public void aliPay(RewardBean rewardBean) {
                    if (PayUtil.checkAliPayExist(mContext)) {
                        getAliPaySignDeduction(rewardBean);
                    } else {
                        ToastUtils.showShort(mContext, "您的手机还未安装支付宝APP，请先安装后再购买或者使用代付功能。");
                    }
                }

                @Override
                public void parentPay(RewardBean rewardBean) {
                    Intent intent = new Intent(mContext, ReplacePayActivity.class);
                    intent.putExtra(ReplacePayActivity.FROMPAYBEAN, true);
                    intent.putExtra(ReplacePayActivity.REWARDBEAN, rewardBean);
                    startActivity(intent);
                }

                @Override
                public void wxPay(RewardBean rewardBean) {
                    if (PayUtil.checkWxExist(mContext)) {
                        getWxPaySignDeduction(rewardBean);
                    } else {
                        ToastUtils.showShort(mContext, "您的手机还未安装微信APP，请先安装后再购买。");
                    }
                }
            });
            payBeanDialog.show();
        }else if( mBean instanceof ProductBean){
            final ProductBean productBean = (ProductBean)mBean;
            PaySuiteDialog paySuiteDialog = new PaySuiteDialog(mContext, productBean.getChargeDdAmt(), productBean.getName(), isParent);
            paySuiteDialog.setMethodListener(new PaySuiteDialog.selectMethodListener() {
                @Override
                public void beanCharge() {
                    exchangeProductSuite(productBean);
                }

                @Override
                public void aliPay(int bean) {
                    if (PayUtil.checkAliPayExist(mContext)) {
                        getAliPaySignDeduction(productBean, bean);
                    } else {
                        ToastUtils.showShort(mContext, "您的手机还未安装支付宝APP，请先安装后再购买或者使用代付功能。");
                    }
                }

                @Override
                public void parentPay(int bean) {
                    Intent intent = new Intent(mContext, ReplacePayActivity.class);
                    intent.putExtra(ReplacePayActivity.PRODUCTBEAN, productBean);
                    intent.putExtra(ReplacePayActivity.STUDYBEAN, bean);
                    startActivity(intent);
                }

                @Override
                public void wxPay(int bean) {
                    if (PayUtil.checkWxExist(mContext)) {
                        getWxPaySignDeduction(productBean, bean);
                    } else {
                        ToastUtils.showShort(mContext, "您的手机还未安装微信APP，请先安装后再购买。");
                    }
                }
            });
            paySuiteDialog.show();
        }else if( mBean instanceof PracticeBean){
            //走充值流程
            final PracticeBean practiceBean = (PracticeBean)mBean;
            mPracticeBean = practiceBean;
            PaySuiteDialog paySuiteDialog = new PaySuiteDialog(mContext, practiceBean.getXuedou(), ""/*practiceBean.getTitle()*/, isParent);
            paySuiteDialog.setTipsView( "你还没有该套题的学习特权，去购买套题开始愉快的学习吧～" );
            paySuiteDialog.setMethodListener(new PaySuiteDialog.selectMethodListener() {
                @Override
                public void beanCharge() {
                    exchangePracticeProduct(practiceBean);
                }

                @Override
                public void aliPay(int bean) {
                    if (PayUtil.checkAliPayExist(mContext)) {
                        getAliPaySignDeduction(practiceBean, bean);
                    } else {
                        ToastUtils.showShort(mContext, "您的手机还未安装支付宝APP，请先安装后再购买或者使用代付功能。");
                    }
                }

                @Override
                public void parentPay(int bean) {
                    Intent intent = new Intent(mContext, ReplacePayActivity.class);
                    intent.putExtra(ReplacePayActivity.PRACTICEBEAN, practiceBean);
                    intent.putExtra(ReplacePayActivity.STUDYBEAN, bean);
                    startActivity(intent);
                }

                @Override
                public void wxPay(int bean) {
                    if (PayUtil.checkWxExist(mContext)) {
                        getWxPaySignDeduction(practiceBean, bean);
                    } else {
                        ToastUtils.showShort(mContext, "您的手机还未安装微信APP，请先安装后再购买。");
                    }
                }
            });
            paySuiteDialog.show();
        }
    }

    //触发学豆更新
    protected abstract void updateLearnDouCount();

    //----------------------------------------------------------------------------------------------
    private void initData() {
        api = WXAPIFactory.createWXAPI(mContext, AppConst.APP_ID);
    }

    //套餐兑换
    private void exchangeProductSuite(final ProductBean suiteBean) {
        final StudyBean studyBean = getStudyBean();
        if (studyBean == null) {
            return;
        }
        //先区分学豆是否足够
        int currLearnDou = studyBean.getTotalDdAmt();
        if (currLearnDou < suiteBean.getChargeDdAmt()) {        //提示去充值学豆
            ToastUtils.showShort(mContext, "学豆不足");
        } else {
            startExchangeSuite(suiteBean);
        }
    }

    //执行兑换次数，调用接口
    private void startExchangeSuite(final ProductBean suiteBean) {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( classInfo==null || detailinfo==null || loginInfo == null ) return;

        new ProductModel().exchangeProductSuite( classInfo.getClassId(), detailinfo.getStudentId(), suiteBean.getProductSuiteId(), loginInfo.getAccountId(), new RequestListener<Boolean>() {
            @Override
            public void onSuccess(Boolean res) {

                //兑换失败
                if (!res) {
                    AlertManager.showCustomImageBtnDialog(mContext, "抱歉，本次操作失败，需要重新兑换。", "返回", null, null);
                    return;
                }
                //兑换成功后更新学豆数量
                updateLearnDouCount();
                //refreshSuiteList();

                BuySuccessDialog dialog = new BuySuccessDialog( mContext, R.style.FullTransparentDialog);
                dialog.setData( suiteBean.getVipLevel(), suiteBean.getProductVoList().size());
                dialog.show();
                EventBus.getDefault().post( new BuySuiteEvent(suiteBean.getProductSuiteId()));
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                String data = response.getInform();
                if (TextUtils.isEmpty(data))
                    data = "抱歉，本次操作失败，需要重新兑换。";
                AlertManager.showCustomImageBtnDialog(mContext, data, "返回", null, null);
            }
        });
    }

    private void getAliPaySignDeduction(final RewardBean rewardBean) {
        Map<String, String> params = PayUtil.getAliPayBeanSignParams(rewardBean, PayUtil.DIRECT);
        new PayModel().getAlipaySign(params, new RequestListener<AlipaySignBean>() {

            @Override
            public void onSuccess(AlipaySignBean vo) {
                LogUtils.i("getAliPaySignDeduction onSuccess");
                if (vo == null || TextUtils.isEmpty(vo.getSign())) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                PayUtil.updateStatusbefore(vo.getTradeNo());
                payV2(vo);
            }

            @Override
            public void onFail(HttpResponse<AlipaySignBean> response, Exception ex) {
                LogUtils.i("getAliPaySignDeduction onFail ex:" + ex.getMessage());
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (TextUtils.isEmpty(inform)) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        toPay(rewardBean);
                    } else if (inform.equals("充值赠送规则已失效")) {
                        ToastUtils.showShort(mContext, inform);
                    } else {
                        AlertManager.showErrorInfo(mContext, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(mContext, ex);
                }
            }
        });
    }

    private void getWxPaySignDeduction(final RewardBean rewardBean) {
        Map<String, String> params = WXPayUtils.getUnifiedorderBeanParams(rewardBean, WXPayUtils.APPPAY);
        new PayModel().getWxSign(params, new RequestListener<WxSignBean>() {

            @Override
            public void onSuccess(WxSignBean vo) {
                LogUtils.i("getWxPaySignDeduction onSuccess");
                if (vo == null || TextUtils.isEmpty(vo.getRechargeNum())) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                mRechargeNum = vo.getRechargeNum();
                PayUtil.updateStatusbefore(mRechargeNum);
                analogWechatPay(vo);
            }

            @Override
            public void onFail(HttpResponse<WxSignBean> response, Exception ex) {
                LogUtils.i("getWxPaySignDeduction onFail ex:" + ex.getMessage());
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        toPay(rewardBean);
                    } else {
                        AlertManager.showErrorInfo(mContext, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(mContext, ex);
                }
            }
        });
    }

    private void getAliPaySignDeduction(final ProductBean productBean, int bean) {
        Map<String, String> params = PayUtil.getAliPaySuiteSignParams(productBean, bean, PayUtil.DIRECT);
        new PayModel().getAlipaySignDeduction(params, new RequestListener<AlipaySignBean>() {

            @Override
            public void onSuccess(AlipaySignBean vo) {
                LogUtils.i("getAliPaySignDeduction onSuccess");
                if (vo == null || TextUtils.isEmpty(vo.getSign())) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    //    mProgressDialog.dismiss();
                    return;
                }
                PayUtil.updateStatusbefore(vo.getTradeNo());
                payV2(vo);
                mBuyingProductBean = productBean;
            }

            @Override
            public void onFail(HttpResponse<AlipaySignBean> response, Exception ex) {
                LogUtils.i("getAliPaySignDeduction onFail ex:" + ex.getMessage());
                //mProgressDialog.dismiss();
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        toPay(productBean);
                    } else {
                        AlertManager.showErrorInfo(mContext, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(mContext, ex);
                }
            }
        });
    }

    private void getWxPaySignDeduction(final ProductBean productBean, int bean) {
        Map<String, String> params = WXPayUtils.getUnifiedorderSuiteParams(productBean, bean, WXPayUtils.APPPAY);
        new PayModel().getWxSign(params, new RequestListener<WxSignBean>() {

            @Override
            public void onSuccess(WxSignBean vo) {
                LogUtils.i("getWxPaySignDeduction onSuccess");
                if (vo == null || TextUtils.isEmpty(vo.getRechargeNum())) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                mRechargeNum = vo.getRechargeNum();
                PayUtil.updateStatusbefore(mRechargeNum);
                analogWechatPay(vo);
                mBuyingProductBean = productBean;
            }

            @Override
            public void onFail(HttpResponse<WxSignBean> response, Exception ex) {
                LogUtils.i("getWxPaySignDeduction onFail ex:" + ex.getMessage());
                //mProgressDialog.dismiss();
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        toPay(productBean);
                    } else {
                        AlertManager.showErrorInfo(mContext, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(mContext, ex);
                }
            }
        });
    }

    private void getAliPaySignDeduction(final PracticeBean practiceBean, int bean) {
        mPracticeBean = practiceBean;
        Map<String, String> params = PayUtil.getAliPayPracticeSignParams(practiceBean, bean, PayUtil.DIRECT);

        new PayModel().getAlipaySign(params, new RequestListener<AlipaySignBean>() {

            @Override
            public void onSuccess(AlipaySignBean vo) {
                LogUtils.i("getAliPaySignDeduction onSuccess");
                if (vo == null || TextUtils.isEmpty(vo.getSign())) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                PayUtil.updateStatusbefore(vo.getTradeNo());
                payV2(vo);
            }

            @Override
            public void onFail(HttpResponse<AlipaySignBean> response, Exception ex) {
                LogUtils.i("getAliPaySignDeduction onFail ex:" + ex.getMessage());
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (TextUtils.isEmpty(inform)) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        toPay(practiceBean);
                    } else if (inform.equals("充值赠送规则已失效")) {
                        ToastUtils.showShort(mContext, inform);
                    } else {
                        AlertManager.showErrorInfo(mContext, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(mContext, ex);
                }
            }
        });
    }

    private void getWxPaySignDeduction(final PracticeBean practiceBean, int bean) {
        mPracticeBean = practiceBean;
        Map<String, String> params = WXPayUtils.getUnifiedorderPracticeParams(practiceBean, bean, WXPayUtils.APPPAY);
        new PayModel().getWxSign(params, new RequestListener<WxSignBean>() {

            @Override
            public void onSuccess(WxSignBean vo) {
                LogUtils.i("getWxPaySignDeduction onSuccess");
                if (vo == null || TextUtils.isEmpty(vo.getRechargeNum())) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                    return;
                }
                mRechargeNum = vo.getRechargeNum();
                PayUtil.updateStatusbefore(mRechargeNum);
                analogWechatPay(vo);
            }

            @Override
            public void onFail(HttpResponse<WxSignBean> response, Exception ex) {
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        toPay(practiceBean);
                    } else {
                        AlertManager.showErrorInfo(mContext, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(mContext, ex);
                }
            }
        });
    }


    /**
     * 微信官方——调起支付接口
     */
    private void analogWechatPay(WxSignBean vo) {
        PayReq req = new PayReq();
        req.appId = AppConst.APP_ID;
        req.partnerId = AppConst.PARTNER_ID;
        req.prepayId = vo.getPrepayid();
        req.nonceStr = vo.getNonceStr();
        req.timeStamp = vo.getTimeStamp();
        req.packageValue = vo.getPackageStr();
        req.sign = vo.getSign();
        api.sendReq(req);
    }

    /**
     * 支付宝支付业务
     */
    private void payV2(final AlipaySignBean res) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask( getActivity() );
                Map<String, String> result = alipay.payV2(res.getSign(), true);
                LogUtils.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
                updateStatus(res, result);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void updateStatus(AlipaySignBean vo, Map<String, String> result) {
        PayResult payResult = new PayResult(result);
        //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
        String resultStatus = payResult.getResultStatus();
        if (TextUtils.equals(resultStatus, "9000")) {
            return;
        }
        String resultInfo = payResult.getResult();
        String memo = payResult.getMemo();
        String remark = "resultStatus:" + resultStatus + " resultInfo:" + resultInfo + " memo:" + memo;
        PayUtil.updateStatusAfter(vo.getTradeNo(), remark);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(WxPayResultEvent event) {
        if (event.errCode == 0) {

            //购买精品套题
            if( mPracticeBean!=null ){
                exchangePracticeProduct(mPracticeBean);
            }else if( mBuyingProductBean != null ){
                BuySuccessDialog dialog = new BuySuccessDialog( mContext, R.style.FullTransparentDialog);
                dialog.setData( mBuyingProductBean.getVipLevel(), mBuyingProductBean.getProductVoList().size());
                dialog.show();
                if( !TextUtils.isEmpty(mBuyingProductBean.getProductSuiteId()) ){
                    EventBus.getDefault().post( new BuySuiteEvent(mBuyingProductBean.getProductSuiteId()));
                }
            }else{
                ToastUtils.showShort(mContext, "支付成功");
            }
        } else {
            if (event.errCode == -2) {
                ToastUtils.showShort(mContext, "已取消支付");
            } else {
                ToastUtils.showShort(mContext, "支付出现错误");
            }
            String remark = "errCode:" + event.errCode + " errStr:" + event.errStr;
            PayUtil.updateStatusAfter(mRechargeNum, remark);
        }
    }


    //兑换精品套题商品,不区分用免费使用权 还是 学豆，服务器后台处理
    //购买与 兑换一起
    private void exchangePracticeProduct( final PracticeBean practiceBean ){

        PayUtil.exchangePracticeProduct( practiceBean, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                //成功 状态改变
                //PracticeProductBean mPracticeProductBean = practiceBean.getProductBean();
                //practiceBean.setStatus( PracticeBean.ST_BUYED );
                //刷新界面
                EventBusUtils.postDelay(new RefreshPracticeEvent(), new Handler());

                EventBus.getDefault().post( new BuyPracticeEvent(practiceBean.getExcluId()) );
                //提示用户
                // 购买成功提示
                ToastUtils.show( mContext, "购买成功");
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                String data = "兑换失败，再来一次吧！";
                AlertManager.showCustomImageBtnDialog(getContext(), data, "再次发起兑换",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //再次购买
                        exchangePracticeProduct( practiceBean );
                    }
                }, null );
            }
        });
    }

}
