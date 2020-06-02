package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * Created by Administrator on 2017/12/8.
 * 支付方法选择Dialog
 */

public class PayDialog extends Dialog implements View.OnClickListener {

    private Context    context;
    private RewardBean rewardBean;

    private ImageView ivClose;
    private TextView  tvMoney;
    private ImageView ivWechatPay;
    private ImageView ivAlipay;
    private ImageView ivParentPay;
    private Button    btConfirmPay;

    private selectMethodListener mMethodListener;

    public PayDialog(@NonNull Context context, @StyleRes int themeResId, RewardBean rewardBean) {
        super(context, themeResId);
        this.context = context;
        this.rewardBean = rewardBean;
        initView();
    }

    private void initView() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_payview);
        } else {
            setContentView(R.layout.dialog_payview_phone);
        }
        ivClose =  findViewById(R.id.iv_close);
        tvMoney =  findViewById(R.id.tv_money);
        ivWechatPay =  findViewById(R.id.iv_WechatPay);
        ivAlipay =  findViewById(R.id.iv_Alipay);
        ivParentPay =  findViewById(R.id.iv_parentPay);
        btConfirmPay =  findViewById(R.id.bt_confirmPay);

        tvMoney.setText(String.valueOf(rewardBean.getRechargeMoney()));

        ivClose.setOnClickListener(this);
        ivWechatPay.setOnClickListener(this);
        ivWechatPay.setClickable(false);
        ivAlipay.setOnClickListener(this);
        ivParentPay.setOnClickListener(this);
        btConfirmPay.setOnClickListener(this);

        ivAlipay.setActivated(true);

        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_WechatPay:
                //                ToastUtils.showShort(context, "微信支付" + money + "元");

                break;
            case R.id.iv_Alipay:
                //                ToastUtils.showShort(context, "支付宝支付" + money + "元");
                ivAlipay.setActivated(!ivAlipay.isActivated());
                ivParentPay.setActivated(false);
                break;
            case R.id.iv_parentPay:
                //                ToastUtils.showShort(context, "家长代付" + money + "元");
                //                dismiss();
                ivAlipay.setActivated(false);
                ivParentPay.setActivated(!ivParentPay.isActivated());
                break;
            case R.id.bt_confirmPay:
                //                ToastUtils.showShort(context, "确认支付" + money + "元");
                toPay();
                dismiss();
                break;
            default:
                break;
        }
    }

    private void toPay() {
        if (ivWechatPay.isActivated()) {
            if (mMethodListener != null) {
                mMethodListener.wechatPay();
            }
        } else if (ivAlipay.isActivated()) {
//            ToastUtils.showShort(context, "支付宝支付" + money + "元");
            if (mMethodListener != null) {
                mMethodListener.aliPay();
            }
        } else if (ivParentPay.isActivated()) {
            if (mMethodListener != null) {
                mMethodListener.parentPay();
            }
        }
    }

    public void setMethodListener(selectMethodListener methodListener) {
        mMethodListener = methodListener;
    }

    public interface selectMethodListener {
        void wechatPay();

        void aliPay();

        void parentPay();
    }
}
