package com.tsinghuabigdata.edu.ddmath.fragment;

import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.event.UpdateClassEvent;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.view.UnLoginView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 首页
 * Created by Administrator on 2016/12/14.
 */

public class FirstFragment extends MainBaseFragment {

    //private Context mContext;

    private UnLoginView  mUnLoginView;
    private LinearLayout mLlNoClassTips;
    private LinearLayout mLlNoFormalClassTips;

    private MyWorldkFragment mMyWorldkFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(GlobalData.isPad() ? R.layout.fragment_first : R.layout.fragment_first_phone, container, false);
        initView(root);
        initData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }


    private void initView(View root) {
        //mContext = getActivity();
        mUnLoginView =  root.findViewById(R.id.unLoginView);
        mLlNoClassTips =  root.findViewById(R.id.ll_no_class_tips);
        mLlNoFormalClassTips =  root.findViewById(R.id.ll_no_formal_class_tips);
    }


    private void initData() {
        createLoginInfo();
        EventBus.getDefault().register(this);
    }

    private void createLoginInfo() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo != null) {
            if (!AccountUtils.hasClass()) {
                showNoClassView();
            } else if (!AccountUtils.hasFormalClass()) {
                showNoFormalClassView();
            } else {
                showCourseContentView();
            }
        } else {
            showUnregisterView();
        }
    }

    //更新班级信息
    private void updateClassInfo() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo != null) {
            if (!AccountUtils.hasClass()) {
                showNoClassView();
            } else if (!AccountUtils.hasFormalClass()) {
                showNoFormalClassView();
            } else {
                showCourseContentView();
                //refreshData();
            }
        } else {
            showUnregisterView();
        }
    }

    //展示 1、未登录页面
    private void showUnregisterView() {
        mUnLoginView.setVisibility(View.VISIBLE);
        mLlNoClassTips.setVisibility(View.INVISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        showMyWorld(false);
    }

    //展示 2、未加入班级页面
    private void showNoClassView() {
        mUnLoginView.setVisibility(View.INVISIBLE);
        mLlNoClassTips.setVisibility(View.VISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        showMyWorld(false);
    }

    //展示 3、未加入正式班级页面
    private void showNoFormalClassView() {
        mUnLoginView.setVisibility(View.INVISIBLE);
        mLlNoClassTips.setVisibility(View.VISIBLE);
        mLlNoFormalClassTips.setVisibility(View.VISIBLE);
        showMyWorld(false);
    }

    //展示 4、已加入班级页面
    private void showCourseContentView() {
        mUnLoginView.setVisibility(View.INVISIBLE);
        mLlNoClassTips.setVisibility(View.INVISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        showMyWorld(true);
    }

    private void showMyWorld(boolean isLogin) {
        if (isLogin) {
            initFragment();
        } else {
            if (mMyWorldkFragment != null) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(mMyWorldkFragment).commitAllowingStateLoss();
            }
        }
    }



    private void initFragment() {
        LogUtils.i("initFragment");
        mMyWorldkFragment = new MyWorldkFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fl_my_world_container, mMyWorldkFragment).commitAllowingStateLoss();
    }

    @Override
    protected void login(boolean isLogin) {
        boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
        LogUtils.i("isLogin=" + isLogin + "isMainThread=" + isMainThread);
        if (isLogin) {
            if (!AccountUtils.hasClass()) {
                showNoClassView();
            } else if (!AccountUtils.hasFormalClass()) {
                showNoFormalClassView();
            } else {
                showCourseContentView();
            }
        } else {
            showUnregisterView();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(UpdateClassEvent event) {
        updateClassInfo();
    }

    public String getUmEventName() {
        return "main";
    }
}
