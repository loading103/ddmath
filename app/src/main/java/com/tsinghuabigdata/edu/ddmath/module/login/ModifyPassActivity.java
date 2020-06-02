package com.tsinghuabigdata.edu.ddmath.module.login;

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
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.ServerUpgradingEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.login.view.PwordTextwatcher;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.tsinghuabigdata.edu.utils.SetTimeout;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * 修改密码
 * Created by Administrator on 2016/12/14.
 */

public class ModifyPassActivity extends RoboActivity implements View.OnClickListener {

    public final static String PARAM_PHONE = "phone";

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    @ViewInject(R.id.bt_done)
    private Button btDone;

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

    private Validator mValidator;

    private ProgressDialog progressDialog;

    private SetTimeout mSetTimeout;
    private final int mMaxTime = AppConst.CODE_MAX_TIME;

    private boolean isTimeCountStop;
    private Context context;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( GlobalData.isPad()?R.layout.activity_modify_pass:R.layout.activity_modify_pass_mobile);
        x.view().inject(this);
        context = this;

        initView();
        initData();
        initValidator();
    }

    private void initValidator() {
        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                KeyboardUtil.hidInput((Activity) context);
                getAccountId();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    ((EditText) error.getView()).setError(error.getFailedRules().get(0).getMessage(context));
                }
            }
        });
    }

    private void initView() {
        workToolbar.setTitle("修改密码");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        PwordTextwatcher pwTextwatcher = new PwordTextwatcher(etOldPasswd, null);
        etOldPasswd.addTextChangedListener(pwTextwatcher);
        PwordTextwatcher pwTextwatcher1 = new PwordTextwatcher(etNewPasswd, null);
        etNewPasswd.addTextChangedListener(pwTextwatcher1);
        PwordTextwatcher pwTextwatcher2 = new PwordTextwatcher(etConfirmPasswd, null);
        etConfirmPasswd.addTextChangedListener(pwTextwatcher2);

        //listener
        btDone.setOnClickListener(this);

        progressDialog = new MyProgressDialog(context);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_done:
                mValidator.validate();
                break;
            default:
                break;
        }
    }

    private void getAccountId() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
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
        progressDialog.setMessage("修改密码中...");
        progressDialog.show();

        new LoginModel().modifyPass(accountId, oldPasswd, newPasswd, confirmPasswd, new RequestListener<String>() {
            @Override
            public void onSuccess(String accountId) {
                progressDialog.dismiss();
                AlertManager.toast(context, "密码修改成功");
                //成功后，进入登录界面
                AccountUtils.clear();
                //LoginState.getInstance().setLoginListener(false);
                LoginController.getInstance().Login(false);
                goActivity(LoginActivity.class);
                EventBus.getDefault().post( new ServerUpgradingEvent() );
                finish();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    Log.i("sky", "inform=" + inform);
                    if (inform.equals("old password error!")) {
                        AlertManager.toast(context, "您输入的旧密码不正确，忘记密码请联系客服");
                    } else {
                        AlertManager.showErrorInfo(context, ex);
                    }
                } else {
                    AlertManager.showErrorInfo(context, ex);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSetTimeout != null) {
            Log.d("recover", "onDestroy: ");
            isTimeCountStop = true;
            mSetTimeout.cancel();
        }
    }

}
