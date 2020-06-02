package com.tsinghuabigdata.edu.ddmath.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryNewReportInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.EditInfoEvent;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment.AllroundReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment.ClassicReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment.KnowledgeCharttFragment;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment.WeekAnalysisReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment.WorkExamFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 我的报告
 * Created by Administrator on 2017/9/20.
 */

public class MyReportFragment extends MyBaseFragment implements View.OnClickListener {

    public static final int WORK_I                = 0;
    public static final int DAILY_CLEAR_I         = 1;
    public static final int WEEK_EXTRACT_I        = 2;
    //public static final int EXCLUSIVE_EXERCISES_I = 2;
    //public static final int CLASSIC_I             = 2;
    public static final int KNOWLEDGE_I           = 3;
    public static final int ALLROUND_I            = 4;

    private RelativeLayout mRlWorkReport, mRlDailyClearReport, mPromteReport, mRlKnowledgeReport, mRlAllroundReport;
    //private ImageView mIvWorkRedpoint, mIvDailyClearRedpoint, mPromteRedpoint, mIvKnowledgeRedpoint, mIvAllroundRedpoint;

    private List<View> tabviews      = new ArrayList<>();
    private List<View> redpointViews = new ArrayList<>();

    //报告类型 （0：综合，1：知识分析，2：考试报告，3：作业报告, 4:周练, 5:周题练）
    private String accessToken;
    private String studentId;
    private int    mCurPosition;


    private List<ReportBaseFragment>  mFragments;
    private List<ReportBaseFragment>  mAllFragments;
    private WorkExamFragment mWorkExamFragment;
    private WeekAnalysisReportFragment mWeekAnalysisReportFragment;
    private ClassicReportFragment mWeekExtractReportFragment;       //培优报告
//    private ExclusiveReportFragment   mExclusiveReportFragment;
//    private ClassicReportFragment mClassicReportFragment;
    private KnowledgeCharttFragment   mKnowledgeCharttFragment;
    private AllroundReportFragment    mAllroundReportFragment;

    private HashMap<ReportBaseFragment, Integer> mFragmentMap = new HashMap<>();


    //private Context            mContext;
    private QueryNewReportInfo queryNewReportInfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_report, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_report_mobile, container, false);
        }
        initFragment();
        initView(root);
        initData();
        setPrepared();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initFragment() {
        mAllFragments = new ArrayList<>();
        mAllFragments.add(mWorkExamFragment);
        mAllFragments.add(mWeekAnalysisReportFragment);
        mAllFragments.add(mWeekExtractReportFragment);
//        mAllFragments.add(mExclusiveReportFragment);
//        mAllFragments.add(mClassicReportFragment);
        mAllFragments.add(mKnowledgeCharttFragment);
        mAllFragments.add(mAllroundReportFragment);

        mWorkExamFragment = new WorkExamFragment();
        mFragments = new ArrayList<>();
        mFragmentMap.put(mWorkExamFragment, WORK_I);
        addToTransaction(mWorkExamFragment);
    }


    private void initView(View root) {
//        mContext = getActivity();
        mRlWorkReport =  root.findViewById(R.id.rl_work_report);
        ImageView mIvWorkRedpoint =  root.findViewById(R.id.iv_work_redpoint);

        mRlDailyClearReport =  root.findViewById(R.id.rl_daily_clear_report);
        ImageView mIvDailyClearRedpoint =  root.findViewById(R.id.iv_daily_clear_redpoint);

        mPromteReport =  root.findViewById(R.id.rl_promote_report);
        ImageView mPromteRedpoint =  root.findViewById(R.id.iv_promote_redpoint);
//        mRlExclusiveReport = (RelativeLayout) root.findViewById(R.id.rl_exclusive_report);
//        mRlClassicReport   = (RelativeLayout)root.findViewById( R.id.rl_classic_report );
//        mIvExclusiveRedpoint = (ImageView) root.findViewById(R.id.iv_exclusive_redpoint);
//        mIvClassicRedpoint   = (ImageView)root.findViewById( R.id.iv_classic_redpoint );
        mRlKnowledgeReport =  root.findViewById(R.id.rl_knowledge_report);
        ImageView mIvKnowledgeRedpoint =  root.findViewById(R.id.iv_knowledge_redpoint);
        mRlAllroundReport =  root.findViewById(R.id.rl_allround_report);
        ImageView mIvAllroundRedpoint =  root.findViewById(R.id.iv_allround_redpoint);

        mRlWorkReport.setOnClickListener(this);
        mRlDailyClearReport.setOnClickListener(this);
        mPromteReport.setOnClickListener(this);
//        mRlExclusiveReport.setOnClickListener(this);
//        mRlClassicReport.setOnClickListener(this);
        mRlKnowledgeReport.setOnClickListener(this);
        mRlAllroundReport.setOnClickListener(this);

        tabviews.add(mRlWorkReport);
        tabviews.add(mRlDailyClearReport);
        tabviews.add(mPromteReport);
//        tabviews.add(mRlExclusiveReport);
//        tabviews.add(mRlClassicReport);
        tabviews.add(mRlKnowledgeReport);
        tabviews.add(mRlAllroundReport);

        mRlWorkReport.setActivated(true);

        //小红点
        redpointViews.add(mIvWorkRedpoint);
        redpointViews.add(mIvDailyClearRedpoint);
        redpointViews.add(mPromteRedpoint);
//        redpointViews.add(mIvExclusiveRedpoint);
//        redpointViews.add( mIvClassicRedpoint );
        redpointViews.add(mIvKnowledgeRedpoint);
        redpointViews.add(mIvAllroundRedpoint);
    }


    @Override
    public String getUmEventName(){
        if (mRlWorkReport!=null && mRlWorkReport.isActivated() && mWorkExamFragment!=null ){
            return mWorkExamFragment.getUmEventName();
        }else if (mRlDailyClearReport!=null && mRlDailyClearReport.isActivated() && mWeekAnalysisReportFragment!=null ){
            return mWeekAnalysisReportFragment.getUmEventName();
        }else if (mPromteReport!=null && mPromteReport.isActivated() && mWeekExtractReportFragment!=null ){
            return mWeekExtractReportFragment.getUmEventName();
//        }else if (mRlExclusiveReport!=null && mRlExclusiveReport.isActivated() && mExclusiveReportFragment!=null ){
//            return mExclusiveReportFragment.getUmEventName();
//        }else if (mRlClassicReport!=null && mRlClassicReport.isActivated() && mClassicReportFragment!=null ){
//            return mClassicReportFragment.getUmEventName();
        }else if (mRlKnowledgeReport!=null && mRlKnowledgeReport.isActivated() && mKnowledgeCharttFragment!=null ){
            return mKnowledgeCharttFragment.getUmEventName();
        }else if (mRlAllroundReport!=null && mRlAllroundReport.isActivated() && mAllroundReportFragment!=null ){
            return mAllroundReportFragment.getUmEventName();
        }
        return "report_workexam";
    }

    private void initData() {
        if (DataUtils.isLoginSuccess()) {
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            if( loginInfo!=null ) accessToken = loginInfo.getAccessToken();
            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if( detailinfo!=null ) studentId = detailinfo.getStudentId();
            queryHaveNewReports();
        }
        EventBus.getDefault().register(this);
    }


    private void queryHaveNewReports() {
        new StudyfeedbackModel().isHaveNewReports(accessToken, studentId, new RequestListener<QueryNewReportInfo>() {
            @Override
            public void onSuccess(QueryNewReportInfo res) {
                LogUtils.i("queryHaveNewReports() onSuccess");
                queryNewReportInfo = res;

                refreshRedpoints(false);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                LogUtils.i("queryHaveNewReports() onFail");
            }
        });
    }

    private void refreshRedpoints(boolean refreshCur) {
        if (queryNewReportInfo == null) {
            return;
        }
        if (refreshCur) {
            switch (mCurPosition) {
                case WORK_I:
                    queryNewReportInfo.setNewExerhomeReport(false);
                    queryNewReportInfo.setNewWeekExamReport(false);
                    break;
                case DAILY_CLEAR_I:
                    queryNewReportInfo.setNewWeekReport(false);
                    break;
//                case WEEK_EXTRACT_I:
//                    queryNewReportInfo.setNewWeekExerciseReport(false);
//                    break;
//                case EXCLUSIVE_EXERCISES_I:
//                    queryNewReportInfo.setNewExclusiveReport(false);
//                    break;
                case KNOWLEDGE_I:
                    queryNewReportInfo.setNewKnowledgeReport(false);
                    break;
                case ALLROUND_I:
                    queryNewReportInfo.setNewIntegratedReport(false);
                    break;
                default:
                    break;
            }
        }
        Boolean[] status = new Boolean[]{
                queryNewReportInfo.isNewExerhomeReport() || queryNewReportInfo.isNewExamReport() || queryNewReportInfo.isNewWeekExamReport(),
                queryNewReportInfo.isNewWeekReport(),
                queryNewReportInfo.isNewWeekExerciseReport() ||  queryNewReportInfo.isNewExclusiveReport(),
                queryNewReportInfo.isNewKnowledgeReport(),
                queryNewReportInfo.isNewIntegratedReport()
        };

        for (int i = 0; i < redpointViews.size(); i++) {
            //LogUtils.i("i=" + i + " status=" + status[i]);
            if (status[i]) {
                redpointViews.get(i).setVisibility(View.VISIBLE);
            } else {
                redpointViews.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void clickTab(int index) {
        if (mCurPosition == index) {
            if (getState(index)) {
                ReportBaseFragment reportBaseFragment = mAllFragments.get(index);
                if (reportBaseFragment != null) {
                    reportBaseFragment.refreshReport();
                }
                //刷新红点
                refreshRedpoints(true);
            }
            return;
        }
        ReportBaseFragment reportBaseFragment = mAllFragments.get(index);
        if (getState(index)) {
            if (reportBaseFragment != null) {
                reportBaseFragment.refreshReport();
            }
            switchFragment(index);
            //刷新红点
            refreshRedpoints(true);
        } else {
            switchFragment(index);
        }
    }

    private void switchFragment(int position) {
        if (mCurPosition == position) {
            return;
        }
        for (int i = 0; i < tabviews.size(); i++) {
            if (position == i) {
                tabviews.get(i).setActivated(true);
            } else {
                tabviews.get(i).setActivated(false);
            }
        }
        createFragment(position);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            MyBaseFragment fragment = mFragments.get(i);
            if (mFragmentMap.containsKey(fragment) && mFragmentMap.get(fragment) == position) {
                fragmentTransaction.show(fragment);
                fragment.setUserVisibleHint( true );
            } else {
                fragmentTransaction.hide(fragment);
                fragment.setUserVisibleHint( false );
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
        mCurPosition = position;
    }


    private void createFragment(int position) {
        if (position == DAILY_CLEAR_I && mWeekAnalysisReportFragment == null) {
            mWeekAnalysisReportFragment = new WeekAnalysisReportFragment();
            mFragmentMap.put(mWeekAnalysisReportFragment, DAILY_CLEAR_I);
            addToTransaction(mWeekAnalysisReportFragment);
        } else if (position == WEEK_EXTRACT_I && mWeekExtractReportFragment == null) {
            mWeekExtractReportFragment = new ClassicReportFragment();
            mFragmentMap.put(mWeekExtractReportFragment, WEEK_EXTRACT_I);
            addToTransaction(mWeekExtractReportFragment);
//        } else if (position == EXCLUSIVE_EXERCISES_I && mExclusiveReportFragment == null) {
//            mExclusiveReportFragment = new ExclusiveReportFragment();
//            mFragmentMap.put(mExclusiveReportFragment, EXCLUSIVE_EXERCISES_I);
//            addToTransaction(mExclusiveReportFragment);
//        } else if (position == CLASSIC_I && mClassicReportFragment == null) {
//            mClassicReportFragment = new ClassicReportFragment();
//            mFragmentMap.put(mClassicReportFragment, CLASSIC_I);
//            addToTransaction(mClassicReportFragment);
        } else if (position == KNOWLEDGE_I && mKnowledgeCharttFragment == null) {
            mKnowledgeCharttFragment = new KnowledgeCharttFragment();
            mFragmentMap.put(mKnowledgeCharttFragment, KNOWLEDGE_I);
            addToTransaction(mKnowledgeCharttFragment);
        } else if (position == ALLROUND_I && mAllroundReportFragment == null) {
            mAllroundReportFragment = new AllroundReportFragment();
            mFragmentMap.put(mAllroundReportFragment, ALLROUND_I);
            addToTransaction(mAllroundReportFragment);
        }

    }

    private void addToTransaction(ReportBaseFragment fragment) {
        mFragments.add(fragment);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_report, fragment).commitAllowingStateLoss();
    }

    private boolean getState(int index) {
        boolean isneedLoadData = false;
        if (queryNewReportInfo != null) {
            switch (index) {
                case WORK_I:
                    if (queryNewReportInfo.isNewExerhomeReport() || queryNewReportInfo.isNewWeekExamReport()) {
                        isneedLoadData = true;
                    }
                    break;
                case DAILY_CLEAR_I:
                    if (queryNewReportInfo.isNewWeekReport()) {
                        isneedLoadData = true;
                    }
                    break;
                case WEEK_EXTRACT_I:
                    if (queryNewReportInfo.isNewWeekExerciseReport()) {
                        isneedLoadData = true;
                    }
                    break;
//                case EXCLUSIVE_EXERCISES_I:
//                    if (queryNewReportInfo.isNewExclusiveReport()) {
//                        isneedLoadData = true;
//                    }
//                    break;
                case KNOWLEDGE_I:
                    if (queryNewReportInfo.isNewKnowledgeReport()) {
                        isneedLoadData = true;
                    }
                    break;
                case ALLROUND_I:
                    if (queryNewReportInfo.isNewIntegratedReport()) {
                        isneedLoadData = true;
                    }
                    break;
                default:
                    break;
            }
        }
        return isneedLoadData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_work_report:
                clickTab(WORK_I);
                break;
            case R.id.rl_daily_clear_report:
                clickTab(DAILY_CLEAR_I);
                break;
            case R.id.rl_promote_report:
                clickTab(WEEK_EXTRACT_I);
                break;
            case R.id.rl_knowledge_report:
                clickTab(KNOWLEDGE_I);
                break;
//            case R.id.rl_classic_report:
//                clickTab(CLASSIC_I);
//                break;
//            case R.id.rl_exclusive_report:
//                clickTab(EXCLUSIVE_EXERCISES_I);
//                break;
            case R.id.rl_allround_report:
                clickTab(ALLROUND_I);
                break;
            default:
                break;
        }
    }

    public void goToReport(final int index) {
        if (isPrepared) {
            clickTab(index);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToReport(index);
                }
            }, 300);
        }
    }

    @Subscribe
    public void receive(EditInfoEvent event) {
        AppLog.d("event = " + event );
        if (isPrepared) {
            queryHaveNewReports();
        }
    }


}
