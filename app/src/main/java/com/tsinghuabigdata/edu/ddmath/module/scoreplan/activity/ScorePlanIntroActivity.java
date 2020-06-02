package com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.ScorePlanModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ScorePlanBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.BaseConfig;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.BuySuiteActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Locale;

/**
 * 提分方案介绍, 购买，加入界面
 */

public class ScorePlanIntroActivity extends RoboActivity implements View.OnClickListener{

    private static final String PARAM_FROM_TYPE = "fromtype";        //
    private static final String PARAM_FROM_BEAN = "planbean";
    private static final String PARAM_FROM_SCORE = "isscored";//

    //
    public static final int FROM_TYPE_NOBUY = 0;        //没有购买
    public static final int FROM_TYPE_NOUSE = 1;        //已购买，没有使用
    public static final int FROM_TYPE_USING = 2;        //购买使用中

    //启动接口
    public static void openActivity(Context context, int fromType,boolean fromScore, ScorePlanBean scorePlanBean){
        if( context == null ) return;
        Intent intent = new Intent(context, ScorePlanIntroActivity.class);
        intent.putExtra( PARAM_FROM_TYPE, fromType );
        intent.putExtra( PARAM_FROM_SCORE, fromScore );
        if( scorePlanBean!=null )intent.putExtra( PARAM_FROM_BEAN, scorePlanBean);
        context.startActivity(intent);
    }

    private Context mContext;

    @ViewInject(R.id.worktoolbar)
    private WorkToolbar workToolbar;

    @ViewInject(R.id.progress_webview)
    private ProgressWebView progressWebView;

    @ViewInject(R.id.layout_btntool)
    private LinearLayout btnToolLayout;
    @ViewInject(R.id.tv_use_total)
    private TextView useTotalView;
    @ViewInject(R.id.btn_jion)
    private Button jionBtn;

    private int fromType = FROM_TYPE_NOBUY;
    private boolean fromScore = false;
    private ScorePlanBean mScorePlanBean;

    @Override
    protected void onCreate( Bundle savedInstanceState){
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(GlobalData.isPad()?R.layout.activity_scoreplan_intro:R.layout.activity_scoreplan_intro_phone);

        x.view().inject(this);

        mContext = this;

        parseIntent();
        initView();
        loadData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.btn_jion ){
            if( FROM_TYPE_NOUSE == fromType ){
                confirmJoin();
            }else if( FROM_TYPE_NOBUY == fromType ){
                if( mScorePlanBean!=null ){
                    BuySuiteActivity.openActivityForSuite( this, mScorePlanBean.getCustomizedPrivilegeIds() );
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    boolean buySuccess = false;
    @Subscribe
    public void receive(BuySuiteEvent event) {
        AppLog.d("event = " + event );
        if( buySuccess ) return;        //购买成功和消息各发了一次
        buySuccess = true;
        if( mScorePlanBean!=null && !TextUtils.isEmpty(event.getSuiteId())){
            //刷新页面
            fromType = FROM_TYPE_NOUSE;
            jionBtn.setText("我已了解，立即加入");
            //自动加入定制学
            confirmJoin();
        }
    }

    //------------------------------------------------------------------------------------------
    private void initView(){
        if(fromScore){
            workToolbar.setTitle("定制学简介");
        }else {
            workToolbar.setTitle("定制学");
        }
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

        jionBtn.setOnClickListener( this );
    }

    private void parseIntent(){
        Intent intent = getIntent();
        fromType = intent.getIntExtra( PARAM_FROM_TYPE, FROM_TYPE_NOBUY );
        fromScore = intent.getBooleanExtra( PARAM_FROM_SCORE, false );
        if( intent.hasExtra( PARAM_FROM_BEAN ) )
            mScorePlanBean = (ScorePlanBean)intent.getSerializableExtra( PARAM_FROM_BEAN );

        //return fromType >= FROM_TYPE_USING || mScorePlanBean!=null;
    }

    private void confirmJoin(){

        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null ){
            ToastUtils.show( this, "请重新登陆");
            return;
        }

        new ScorePlanModel().confirmJoinCustomPlan(detailinfo.getStudentId(), new RequestListener<Boolean>() {
            @Override
            public void onSuccess(Boolean res) {
                goActivity(ScorePlanActivity.class);
                ToastUtils.show( mContext,"成功加入");
                finish();
            }
            @Override
            public void onFail(HttpResponse<Boolean> response, Exception ex) {
                AlertManager.showErrorInfo( mContext, ex);
            }
        });
    }

    private void loadData(){
        if( FROM_TYPE_NOBUY == fromType ){
            jionBtn.setText("购买套餐，立即加入");
        }else if( FROM_TYPE_NOUSE == fromType ){
            jionBtn.setText("我已了解，立即加入");
        }else{
            btnToolLayout.setVisibility(View.GONE);
        }

        if( mScorePlanBean!=null ){
            useTotalView.setText( String.format(Locale.getDefault(),"已有%d人加入个人专属提分方案",mScorePlanBean.getUserNum()));
        }

        //网页地址
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( detailinfo!=null && classInfo!=null ){
            String url = BaseConfig.WEB_ADDRESS + AppRequestConst.WEB_CUSTOMPLAN_INTRODUCE + detailinfo.getStudentId();
            url = DataUtils.getUrl(mContext, url);
            url += "&classId=" + classInfo.getClassId();
            progressWebView.loadUrl(url);
        }
    }

}
