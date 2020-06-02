package com.tsinghuabigdata.edu.ddmath.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.UnLoginView;

import javax.annotation.Nonnull;


/**
 * 学情反馈
 * Created by Administrator on 2016/12/14.
 */

public class StudyConditionFragment extends MainBaseFragment {

    //private Context          mContext;
    private UnLoginView      mUnLoginView;
    private LinearLayout     mLlReport;
    private MyReportFragment mMyReportFragment;
    private boolean showMyReport;


    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_study_condition, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_study_condition_phone, container, false);
        }
        initView(root);
        initData();
        return root;
    }

    private void initView(View root) {
        //mContext = getActivity();
        mUnLoginView =  root.findViewById(R.id.unLoginView);
        mLlReport =  root.findViewById(R.id.ll_report);
    }


    private void initData() {
//        LoginInfo loginInfo = AccountUtils.getLoginUser();
//        showView(loginInfo != null);
    }

    @Override
    protected void login(boolean isLogin) {
        showView(isLogin);
        showMyReport = isLogin;
    }


    private void showView(boolean isLogin) {
        if (isLogin) {
            mUnLoginView.setVisibility(View.INVISIBLE);
            mLlReport.setVisibility(View.INVISIBLE);
            initFragment();
        } else {
            mUnLoginView.setVisibility(View.VISIBLE);
            mLlReport.setVisibility(View.VISIBLE);
            if (mMyReportFragment != null) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(mMyReportFragment).commitAllowingStateLoss();
            }
        }
    }

    private void initFragment() {
        mMyReportFragment = new MyReportFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_report_container, mMyReportFragment).commitAllowingStateLoss();
    }

    public void goTo(int index) {
        if (mMyReportFragment != null) {
            mMyReportFragment.goToReport(index);
        }
    }

    public void addView() {
        boolean isLogin = AccountUtils.getLoginUser() != null;
        if (showMyReport != isLogin){
            showMyReport = isLogin;
            showView(isLogin);
        }
    }

    @Override
    public String getUmEventName(){
        if (mMyReportFragment != null){
            return mMyReportFragment.getUmEventName();
        }
        return super.getUmEventName();
    }

}
