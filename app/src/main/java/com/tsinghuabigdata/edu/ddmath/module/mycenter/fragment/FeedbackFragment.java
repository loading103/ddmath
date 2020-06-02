package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.CommonWebviewActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.dialog.FeedbackDialog;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import java.util.HashMap;

/**
 * 意见反馈
 * Created by Administrator on 2017/10/16.
 */

public class FeedbackFragment extends MyBaseFragment implements View.OnClickListener {

    private EditText     mEdtFeedback;
    private Button       mBtnSubbmit;

    private Context          mContext;
    private FeedbackDialog   mFeedbackDialog;
    private MyProgressDialog mProgressDialog;
    private LinearLayout  mlin_function;
    private LinearLayout  mlin_question;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_feedback, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_feedback_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    public String getUmEventName() {
        return "mycenter_feedback";
    }
    private void initView(View root) {
        mEdtFeedback = (EditText) root.findViewById(R.id.edt_feedback);
        mBtnSubbmit = (Button) root.findViewById(R.id.btn_subbmit);
        mlin_function = (LinearLayout) root.findViewById(R.id.lin_function);
        mlin_question = (LinearLayout) root.findViewById(R.id.lin_question);

        mlin_function.setOnClickListener(this);
        mlin_question.setOnClickListener(this);
        mBtnSubbmit.setOnClickListener(this);
        mBtnSubbmit.setEnabled(false);
        mEdtFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().trim();
                mBtnSubbmit.setEnabled(!TextUtils.isEmpty(str));
                if (TextUtils.isEmpty(str)) {
                    mBtnSubbmit.setBackgroundResource(R.drawable.bg_submit_button_no);
                } else {
                    mBtnSubbmit.setBackgroundResource(R.drawable.bg_submit_button_yes);
                }
            }
        });
    }

    private void initData() {
        mContext = getActivity();
        mProgressDialog = new MyProgressDialog(mContext);
        mProgressDialog.setMessage("提交中...");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subbmit:
                subbmit();
                break;
            case R.id.lin_function:
                CommonWebviewActivity.startActivity(getContext(),"功能使用说明",AppRequestConst.RESTFUL_ADDRESS + AppRequestConst.FUCKYION_USE_DESCRIP);
                break;
            case R.id.lin_question:
                CommonWebviewActivity.startActivity(getContext(), "常见问题",AppRequestConst.RESTFUL_ADDRESS + AppRequestConst.COMMON_QUESWTION);
                break;
            default:
                break;
        }
    }

    private void subbmit() {
        if (mFeedbackDialog == null) {
            mFeedbackDialog = new FeedbackDialog(mContext, R.style.dialog);
        }
        HashMap<String, String> params = new HashMap<>();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            params.put("accountId", detailinfo.getStudentId());
            params.put("callName", detailinfo.getReallyName());
        }
        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
        if (currentClassInfo != null) {
            params.put("classId", currentClassInfo.getClassId());
        }
        params.put("sourceType", "Android");
        params.put("adviceContent", mEdtFeedback.getText().toString().trim());
        new UserCenterModel().addAdvice(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                mProgressDialog.dismiss();
                mEdtFeedback.setText(null);
                mFeedbackDialog.show();
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                mProgressDialog.dismiss();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
        mProgressDialog.show();
    }


}
