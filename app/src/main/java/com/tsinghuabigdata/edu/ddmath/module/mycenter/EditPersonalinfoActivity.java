package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.EditInfoEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.StringUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.ValidatorUtils;
import com.tsinghuabigdata.edu.ddmath.view.GenderSelectorView;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;
import com.tsinghuabigdata.edu.utils.SetTimeout;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * 个人信息编辑
 * Created by Administrator on 2016/12/14.
 */

public class EditPersonalinfoActivity extends RoboActivity implements View.OnClickListener {

//    private final static int ST_NONE   = 0;
//    private final static int ST_RUNING = 1;
//    private final static int ST_FINISH = 2;

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    //private View   root;
    @ViewInject(R.id.bt_done)
    private Button btDone;

    @ViewInject(R.id.genderview)
    private GenderSelectorView genderSelectorView;

    @ViewInject(R.id.et_nickname)
    private EditText etNickname;

    /*@ViewInject(R.id.et_reallyname)
    private EditText etReallyname;*/

    @ViewInject(R.id.et_phone)
    private EditText etPhone;

    @ViewInject(R.id.et_verifycode)
    private EditText etVerifycode;

    @ViewInject(R.id.bt_verifycode)
    private Button btVerifycode;

    @ViewInject(R.id.et_encrollyears)
    private TextView etEnrollyears;

    @ViewInject(R.id.et_mailaddress)
    private EditText mailAddrView;

    private MyProgressDialog progressDialog;
    private Context          context;

    //private SetTimeout mSetTimeout;
    //private       int timeCountStatus = ST_NONE;
    private final int mMaxTime        = AppConst.CODE_MAX_TIME;
    private String  mPhone;
    private boolean countting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( GlobalData.isPad()?R.layout.activity_edit_personalinfo:R.layout.activity_edit_personalinfo_mobile);
        x.view().inject(this);
        context = this;
        initView();
        initData();
    }

    private void initView() {
        workToolbar.setBackText("取消",false);
        workToolbar.setTitle("编辑个人信息");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        //listener
        btVerifycode.setOnClickListener(this);
        btDone.setOnClickListener(this);
        //etEnrollyears.setOnClickListener(this);

        progressDialog = new MyProgressDialog(context);
        //initDatepicker();

        btDone.setEnabled(false);
        btVerifycode.setEnabled(false);
        etEnrollyears.addTextChangedListener(new MyVerifyTextwatcher());
        etPhone.addTextChangedListener(new PhoneTextwatcher());
        mailAddrView.addTextChangedListener( new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                int MAX = 120;
                if( mailAddrView.getText().length() > MAX ){
                    String data = mailAddrView.getText().toString();
                    mailAddrView.setText( data.substring(0,MAX) );
                    ToastUtils.show(getBaseContext(),"地址最大支持120个字");
                    mailAddrView.setSelection(MAX);
                }
            }
        } );
    }

    private void initData() {
        UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if (userDetailinfo != null) {
            etNickname.setText(userDetailinfo.getNickName());
            genderSelectorView.setSelGender(userDetailinfo.getSex().equals("女") ? GenderSelectorView.GIRL : GenderSelectorView.BOY);
            etEnrollyears.setText(userDetailinfo.getSerial());
            mPhone = userDetailinfo.getPhone();
            etPhone.setText(mPhone);
            mailAddrView.setText( userDetailinfo.getMailAddr() );
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_verifycode:
                sendVerifycode();
                break;
            case R.id.bt_done:
                String phone = etPhone.getText().toString();
                String code = etVerifycode.getText().toString().trim();
                String mailAddr = mailAddrView.getText().toString().trim();

                if (!ValidatorUtils.validateMobile(phone)) {
                    ToastUtils.showShort(context, "手机号格式不正确");
                } else if (!phone.equals(mPhone) && TextUtils.isEmpty(code)) {
                    ToastUtils.showShort(context, "验证码不能为空");
                } else if( !TextUtils.isEmpty(mailAddr) && StringUtil.containsEmoji(mailAddr) ){
                    ToastUtils.showShort(context, "邮寄地址里面不能包含表情符号.");
                } else {
                    updateInfo(phone);
                }
                break;
            default:
                break;
            /*case R.id.et_encrollyears:
                picker.show();
                break;*/
        }
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
        String type = "update"; //学生注册字段为register
        new LoginModel().getVerifycode(phone, type, new RequestListener<String>() {
            @Override
            public void onSuccess(String res) {
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
        btVerifycode.setText( String.format(Locale.getDefault(), getString(R.string.resend_verifycode),mMaxTime ));

        SetTimeout mSetTimeout = new SetTimeout(mMaxTime, TimeUnit.SECONDS, 1);
        mSetTimeout.setHandler(new SetTimeout.SetTimeoutHandler() {
            @Override
            public void handler(final int current) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int remain = mMaxTime - current - 1;
                        if (remain > 0) {
                            btVerifycode.setText(  String.format(Locale.getDefault(), getString(R.string.resend_verifycode),remain ) );
                        } else {
                            btVerifycode.setText(getText(R.string.send_verifycode));
                            btVerifycode.setEnabled(true);
                            countting = false;
                        }
                    }
                });
            }
        });
        mSetTimeout.start();
        countting = true;
    }

    private void updateInfo(String phone) {
        HashMap<String, String> params = new LinkedHashMap<>();
        String nickName = etNickname.getText().toString().trim();
        String code = etVerifycode.getText().toString().trim();
        String sex = genderSelectorView.getSelGender() == GenderSelectorView.GIRL ? "female" : "male";
        if (AccountUtils.getLoginUser() != null) {
            params.put("accountId", AccountUtils.getLoginUser().getAccountId());
        }
        if (!phone.equals(mPhone)) {
            params.put("phone", phone);
            params.put("verifyCode", code);
        }
        params.put("sex", sex);
        params.put("nickName", nickName);
        params.put("mailingAddress", mailAddrView.getText().toString() );
        progressDialog.setMessage("修改个人信息中...");
        progressDialog.show();
        new MycenterModel().updatePersoninfo(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                queryUserdetailInfo();
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                progressDialog.dismiss();
                AlertManager.showErrorInfo(context, ex);
            }
        });
    }

    private void queryUserdetailInfo() {
        //成功后，更新本地的个人信息
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
                EventBus.getDefault().post(new EditInfoEvent());
                progressDialog.dismiss();
                AlertManager.toast(context, "修改成功");
                finish();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                AlertManager.toast(context, "修改失败，请重试");
            }
        });
    }


    class MyVerifyTextwatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String nickname = etNickname.getText().toString().trim();
            if (TextUtils.isEmpty(nickname)) {
                btDone.setEnabled(false);
            } else {
                btDone.setEnabled(true);
            }
        }
    }

    class PhoneTextwatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (countting) {
                return;
            }
            String phone = etPhone.getText().toString().trim();
            if (phone.equals(mPhone)) {
                btVerifycode.setEnabled(false);
            } else {
                btVerifycode.setEnabled(true);
            }
        }
    }
}
