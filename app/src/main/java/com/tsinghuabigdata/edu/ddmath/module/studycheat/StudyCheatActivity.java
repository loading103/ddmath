package com.tsinghuabigdata.edu.ddmath.module.studycheat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.CheatsBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.StudyAbilityInfo;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.view.CheatTaskView;
import com.tsinghuabigdata.edu.ddmath.requestHandler.StudyCheatService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.StudyCheatServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.UnLoginView;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import static com.tsinghuabigdata.edu.ddmath.module.studycheat.view.CheatTaskView.TYPE_FORCE_TRAIN;
import static com.tsinghuabigdata.edu.ddmath.module.studycheat.view.CheatTaskView.TYPE_QUESTION_REVIEW;


/**
 * 学情反馈
 * Created by Administrator on 2016/12/14.
 */

public class StudyCheatActivity extends RoboActivity {

    private Context mContext;

    //没有数据
    @ViewInject(R.id.cheat_nodata_layout)
    private RelativeLayout nodataLayout;

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;

    //没有登录
    @ViewInject(R.id.cheat_unregister_layout)
    private UnLoginView nologinLayout;

    //
    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    //
    @ViewInject(R.id.task_mainlayout)
    private LinearLayout mainLayout;

    @ViewInject(R.id.cheat_finishedcount_layout)
    private RelativeLayout finishLayout;

    @ViewInject(R.id.cheat_finishedcount_view)
    private TextView finishLabelView;
    @ViewInject(R.id.cheat_errquestion_cheatTaskView)
    private CheatTaskView errQuestionTaskView;
    @ViewInject(R.id.cheat_forcetrain_cheatTaskView)
    private CheatTaskView forceTrainTaskView;
    @ViewInject(R.id.cheat_middle_spaceView)
    private View spaceView;

    private StudyCheatService studyCheatService;
    private QueryStudyAbilityTask queryStudyAbilityTask;

    public void onCreate(Bundle savedInstanceState){
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( GlobalData.isPad()?R.layout.fragment_study_cheat:R.layout.fragment_study_cheat_phone );
        mContext = this;

        x.view().inject( this );

        studyCheatService = new StudyCheatServiceImpl();
        initView();
        initData();

        MobclickAgent.onEvent( this, "cheat_study" );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK ){
            loadData( false );
        }
    }

    //------------------------------------------------------------------------------------------
    private void initView() {

        workToolbar.setTitle( "提分秘籍" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        //
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData( true );
            }
        });

        errQuestionTaskView.setStartOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult( new Intent( mContext, ErrQuestionReviewActivity.class ), 100 );
            }
        });
        forceTrainTaskView.setStartOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult( new Intent( mContext, ForceTrainActivity.class ), 100 );
            }
        });
    }

    private void initData() {
        //判断有没有登录
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo != null && detailinfo != null) {
            showView();
        } else {
            showUnregisterView();
        }
    }

    //展示 1、未登录页面
    private void showUnregisterView() {
        nologinLayout.setVisibility(View.VISIBLE);
        nodataLayout.setVisibility( View.GONE );
        mLoadingPager.hideall();
        mainLayout.setVisibility( View.GONE );
        finishLayout.setVisibility( View.GONE );
    }

    //展示 2、已登录页面,有数据，先加载数据
    private void showView() {
        nologinLayout.setVisibility(View.GONE);
        nodataLayout.setVisibility( View.GONE );
        mainLayout.setVisibility( View.GONE );
        finishLayout.setVisibility( View.GONE );

        //拉取数据
        loadData( true );
    }

    //展示 3、没有数据
    private void showNodataView() {
        nologinLayout.setVisibility(View.GONE);
        mLoadingPager.hideall();
        nodataLayout.setVisibility( View.VISIBLE );
        mainLayout.setVisibility( View.GONE );
        finishLayout.setVisibility( View.GONE );
    }

    // 4，请求异常
    private void showExceptionView( Exception e ) {
        mLoadingPager.showFault( e );
        nologinLayout.setVisibility(View.GONE);
        nodataLayout.setVisibility( View.GONE );
        mainLayout.setVisibility( View.GONE );
        finishLayout.setVisibility( View.GONE );
    }

    private void loadData(boolean showLoading ) {

        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo == null || detailinfo == null) {
            ToastUtils.showShort(mContext, "请登录");
            return;
        }

        if (showLoading) {
            finishLayout.setVisibility(View.GONE);
            mLoadingPager.showLoading();
        }
        if( queryStudyAbilityTask == null || queryStudyAbilityTask.isComplete() || queryStudyAbilityTask.isCancelled() ){
            queryStudyAbilityTask = new QueryStudyAbilityTask( new RequestListener<StudyAbilityInfo>() {

                @Override
                public void onSuccess(StudyAbilityInfo vo) {
                    //已退出
                    //if( isDetached() ) return;

                    if (vo == null) {
                        mLoadingPager.hideall();
                        showNodataView();
                        return;
                    }
                    mLoadingPager.hideall();
                    mainLayout.setVisibility( View.VISIBLE );
                    finishLayout.setVisibility( View.VISIBLE );

                    setLearnTask(vo);

//                    mTvMyStudyValue.setText(abilityInfo.getCurrentValue() + "");
//                    if (abilityInfo.getPercentExceed() < 0.1) {
//                        mTvPercentExceed.setText("你快要被大家超越咯");
//                    } else {
//                        int perShow = Math.round(abilityInfo.getPercentExceed() * 100);
//                        mTvPercentExceed.setText("超过" + perShow + "%同年级同学");
//                    }
//                    if (abilityInfo.getPercentExceed() < 0.9) {
//                        mTvPercentExceedDes.setText("再来几分，你没问题的，相信自己哦！(/≧▽≦)/");
//                    } else {
//                        mTvPercentExceedDes.setText("学霸，你已经超神了");
//                    }
//                    setPower(vo);
//                    setTodayStudy(vo);
//                    mTvTodayStudyValue.setText("+" + abilityInfo.getTodayValue());
//                    mLlTodayStudy.setVisibility(View.VISIBLE);
//                    if (abilityInfo.getRemainedReviewChance() > 0) {
//                        PreferencesUtils.putBoolean(mContext, "hasQuestionReview", true);
//                    } else {
//                        PreferencesUtils.putBoolean(mContext, "hasQuestionReview", false);
//                    }
//
//                    mViewPager.setVisibility(View.VISIBLE);
//                    mEnergyProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFail(HttpResponse<StudyAbilityInfo> response, Exception e) {
                    showExceptionView( e );
                }
            });
            queryStudyAbilityTask.executeMulti( loginInfo.getAccessToken(), detailinfo.getStudentId() );
        }
    }

    private void setLearnTask(StudyAbilityInfo abilityInfo) {

        boolean surplus = abilityInfo.isSurplusReview() || abilityInfo.isSurplusStength();
        if( !surplus ){     //今日没有推荐的题目
            showNodataView();
            return;
        }
        finishLayout.setVisibility( View.VISIBLE );
        //判断是否已完成任务
        if( abilityInfo.getRemainStrengthChanc()<= 0 && abilityInfo.getRemainedReviewChance() <= 0 ){     //今日任务已完成
            finishLabelView.setText( getResources().getText(R.string.cheat_finish_success) );
//            finishLabel1View.setText( getResources().getText(R.string.cheat_finish_success) );
//            finishTipsTextView.setVisibility( View.GONE );
//            finishLabel2View.setVisibility( View.GONE );
        }else{
            int count = abilityInfo.getCompleteCount();
            if( count == 0 ){
                finishLabelView.setText( getResources().getText(R.string.cheat_nofinish_tips) );
//                finishLabel1View.setText( getResources().getText(R.string.cheat_nofinish_tips) );
//                finishTipsTextView.setVisibility( View.GONE );
//                finishLabel2View.setVisibility( View.GONE );
            }else{
                String data = getResources().getText( count>1? R.string.cheat_allfinish_tips_2:R.string.cheat_onefinish_tips_2).toString();
                data = String.format( data, count );
                finishLabelView.setText( data );

//                finishTipsTextView.setText( String.valueOf(abilityInfo.getCompleteCount()) );
//
//                finishLabel1View.setText( "已有" );
//                finishTipsTextView.setVisibility( View.VISIBLE );
//                finishLabel2View.setVisibility( View.VISIBLE );
//
//                finishLabel2View.setText( getResources().getText( count>1? R.string.cheat_allfinish_tips_2:R.string.cheat_onefinish_tips_2) );
            }
        }

        List<CheatsBean> list = abilityInfo.getCheats();

        spaceView.setVisibility( View.VISIBLE );
        errQuestionTaskView.setVisibility(View.VISIBLE);
        forceTrainTaskView.setVisibility(View.VISIBLE);
        setCheatTaskView( list, TYPE_QUESTION_REVIEW,  errQuestionTaskView, abilityInfo.isSurplusReview() );
        setCheatTaskView( list, TYPE_FORCE_TRAIN,  forceTrainTaskView, abilityInfo.isSurplusStength() );

    }

    private void setCheatTaskView( List<CheatsBean> list, int type, CheatTaskView taskView, boolean hastask ){
        CheatsBean cheatsBean = null;
        //
        if (list != null){
            for( CheatsBean bean : list ){
                if( type == bean.getType() ){
                    cheatsBean = bean;
                    break;
                }
            }
        }

        if( cheatsBean == null ){
            if( hastask ){      //今日有任务，已完成
                taskView.setData( (TYPE_FORCE_TRAIN == type)?20:8, 120 );
                taskView.finishCheatTask();
            }else{              //今日没有任务
                taskView.setVisibility( View.GONE );
                spaceView.setVisibility( View.GONE );
            }
        }else{      //还有任务未完成
            taskView.setData( cheatsBean.getScore(), cheatsBean.getTime() );
        }
    }

    //-------------------------------------------------------------------------------------------------------------------
    private class QueryStudyAbilityTask extends AppAsyncTask<String, Void, StudyAbilityInfo> {

        private RequestListener reqListener;

        QueryStudyAbilityTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected StudyAbilityInfo doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            return studyCheatService.queryStudyAbility(accessToken, studentId);
        }

        @Override
        protected void onResult(StudyAbilityInfo studyAbilityInfo) {
            if(reqListener!=null)reqListener.onSuccess(studyAbilityInfo);
        }

        @Override
        protected void onFailure(HttpResponse<StudyAbilityInfo> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

}
