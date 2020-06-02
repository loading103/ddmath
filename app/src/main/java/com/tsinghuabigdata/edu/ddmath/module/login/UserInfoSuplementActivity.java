package com.tsinghuabigdata.edu.ddmath.module.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.JoinClassEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.SchoolBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.GenderSelectorView;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Locale;


/**
 * 个人信息补充界面
 * Created by Administrator on 2016/12/14.
 */

public class UserInfoSuplementActivity extends RoboActivity implements View.OnClickListener {

    //private static final String TAG = "UserInfoSuplementFragment";
    //班级码状态
    private final static int ST_IDLE = 0;
    private final static int ST_LOADING = 1;
    private final static int ST_SUCC = 2;

    @ViewInject(R.id.toolbar)
    private WorkToolbar toolbar;

    @ViewInject(R.id.bt_enterdoudou)
    private Button btEnterdoudou;

    @ViewInject(R.id.et_nickname)
    private EditText etNickName;

    @ViewInject(R.id.sex_genderview)
    private GenderSelectorView genderSelectorView;

    @ViewInject(R.id.et_reallyname)
    private EditText etReallyName;

    //---------------------------------
    @ViewInject(R.id.tv_select_schoolclass)
    private TextView selectSchoolClassView;


    @ViewInject(R.id.tv_noclasscode_view)
    private TextView noClassCodeView;

    @ViewInject(R.id.et_classcode_input)
    private EditText etClassCode;
    @ViewInject(R.id.loadingPragressBar)
    private ProgressBar loadingProgressBar;
    @ViewInject(R.id.im_cleardata)
    private ImageView clearDataView;

    private MyProgressDialog progressDialog;
    private Context context;

    private SchoolBean mSchoolBean;
    private ClassBean mClassBean;

    private boolean fromRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad() ? R.layout.activity_userinfo_suplement : R.layout.activity_userinfo_suplement_mobile);
        x.view().inject(this);
        context = this;
        fromRegister = getIntent().getBooleanExtra("register", false);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        regsterQuit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_enterdoudou:
                enterDoudou();
                break;
            case R.id.tv_noclasscode_view:{
                startActivityForResult( new Intent(context,UserJoinClassActivity.class), 100);
                break;
            }
            case R.id.im_cleardata:{
                mClassBean = null;
                mSchoolBean = null;
                etClassCode.setText("");
                showLoadClassCode(ST_IDLE);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            mSchoolBean = (SchoolBean)data.getSerializableExtra( "school" );
            mClassBean = (ClassBean)data.getSerializableExtra("class");

            if( mSchoolBean!=null && mClassBean!=null ){
                selectSchoolClassView.setText( mSchoolBean.getSchoolName() );
                selectSchoolClassView.append( mClassBean.getClassName() );
            }
        }
    }
    //-----------------------------------------------------------------------------------------------
    private void showLoadClassCode(int status){
        if( ST_IDLE == status ){
            loadingProgressBar.setVisibility( View.GONE );
            clearDataView.setVisibility(View.VISIBLE);
        }else if( ST_LOADING == status ){
            loadingProgressBar.setVisibility( View.VISIBLE );
            clearDataView.setVisibility(View.GONE);
        }else if( ST_SUCC == status ){
            loadingProgressBar.setVisibility( View.GONE );
            clearDataView.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        toolbar.setTitle("个人信息");
        toolbar.setBackText( "取消", false );
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);

        btEnterdoudou.setOnClickListener(this);
        clearDataView.setOnClickListener(this);

        etClassCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String data = etClassCode.getText().toString().trim();

                clearDataView.setVisibility( !TextUtils.isEmpty(data)?View.VISIBLE:View.GONE);
                if( !TextUtils.isEmpty(data) && data.length() >= 6 ){       //班级码最长是6
                    if( data.contains("(") && data.endsWith(")") ){
                        //设置了班级信息
                        AppLog.d("data = "+data);
                    }else {
                        //通过班级码获得班级信息
                        mClassBean = null;
                        searchClass( data );
                    }
                }else{
                    mClassBean = null;
                }
            }
        });

        noClassCodeView.setOnClickListener( this );
        noClassCodeView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        noClassCodeView.getPaint().setAntiAlias(true);//抗锯齿

        // 默认选中小学
//        imgPrimary.setSelected(true);
//        mSchoolType = AppConst.PRIMARY;
        progressDialog = new MyProgressDialog(context);

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo!=null ){
            etNickName.setText( TextUtils.isEmpty(detailinfo.getNickName())?"":detailinfo.getNickName());
            etReallyName.setText( TextUtils.isEmpty(detailinfo.getReallyName())?"":detailinfo.getReallyName());
        }
    }

    private void regsterQuit(){
        if( fromRegister  ){
            //启动主页
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("register", true);
            context.startActivity(intent);
        }
    }

    private void searchClass( String classcode ){

        showLoadClassCode(ST_LOADING);
        new LoginModel().queryClassInfo( classcode, new RequestListener<ClassBean>() {
            @Override
            public void onSuccess( ClassBean bean) {
                if (bean == null) {
                    ToastUtils.show( context, "该班级码不存在" );
                    showLoadClassCode(ST_SUCC);
                    return;
                }
                mClassBean = bean;
                showLoadClassCode(ST_SUCC);

                selectSchoolClassView.setText( String.format( Locale.getDefault(),"%s%s", bean.getSchoolName(), bean.getClassName()));
            }

            @Override
            public void onFail(HttpResponse<ClassBean> response, Exception ex) {
                if( "Data is error".equals( response.getMessage() ) ){
                    ToastUtils.show( context, "该班级码不存在" );
                }else{
                    AlertManager.showErrorInfo( context, ex );
                }
                showLoadClassCode(ST_SUCC);
            }
        });
    }

    private void enterDoudou() {

        final LoginInfo loginInfo =  AccountUtils.getLoginUser();
        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( loginInfo==null || detailinfo == null ){
            ToastUtils.show(context, getResources().getString(R.string.relogin));
            finish();
            return;
        }

        //补充个人信息
        String nickName = etNickName.getText().toString().trim();
        if( TextUtils.isEmpty(nickName) ){
            ToastUtils.show(context, "请填写昵称");
            return;
        }
        String sex = genderSelectorView.getSelGender() == GenderSelectorView.GIRL ? "female" : "male";

        String reallyname = etReallyName.getText().toString().trim();
        if (TextUtils.isEmpty(reallyname)) {
            ToastUtils.show(context, "请填写真实姓名");
            return;
        }

        if( mClassBean == null ){
            ToastUtils.show(context, "请输入班级码");
            return;
        }

        final String accountId = loginInfo.getAccountId();
        progressDialog.setMessage("更新信息中...");
        progressDialog.show();
        new LoginModel().updateExtraPersoninfo(accountId, reallyname, mClassBean.getEnrollmentYear(), mClassBean.getSchoolId(), nickName, sex, mClassBean.getClassId(), new RequestListener() {
            @Override
            public void onSuccess(Object res) {

                if( TextUtils.isEmpty(mClassBean.getClassId()) ){   //班级这时还不存在，需要单独创建加入

                    new LoginModel().joinClass( detailinfo.getStudentId(), detailinfo.getReallyName(), "",
                            mClassBean.getClassName(), mSchoolBean.getSchoolId(), mClassBean.getEnrollmentYear(), new RequestListener() {
                                @Override
                                public void onSuccess(Object res) {
                                    updateUserInfo( loginInfo );
                                }

                                @Override
                                public void onFail(HttpResponse response, Exception ex) {
                                    progressDialog.dismiss();
                                    AlertManager.showErrorInfo( context, ex );
                                }
                            });
                }else{
                    updateUserInfo( loginInfo );
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                AlertManager.showErrorInfo(context, ex);
                progressDialog.dismiss();
            }
        });
    }

    private void updateUserInfo( LoginInfo loginInfo ){
        //更新成功后，重新获取个人信息
        new LoginModel().queryUserdetailInfo( loginInfo.getAccessToken(), loginInfo.getAccountId(), new RequestListener<UserDetailinfo>() {
            @Override
            public void onSuccess(UserDetailinfo res) {
                if (res != null) {
                    AccountUtils.setUserdetailInfo(res);
                }
                progressDialog.dismiss();

                //班级码自动加班，更新班级信息
                EventBus.getDefault().post(new JoinClassEvent());
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("joinclass", true);
                context.startActivity(intent);
                finishAll();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                progressDialog.dismiss();
                AlertManager.showErrorInfo( context, ex );
                //AccountUtils.clear();
                //regsterQuit();
                //finish();
            }
        });
    }
}
