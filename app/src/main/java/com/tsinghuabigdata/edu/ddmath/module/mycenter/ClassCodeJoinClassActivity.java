package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.JoinClassEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ClassBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Locale;

/**
 *
 * 班级码加入班级界面
 */

public class ClassCodeJoinClassActivity extends RoboActivity implements View.OnClickListener{

    @ViewInject(R.id.toolbar)
    private WorkToolbar toolbar;

    //搜索班级
    @ViewInject(R.id.edittext)
    private EditText editText;

    @ViewInject(R.id.serach_btn)
    private ImageView searchBtn;
    @ViewInject(R.id.iv_cleardata)
    private ImageView clearImageView;

    //班级信息
    @ViewInject(R.id.layout_classinfo)
    private LinearLayout classInfoLayout;

    @ViewInject(R.id.tv_schoolname)
    private TextView schoolNameView;

    @ViewInject(R.id.tv_classname)
    private TextView classNameView;

    @ViewInject(R.id.tv_studentcount)
    private TextView studentCountView;

    @ViewInject(R.id.tv_teachername)
    private TextView teacherNameView;

    @ViewInject(R.id.btn_jionclass)
    private Button joinClassBtn;

    //
    @ViewInject(R.id.tv_select_jion_class)
    private TextView selectJionClass;

    private MyProgressDialog progressDialog;

    private Context mContext;
    private ClassBean classBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad()? R.layout.activity_classcode_join_class: R.layout.activity_classcode_join_class_phone);
        x.view().inject(this);
        mContext = this;
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jionclass:
                ensureJoinClass();
                break;
            case R.id.serach_btn:
                searchClass();
                break;
//            case R.id.tv_select_jion_class:{
//                Intent intent = new Intent( mContext, SchoolSelectActivity.class);
//                intent.putExtra( SchoolSelectActivity.PARAM_ACTION_TYPE, SchoolSelectActivity.TYPE_ADDCLASS );
//                startActivity(intent);
//                break;
//            }
            case R.id.iv_cleardata:{
                editText.setText("");
                break;
            }
            default:
                break;
        }
    }

    //---------------------------------------------------------------------------------------------------------
    /**
     * 禁止输入空格
     */
    public class SpaceFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            //返回null表示接收输入的字符,返回空字符串表示不接受输入的字符
            if (source.equals(" "))
                return "";
            return null;
        }
    }

    private void initView() {

        toolbar.setTitle("加入班级");
        toolbar.setBackText( "返回", true );
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);

        classInfoLayout.setVisibility( View.GONE );

        searchBtn.setOnClickListener( this );
        joinClassBtn.setOnClickListener( this );
        selectJionClass.setOnClickListener(this);
        selectJionClass.setVisibility( View.GONE );

        editText.setFilters(new InputFilter[]{new SpaceFilter()});
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String data = s.toString().trim();
                clearImageView.setVisibility( TextUtils.isEmpty(data)?View.GONE:View.VISIBLE );
                if( data.length()>=6 ){
                    searchClass();
                }
            }
        });

        clearImageView.setOnClickListener( this );
        progressDialog = new MyProgressDialog(mContext);
    }

    private void ensureJoinClass() {

        final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        // 确定加入班级
        if (classBean == null || detailinfo == null) {
            return;
        }

        if (classBean.getAllowAddStudent() == 1) {
            AlertManager.toast( mContext, "此班级不允许加入，请联系老师!");
            return;
        }

        //判断是否已经在此班级
        if (AccountUtils.checkInClass(classBean.getClassId())) {
            AlertManager.toast(mContext, "你已经在此班级了，不能重复加入!");
            return;
        }

        //加入班级提醒
        AlertManager.showCustomImageBtnDialog(mContext, "加入班级成功后，不可以退班哦！", "确定加入", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                progressDialog.setMessage("加入班级中...");
                progressDialog.show();
                String studentId = detailinfo.getStudentId();
                String studentName = detailinfo.getReallyName();

                new LoginModel().joinClass(studentId, studentName, classBean.getClassId(),
                        classBean.getClassName(), classBean.getSchoolId(), classBean.getEnrollmentYear(), new RequestListener() {
                            @Override
                            public void onSuccess(Object res) {
                                progressDialog.dismiss();
                                AppLog.i("joinClass success");
                                AlertManager.toast(mContext, "加入班级成功");
                                EventBus.getDefault().post(new JoinClassEvent());

                                //更新班级信息
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.putExtra("joinclass", true);
                                mContext.startActivity(intent);
                                finishAll();
                            }

                            @Override
                            public void onFail(HttpResponse response, Exception ex) {
                                progressDialog.dismiss();
                                AppLog.i("joinClass failed" + ex.getMessage());
                                ToastUtils.show(mContext, "加入失败，请重试");
                            }
                        });
            }
        }, null);
    }

    private void searchClass() {
        String classcode = editText.getText().toString().trim();
        if(TextUtils.isEmpty(classcode) || classcode.length() < 6){
            ToastUtils.show( mContext, getString(R.string.join_classcode));
            return;
        }
        progressDialog.setMessage("请稍等...");
        progressDialog.show();
        new LoginModel().queryClassInfo( classcode, new RequestListener<ClassBean>() {
            @Override
            public void onSuccess( ClassBean bean) {
                if (bean == null) {
                    ToastUtils.show( mContext, "该班级码不存在" );
                    return;
                }
                classBean = bean;
                showClassInfo(bean);
                progressDialog.dismiss();
            }

            @Override
            public void onFail(HttpResponse<ClassBean> response, Exception ex) {
                if( "Data is error".equals( response.getMessage() ) ){
                    ToastUtils.show( mContext, "该班级码不存在" );
                }else{
                    AlertManager.showErrorInfo( mContext, ex );
                }
                progressDialog.dismiss();
            }
        });

    }

    private void showClassInfo(ClassBean classInfo){

        classInfoLayout.setVisibility( View.VISIBLE );

        schoolNameView.setText(classInfo.getSchoolName());
        classNameView.setText(classInfo.getClassName());
        studentCountView.setText(String.format( Locale.getDefault(), "已加入%d人", classInfo.getStudentNum() ));

        StringBuilder sb = new StringBuilder();
        if( classInfo.getTeaNameList() !=null && classInfo.getTeaNameList().size()>0 ){
            for( String tname : classInfo.getTeaNameList() ){
                sb.append( tname ).append(" ");
            }
        }
        teacherNameView.setText(String.format( Locale.getDefault(), "老师: %s", sb.toString() ));   //多个老师处理
    }

}