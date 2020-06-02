package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.dialog.BaseDialog;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.VerifyBean;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadManager;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.ValidatorUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.tsinghuabigdata.edu.utils.SetTimeout;

import org.xutils.view.annotation.ViewInject;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * 强制验证手机Dialog
 * Created by Administrator on 2018/1/24.
 */

public class VerifyPhoneDialog extends BaseDialog implements View.OnClickListener {


    private final static int ST_NONE   = 0;
    private final static int ST_RUNING = 1;
    private final static int ST_FINISH = 2;

    @Order(1)
    @NotEmpty(trim = true, message = "手机号不能为空", sequence = 1)
    @Pattern(regex = ValidatorUtils.MOBILE_REGEX, message = "输入正确手机号码", sequence = 2)
    @ViewInject(R.id.et_phone_num)
    private EditText etPhone;

    @Order(2)
    @NotEmpty(trim = true, message = "验证码不能为空", sequence = 1)
    @ViewInject(R.id.et_verifycode)
    private EditText etVerifycode;

    private Button    btVerifycode;
    private ImageView mIvChangeAccount;
    private ImageView ivFinish;

    private Context   mContext;
    private Validator mValidator;

    private MyProgressDialog progressDialog;
    private Activity         mActivity;

    private SetTimeout mSetTimeout;
    private boolean    isTimeCountStop;
    private       int timeCountStatus = ST_NONE;
    private final int mMaxTime        = AppConst.CODE_MAX_TIME;
    private boolean mDetachedFromWindow;
    private String mStudentId = "";


    public VerifyPhoneDialog(@NonNull Context context) {
        this(context, R.style.dialog);
    }

    public VerifyPhoneDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        level = 2;
        initView();
        initData();
        initValidator();
    }


    private void initView() {
        mContext = getContext();
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_verify_phone);
        } else {
            setContentView(R.layout.dialog_verify_phone_phone);
        }


        etPhone = (EditText) findViewById(R.id.et_phone_num);
        etVerifycode = (EditText) findViewById(R.id.et_verifycode);
        btVerifycode = (Button) findViewById(R.id.bt_verifycode);
        mIvChangeAccount = (ImageView) findViewById(R.id.iv_change_account);
        ivFinish = (ImageView) findViewById(R.id.iv_finish);
        if (AppUtils.isDebug()) {
            setCancelable(true);
        } else {
            setCancelable(false);
        }
        setCanceledOnTouchOutside(false);

        etPhone.clearFocus();
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //        dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        //        dialogWindow.setType(WindowManager.LayoutParams.TYPE_TOAST);
        btVerifycode.setOnClickListener(this);
        mIvChangeAccount.setOnClickListener(this);
        ivFinish.setOnClickListener(this);
        progressDialog = new MyProgressDialog(mContext);
    }

    private void initData() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
    }

    private void initValidator() {
        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                KeyboardUtil.hidInput(mActivity);
                verifyPhone();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    ((EditText) error.getView()).setError(error.getFailedRules().get(0).getMessage(mActivity));
                }
            }
        });
    }

    private void verifyPhone() {
        //etPhone.setError("该号码已经在豆豆数学平台验证过，请查看后重试");
        progressDialog.setMessage("验证中...");
        progressDialog.show();
        String phone = etPhone.getText().toString().trim();
        String code = etVerifycode.getText().toString().trim();
        new LoginModel().verifyMobile(mStudentId, phone, code, new RequestListener<VerifyBean>() {

            @Override
            public void onSuccess(VerifyBean vo) {
                LogUtils.i("verifyPhone onSuccess");
                if (mDetachedFromWindow) {
                    return;
                }
                progressDialog.dismiss();
                if (vo == null || !vo.isSuccess()) {
                    ToastUtils.showShort(mContext, R.string.server_error);
                } else {
                    AccountUtils.saveVerifyState(mContext, mStudentId);
                    queryUserdetailInfo();
                    finishDismiss();
                }
            }

            @Override
            public void onFail(HttpResponse<VerifyBean> response, Exception ex) {
                LogUtils.i("verifyPhone onFail");
                if (mDetachedFromWindow) {
                    return;
                }
                progressDialog.dismiss();
                AlertManager.showErrorInfo(getContext(), ex);
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
        String type = "confirm"; //短信类型，学生注册此字段为register，手机号确认comfirm，更新个人信息update
        new LoginModel().getVerifycode(phone, type, new RequestListener<String>() {
            @Override
            public void onSuccess(String res) {
                if (mDetachedFromWindow) {
                    return;
                }
                progressDialog.dismiss();
                AlertManager.toast(mContext, "验证码已发送至手机，请查看");
                getVerifycodeSuccess();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (mDetachedFromWindow) {
                    return;
                }
                progressDialog.dismiss();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    private void getVerifycodeSuccess() {
        btVerifycode.setEnabled(false);
        btVerifycode.setText( String.format(Locale.getDefault(), mContext.getString(R.string.resend_verifycode),mMaxTime ));

        mSetTimeout = new SetTimeout(mMaxTime, TimeUnit.SECONDS, 1);
        mSetTimeout.setHandler(new SetTimeout.SetTimeoutHandler() {
            @Override
            public void handler(final int current) {
                if (isTimeCountStop) {
                    return;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int remain = mMaxTime - current - 1;
                        if (remain > 0) {
                            btVerifycode.setText( String.format(Locale.getDefault(), mContext.getString(R.string.resend_verifycode),remain ));
                        } else {
                            btVerifycode.setText(mContext.getText(R.string.send_verifycode));
                            btVerifycode.setEnabled(true);
                        }
                    }
                });
            }
        });
        mSetTimeout.start();
    }

    private void queryUserdetailInfo() {
        //绑定成功后，更新本地的个人信息
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null) {
            return;
        }
        new LoginModel().queryUserdetailInfo(loginInfo.getAccessToken(), loginInfo.getAccountId(), new RequestListener<UserDetailinfo>() {
            @Override
            public void onSuccess(UserDetailinfo res) {
                if (res != null) {
                    AccountUtils.setUserdetailInfo(res);
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {

            }
        });
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_verifycode:
                sendVerifycode();
                break;
            case R.id.iv_change_account:
                logout();
                break;
            case R.id.iv_finish:
                mValidator.validate();
                break;
            default:
                break;
        }
    }

    private void logout() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null) {
            return;
        }
        progressDialog.setMessage("正在退出...");
        progressDialog.show();
        new LoginModel().logout(loginInfo.getAccessToken(), new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                if (mDetachedFromWindow)
                    return;
                UploadManager uploadManager = UploadManager.getUploadManager();
                if (uploadManager != null)
                    uploadManager.stopAllUploadTask();
                progressDialog.dismiss();
                AccountUtils.clear();
                LoginController.getInstance().Login(false);
                goActivity(LoginActivity.class);
                finishDismiss();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if (mDetachedFromWindow)
                    return;
                progressDialog.dismiss();
                AccountUtils.clear();
                LoginController.getInstance().Login(false);
                goActivity(LoginActivity.class);
                finishDismiss();
            }
        });
    }

    private void goActivity(Class clazz) {
        Intent intent = new Intent(getContext(), clazz);
        getContext().startActivity(intent);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDetachedFromWindow = true;
    }
}
