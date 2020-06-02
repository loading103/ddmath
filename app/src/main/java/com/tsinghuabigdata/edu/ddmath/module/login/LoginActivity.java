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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.commons.http.InformMapping;
import com.tsinghuabigdata.edu.ddmath.commons.http.ResponseCode;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.RegisterAndLoginSuccessEvent;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginSuccessHandler;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.UseCountBean;
import com.tsinghuabigdata.edu.ddmath.module.upgrade.UpgradeManager;
import com.tsinghuabigdata.edu.ddmath.parent.MVPModel.ParentCenterModel;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentMainActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.KeyboardUtil;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.tsinghuabigdata.edu.utils.NetworkUtils;
import com.tsinghuabigdata.edu.utils.RestfulUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;


/**
 * login
 * Created by Administrator on 2016/12/14.
 */

public class LoginActivity extends RoboActivity implements View.OnClickListener {
    /**
     * 提示消息
     */
    public static final String MESSAGE   = "message";
    public static final String LOGINNAME = "loginname";
    public static final String ROLE_TYPE = "role";
    //private View root;

    //登录角色
    public static final String ROLE_STUDENT   = "student";
    public static final String ROLE_PARENT    = "parent";

    public static void openLoginActivity(Context context, String type ){
        Intent intent = new Intent( context, LoginActivity.class);
        intent.putExtra( ROLE_TYPE, type );
        context.startActivity( intent );
    }

    private String logineRole = ROLE_STUDENT;

    @ViewInject(R.id.login_mainlayout)
    private RelativeLayout mainLayout;
    @ViewInject(R.id.iv_topimage)
    private ImageView topImageView;

    @ViewInject(R.id.bt_enterdoudou)
    private Button   btEnterdoudou;
    @Length(trim = true, max = 16, message = "帐号不合法", sequence = 2)
    @NotEmpty(trim = true, message = "帐号不能为空", sequence = 1)
    @ViewInject(R.id.et_phone)
    private EditText etPhone;
    @Length(trim = true, max = 20, min = 6, message = "密码最少6位，请重新输入", sequence = 2)
    @NotEmpty(trim = true, message = "密码不能为空", sequence = 1)
    @ViewInject(R.id.et_passwd)
    private EditText etPasswd;

    @ViewInject(R.id.iv_delete)
    private ImageView ivDelete;
    @ViewInject(R.id.tv_forget_pass)
    private TextView  tvForgetPass;
    @ViewInject(R.id.tv_register)
    private TextView  tvRegister;

    @ViewInject( R.id.tv_use_total )
    private TextView useTotalView;

    @ViewInject( R.id.role_student_image )
    private ImageView studentImageView;
    @ViewInject( R.id.role_student )
    private ImageView studentTextView;

    @ViewInject( R.id.role_parent_image )
    private ImageView parentImageView;
    @ViewInject( R.id.role_parent )
    private ImageView parentTextView;

    @ViewInject( R.id.tv_forget_pw_parent )
    private TextView forgetPwView;
    //    @ViewInject(R.id.toolbar)
//    private WorkToolbar toolbar;
//    @ViewInject(R.id.checkbox)
//    private CheckBox checkBox;
//    @ViewInject(R.id.tv_protocol)
    //private TextView tvProtocol;

    private Validator mValidator;

    private MyProgressDialog progressDialog;
    private Context          context;

    private String mMessage;
    private String mLoginName;

    private boolean bQuit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad() ? R.layout.activity_login : R.layout.activity_login_mobile);

        context = this;
        x.view().inject(this);

        parseIntent();
        initView();
        initData();
        initValidator();
        new UpgradeManager(this).checkApkUpdate();

        if (!TextUtils.isEmpty(mMessage)) {
            AlertManager.toast(this, mMessage, true);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_enterdoudou:
                mValidator.validate();
                break;
            case R.id.tv_forget_pass:
                goActivity(RecoverPassActivity.class);
                break;
            case R.id.tv_register:
                //goActivity(RegisterActivity.class);
                ToastUtils.showLong( context, context.getResources().getString(R.string.regisiter_tips));
                break;
            case R.id.iv_delete:
                etPhone.setText(null);
                break;

            case R.id.role_parent:
            case R.id.role_parent_image:
                changeRole( false );
                break;
            case R.id.role_student:
            case R.id.role_student_image:
                changeRole( true );
                break;
            case R.id.tv_forget_pw_parent:
                Intent intent = new Intent( this, RecoverPassActivity.class );
                intent.putExtra( ROLE_TYPE, ROLE_PARENT);
                startActivity( intent );
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(RegisterAndLoginSuccessEvent event) {
        finish();
    }

    //---------------------------------------------------------------------------------------------
    private void initData() {

        if(RestfulUtils.mRequestQueue == null){
            // 接口类初始化
            AppRequestUtils.initialization( this );
        }

        EventBus.getDefault().register(this);
        getRegisteCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        bQuit = true;
    }

    private void initView() {
//        toolbar.setTitle("登录");
//        toolbar.setClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        }, null);

        progressDialog = new MyProgressDialog(context);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && etPhone.isFocused()) {
                    ivDelete.setVisibility(View.VISIBLE);
                }else {
                    ivDelete.setVisibility(View.INVISIBLE);
                }
            }
        });
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String str = etPhone.getText().toString();
                if (hasFocus && !TextUtils.isEmpty(str)){
                    ivDelete.setVisibility(View.VISIBLE);
                }else {
                    ivDelete.setVisibility(View.INVISIBLE);
                }
            }
        });

        //记忆登录名
        boolean isStudent = ROLE_STUDENT.equals( logineRole );
        if (TextUtils.isEmpty(mLoginName)) {
            etPhone.setText( PreferencesUtils.getString(context, isStudent?AppConst.LOGIN_NAME:AppConst.LOGIN_PARENTNAME, "") );
        } else {
            etPhone.setText(mLoginName);
        }
        //        etPhone.setText("leifeng");
        //        etPasswd.setText("123456");
        //listener
        btEnterdoudou.setOnClickListener(this);
        tvForgetPass.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        studentImageView.setOnClickListener( this );
        studentTextView.setOnClickListener( this );
        parentImageView.setOnClickListener( this );
        parentTextView.setOnClickListener( this );
        forgetPwView.setOnClickListener( this );
        //tvProtocol.setOnClickListener(this);
        //checkBox.setOnClickListener(this);
        // 《用户使用协议》默认勾选
        //checkBox.setChecked(true);

        changeRole( ROLE_STUDENT.equals(logineRole) );
    }

    private void parseIntent() {
        mMessage = getIntent().getStringExtra(MESSAGE);
        mLoginName = getIntent().getStringExtra(LOGINNAME);
        logineRole = getIntent().getStringExtra( ROLE_TYPE );
        if( TextUtils.isEmpty(logineRole) ) logineRole = ROLE_STUDENT;
    }

    private void initValidator() {
        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                KeyboardUtil.hidInput((Activity) context);
                //if (checkBox.isChecked()) {
                    login();
//                } else {
//                    ToastUtils.showShort(LoginActivity.this, "请同意《用户使用协议》");
//                }
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                for (ValidationError error : errors) {
                    ((EditText) error.getView()).setError(error.getFailedRules().get(0).getMessage(context));
                }
            }
        });
    }

    private void onfail(HttpResponse response, Exception ex){
        if (bQuit)
            return;
        progressDialog.dismiss();
        if (!AppUtils.isNetworkConnected(context) || NetworkUtils.isNoConnection(ex)) {
            AlertManager.toast(context, getString(R.string.no_connection));
        } else if (response.getCode() != ResponseCode.CODE_10000 && response.getInform() != null) {
            if (response.getInform().contains("student is null")) {
                AlertManager.toast(context, "获取个人信息失败");
            } else {
                AlertManager.toast(context, InformMapping.getInstance().get(response.getInform()));
            }
        } else if (ex instanceof HttpRequestException) {
            if (((HttpRequestException) ex).getRequest().getStatusCode() == 412 || ((HttpRequestException) ex).getRequest().getStatusCode() == 401) {
                AlertManager.toast(context, "用户名或密码错误");
            } else {
                AlertManager.toast(context, getString(R.string.server_error));
            }
        } else {
            AlertManager.toast(context, getString(R.string.server_error));
        }
    }
    private void login() {
        progressDialog.setMessage("正在登录...");
        progressDialog.show();

        final String loginName = etPhone.getText().toString().trim();
        final String passwd = etPasswd.getText().toString().trim();
        final String deviceId= AppUtils.getDeviceId( context );

        //学生角色登录
        if( ROLE_STUDENT.equals( logineRole ) ){
            new LoginModel().login(context, loginName, passwd, new RequestListener<LoginInfo>() {
                @Override
                public void onSuccess(LoginInfo loginInfo) {
                    if (bQuit)
                        return;
                    progressDialog.dismiss();
                    if (loginInfo == null) {
                        AlertManager.toast(context, "用户名或密码错误");
                    } else {
                        //保存用户名和密码
                        LoginSuccessHandler.loginSuccessHandler(context, loginName, passwd);
                        //AccountUtils.checkJionSchoolClass( context );
                        LoginController.getInstance().Login(true);

                        Intent intent = new Intent( context, MainActivity.class );
                        intent.putExtra("from", "login" );
                        startActivity( intent );
                        finish();
                    }
                }

                @Override
                public void onFail(HttpResponse response, Exception ex) {
                    onfail(response,ex);
                }
            });
        }
        //家长角色登录
        else{
            new ParentCenterModel().login(loginName, passwd, deviceId, new RequestListener<LoginInfo>() {
                @Override
                public void onSuccess(LoginInfo loginInfo) {
                    if (bQuit)    return;
                    progressDialog.dismiss();
                    if (loginInfo == null) {
                        AlertManager.toast(context, "用户名或密码错误");
                        return;
                    }
                    LoginSuccessHandler.cacheParentHandler( context, loginName, passwd );
                    AccountUtils.setLoginParent( loginInfo );
                    finishAll();
                    startActivity( new Intent( context, ParentMainActivity.class ) );
                    finish();
                }

                @Override
                public void onFail(HttpResponse<LoginInfo> response, Exception ex) {
                    onfail(response,ex);
                }
            });
        }
    }

    private void changeRole( boolean student ){

        logineRole = student?ROLE_STUDENT:ROLE_PARENT;
        mainLayout.setBackgroundColor( getResources().getColor( student?R.color.color_F1FBFF : R.color.color_F2FFFA ));
        topImageView.setImageResource( student?R.drawable.studentbg_top:R.drawable.parentbg_top );
        //
        useTotalView.setTextColor( getResources().getColor( student?R.color.color_0286D9:R.color.color_11AB6A) );
        Drawable drawable = getResources().getDrawable( student?R.drawable.usertotal:R.drawable.usertotal_green);

        int w = DensityUtils.dp2px( context, GlobalData.isPad()?24:16 );
        drawable.setBounds( 0, 0, w, w );
        useTotalView.setCompoundDrawables( drawable, null,null,null );

        //
        studentImageView.setSelected( student );
        studentTextView.setSelected( student );

        etPhone.setText( PreferencesUtils.getString(context, student?AppConst.LOGIN_NAME:AppConst.LOGIN_PARENTNAME, "") );

        parentImageView.setSelected( !student );
        parentTextView.setSelected( !student );

        etPhone.setHint( getResources().getText( student?R.string.student_login_hint:R.string.parent_login_hint) );
        etPasswd.setHint( getResources().getText( student?R.string.student_pw_hint:R.string.parent_pw_hint) );

        btEnterdoudou.setBackgroundResource( student?R.drawable.bg_login_btn_blue:R.drawable.bg_login_btn_parent );
        btEnterdoudou.setText( getResources().getText( student?R.string.student_login:R.string.login) );

        tvForgetPass.setVisibility( student?View.VISIBLE:View.GONE );
        tvRegister.setVisibility( student?View.VISIBLE:View.GONE );
        forgetPwView.setVisibility( student?View.GONE:View.VISIBLE );
    }

    private void getRegisteCount() {
        new LoginModel().getUseCount(new RequestListener<UseCountBean>() {

            @Override
            public void onSuccess(UseCountBean countBean) {
                if (countBean != null) {
                    String data = String.format(getResources().getString(R.string.register_count), countBean.getUseCount());
                    //获取使用人次
                    useTotalView.setVisibility( View.VISIBLE );
                    useTotalView.setText( data );
                }
            }

            @Override
            public void onFail(HttpResponse<UseCountBean> response, Exception ex) {
            }
        });
    }
}
