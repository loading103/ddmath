package com.tsinghuabigdata.edu.ddmath.module.mystudybean.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.SyncShowStudybeanFromDialogEvent;
import com.tsinghuabigdata.edu.ddmath.module.login.UserProtocolActivity;
import com.tsinghuabigdata.edu.ddmath.parent.view.PaySelectView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

/**
 * Created by Administrator on 2018/4/10.
 * 学豆充值Dialog
 */

public class PayBeanDialog extends Dialog implements View.OnClickListener {

    private static final int WX_PAY     = 0;
    private static final int ALI_PAY    = 1;
    private static final int PARENT_PAY = 2;

    //传递过来的参数
    private RewardBean        mParamBean;

    private Context          mContext;
    private SelectMethodListener<RewardBean> mMethodListener;

    private String mStudentId  = "";
    private int    curPosition = -1;
    private boolean mDetached;

    //private ImageView mIvClose;
    private TextView  mTvPayMoney;
    private TextView  mTvGetBean;
    private ImageView mIvWxpay;
    private ImageView mIvAlipay;
    private ImageView mIvParentpay;

    private PaySelectView wxPayView;
    private PaySelectView zfbPayView;
    //private TextView  mTvProtocol;
    //private Button    mBtnConfirmPay;

    private boolean isParent = false;

    public PayBeanDialog(Context context, RewardBean rewardBean, boolean parent) {
        super(context, R.style.dialog);
        mContext = context;
        mParamBean = rewardBean;
        isParent = parent;
        initView();
        initData();
    }

    private void initView() {
        if( isParent ){
            setContentView(R.layout.dialog_pay_bean_parent);
        }else if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_pay_bean);
        } else {
            setContentView(R.layout.dialog_pay_bean_phone);
        }
        ImageView mIvClose = (ImageView) findViewById(R.id.iv_close);
        mTvPayMoney = (TextView) findViewById(R.id.tv_pay_money);
        mTvGetBean = (TextView) findViewById(R.id.tv_get_bean);
        mIvWxpay = (ImageView) findViewById(R.id.iv_wxpay);
        mIvAlipay = (ImageView) findViewById(R.id.iv_alipay);
        mIvParentpay = (ImageView) findViewById(R.id.iv_parentpay);
        Button mBtnConfirmPay = (Button) findViewById(R.id.btn_confirm_pay);

        wxPayView = (PaySelectView)findViewById(R.id.psv_pay_weixin);
        if(wxPayView!=null){
            wxPayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(WX_PAY);
                }
            });
        }
        zfbPayView= (PaySelectView)findViewById(R.id.psv_pay_zhifubao);
        if(zfbPayView!=null){
            zfbPayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(ALI_PAY);
                }
            });
        }

        TextView tvProtocol = (TextView) findViewById(R.id.tv_protocol);
        tvProtocol.setOnClickListener(this);

        mIvClose.setOnClickListener(this);
        mIvAlipay.setOnClickListener(this);
        mIvParentpay.setOnClickListener(this);
        mIvWxpay.setOnClickListener(this);
        mBtnConfirmPay.setOnClickListener(this);

        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        if( dialogWindow == null ) return;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.recharge_dialog_anim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

//    private void makePayMethod(boolean enable) {
//        mIvAlipay.setEnabled(enable);
//        mIvParentpay.setEnabled(enable);
//        mIvWxpay.setEnabled(enable);
//    }

    private void initData() {
        select(WX_PAY);
        if (AccountUtils.getUserdetailInfo() != null) {
            mStudentId = AccountUtils.getUserdetailInfo().getStudentId();
        }
        RewardBean rewardBean = mParamBean;
        mTvPayMoney.setText( String.format(Locale.getDefault(),"%d元",rewardBean.getRechargeMoney() ));
        int getBean = rewardBean.getRechargeMoney() * 10;
        if( !isParent ){
            getBean +=  rewardBean.getReturnDdAmt();
        }
        mTvGetBean.setText(String.format(Locale.getDefault(),"%d个",getBean));

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
            case R.id.psv_pay_zhifubao:
                select(ALI_PAY);
                break;
            case R.id.iv_parentpay:
                select(PARENT_PAY);
                break;
            case R.id.iv_wxpay:
                select(WX_PAY);
                break;
            case R.id.btn_confirm_pay:
                dismiss();
                toPay();
                break;
            case R.id.tv_protocol:
                Intent intent = new Intent( mContext, UserProtocolActivity.class);
                intent.putExtra( UserProtocolActivity.PARAM_FROM_PARENT, isParent);
                mContext.startActivity( intent );
                break;
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
            if(zfbPayView!=null) zfbPayView.setCheck(true);
            if(wxPayView!=null) wxPayView.setCheck( false );
        } else if (position == PARENT_PAY) {
            mIvAlipay.setSelected(false);
            mIvParentpay.setSelected(true);
            mIvWxpay.setSelected(false);
        } else {
            mIvAlipay.setSelected(false);
            mIvParentpay.setSelected(false);
            mIvWxpay.setSelected(true);
            if(zfbPayView!=null) zfbPayView.setCheck( false );
            if(wxPayView!=null) wxPayView.setCheck( true );
        }
        curPosition = position;
    }

    private void toPay() {
        if(mMethodListener==null) return;
        if (curPosition == ALI_PAY) {
            mMethodListener.aliPay(mParamBean);
        } else if (curPosition == PARENT_PAY) {
            mMethodListener.parentPay(mParamBean);
        } else {
            mMethodListener.wxPay(mParamBean);
        }
    }

    public void setMethodListener(SelectMethodListener<RewardBean> methodListener) {
        mMethodListener = methodListener;
    }

    public interface SelectMethodListener<T> {

        void aliPay(T bean);

        void parentPay(T bean);

        void wxPay(T bean);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDetached = true;
    }
}
