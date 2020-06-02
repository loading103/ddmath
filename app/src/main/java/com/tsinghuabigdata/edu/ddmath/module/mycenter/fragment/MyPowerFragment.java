package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.UserCenterModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.TotalStudyAbility;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.ChangeAbilityEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.PhonePowerProgressBar;
import com.tsinghuabigdata.edu.ddmath.view.PowerProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 我的学力
 * Created by Administrator on 2017/9/4.
 */

public class MyPowerFragment extends MyBaseFragment {


    private LoadingPager mLoadingPager;
    private LinearLayout mLlPowerContent;
    private View         mPowerProgressBar;
    private TextView     mTvTodayPower;
    private TextView     mTvWinClassmates;
    private TextView     mTvSubmitWork;
    private TextView     mTvSubmitExam;
    private TextView     mTvQuestionReview;
    private TextView     mTvForceTrain;

    private UserCenterModel mModel = new UserCenterModel();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_power, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_power_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View root) {
        mLoadingPager = (LoadingPager) root.findViewById(R.id.loadingPager);
        mLlPowerContent = (LinearLayout) root.findViewById(R.id.ll_power_content);
        mPowerProgressBar = root.findViewById(R.id.powerProgressBar);
        mTvTodayPower = (TextView) root.findViewById(R.id.tv_today_power);
        mTvWinClassmates = (TextView) root.findViewById(R.id.tv_win_classmates);
        mTvSubmitWork = (TextView) root.findViewById(R.id.tv_submit_work);
        mTvSubmitExam = (TextView) root.findViewById(R.id.tv_submit_exam);
        mTvQuestionReview = (TextView) root.findViewById(R.id.tv_question_review);
        mTvForceTrain = (TextView) root.findViewById(R.id.tv_force_train);
        mLoadingPager.setTargetView(mLlPowerContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryTotalAbility();
            }
        });
    }

    private void initData() {
        queryTotalAbility();
        EventBus.getDefault().register(this);
    }

    private void queryTotalAbility() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            String studentId = detailinfo.getStudentId();
            mModel.queryTotalAbility(studentId, new RequestListener<TotalStudyAbility>() {

                @Override
                public void onSuccess(TotalStudyAbility vo) {
                    LogUtils.i("queryTotalAbility onSuccess");
                    if (vo == null) {
                        mLoadingPager.showServerFault();
                        return;
                    }
                    showView(vo);
                    mLoadingPager.showTarget();

                }

                @Override
                public void onFail(HttpResponse<TotalStudyAbility> response, Exception ex) {
                    LogUtils.i("queryTotalAbility onFail");
                    mLoadingPager.showFault(ex);
                }
            });
        }
    }

    private void showView(TotalStudyAbility vo) {
        if (mPowerProgressBar instanceof PowerProgressBar) {
            PowerProgressBar powerProgressBar = (PowerProgressBar) mPowerProgressBar;
            powerProgressBar.setStartValue(vo.getStartValue());
            powerProgressBar.setEndValue(vo.getEndValue());
            powerProgressBar.setPower(vo.getTotalStudyAbilityValue());
        } else if (mPowerProgressBar instanceof PhonePowerProgressBar) {
            PhonePowerProgressBar powerProgressBar = (PhonePowerProgressBar) mPowerProgressBar;
            powerProgressBar.setStartValue(vo.getStartValue());
            powerProgressBar.setEndValue(vo.getEndValue());
            powerProgressBar.setPower(vo.getTotalStudyAbilityValue());
        }
        int value =  vo.getTodayStudyAbilityValue();
        if( value < 0 ) value = 0;
        mTvTodayPower.setText("+" + value);
        int percent = Math.round(vo.getExceedPercent() * 100);
        mTvWinClassmates.setText(percent + "%");
        mTvSubmitWork.setText(vo.getHomeworkSubmitTimes() + "");
        mTvSubmitExam.setText(vo.getReviseCount() + "");
        mTvQuestionReview.setText(vo.getWrongQuestionReviewCount() + "");
        mTvForceTrain.setText(vo.getStrengthExerciseCount() + "");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeAbilityEvent event) {
        queryTotalAbility();
    }

    public String getUmEventName() {
        return "mycenter_mypower";
    }
}
