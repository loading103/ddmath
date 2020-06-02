package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.ValidatorUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.tsinghuabigdata.edu.utils.SetTimeout;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 家长端--绑定手机 更换手机号
 */

public class ParentBindingPhoneActivity extends RoboActivity implements View.OnClickListener{

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @ViewInject(R.id.tv_currphone)
    private TextView currphoneView;

    @Order(1)
    @NotEmpty(trim = true, message = "手机号不能为空", sequence = 1)
    @Pattern(regex = ValidatorUtils.MOBILE_REGEX, message = "输入正确手机号码", sequence = 2)
    @ViewInject(R.id.et_new_phone)
    private EditText newphoneEditText;

    @Order(2)
    @NotEmpty(trim = true, message = "验证码不能为空", sequence = 1)
    @ViewInject(R.id.et_verfy_code )
    private EditText verifyCodeEditText;

    @ViewInject(R.id.btn_get_verifycode)
    private Button verifyCodeButton;
    @ViewInject( R.id.btn_finish )
    private Button finishButton;

    private Context mContext;


    private MyProgressDialog progressDialog;

    private SetTimeout mSetTimeout;
    private boolean    isTimeCountStop;
    private final int mMaxTime = AppConst.CODE_MAX_TIME;
    //private boolean countting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_phone);
        x.view().inject( this );
        mContext = this;
        initView();
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.btn_get_verifycode:
                sendVerifycode();
                break;
            case R.id.btn_finish:
                bindPhone();
                break;
            default:
                break;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSetTimeout != null) {
            isTimeCountStop = true;
            mSetTimeout.cancel();
        }
    }

    @Override
    public String getUmEventName(){
        return "parent_mycenter_bindphone";
    }
    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "更换手机号码" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        if( phone==null )
            phone = "";
        currphoneView.setText( String.format(Locale.getDefault(),"当前手机号码：%s",phone) );

        //
        newphoneEditText.addTextChangedListener(new PhoneTextwatcher());
        verifyCodeEditText.addTextChangedListener( new MyRegisterTextwatcher() );

        verifyCodeButton.setOnClickListener( this );
        finishButton.setOnClickListener( this );
        finishButton.setEnabled( false );

        progressDialog = new MyProgressDialog(mContext);
    }

    //----------------------------------------------------------------------------------------------
    private void sendVerifycode() {
        String phone = newphoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show( mContext,"手机号不能为空");
            return;
        } else if (!ValidatorUtils.validateMobile(phone)) {
            ToastUtils.show( mContext,"请输入正确手机号");
            return;
        }

        progressDialog.setMessage("获取验证码...");
        progressDialog.show();
        String type = "parentUpdate";
        new LoginModel().getVerifycode(phone, type, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                progressDialog.dismiss();
                AlertManager.toast(mContext, "验证码已发送至手机，请查看");
                getVerifycodeSuccess();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    private void getVerifycodeSuccess() {
        verifyCodeButton.setEnabled(false);
        verifyCodeButton.setText( String.format(Locale.getDefault(), getString(R.string.resend_verifycode),mMaxTime ) );

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
                            verifyCodeButton.setText( String.format(Locale.getDefault(), getString(R.string.resend_verifycode),remain ));
                        } else {
                            verifyCodeButton.setText(getText(R.string.send_verifycode));
                            verifyCodeButton.setEnabled(true);
                            newphoneEditText.setEnabled(true);
                            //countting = false;
                        }
                    }
                });
            }
        });
        mSetTimeout.start();
        newphoneEditText.setEnabled(false);
        //countting = true;
    }

    private void bindPhone(){

        final String phone = newphoneEditText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show( mContext,"手机号不能为空");
            return;
        } else if (!ValidatorUtils.validateMobile(phone)) {
            ToastUtils.show( mContext,"请输入正确手机号");
            return;
        }

        String verifyCode = verifyCodeEditText.getText().toString().trim();
        if( TextUtils.isEmpty(verifyCode) ){
            ToastUtils.show( mContext,"验证码不能为空");
            return;
        }

        LoginInfo loginInfo = AccountUtils.getLoginParent();
        if( loginInfo == null || TextUtils.isEmpty(loginInfo.getAccountId()) ){
            ToastUtils.show( mContext,"请重新登录");
            return;
        }

        progressDialog.setMessage("更换手机号码中...");
        progressDialog.show();

        new LoginModel().bindMobile(loginInfo.getAccountId(), phone, verifyCode, new RequestListener() {
            @Override
            public void onSuccess(Object res) {
                progressDialog.dismiss();
                ToastUtils.show( mContext,"更换手机号码成功!");

                //更新信息
                ParentInfo parentInfo = AccountUtils.getParentInfo();
                if( parentInfo!=null ) parentInfo.setPhoneNumber( phone );
                //先缓存
                AccountUtils.setParentInfo( parentInfo );

                Intent intent = new Intent();
                intent.putExtra("phone", phone);
                setResult( RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                AlertManager.showErrorInfo( mContext, ex );
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    class PhoneTextwatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String phone = s.toString().trim();
            if (!TextUtils.isEmpty(phone) && s.length() == 11) {
                verifyCodeButton.setEnabled(true);
            } else {
                verifyCodeButton.setEnabled(false);
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
            String phone = newphoneEditText.getText().toString().trim();
            String code  = verifyCodeEditText.getText().toString().trim();
            boolean invalid = TextUtils.isEmpty(phone) || TextUtils.isEmpty(code) || !ValidatorUtils.validateMobile(phone) ;
            finishButton.setEnabled(!invalid);
        }
    }

}
