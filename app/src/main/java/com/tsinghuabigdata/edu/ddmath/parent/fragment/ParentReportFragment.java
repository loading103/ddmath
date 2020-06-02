package com.tsinghuabigdata.edu.ddmath.parent.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CoursePagerAdapter;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.view.RollTextView;

import java.util.ArrayList;

/**
 * 报告模块
 * Created by Administrator on 2018/6/27.
 */

public class ParentReportFragment extends MyBaseFragment implements View.OnClickListener {


    private TextView  mTvDayReport;
    private TextView  mTvWeekReport;
    private View      mSlider;
    private View      mSlider2;
    private ViewPager mViewPager;
    //private RollTextView rollTextView;

    private int tabWidth;
    private int currTabIndex = 0; //当前页卡编号

    private ArrayList<Fragment> courseFragments = new ArrayList<>();
    private DayReportFragment  mDayReportFragment;
    private WeekReportFragment mWeekReportFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("ParentReportFragment onCreateView");
        View root = inflater.inflate(R.layout.fragment_parent_report_phone, container, false);
        initView(root);
        setPrepared();
        initFragment();
        initData();
        //EventBus.getDefault().register(this);
        return root;
    }


    private void initView(View root) {
        mTvDayReport = (TextView) root.findViewById(R.id.tv_day_report);
        mTvWeekReport = (TextView) root.findViewById(R.id.tv_week_report);
        mSlider = root.findViewById(R.id.slider);
        mSlider2 = root.findViewById(R.id.slider2);
        mViewPager = (ViewPager) root.findViewById(R.id.view_pager);
        RollTextView rollTextView = (RollTextView) root.findViewById(R.id.marqueTextView);
        rollTextView.setTextColor(Color.rgb(0x08,0xC2,0x74) );

        mTvDayReport.setOnClickListener(this);
        mTvWeekReport.setOnClickListener(this);
        mTvDayReport.postDelayed(new Runnable() {
            @Override
            public void run() {
                tabWidth = mSlider2.getLeft() - mSlider.getLeft();
                LogUtils.i("tabWidth=" + tabWidth);
            }
        }, 100);
    }

    private void initData() {

    }

    //初始化连个个Fragment
    private void initFragment() {
        mDayReportFragment = new DayReportFragment();
        mWeekReportFragment = new WeekReportFragment();
        courseFragments.add(mDayReportFragment);
        courseFragments.add(mWeekReportFragment);

        //给ViewPager设置适配器
        mViewPager.setOffscreenPageLimit(2);
        CoursePagerAdapter adapter = new CoursePagerAdapter(getChildFragmentManager(), courseFragments);
        mViewPager.setAdapter(adapter);
        //        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }


    //实现标题栏滑块动画以及被选中标题加粗
    private void select(int i) {
        LogUtils.i("select i= " + i);
        if (currTabIndex == i) {
            return;
        }
        Animation animation = new TranslateAnimation(currTabIndex * tabWidth, i * tabWidth, 0, 0);
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(300);
        mSlider.startAnimation(animation);
        showTextView(i);
        currTabIndex = i;
    }

    private void showTextView(int i) {
        if (i == 0) {
            mTvDayReport.setActivated(true);
            mTvWeekReport.setActivated(false);
        } else if (i == 1) {
            mTvDayReport.setActivated(false);
            mTvWeekReport.setActivated(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_day_report:
                select(0);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_week_report:
                select(1);
                mViewPager.setCurrentItem(1);
                break;
        }
    }


    @Override
    public String getUmEventName(){
        if( currTabIndex == 0 && mDayReportFragment!=null ){
            return mDayReportFragment.getUmEventName();
        }else if( currTabIndex == 1 && mWeekReportFragment!=null ){
            return mWeekReportFragment.getUmEventName();
        }
        return "parent_main_report";
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

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
}
