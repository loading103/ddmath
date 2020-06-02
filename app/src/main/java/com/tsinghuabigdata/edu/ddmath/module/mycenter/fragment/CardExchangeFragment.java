package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.PayModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ProductModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.ExchangeIntroActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ExchangeCardVo;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.CardExchangeDialog;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.WebViewActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.ActivityUtil;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VIP卡兑换模块
 * Created by Administrator on 2018/5/15.
 */

public class CardExchangeFragment extends MyBaseFragment implements View.OnClickListener {

    private Button   mBtnExchangeRecord;
    private TextView mTvExchangeTips;
    private EditText mEdtCardCode;
    private Button   mBtnExchange;
    private TextView mTvExchangeIntro;


    private Context          mContext;
    private MyProgressDialog mProgressDialog;

    private String mStudentId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_card_exchange, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_card_exchange_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    public String getUmEventName() {
        return "mycenter_vipcard";
    }

    private void initView(View root) {
        mContext = getActivity();
        mProgressDialog = new MyProgressDialog(getContext());
        mProgressDialog.setMessage("兑换中");

        mBtnExchangeRecord = (Button) root.findViewById(R.id.btn_exchange_record);
        mTvExchangeTips = (TextView) root.findViewById(R.id.tv_exchange_tips);
        mEdtCardCode = (EditText) root.findViewById(R.id.edt_card_code);
        mBtnExchange = (Button) root.findViewById(R.id.btn_exchange);
        mTvExchangeIntro = (TextView) root.findViewById(R.id.tv_exchange_intro);
        mBtnExchangeRecord.setOnClickListener(this);
        mBtnExchange.setOnClickListener(this);
        mTvExchangeIntro.setOnClickListener(this);
        mBtnExchange.setEnabled(false);
        mEdtCardCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = s.toString().trim();
                mBtnExchange.setEnabled(code.length() >= 8);
            }
        });

        View textView = root.findViewById(R.id.tv_hide_view);
        if( textView!=null ){
            textView.setOnClickListener( this );
        }
    }

    private void initData() {
        createLoginInfo();
    }

    private void createLoginInfo() {
        if (AccountUtils.getUserdetailInfo() != null) {
            mStudentId = AccountUtils.getUserdetailInfo().getStudentId();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exchange:
                judgeCode();
                break;
            case R.id.btn_exchange_record:
                Intent in2 = new Intent(getContext(), WebViewActivity.class);
                in2.putExtra(WebViewActivity.MSG_URL, MessageUtils.getCardExchangelUrl(mStudentId));
                in2.putExtra(WebViewActivity.MSG_TITLE, "VIP卡兑换记录");
                getContext().startActivity(in2);
                break;
            case R.id.tv_exchange_intro:
                ActivityUtil.goActivity(getContext(), ExchangeIntroActivity.class);
                break;
            case R.id.tv_hide_view:{

//                if( !AppUtils.isDebug()) return;
//
//                HashMap<String, String> params = new HashMap<>();
//                UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
//                if (detailinfo != null) {
//                    params.put("studentId", detailinfo.getStudentId());
//                }
//                params.put("exchangeCode", "hjsdfhfslf");
//                new UserCenterModel().submitRedeemCode(params, new RequestListener<String>() {
//                    @Override
//                    public void onSuccess(String res) {
//                    }
//                    @Override
//                    public void onFail(HttpResponse<String> response, Exception ex) {
//                    }
//                });
                break;
            }
            default:
                break;
        }
    }

    private void judgeCode() {
        String code = mEdtCardCode.getText().toString().trim().toUpperCase();
        exchange(code);
    }

    private void exchange(String code) {
        Map<String, String> params = new LinkedHashMap<>();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            params.put("studentId", detailinfo.getStudentId());
            params.put("studentName", detailinfo.getReallyName());
        }
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo != null) {
            params.put("loginName", loginInfo.getLoginName());
        }
        if (AccountUtils.getCurrentClassInfo() != null) {
            params.put("schoolId", AccountUtils.getCurrentClassInfo().getSchoolId());
            params.put("schoolName", AccountUtils.getCurrentClassInfo().getSchoolName());
            params.put("classId", AccountUtils.getCurrentClassInfo().getClassId());
        }
        params.put("exchangeCode", code);
        mProgressDialog.show();
        new PayModel().exchangeCard(params, new RequestListener<ExchangeCardVo>() {

            @Override
            public void onSuccess(ExchangeCardVo vo) {
                LogUtils.i("exchange onSuccess");
                if (vo == null || TextUtils.isEmpty(vo.getProductSuiteId())) {
                    mProgressDialog.dismiss();
                    mEdtCardCode.setText(null);
                    mTvExchangeTips.setText(null);
                    ToastUtils.showShort(getContext(), "兑换成功");
                    return;
                }
                querySuitedetail(vo.getProductSuiteId());
            }

            @Override
            public void onFail(HttpResponse<ExchangeCardVo> response, Exception ex) {
                LogUtils.i("exchange onFail");
                mProgressDialog.dismiss();
                String error = getError(ex);
                if (!TextUtils.isEmpty(error)) {
                    mTvExchangeTips.setText(error);
                    //querySuitedetail("A036B188DE7A496481017A9B5E7D598B");
                    //querySuitedetail("6B274811F47B470FB7E835E793081571");
                } else {
                    AlertManager.showLongErrorInfo(mContext, ex);
                    mTvExchangeTips.setText(null);
                }
            }
        });
    }

    private void querySuitedetail(String suiteId) {
        new ProductModel().queryProductSuitedetail(suiteId, new RequestListener<ProductBean>() {

            @Override
            public void onSuccess(ProductBean bean) {
                LogUtils.i("querySuitedetail onSuccess");
                mProgressDialog.dismiss();
                mEdtCardCode.setText(null);
                mTvExchangeTips.setText(null);
                if (bean == null || bean.getProductVoList() == null) {
                    ToastUtils.showShort(getContext(), "兑换成功");
                    return;
                }
                CardExchangeDialog cardExchangeDialog = new CardExchangeDialog(getContext());
                cardExchangeDialog.setSuit(bean);
                cardExchangeDialog.show();
            }

            @Override
            public void onFail(HttpResponse<ProductBean> response, Exception ex) {
                LogUtils.i("querySuitedetail onFail");
                mProgressDialog.dismiss();
                ToastUtils.showShort(getContext(), "兑换成功");
            }
        });
    }

    private String getError(Exception ex) {
        if (ex instanceof AppRequestException) {
            String inform = ((AppRequestException) ex).getResponse().getInform();
            if (TextUtils.isEmpty(inform)) {
                return null;
            } else if (inform.contains("wrong exchange code")) {
                return "您输入的VIP卡兑换码不正确，请检查后重新输入！";
            } else if (inform.contains("exchange not begin")) {
                return "您输入的VIP卡兑换码还未到兑换时间，请检查后重新输入！";
            } else if (inform.contains("wrong times exceed six")) {
                return "您的错误次数超过6次，请明天再来兑换！";
            } else if (inform.contains("exchange code used")) {
                return "您输入的VIP卡兑换码已被使用，请检查后重新输入！";
            } else if (inform.contains("exchange code expired")) {
                return "您输入的VIP卡兑换码已过期，请检查后重新输入！";
            } else if (inform.contains("exchange code deprecated")) {
                return "您输入的VIP卡兑换码已作废，请检查后重新输入！";
            }
        }
        return null;
    }

    //    WRONG_EXCHANGECODE("wrong exchange code"),
    //    EXCHANGECODE_USED("exchange code used"),
    //    EXCHANGECODE_DEPRECATED("exchange code deprecated"),
    //    NOT_YET_TO_START_EXCHANGE("exchange not begin"),
    //    EXCHANGE_EXPIRED("exchange code expired"),
    //    BEYOND_SIX_TIMES("wrong times exceed six");

    //    WRONG_EXCHANGECODE("兑换码错误"),
    //    EXCHANGECODE_USED("兑换码已被使用"),
    //    EXCHANGECODE_DEPRECATED("兑换码已作废"),
    //    NOT_YET_TO_START_EXCHANGE("还未到兑换时间"),
    //    EXCHANGE_EXPIRED("兑换码已过期"),
    //    BEYOND_SIX_TIMES("当天错误次数超过六次");
}
