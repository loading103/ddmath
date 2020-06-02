package com.tsinghuabigdata.edu.ddmath.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CoursePagerAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.event.UpdateClassEvent;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment.ErrorDayCleanFragment;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.FamousTeacherFragment;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.CheckWorkFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.view.UnLoginView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;


/**
 * 学习任务
 * Created by Administrator on 2016/12/14.
 */

public class MyStudyFragment extends MainBaseFragment implements View.OnClickListener {

    //模块序号
    public final static int MODEL_SCHOOLWORK     = 0;
    public final static int MODEL_CHECK_WORK     = 1;
    public final static int MODEL_ERRORREVISE    = 2;
    public final static int MODEL_FAMOUS_TEACHER = 3;

    //private Context mContext;

    private LinearLayout mLlTab;
    private ImageView    mIvSchoolWork;
    //private ImageView    mIvExclusiveExercises;
    private ImageView    mIvErrorRevise;
    private ImageView    mIvFamousTeacher;
    private ImageView    mIvCheckWork;

    private ViewPager mViewPager;
    private UnLoginView  mUnLoginView;
    private LinearLayout mLlNoClassTips;
    private LinearLayout mLlNoFormalClassTips;

    private int currTabIndex = 0; //当前页卡编号

    private ArrayList<Fragment> courseFragments = new ArrayList<>();
    private AgencyWorkFragment    mAgencyWorkFragment;
    private ErrorDayCleanFragment mErrorReviseFrament;
    private FamousTeacherFragment mFamousTeacherFragment;
    private CheckWorkFragment     mCheckWorkFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_study, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_study_phone, container, false);
        }
        initView(root);
        initData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getUmEventName() {
        if (mIvSchoolWork.isActivated() && mAgencyWorkFragment != null) {
            return mAgencyWorkFragment.getUmEventName();
        } else if (mIvCheckWork.isActivated() && mCheckWorkFragment != null) {
            return mCheckWorkFragment.getUmEventName();
        } else if (mIvFamousTeacher.isActivated() && mFamousTeacherFragment != null) {
            return mFamousTeacherFragment.getUmEventName();
        } else if (mIvErrorRevise.isActivated() && mErrorReviseFrament != null) {
            return mErrorReviseFrament.getUmEventName();
        }/* else if (mIvFineExercises.isActivated() && mFinePracticeFragment != null) {
            return mFinePracticeFragment.getUmEventName();
        }*/
        return super.getUmEventName();
    }

    private void initView(View root) {
        //mContext = getActivity();
        mLlTab =  root.findViewById(R.id.ll_tab);
        mIvSchoolWork =  root.findViewById(R.id.iv_school_work);
        mIvErrorRevise =  root.findViewById(R.id.iv_error_revise);
        mIvCheckWork =  root.findViewById(R.id.iv_check_work);
        mIvFamousTeacher =  root.findViewById(R.id.iv_famous_teacher);
        mViewPager =  root.findViewById(R.id.view_pager);
        mUnLoginView =  root.findViewById(R.id.unLoginView);
        mLlNoClassTips =  root.findViewById(R.id.ll_no_class_tips);
        mLlNoFormalClassTips =  root.findViewById(R.id.ll_no_formal_class_tips);
        //mIvFineExercises = (ImageView) root.findViewById(R.id.iv_fine_exercises);

        mIvSchoolWork.setActivated(true);
        mIvSchoolWork.setOnClickListener(this);
        mIvErrorRevise.setOnClickListener(this);
        mIvCheckWork.setOnClickListener(this);
        mIvFamousTeacher.setOnClickListener(this);
        //mIvFineExercises.setOnClickListener(this);
    }


    //初始化三个个Fragment————学校作业、专属套题
    private void initFragment() {
        mAgencyWorkFragment = new AgencyWorkFragment();
        mErrorReviseFrament = new ErrorDayCleanFragment();
        //mPracticeFragment.setPracticeType(PracticeFragment.TYPE_EXCLUSIVE);
        mFamousTeacherFragment = new FamousTeacherFragment();
        mCheckWorkFragment = new CheckWorkFragment();
        //mFinePracticeFragment = new PracticeFragment();
        //mFinePracticeFragment.setPracticeType(PracticeFragment.TYPE_CLASSIC);

        courseFragments.clear();
        courseFragments.add(mAgencyWorkFragment);
        courseFragments.add(mCheckWorkFragment);
        courseFragments.add(mErrorReviseFrament);
        //courseFragments.add(mFinePracticeFragment);
        courseFragments.add(mFamousTeacherFragment);
        if (mViewPager.getAdapter() == null) {
            mViewPager.setOffscreenPageLimit(4);
            CoursePagerAdapter adapter = new CoursePagerAdapter(getChildFragmentManager(), courseFragments);
            mViewPager.setAdapter(adapter);
            //        mViewPager.setCurrentItem(0);
            mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        } else {
            CoursePagerAdapter adapter = new CoursePagerAdapter(getChildFragmentManager(), courseFragments);
            mViewPager.setAdapter(adapter);
        }

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
                initFragment();
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
                initFragment();
                showCourseContentView();
            }
        } else {
            showUnregisterView();
        }
    }

    //展示 1、未登录页面
    private void showUnregisterView() {
        makeTitleEnable(false);
        mUnLoginView.setVisibility(View.VISIBLE);
        mLlNoClassTips.setVisibility(View.INVISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
    }

    //展示 2、未加入班级页面
    private void showNoClassView() {
        makeTitleEnable(false);
        mUnLoginView.setVisibility(View.INVISIBLE);
        mLlNoClassTips.setVisibility(View.VISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
    }

    //展示 3、未加入正式班级页面
    private void showNoFormalClassView() {
        makeTitleEnable(false);
        mUnLoginView.setVisibility(View.INVISIBLE);
        mLlNoClassTips.setVisibility(View.VISIBLE);
        mLlNoFormalClassTips.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
    }

    //展示 4、已加入班级页面
    private void showCourseContentView() {
        makeTitleEnable(true);
        mUnLoginView.setVisibility(View.INVISIBLE);
        mLlNoClassTips.setVisibility(View.INVISIBLE);
        mLlNoFormalClassTips.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
    }

    private void makeTitleEnable(boolean enable) {
        for (int i = 0; i < mLlTab.getChildCount(); i++) {
            mLlTab.getChildAt(i).setEnabled(enable);
        }
    }

    private void moveTo(int i) {
        if (currTabIndex == i) {
            return;
        }
        showTextView(i);
        currTabIndex = i;
    }


    //实现标题栏滑块动画以及被选中标题加粗
    private void select(int i) {
        LogUtils.i("select i= " + i);
        if (currTabIndex == i) {
            return;
        }
        showTextView(i);
        currTabIndex = i;
    }

    private void showTextView(int index) {
        LogUtils.i("showTextView index= " + index);
        for (int i = 0; i < mLlTab.getChildCount(); i++) {
            boolean select = index == i;
            mLlTab.getChildAt(i).setActivated(select);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_school_work:
                click(MODEL_SCHOOLWORK);
                break;
            case R.id.iv_check_work:
                click(MODEL_CHECK_WORK);
                break;
            case R.id.iv_famous_teacher:
                click(MODEL_FAMOUS_TEACHER);
                break;
            case R.id.iv_error_revise:
                click(MODEL_ERRORREVISE);
                break;
            /*case R.id.iv_fine_exercises:
                click(MODEL_FINEPRACTICE);
                break;*/
            default:
                break;
        }
    }

    private void click(int index) {
        select(index);
        mViewPager.setCurrentItem(index);
    }


    @Override
    protected void login(boolean isLogin) {
        if( !isAdded() || isDetached() ) return;

        if (isLogin) {
            if (!AccountUtils.hasClass()) {
                showNoClassView();
            } else if (!AccountUtils.hasFormalClass()) {
                showNoFormalClassView();
            } else {
                initFragment();
                showCourseContentView();
            }
        } else {
            showUnregisterView();
            moveTo(0);
        }
    }

    public void goTo(int index) {
        select(index);
        mViewPager.setCurrentItem(index);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            LogUtils.i("onPageSelected position= " + position);
            select(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(UpdateClassEvent event) {
        updateClassInfo();
    }

}
