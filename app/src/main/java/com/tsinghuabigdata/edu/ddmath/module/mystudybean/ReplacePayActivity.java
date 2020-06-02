package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.PayModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.BuyPracticeEvent;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.BuySuccessDialog;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.NegotiationBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.QrcodeBean;
import com.tsinghuabigdata.edu.ddmath.module.product.PayUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.QRCodeUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.Map;


/**
 * 家长代付页面
 * Created by Administrator on 2018/4/13.
 */

public class ReplacePayActivity extends RoboActivity implements View.OnClickListener {

    public static final  String PRODUCTBEAN = "productBean";
    public static final  String PRACTICEBEAN = "practiceBean";

    public static final  String STUDYBEAN   = "StudyBean";
    public static final  String REWARDBEAN  = "rewardBean";
    public static final  String FROMPAYBEAN = "fromPayBean";


    private MyProgressDialog mProgressDialog;
    //private WorkToolbar      mWorktoolbar;
    //private LinearLayout     mLlNegotiationContent;
    private TextView         mTvRechargeSum;
    private ImageView        mIvAliCode;
    //private Button           mBtnPayFinish;
    private TextView         mTvBeanTips;
    private LoadingPager     mLoadingPager;

    private int         codeWidth;
    private ProductBean mProductBean;
    private PracticeBean mPracticeBean;
    private int         mStudyBean;
    private RewardBean  mRewardBean;
    private boolean     mFromPayBean;
    private String      mTradeNo; //支付宝商户号
    private Context     mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        // 设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad() ? R.layout.activity_negotiation : R.layout.activity_negotiation_phone);
        initView();
        initData();
    }


    private void initView() {
        mContext = this;
        mProgressDialog = new MyProgressDialog(this);
        mProgressDialog.setMessage("查询中");
        WorkToolbar mWorktoolbar =  findViewById(R.id.worktoolbar);
        LinearLayout mLlNegotiationContent = findViewById(R.id.ll_negotiation_content);
        mTvRechargeSum = findViewById(R.id.tv_recharge_sum);
        mIvAliCode = findViewById(R.id.iv_ali_code);
        Button mBtnPayFinish = findViewById(R.id.btn_pay_finish);
        mTvBeanTips = findViewById(R.id.tv_bean_tips);
        mLoadingPager = findViewById(R.id.loadingPager);

        mLoadingPager.setTargetView(mLlNegotiationContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                getAliQrcode();
            }
        });

        mWorktoolbar.setTitle("支付宝扫码代付");
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        mBtnPayFinish.setOnClickListener(this);
    }

    private void initData() {
        codeWidth = DensityUtils.dp2px(this, GlobalData.isPad() ? 300 : 140);
        Intent intent = getIntent();
        mFromPayBean = intent.getBooleanExtra(FROMPAYBEAN, false);
        if (mFromPayBean) {
            mRewardBean = (RewardBean) intent.getSerializableExtra(REWARDBEAN);
            mTvRechargeSum.setText( String.format(Locale.getDefault(),"%d元",mRewardBean.getRechargeMoney()));
            mTvBeanTips.setVisibility(View.GONE);
        } else {
            if( intent.hasExtra(PRODUCTBEAN) )mProductBean = (ProductBean) intent.getSerializableExtra(PRODUCTBEAN);
            if( intent.hasExtra(PRACTICEBEAN))mPracticeBean = (PracticeBean) intent.getSerializableExtra(PRACTICEBEAN);
            mStudyBean = intent.getIntExtra(STUDYBEAN, 0);
            int chargeDdAmt = 0;
            if( mPracticeBean!=null ) chargeDdAmt = mPracticeBean.getXuedou();
            if( mProductBean!=null )  chargeDdAmt = mProductBean.getChargeDdAmt();
            mTvRechargeSum.setText(String.format(Locale.getDefault(),"%s元",PayUtil.getPrice(chargeDdAmt - mStudyBean)) );
        }
        getAliQrcode();
    }


    private void queryTradeStatus() {
        mProgressDialog.show();
        new PayModel().queryTrade(mTradeNo, new RequestListener<NegotiationBean>() {

            @Override
            public void onSuccess(NegotiationBean vo) {
                mProgressDialog.dismiss();
                LogUtils.i("queryTradeStatus onSuccess");
                if (vo == null) {
                    ToastUtils.showShort(mContext, "暂未获取支付信息");
                    return;
                }
                if (vo.getRechargeStatus() == 3) {
                    if( mPracticeBean!=null ){
                        exchangePracticeProduct( mPracticeBean );
                    } else if( mProductBean != null ){
                        BuySuccessDialog dialog = new BuySuccessDialog( mContext, R.style.FullTransparentDialog);
                        dialog.setData( mProductBean.getVipLevel(), mProductBean.getProductVoList().size());
                        dialog.show();
                        if( !TextUtils.isEmpty(mProductBean.getProductSuiteId()) ){
                            EventBus.getDefault().post( new BuySuiteEvent(mProductBean.getProductSuiteId()));
                        }
                    }else{
                        ToastUtils.showShort(mContext, "购买成功，可享受个性化提分服务！");
                    }
                    updateLearnDouCount();
                    finish();
                } else if (vo.getRechargeStatus() == 4) {
                    ToastUtils.showShort(mContext, "购买失败");
                } else {
                    ToastUtils.showShort(mContext, "暂未获取支付信息");
                }
            }

            @Override
            public void onFail(HttpResponse<NegotiationBean> response, Exception ex) {
                mProgressDialog.dismiss();
                LogUtils.i("queryTradeStatus onFail ex" + ex.getMessage());
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    //兑换精品套题商品,不区分用免费使用权 还是 学豆，服务器后台处理
    //购买与 兑换一起
    private void exchangePracticeProduct( final PracticeBean practiceBean ){

        PayUtil.exchangePracticeProduct( practiceBean, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                finishAll();
                EventBusUtils.postDelay(new BuyPracticeEvent(practiceBean.getExcluId()), new Handler(), 800);
                ToastUtils.show( mContext, "支付成功");
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                String data = "兑换失败，再来一次吧！";
                AlertManager.showCustomImageBtnDialog( mContext, data, "再次发起兑换",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //再次购买
                        exchangePracticeProduct( practiceBean );
                    }
                }, null );
            }
        });
    }

    //触发学豆更新
    private void updateLearnDouCount() {
        ProductUtil.updateLearnDou(new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {

            }

            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {

            }
        });
    }


    private void getAliQrcode() {
        if( mProductBean!=null ) {   //套餐
            getAliQrcodeDeduction();
        }else {
            //充值  或者 购买精品套题
            getAliQrcodeWithoutDeduction();
        }
    }

    private void getAliQrcodeWithoutDeduction() {
        Map<String, String> params = null;
        if( mFromPayBean )params = PayUtil.getAliPayBeanSignParams(mRewardBean, PayUtil.SUBSTITUTE);
        else if( mPracticeBean!=null )params = PayUtil.getAliPayPracticeSignParams(mPracticeBean, mStudyBean, PayUtil.DIRECT);
        new PayModel().getAlipayQrcode(params, new RequestListener<QrcodeBean>() {

            @Override
            public void onSuccess(QrcodeBean vo) {
                LogUtils.i("getAliQrcodeWithoutDeduction onSuccess");
                if (vo == null) {
                    mLoadingPager.showServerFault();
                    return;
                }
                showCode(vo);
            }

            @Override
            public void onFail(HttpResponse<QrcodeBean> response, Exception ex) {
                LogUtils.i("getAliQrcodeWithoutDeduction onFail ex" + ex.getMessage());
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (TextUtils.isEmpty(inform)) {
                        mLoadingPager.showFault(ex);
                    } else if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        finish();
                    } else {
                        mLoadingPager.showFault(ex);
                    }
                } else {
                    mLoadingPager.showFault(ex);
                }
            }
        });
    }

    private void getAliQrcodeDeduction() {
        Map<String, String> params = PayUtil.getAliPaySuiteSignParams(mProductBean, mStudyBean, PayUtil.SUBSTITUTE);
        new PayModel().getAlipayQrcodeDeduction(params, new RequestListener<QrcodeBean>() {

            @Override
            public void onSuccess(QrcodeBean vo) {
                LogUtils.i("getAliQrcodeDeduction onSuccess");
                if (vo == null) {
                    mLoadingPager.showServerFault();
                    return;
                }
                showCode(vo);
            }

            @Override
            public void onFail(HttpResponse<QrcodeBean> response, Exception ex) {
                LogUtils.i("getAliQrcodeDeduction onFail ex" + ex.getMessage());
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    if (TextUtils.isEmpty(inform)) {
                        mLoadingPager.showFault(ex);
                    } else if (inform.contains("您目前已有") || inform.contains("此商品只能兑换一次")) {
                        ToastUtils.showShort(mContext, inform);
                        finish();
                    } else if (inform.contains("the available dd is not enough")) {
                        ToastUtils.showShort(mContext, "可用学豆不足,请重新支付");
                        finish();
                    } else {
                        mLoadingPager.showFault(ex);
                    }
                } else {
                    mLoadingPager.showFault(ex);
                }
            }
        });
    }

    private void showCode(QrcodeBean vo) {
        mTradeNo = vo.getOutTradeNo();
        Bitmap aliBitmap = QRCodeUtil.createQRCodeBitmap(vo.getQr_code(), codeWidth, Color.BLACK, getResources().getColor(R.color.color_FFFDEC));
        //将二维码在界面中显示
        mIvAliCode.setImageBitmap(aliBitmap);
        mLoadingPager.showTarget();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pay_finish:
                queryTradeStatus();
                break;
            default:
                break;
        }
    }


}
