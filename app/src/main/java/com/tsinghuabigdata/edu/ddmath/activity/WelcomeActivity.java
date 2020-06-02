package com.tsinghuabigdata.edu.ddmath.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.GuidePageAdapter;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AssetsUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private Context      mContext;
    private ViewPager    mViewPager;
    private LinearLayout mIndicatorContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_welcome);
        } else {
            setContentView(R.layout.activity_welcome_phone);
        }
        initView();
        initData();
    }


    private void initView() {
        mContext = this;
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mIndicatorContainer = (LinearLayout) findViewById(R.id.indicator_container);
        mViewPager.addOnPageChangeListener(this);
    }

    private void initData() {
        View view;
        if (GlobalData.isPad()) {
            view = getLayoutInflater().inflate(R.layout.view_guide_item, null);
        } else {
            view = getLayoutInflater().inflate(R.layout.view_guide_item_phone, null);
        }
        FrameLayout flRoot = (FrameLayout) view.findViewById(R.id.fl_guide_root);
        TextView tvExperience = (TextView) view.findViewById(R.id.tv_experience);
        tvExperience.setTypeface(AssetsUtils.getMyTypeface(mContext));
        tvExperience.setOnClickListener(this);
        List<View> list = new ArrayList<>();
        for (int i = 0; i < flRoot.getChildCount(); i++) {
            View child = flRoot.getChildAt(i);
            list.add(child);
        }
        flRoot.removeAllViews();
        GuidePageAdapter adapter = new GuidePageAdapter(list);
        mViewPager.setAdapter(adapter);
        select(0);
    }

    private void select(int position) {
        for (int i = 0; i < mIndicatorContainer.getChildCount(); i++) {
            ImageView imageView = (ImageView) mIndicatorContainer.getChildAt(i);
            if (i == position) {
                imageView.setSelected(true);
            } else {
                imageView.setSelected(false);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁止用户按返回键退出
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


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

    @Override
    public void onClick(View v) {
        PreferencesUtils.putInt(mContext, AppConst.WELCOME_OPENED, 1);
        AccountUtils.gotoMainActivity( mContext );
        finish();
    }
}
