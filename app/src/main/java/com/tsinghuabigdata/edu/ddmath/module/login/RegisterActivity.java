package com.tsinghuabigdata.edu.ddmath.module.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RegisterAndLoginSuccessEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginState;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginSuccessHandler;
import com.tsinghuabigdata.edu.ddmath.module.login.view.PwordTextwatcher;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.RegRewardBean;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.ValidatorUtils;
import com.tsinghuabigdata.edu.ddmath.view.GenderSelectorView;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.tsinghuabigdata.edu.utils.SetTimeout;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.util.DensityUtil;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * 注册
 * Created by Administrator on 2016/12/14.
 */

public class RegisterActivity extends RoboActivity implements View.OnClickListener {

    private Button btRegister;
    //private TextView btLogin;

    @ViewInject(R.id.bt_verifycode)
    private Button btVerifycode;


    @ViewInject(R.id.genderview)
    private GenderSelectorView genderSelectorView;

    @Order(1)
    @NotEmpty(trim = true, message = "手机号不能为空", sequence = 1)
    @Pattern(regex = ValidatorUtils.MOBILE_REGEX, message = "输入正确手机号码", sequence = 2)
    @ViewInject(R.id.et_phone_num)
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
    @NotEmpty(trim = true, message = "昵称不能为空", sequence = 1)
    @ViewInject(R.id.et_nickname)
    private EditText etNickname;


    //    @Pattern(regex = ValidatorUtils.MOBILE_REGEX, message = "输入正确手机号码")
    @ViewInject(R.id.et_recommend_num)
    private EditText etRecommendNum;

    @ViewInject(R.id.tv_give_bean)
    private TextView tvGiveBean;

    @ViewInject(R.id.toolbar)
    private WorkToolbar toolbar;

    @ViewInject(R.id.checkbox)
    private CheckBox checkBox;
    @ViewInject( R.id.tv_protocol )
    private TextView protocolView;

    private Validator mValidator;

    private MyProgressDialog progressDialog;

    private SetTimeout mSetTimeout;
    private boolean    isTimeCountStop;
    private final int mMaxTime = AppConst.CODE_MAX_TIME;
    private boolean countting;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad() ? R.layout.activity_register : R.layout.activity_register_mobile);
        x.view().inject(this);

        context = this;
        initView();
        initData();
        initValidator();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register:
                mValidator.validate();
                break;
            case R.id.bt_login:
                goActivity(LoginActivity.class);
                finish();
                break;
            case R.id.bt_verifycode:
                sendVerifycode();
                break;
            case R.id.tv_protocol:
                goActivity(UserProtocolActivity.class);
                break;
            default:
                break;
        }
    }

    //----------------------------------------------------------------------------------------------
    private void initValidator() {
        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                KeyboardUtil.hidInput((Activity) context);
                register();
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
        toolbar.setTitle("注册");
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);

        btRegister = (Button) findViewById(R.id.bt_register);
        TextView btLogin = (TextView) findViewById(R.id.bt_login);

        int w = DensityUtil.dip2px( GlobalData.isPad()?48:32 );
        int h = DensityUtil.dip2px( GlobalData.isPad()?48:32 );
        Drawable drawable = getResources().getDrawable( R.drawable.ic_yanzhengma );
        drawable.setBounds( 0, 0, w,h );
        etVerifycode.setCompoundDrawables( drawable, null,null,null);

        drawable = getResources().getDrawable( R.drawable.ic_iphone_register );
        drawable.setBounds( 0, 0, w,h );
        etPhone.setCompoundDrawables( drawable, null,null,null);
        drawable = getResources().getDrawable( R.drawable.ic_key );
        drawable.setBounds( 0, 0, w,h );
        etPasswd.setCompoundDrawables( drawable, null,null,null);
        drawable = getResources().getDrawable( R.drawable.ic_iphonetuijian );
        drawable.setBounds( 0, 0, w,h );
        etRecommendNum.setCompoundDrawables( drawable, null,null,null);

        //listener
        btRegister.setOnClickListener(this);
        if(btLogin!=null)btLogin.setOnClickListener(this);
        btVerifycode.setOnClickListener(this);
        btVerifycode.setEnabled(false);
        btRegister.setEnabled(false);

        protocolView.setOnClickListener( this );

        MyRegisterTextwatcher registerTextwatcher = new MyRegisterTextwatcher();
        etNickname.addTextChangedListener(registerTextwatcher);
        etPhone.addTextChangedListener(new PhoneTextwatcher());
        etPasswd.addTextChangedListener(registerTextwatcher);
        etVerifycode.addTextChangedListener(registerTextwatcher);

        PwordTextwatcher pwTextwatcher = new PwordTextwatcher(etPasswd, btRegister);
        etPasswd.addTextChangedListener(pwTextwatcher);

        progressDialog = new MyProgressDialog(context);
    }

    private void initData() {
        getRegisteReward();
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
        String type = "register";
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
                AlertManager.showErrorInfo(context, ex);
            }
        });
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
                            btVerifycode.setText( String.format(Locale.getDefault(), getString(R.string.resend_verifycode),remain ));
                        } else {
                            btVerifycode.setText(getText(R.string.send_verifycode));
                            btVerifycode.setEnabled(true);
                            etPhone.setEnabled(true);
                            countting = false;
                        }
                    }
                });
            }
        });
        mSetTimeout.start();
        etPhone.setEnabled(false);
        countting = true;
    }

    private void getRegisteReward() {
        new LoginModel().getRegisteReward(new RequestListener<RegRewardBean>() {

            @Override
            public void onSuccess(RegRewardBean vo) {
                if (vo != null) {
                    String data = String.format(getResources().getString(R.string.register_give_bean), vo.getRegWithOutRecAward(),(vo.getRegWithRecWard()-vo.getRegWithOutRecAward()));
                    tvGiveBean.setText(data);
                }
            }

            @Override
            public void onFail(HttpResponse<RegRewardBean> response, Exception ex) {

            }
        });
    }

    private void register() {
        String nickname = etNickname.getText().toString().trim();
        String sex = genderSelectorView.getSelGender() == GenderSelectorView.GIRL ? "female" : "male";
        String cellPhoneNumber = etPhone.getText().toString().trim();
        String verifyCode = etVerifycode.getText().toString().trim();
        String password = etPasswd.getText().toString().trim();
        String recPhoneNum = etRecommendNum.getText().toString().trim();
        if (!TextUtils.isEmpty(recPhoneNum)) {
            if (!ValidatorUtils.validateMobile(recPhoneNum)) {
                AlertManager.toast(context, "推荐人手机号码格式不正确");
                return;
            }
        }

        if( !checkBox.isChecked() ){
            ToastUtils.showShort( this, "请同意" + getResources().getString( R.string.user_procotol));
            return;
        }
        progressDialog.setMessage("注册中...");
        progressDialog.show();
        new LoginModel().register(nickname, sex, cellPhoneNumber, password, verifyCode, recPhoneNum, new RequestListener<String>() {
            @Override
            public void onSuccess(String accountId) {
                progressDialog.dismiss();
                AlertManager.toast(context, "注册成功");
                //注册成功后，自动登录一次
                autologin();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();

                if (ex instanceof AppRequestException) {
                    String inform = ((AppRequestException) ex).getResponse().getInform();
                    LogUtils.i("inform=" + inform);
                    if (inform.equals("the loginName exist")) {
                        AlertManager.toast(context, "这个名字太抢手了，已经有小伙伴注册过了，再想一个吧！");
                    } else {
                        AlertManager.showErrorInfo(context, ex);
                    }

                    // 当为30102，说明推荐人手机不存在或者手机号错误。可以重新确认手机号，或者将recPhoneNum字段设为空再次请求
                    int code = ((AppRequestException) ex).getResponse().getCode();
                    if (code == 30102) {
                        AlertManager.toast(context, "推荐人手机号不存在或者手机号错误，请确认手机号");
                    }
                } else {
                    AlertManager.showErrorInfo(context, ex);
                }
            }
        });
    }

    private void autologin() {
        final String loginName = etPhone.getText().toString().trim();
        final String passwd = etPasswd.getText().toString().trim();
        progressDialog.setMessage("登录中...");
        progressDialog.show();
        new LoginModel().login(context, loginName, passwd, new RequestListener<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                progressDialog.dismiss();
                AlertManager.toast(context, "登录成功");
                if (loginInfo == null) {
                    //                    AlertManager.toast(getContext(), "用户名或密码错误");
                    //跳到登录页面
                    finish();
                    goActivity(LoginActivity.class);
                } else {
                    LoginSuccessHandler.loginSuccessHandler(context, loginName, passwd);
                    finish();
                    LoginState.getInstance().setLoginListener(true);
                    LoginController.getInstance().Login(true);
                    EventBus.getDefault().post(new RegisterAndLoginSuccessEvent());
                    Intent intent = new Intent(context,UserInfoSuplementActivity.class);
                    intent.putExtra("register", true);
                    context.startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                finish();
                //跳到登录页面
                goActivity(LoginActivity.class);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSetTimeout != null) {
            //Log.d("recover", "onDestroy: ");
            isTimeCountStop = true;
            mSetTimeout.cancel();
        }
    }


    class PhoneTextwatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean invalid = /*isEdEmpty(etNickname) ||*/ !isPhoneMatch(s.toString().trim()) || isEdEmpty(etPasswd) || isEdEmpty(etVerifycode);
            btRegister.setEnabled(!invalid);
            if (countting) {
                return;
            }
            String phone = s.toString().trim();
            if (!TextUtils.isEmpty(phone) && s.length() == 11) {
                btVerifycode.setEnabled(true);
            } else {
                btVerifycode.setEnabled(false);
            }
        }
    }

    class MyRegisterTextwatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean invalid = /*isEdEmpty(etNickname) ||*/ !isPhoneMatch(etPhone.getText().toString().trim()) || isEdEmpty(etPasswd) || isEdEmpty(etVerifycode);
            btRegister.setEnabled(!invalid);
        }
    }


    private boolean isEdEmpty(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim());
    }

    private boolean isPhoneMatch(String phone) {
        return !(TextUtils.isEmpty(phone) || !ValidatorUtils.validateMobile(phone));
//        if (TextUtils.isEmpty(phone) || !ValidatorUtils.validateMobile(phone)) {
//            return false;
//        }
//        return true;
    }




}
