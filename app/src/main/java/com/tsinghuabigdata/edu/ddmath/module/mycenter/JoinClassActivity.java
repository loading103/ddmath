package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.JoinClassEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ReturnClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.SchoolSelDialog;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.view.ClassSelectView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.MyProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/1/22.
 * 加入班级界面
 */
@Deprecated
public class JoinClassActivity extends RoboActivity implements View.OnClickListener{

    @ViewInject(R.id.toolbar)
    private WorkToolbar toolbar;

    @ViewInject(R.id.tv_school)
    private TextView tvSchool;

    @ViewInject(R.id.bt_join_class)
    private Button bt_join_class;

    @ViewInject(R.id.loading_pager)
    private LoadingPager loadingPager;

    @ViewInject(R.id.ll_content)
    private LinearLayout ll_content;

    @ViewInject(R.id.tv_jump)
    private TextView tv_Jump;

    @ViewInject(R.id.tv_enrolYear)
    private TextView tvEnrolYear;

    @ViewInject(R.id.classSelectView)
    private ClassSelectView selectView;

    // 选择的学校Id
    private String schoolId = "";
    // 选择的学校名
    private String schoolName = "";
    // 入学时间
    private int enrolYear;
    private int learnPeriod;
    private boolean fromRegister = false;

    private MyProgressDialog progressDialog;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(GlobalData.isPad()? R.layout.activity_join_class : R.layout.activity_join_class_phone);
        x.view().inject(this);

        parseIntent();

        initView();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        regsterQuit();
    }
    private void regsterQuit(){
        if( fromRegister  ){
            //启动主页
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("register", true);
            context.startActivity(intent);
        }
    }

    private void parseIntent() {
        schoolId =  getIntent().getStringExtra("schoolId");
        schoolName = getIntent().getStringExtra("schoolName");
        enrolYear = getIntent().getIntExtra("enrolYear", 0);
        learnPeriod=getIntent().getIntExtra( "learnPeriod", AppConst.MIDDLE );
        fromRegister= getIntent().getBooleanExtra("register",false);
        LogUtils.i("schoolId = " + schoolId + "schoolName = " + schoolName + "enrolYear" + enrolYear);
    }

    private void initView() {
        context = this;
        toolbar.setTitle("加入班级");
        toolbar.setBackText( "取消", false );
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);

        tvSchool.setText(schoolName);
        if (enrolYear == 0) {
            //自动添加班级,以当年时间为准
            enrolYear = Calendar.getInstance().get(Calendar.YEAR);
            tvEnrolYear.setOnClickListener( this );
            Drawable drawable = getResources().getDrawable(R.drawable.class_choice );
            if( GlobalData.isPad() ){
                drawable.setBounds(0, 0, drawable.getMinimumWidth()*2, drawable.getMinimumHeight()*2);
            }else{
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            }
            tvEnrolYear.setCompoundDrawables( null,null, drawable,null);
            tv_Jump.setVisibility( View.GONE );
        }
        tvEnrolYear.setText( String.format(Locale.getDefault(), "%d年", enrolYear) );

        progressDialog = new MyProgressDialog(context);
        bt_join_class.setOnClickListener(this);
        tv_Jump.setOnClickListener(this);

        loadingPager.setTargetView(ll_content);
        loadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingPager.showLoading();
                selClass();
            }
        });

        selClass();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_join_class:
                ensureJoinClass();
                break;
            case R.id.tv_jump:
                regsterQuit();
                finishAll();
                break;
            case R.id.tv_enrolYear:{
                SchoolSelDialog.selectEnrolYear( context,learnPeriod, new SchoolSelDialog.OnSureButtonClickListener(){
                    @Override
                    public void onSelect(int year) {
                        if( enrolYear == year ) return;
                        enrolYear = year;
                        tvEnrolYear.setText( String.format(Locale.getDefault(), "%d年", enrolYear));
                        selClass();
                    }
                } );
                break;
            }
            default:
                break;
        }
    }

    private void ensureJoinClass() {
        // 确定加入班级
        final ClassBean classBean = selectView.getSelectClassBean();
        if (classBean == null) {
            ToastUtils.show(context, "请选择班级");
        } else {
            if (classBean.getAllowAddStudent() == 1) {
                AlertManager.toast(context, "此班级不允许加入，请重新选择!");
                return;
            }

            //判断是否已经在此班级
            if (AccountUtils.checkInClass(classBean.getClassId())) {
                AlertManager.toast(context, "你已经在此班级了，不能重复加入!");
                return;
            }

//            if (classBean.getStudentNum() >= 160) {
//                AlertManager.toast(context, "此班级人数已满，请重新选择");
//                return;
//            }

            final UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( detailinfo == null ) return;

            //加入班级提醒
            AlertManager.showCustomImageBtnDialog(context, "加入班级成功后，不可以退班哦！", "确定加入", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    progressDialog.setMessage("加入班级中...");
                    progressDialog.show();
                    String studentId = detailinfo.getStudentId();
                    String studentName = detailinfo.getReallyName();
                    String serial = "";
                    if (enrolYear != 0) {
                        serial = String .valueOf(enrolYear);
                    }
                    new LoginModel().joinClass(studentId, studentName, classBean.getClassId(),
                            classBean.getClassName(), schoolId, serial, new RequestListener() {
                                @Override
                                public void onSuccess(Object res) {
                                    progressDialog.dismiss();
                                    AlertManager.toast(context, "加入班级成功");
                                    EventBus.getDefault().post(new JoinClassEvent());

                                    //更新班级信息
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("joinclass", true);
                                    context.startActivity(intent);
                                    finishAll();
                                }

                                @Override
                                public void onFail(HttpResponse response, Exception ex) {
                                    progressDialog.dismiss();
                                    AppLog.i("joinClass failed" + ex.getMessage());
                                    ToastUtils.show(context, "加入失败，请重试");
                                }
                            });
                }
            }, null);

        }
    }

    private void selClass() {
        String s_enrolYear = "";
        if (enrolYear != 0) {
            s_enrolYear = String.valueOf(enrolYear);
        }
        new LoginModel().queryClassList(schoolId, s_enrolYear, new RequestListener<List<ReturnClassBean>>() {
            @Override
            public void onSuccess(List<ReturnClassBean> res) {
                AppLog.i("queryClassList success");
                if (res == null || res.size() == 0) {
                    loadingPager.showServerFault();
                    return;
                }
                selectView.setClassDataList(res);
                loadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<List<ReturnClassBean>> response, Exception ex) {
                AppLog.i("queryClassList failed" + ex.getMessage());
                loadingPager.showFault(ex);
            }
        });
    }

}