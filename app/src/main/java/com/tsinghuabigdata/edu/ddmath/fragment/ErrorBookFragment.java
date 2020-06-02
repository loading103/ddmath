package com.tsinghuabigdata.edu.ddmath.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CoursePagerAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment.ClassicPracticeFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment.ErrorStageReviewFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment.ErrorWeekTrainFragment;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.fragment.VariantTrainFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.UnLoginView;

import java.util.ArrayList;

import javax.annotation.Nonnull;


/**
 * 错题本主界面
 * Created by cuibo 2017/11/15
 */
public class ErrorBookFragment extends MainBaseFragment implements View.OnClickListener {

    //模块序号
    //public final static int MODEL_DAYCLEAR = 0;
    public final static int MODEL_WEEKTRAIN = 0;        //周错题精炼
    public final static int MODEL_VARTRAIN = 1;         //变式训练
    public final static int MODEL_PRACTICE = 2;         //精品套题
    public final static int MODEL_ERRBOOK = 3;           //错题本下载
    public final static int MODEL_BROWER = 4;          //错题浏览

    //四个模块按钮
    private ImageView weekTrainBtn;
    private ImageView varTrainBtn;
    private ImageView praticeBtn;
    private ImageView errBookBtn;
    //private ImageView browerBtn;

    //显示主要内容
    private ViewPager mViewPager;

    //未登录
    private UnLoginView    mUnLoginView;

    //默认显示
    private int defaultModelIndex = MODEL_WEEKTRAIN;
    //当前模块序号
    private int currTabIndex = defaultModelIndex;

    //详情页面
    private ArrayList<Fragment> mFragmentList = new ArrayList<>();

    private ErrorWeekTrainFragment mWeekTrainFragment;
    private VariantTrainFragment mVarTrainFragment;
    private ClassicPracticeFragment mPraticeFragment;
    private ErrorStageReviewFragment mErrorBookFragment;
    //private ErrorBroweFragment mBrowerFragment;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;

        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_main_errorbook, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_main_errorbook_phone, container, false);
        }
        initView(root);
        showChildFragment();
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ebook_modelbtn_weektrain:
                select(MODEL_WEEKTRAIN);
                break;
            case R.id.ebook_modelbtn_vartrain:
                select(MODEL_VARTRAIN);
                break;
            case R.id.ebook_modelbtn_pratice:{
                select(MODEL_PRACTICE);
                break;
            }
            case R.id.ebook_modelbtn_errbook:
                select(MODEL_ERRBOOK);
                break;
//            case R.id.ebook_modelbtn_brower:
//                select(MODEL_BROWER);
//                break;
            default:
                break;
        }
    }

    public void clearData() {
        select( defaultModelIndex );

        //2017/11/15  优化
        mWeekTrainFragment.clearData();
        mVarTrainFragment.clearData();
        mPraticeFragment.clearData();
        mErrorBookFragment.clearData();
        //mBrowerFragment.clearData();
    }

    @Override
    protected void login(boolean isLogin) {
        if (isLogin) {
            initFragment();
            showCourseContentView();
        } else {
            showUnregisterView();
            select(0);
        }
    }


    @Override
    public String getUmEventName(){
        if( weekTrainBtn.isActivated() && mWeekTrainFragment!=null ){
            return mWeekTrainFragment.getUmEventName();
        }else if( varTrainBtn.isActivated() && mVarTrainFragment!=null  ){
            return mVarTrainFragment.getUmEventName();
        }else if( praticeBtn.isActivated() && mPraticeFragment!=null  ){
            return mPraticeFragment.getUmEventName();
        }else if( errBookBtn.isActivated() && mErrorBookFragment!=null  ){
            return mErrorBookFragment.getUmEventName();
        }/*else if( browerBtn.isActivated() && mBrowerFragment!=null  ){
            return mBrowerFragment.getUmEventName();
        }*/
        return super.getUmEventName();
    }
    //--------------------------------------------------------------------------------------------
    private void initView(View root) {
        weekTrainBtn = root.findViewById( R.id.ebook_modelbtn_weektrain );
        varTrainBtn = root.findViewById( R.id.ebook_modelbtn_vartrain );
        praticeBtn = root.findViewById( R.id.ebook_modelbtn_pratice );
        errBookBtn = root.findViewById( R.id.ebook_modelbtn_errbook);
        //browerBtn = root.findViewById( R.id.ebook_modelbtn_brower );

        weekTrainBtn.setOnClickListener(this);
        varTrainBtn.setOnClickListener(this);
        praticeBtn.setOnClickListener(this);
        errBookBtn.setOnClickListener( this );
        //browerBtn.setOnClickListener(this);

        //默认
        weekTrainBtn.setActivated( true );

        //
        mViewPager =  root.findViewById(R.id.view_pager);
        mUnLoginView =  root.findViewById(R.id.unLoginView);
    }

    //初始化三个Fragment————错题本、学习报告、知识图谱
    private void initFragment() {

        mWeekTrainFragment = new ErrorWeekTrainFragment();
        mVarTrainFragment = new VariantTrainFragment();
        mPraticeFragment = new ClassicPracticeFragment();
        mErrorBookFragment = new ErrorStageReviewFragment();
//        mBrowerFragment = new ErrorBroweFragment();

        mFragmentList.clear();
        mFragmentList.add(mWeekTrainFragment);
        mFragmentList.add(mVarTrainFragment);
        mFragmentList.add(mPraticeFragment);
        mFragmentList.add(mErrorBookFragment);
//        mFragmentList.add(mBrowerFragment);

        if (mViewPager.getAdapter() == null) {
            mViewPager.setOffscreenPageLimit(5);
            CoursePagerAdapter adapter = new CoursePagerAdapter(getChildFragmentManager(), mFragmentList);
            mViewPager.setAdapter(adapter);
            mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        } else {
            CoursePagerAdapter adapter = new CoursePagerAdapter(getChildFragmentManager(), mFragmentList);
            mViewPager.setAdapter(adapter);
        }
    }

    //
    private void showChildFragment() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo != null && detailinfo != null) {
            initFragment();
            showCourseContentView();
        } else {
            showUnregisterView();
        }
    }

    //展示 1、未登录页面
    private void showUnregisterView() {
        makeTitleEnable(false);
        mUnLoginView.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
    }

    //展示 2、已登录页面
    private void showCourseContentView() {
        makeTitleEnable(true);
        mUnLoginView.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
    }

    private void makeTitleEnable(boolean enable) {
        weekTrainBtn.setEnabled(enable);
        varTrainBtn.setEnabled(enable);
        praticeBtn.setEnabled(enable);
        errBookBtn.setEnabled(enable);
//        browerBtn.setEnabled(enable);
    }

//    private void moveTo(int index) {
//        select(index);
//    }

    //实现标题栏滑块动画以及被选中标题加粗
    private void select(int index) {
        if (currTabIndex == index) {
            return;
        }
        showTextView(index);
        currTabIndex = index;
        mViewPager.setCurrentItem( index );
    }

    private void showTextView(int i) {
        // 优化
        weekTrainBtn.setActivated( i==MODEL_WEEKTRAIN );
        varTrainBtn.setActivated( i==MODEL_VARTRAIN );
        praticeBtn.setActivated( i==MODEL_PRACTICE );
        errBookBtn.setActivated( i==MODEL_ERRBOOK );
//        browerBtn.setActivated( i==MODEL_BROWER );
    }

    //2017/11/21 跳转到指定模块
    public void goTo(int index) {
        select( index );
    }

    //---------------------------------------------------------------------------------
    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            select(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

}
