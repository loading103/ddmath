package com.tsinghuabigdata.edu.ddmath.module.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.commons.http.InformMapping;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.dialog.ForgetPassDialog;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.login.view.PwordTextwatcher;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.DeviceUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.ValidatorUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.tsinghuabigdata.edu.utils.SetTimeout;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * 找回密码
 * Created by Administrator on 2016/12/14.
 */
public class RecoverPassActivity extends RoboActivity implements View.OnClickListener {

    public final static String PARAM_PHONE = "phone";

    @Order(1)
    @NotEmpty(trim = true, message = "手机号不能为空", sequence = 1)
    @Pattern(regex = ValidatorUtils.MOBILE_REGEX, message = "请输入正确手机号", sequence = 2)
    @ViewInject(R.id.et_phone)
    private EditText etPhone;

    @Order(2)
    @NotEmpty(trim = true, message = "验证码不能为空", sequence = 1)
    @ViewInject(R.id.et_verifycode)
    private EditText etVerifycode;

    @Order(3)
    @NotEmpty(trim = true, message = "密码不能为空", sequence = 1)
    @Password(message = "密码最少6位，请重新输入", min = 6, sequence = 2)
    @ViewInject(R.id.et_passwd)
    private EditText etPasswd;

    @Order(4)
    @NotEmpty(trim = true, message = "密码不能为空", sequence = 1)
    @ConfirmPassword(message = "两次密码不一致，请重新输入", sequence = 2)
    @ViewInject(R.id.et_confirm_passwd)
    private EditText etConfirmPasswd;

    @ViewInject(R.id.bt_verifycode)
    private Button btVerifycode;

    @ViewInject( R.id.btn_finish )
    private Button finishBtn;

    @ViewInject(R.id.toolbar)
    private WorkToolbar toolbar;

    private Validator mValidator;

    private ProgressDialog progressDialog;
    private ForgetPassDialog mForgetPassDialog;

    private SetTimeout mSetTimeout;
    private final int mMaxTime = AppConst.CODE_MAX_TIME;

    private boolean isTimeCountStop;
    private Context context;

    private String role = LoginActivity.ROLE_STUDENT;       //默认学生

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad()? R.layout.activity_recoverpass : R.layout.activity_recoverpass_phone);

        x.view().inject(this);
        context = this;

        parseIntent();
        initView();
        initData();
        initValidator();
    }

    private void parseIntent(){
        role = getIntent().getStringExtra( "role" );
        if( TextUtils.isEmpty(role) )
            role = LoginActivity.ROLE_STUDENT;
    }
    private void initValidator() {
        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                KeyboardUtil.hidInput((Activity) context);
                if (!ValidatorUtils.validateMobile(etPhone.getText().toString().trim())) {
                    etPhone.setError("请输入正确的手机号");
                    return;
                }
                //全部验证完输入，开始请求找回密码
                resetPassword();
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
        toolbar.setTitle("重置密码");
        //toolbar.setRightBtn("完成");
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);

        PwordTextwatcher pwTextwatcher = new PwordTextwatcher( etPasswd, null );
        etPasswd.addTextChangedListener(pwTextwatcher);
        PwordTextwatcher pwTextwatcher1 = new PwordTextwatcher( etConfirmPasswd, null );
        etConfirmPasswd.addTextChangedListener(pwTextwatcher1);

        btVerifycode.setOnClickListener(this);
        finishBtn.setOnClickListener( this );

        progressDialog = new MyProgressDialog(context);
        mForgetPassDialog = new ForgetPassDialog(context, R.style.dialog);
        mForgetPassDialog.setForgetPassListener(new ForgetPassDialog.ForgetPassListener() {
            @Override
            public void dial() {
                if ( DeviceUtils.hasSimCard( context )) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+AppConst.SERVER_PHONE));
                    startActivity(intent);
                } else {
                    mForgetPassDialog.dismiss();
                    ToastUtils.show( context, "本机不支持电话功能!", Toast.LENGTH_SHORT );
                }
            }
        });
    }

    private void initData() {
        Intent intent =  getIntent();
        String phone = intent.getStringExtra( PARAM_PHONE );
        if( ValidatorUtils.validateMobile( phone ) )
            etPhone.setText( phone );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_verifycode:
                sendVerifycode();
                break;
            case R.id.btn_finish:
                mValidator.validate();
                break;
            default:
                break;
        }
    }

    private void resetPassword() {
        String phone = etPhone.getText().toString().trim();
        String validcode = etVerifycode.getText().toString().trim();
        String passwd = etPasswd.getText().toString().trim();
        String confirmPasswd = etConfirmPasswd.getText().toString().trim();

        progressDialog.setMessage("修改密码中...");
        progressDialog.show();
        new LoginModel().resetPass(phone, validcode, passwd, confirmPasswd, role, new RequestListener<String>() {
            @Override
            public void onSuccess(String accountId) {
                progressDialog.dismiss();
                AlertManager.toast(context, "密码修改成功");
                //成功后，进入登录界面
                AccountUtils.clear();
                //goActivity(LoginActivity.class);
                finish();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                if (!TextUtils.isEmpty(response.getInform())) {
                    AlertManager.toast(context, InformMapping.getInstance().get(response.getInform()));
                } else {
                    AlertManager.showErrorInfo(context, ex);
                }
            }
        });
    }

    private void sendVerifycode() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("手机号不能为空");
            return;
        } else if (!ValidatorUtils.validateMobile(phone)) {
            etPhone.setError("请输入正确手机号");
            return;
        }
        //start get verifycode from server
        progressDialog.setMessage("获取验证码...");
        progressDialog.show();
        String type = LoginActivity.ROLE_STUDENT.equals(role)?"passwdRetrieve":"parentRetrieve";
        new LoginModel().getRegisterCode(phone, type, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                progressDialog.dismiss();
                AlertManager.toast(context, "验证码已发送至手机，请查看");
                getVerifycodeSuccess();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                if (response.getCode() == 21109) {
                    AlertManager.toast(context, "该手机号未注册");
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

    private void getVerifycodeSuccess() {
        btVerifycode.setEnabled(false);
        btVerifycode.setText( String.format(Locale.getDefault(), getString(R.string.resend_verifycode),mMaxTime ) );

        mSetTimeout = new SetTimeout(mMaxTime, TimeUnit.SECONDS, 1);
        mSetTimeout.setHandler(new SetTimeout.SetTimeoutHandler() {
            @Override
            public void handler(final int current) {
                if (isTimeCountStop) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int remain = mMaxTime - current - 1;
                        if (remain > 0) {
                            btVerifycode.setText( String.format(Locale.getDefault(), getString(R.string.resend_verifycode),remain ) );
                        } else {
                            btVerifycode.setText(getText(R.string.send_verifycode));
                            btVerifycode.setEnabled(true);
                        }
                    }
                });
            }
        });
        mSetTimeout.start();
    }
}
