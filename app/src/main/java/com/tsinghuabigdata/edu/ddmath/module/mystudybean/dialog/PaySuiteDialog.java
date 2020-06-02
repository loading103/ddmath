package com.tsinghuabigdata.edu.ddmath.module.mystudybean.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.SyncShowStudybeanFromDialogEvent;
import com.tsinghuabigdata.edu.ddmath.module.login.UserProtocolActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.parent.view.PaySelectView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import static com.tsinghuabigdata.edu.ddmath.util.AccountUtils.getTotalBean;

/**
 * Created by Administrator on 2018/4/10.
 * 购买套餐Dialog
 */

public class PaySuiteDialog extends Dialog implements View.OnClickListener {

    private static final int WX_PAY     = 0;
    private static final int ALI_PAY    = 1;
    private static final int PARENT_PAY = 2;

    private Context              mContext;
    //private ProductBean          mProductBean;
    private int         chargeDdAmt;    //学豆数量
    private String      productName;    //商品名称
    private selectMethodListener mMethodListener;
    private String mStudentId  = "";
    private int    curPosition = -1;
    private boolean mDetached;

    //private ImageView mIvClose;
    private TextView mTitleView;
    private TextView  mTvSuiteName;
    private TextView  mTvSuitePrice;
    private TextView  mTvSuitePriceOld;
    private CheckBox  mCbBeanDeduction;
    private TextView  mTvDeductionPrice;
    private TextView mTvTipsView;
    //private TextView  mTvUseDeduction;
    private ImageView mIvAlipay;
    private ImageView mIvParentpay;
    private ImageView mIvWxpay;

    private PaySelectView wxPaySelectView;
    private PaySelectView zfbPaySelectView;
    //private Button    mBtConfirmPay;

    private boolean isParent = false;

    public PaySuiteDialog(Context context,/* ProductBean productBean, */ int ddAmt, String productName, boolean isParent) {
        super(context, R.style.dialog);
        mContext = context;
        //mProductBean = productBean
        this.chargeDdAmt = ddAmt;
        this.productName = productName;
        this.isParent = isParent;
        initView();
        initData();
    }

    public void setTipsView( String data ){
        if( mTvTipsView!=null ){
            mTvTipsView.setText( data );
            mTvTipsView.setVisibility( View.VISIBLE );
            mTitleView.setText("购买套题");
        }
    }

    private void initView() {
        if( isParent ){
            setContentView(R.layout.dialog_pay_suite_parent);
        }else if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_pay_suite);
        } else {
            setContentView(R.layout.dialog_pay_suite_phone);
        }
        ImageView mIvClose = findViewById(R.id.iv_close);
        mTitleView = findViewById(R.id.tv_title);
        mTvSuiteName = findViewById(R.id.tv_suite_name);
        mTvSuitePrice = findViewById(R.id.tv_suite_price);
        mTvSuitePriceOld = findViewById(R.id.tv_suite_price_old);
        mCbBeanDeduction = findViewById(R.id.cb_bean_deduction);
        TextView mTvUseDeduction = findViewById(R.id.tv_use_deduction);
        mTvDeductionPrice = findViewById(R.id.tv_deduction_price);
        mIvAlipay = findViewById(R.id.iv_alipay);
        mIvParentpay = findViewById(R.id.iv_parentpay);
        mIvWxpay = findViewById(R.id.iv_wxpay);
        mTvTipsView = findViewById(R.id.tv_buy_tips );
        Button mBtConfirmPay = findViewById(R.id.bt_confirmPay);

        wxPaySelectView = findViewById(R.id.psv_pay_weixin);
        if(wxPaySelectView!=null){
            wxPaySelectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(WX_PAY);
                }
            });
        }
        zfbPaySelectView= findViewById(R.id.psv_pay_zhifubao);
        if(zfbPaySelectView!=null){
            zfbPaySelectView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(ALI_PAY);
                }
            });
        }
        //用户使用协议
        //checkBox = (CheckBox)findViewById( R.id.checkbox );
        //checkBox.setOnClickListener( this );
        //View view = findViewById( R.id.tv_have_read );
        //view.setOnClickListener( this );
        View view = findViewById(R.id.tv_protocol);
        view.setOnClickListener(this);

        mIvClose.setOnClickListener(this);
        mIvAlipay.setOnClickListener(this);
        mIvParentpay.setOnClickListener(this);
        mIvWxpay.setOnClickListener(this);
        mBtConfirmPay.setOnClickListener(this);
        mTvUseDeduction.setOnClickListener(this);
        mTvSuitePriceOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvSuitePriceOld.setVisibility(View.GONE);
        mTvDeductionPrice.setVisibility(View.GONE);
        mCbBeanDeduction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTvSuitePriceOld.setText(ProductUtil.getPrice(chargeDdAmt));
                    if (getTotalBean() >= chargeDdAmt) {
                        //全部抵扣
                        makePayMethod(false);
                        mTvSuitePrice.setText("0元");
                        String data1 = chargeDdAmt + "个学豆抵扣" + ProductUtil.getPrice(chargeDdAmt) /*+ "（抵扣后的可用学豆:" + (getTotalBean() - chargeDdAmt) + "个)"*/;
                        mTvDeductionPrice.setText(data1);
                    } else {
                        makePayMethod(true);
                        int paybean = chargeDdAmt - AccountUtils.getTotalBean();
                        mTvSuitePrice.setText(ProductUtil.getPrice(paybean));
                        mTvDeductionPrice.setText(String.format(Locale.getDefault(), "%d个学豆抵扣%s", AccountUtils.getTotalBean(), ProductUtil.getPrice(AccountUtils.getTotalBean())));
                    }
                    mTvSuitePriceOld.setVisibility(View.VISIBLE);
                    mTvDeductionPrice.setVisibility(View.VISIBLE);
                } else {
                    makePayMethod(true);
                    mTvSuitePrice.setText(ProductUtil.getPrice(chargeDdAmt));
                    mTvSuitePriceOld.setVisibility(View.GONE);
                    mTvDeductionPrice.setVisibility(View.GONE);
                }
            }
        });
        if (getTotalBean() == 0) {
            mCbBeanDeduction.setEnabled(false);
            mTvUseDeduction.setEnabled(false);
        }

        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        if( dialogWindow== null)return;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.recharge_dialog_anim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void makePayMethod(boolean enable) {
        mIvAlipay.setEnabled(enable);
        mIvParentpay.setEnabled(enable);
        mIvWxpay.setEnabled(enable);

        if( wxPaySelectView!=null ){
            wxPaySelectView.setSelectEnable( enable );
            if( !enable ) wxPaySelectView.setCheck(false);
        }
        if( zfbPaySelectView!=null ){
            zfbPaySelectView.setSelectEnable(enable);
            if( !enable )zfbPaySelectView.setCheck(false);
        }

    }

    private void initData() {
        mTvSuiteName.setText(productName);
        mTvSuitePrice.setText(ProductUtil.getPrice(chargeDdAmt));
        select(WX_PAY);
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
        updateLearnDouCount();
    }

    //学豆更新
    private void updateLearnDouCount() {
        new UserCenterModel().queryMyStudyBean(mStudentId, new RequestListener<StudyBean>() {
            @Override
            public void onSuccess(StudyBean res) {
                //LogUtils.i("updateLearnDouCount success");
                if (mDetached) {
                    return;
                }
                if (res == null) {
                    return;
                }
                EventBus.getDefault().post(new SyncShowStudybeanFromDialogEvent(res.getTotalDdAmt()));
            }

            @Override
            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
                //LogUtils.i("updateLearnDouCount failed");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_alipay:
                select(ALI_PAY);
                break;
            case R.id.iv_parentpay:
                select(PARENT_PAY);
                break;
            case R.id.iv_wxpay:
                select(WX_PAY);
                break;
            case R.id.bt_confirmPay:
                //                if( !checkBox.isChecked() ){
                //                    ToastUtils.showShort( mContext, "请同意" + mContext.getResources().getString( R.string.user_procotol));
                //                    break;
                //                }
                dismiss();
                toPay();
                break;
            case R.id.tv_use_deduction:
                mCbBeanDeduction.setChecked(!mCbBeanDeduction.isChecked());
                break;
            case R.id.tv_protocol: {
                mContext.startActivity(new Intent(mContext, UserProtocolActivity.class));
                break;
            }
            default:
                break;
        }
    }

    private void select(int position) {
        if (curPosition == position) {
            return;
        }
        if (position == ALI_PAY) {
            mIvAlipay.setSelected(true);
            mIvParentpay.setSelected(false);
            mIvWxpay.setSelected(false);
            if( wxPaySelectView!=null ) wxPaySelectView.setCheck(false);
            if( zfbPaySelectView!=null ) zfbPaySelectView.setCheck(true);
        } else if (position == PARENT_PAY) {
            mIvAlipay.setSelected(false);
            mIvParentpay.setSelected(true);
            mIvWxpay.setSelected(false);
        } else {
            mIvAlipay.setSelected(false);
            mIvParentpay.setSelected(false);
            mIvWxpay.setSelected(true);
            if( wxPaySelectView!=null ) wxPaySelectView.setCheck(true);
            if( zfbPaySelectView!=null ) zfbPaySelectView.setCheck(false);
        }
        curPosition = position;
    }

    private void toPay() {
        if (mCbBeanDeduction.isChecked()) {
            if (getTotalBean() >= chargeDdAmt) {
                //学豆大于商品价格直接走兑换流程
                mMethodListener.beanCharge();
            } else if (curPosition == ALI_PAY) {
                mMethodListener.aliPay(getTotalBean());
            } else if (curPosition == PARENT_PAY) {
                mMethodListener.parentPay(getTotalBean());
            } else {
                mMethodListener.wxPay(getTotalBean());
            }
            return;
        }
        if (curPosition == ALI_PAY) {
            mMethodListener.aliPay(0);
        } else if (curPosition == PARENT_PAY) {
            mMethodListener.parentPay(0);
        } else {
            mMethodListener.wxPay(0);
        }
    }

    public void setMethodListener(selectMethodListener methodListener) {
        mMethodListener = methodListener;
    }

    public interface selectMethodListener {
        void beanCharge();

        void aliPay(int bean);

        void parentPay(int bean);

        void wxPay(int bean);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDetached = true;
    }
}
