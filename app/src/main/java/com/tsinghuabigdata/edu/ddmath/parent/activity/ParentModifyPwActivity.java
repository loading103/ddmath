package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.login.view.PwordTextwatcher;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 家长端--修改密码
 */

public class ParentModifyPwActivity extends RoboActivity implements View.OnClickListener{

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @Order(1)
    @NotEmpty(trim = true, message = "密码不能为空", sequence = 1)
    @Length(message = "密码最少6位，请重新输入", min = 6, sequence = 2)
    @ViewInject(R.id.et_old_passwd)
    private EditText etOldPasswd;

    @Order(2)
    @NotEmpty(trim = true, message = "密码不能为空", sequence = 1)
    @Password(message = "密码最少6位，请重新输入", min = 6, sequence = 2)
    @ViewInject(R.id.et_new_passwd)
    private EditText etNewPasswd;

    @Order(3)
    @NotEmpty(trim = true, message = "密码不能为空", sequence = 1)
    @ConfirmPassword(message = "两次密码不一致，请重新输入", sequence = 2)
    @ViewInject(R.id.et_confirm_passwd)
    private EditText etConfirmPasswd;

    @ViewInject(R.id.btn_finish)
    private Button finishBtn;

    private Validator mValidator;

    private ProgressDialog progressDialog;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_modifypw);
        x.view().inject( this );
        mContext = this;
        initView();

        initView();
        initValidator();
    }


    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.btn_finish:
                //mValidator.validate();

                KeyboardUtil.hidInput((Activity) mContext);
                getAccountId();
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public String getUmEventName() {
        return "parent_mycenter_modifypw";
    }
    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "修改密码" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        PwordTextwatcher pwTextwatcher = new PwordTextwatcher(etOldPasswd, null);
        etOldPasswd.addTextChangedListener(pwTextwatcher);
        PwordTextwatcher pwTextwatcher1 = new PwordTextwatcher(etNewPasswd, null);
        etNewPasswd.addTextChangedListener(pwTextwatcher1);
        PwordTextwatcher pwTextwatcher2 = new PwordTextwatcher(etConfirmPasswd, null);
        etConfirmPasswd.addTextChangedListener(pwTextwatcher2);

        //listener
        finishBtn.setOnClickListener(this);

        progressDialog = new MyProgressDialog(mContext);
    }

    private void initValidator() {
        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                KeyboardUtil.hidInput((Activity) mContext);
                getAccountId();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    ((EditText) error.getView()).setError(error.getFailedRules().get(0).getMessage(mContext));
                }
            }
        });
    }

    private void getAccountId() {
        LoginInfo loginInfo = AccountUtils.getLoginParent();
        if (loginInfo != null) {
            String accountId = loginInfo.getAccountId();
            if (!TextUtils.isEmpty(accountId)) {
                modifyPassword(accountId);
            }
        }
    }

    private void modifyPassword(String accountId) {
        String oldPasswd = etOldPasswd.getText().toString().trim();
        String newPasswd = etNewPasswd.getText().toString().trim();
        String confirmPasswd = etConfirmPasswd.getText().toString().trim();

        if( !newPasswd.equals(confirmPasswd) ){
            AlertManager.toast(mContext, "两次输入的密码不一致");
            return;
        }

        progressDialog.setMessage("修改密码中...");
        progressDialog.show();
        new LoginModel().modifyPass(accountId, oldPasswd, newPasswd, confirmPasswd, new RequestListener<String>() {
            @Override
            public void onSuccess(String accountId) {
                progressDialog.dismiss();
                AlertManager.toast(mContext, "密码修改成功");

                //成功后，进入登录界面
                AccountUtils.setLoginParent( null );
                LoginController.getInstance().Login(false);

                //关闭所有界面，跳转到登录界面
                finishAll();

                LoginActivity.openLoginActivity( mContext, LoginActivity.ROLE_PARENT );
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    Log.i("sky", "inform=" + inform);
                    if (inform.equals("old password error!")) {
                        AlertManager.toast(mContext, "您输入的旧密码不正确，忘记密码请联系客服");
                    } else {
                        AlertManager.showErrorInfo(mContext, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(mContext, ex);
                }
            }
        });
    }

}
