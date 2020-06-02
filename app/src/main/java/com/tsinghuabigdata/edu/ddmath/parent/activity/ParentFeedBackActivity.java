package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.dialog.FeedbackDialog;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;

/**
 * 家长端--关于界面
 */

public class ParentFeedBackActivity extends RoboActivity implements View.OnClickListener {

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @ViewInject(R.id.edt_feedback)
    private EditText mEdtFeedback;
    @ViewInject(R.id.btn_subbmit)
    private Button mBtnSubbmit;

    private FeedbackDialog   mFeedbackDialog;
    private MyProgressDialog mProgressDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_feedback);
        x.view().inject( this );
        mContext = this;

        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_subbmit:
                subbmit();
                break;
            default:
                break;
        }
    }

    @Override
    public String getUmEventName() {
        return "parent_mycenter_feedback";
    }
    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "意见反馈" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
            }
        });

        mProgressDialog = new MyProgressDialog(mContext);
        mProgressDialog.setMessage("提交中...");
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
        params.put("sourceType", "par_android_app");
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
