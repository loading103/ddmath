package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.utils.NetworkUtils;


/**
 * Created by Administrator on 2016/12/23.
 */

public class LoadingPager extends FrameLayout implements View.OnClickListener {

    private View         mainLayout;
    private LinearLayout mFaultLayout;
    private ImageView    mIvFault;
    private TextView     mTvFault;
    private Button       mBtnRetry;
    private LinearLayout mEmptyLayout;
    //    private ImageView    mIvEmpty;
    private TextView     mTvEmpty;
    private LinearLayout mLoadingLayout;
    private ImageView    mGivLoading;
    private TextView     mTvLoading;

    private View              mTargetView;
    private OnClickListener   mListener;
    private AnimationDrawable animationDrawable;

    public LoadingPager(Context context) {
        this(context, null);
    }

    public LoadingPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()) {
            mainLayout = View.inflate(context, R.layout.view_loading_pager, this);
        } else {
            mainLayout = View.inflate(context, R.layout.view_loading_pager_phone, this);
        }
        initView();
    }

    //    public LoadingPager(Context context, AttributeSet attrs, int defStyleAttr) {
    //        super(context, attrs, defStyleAttr);
    //        initialize();
    //    }

    /**
     * 初始化界面元素
     */
    private void initView() {
        mFaultLayout = (LinearLayout) findViewById(R.id.fault_layout);
        mIvFault = (ImageView) findViewById(R.id.iv_fault);
        mTvFault = (TextView) findViewById(R.id.tv_fault);
        mBtnRetry = (Button) findViewById(R.id.btn_retry);
        mEmptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        mGivLoading = (ImageView) findViewById(R.id.giv_loading);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mBtnRetry.setOnClickListener(this);

        animationDrawable = (AnimationDrawable) mGivLoading.getDrawable();
        startAnim();
    }

    public void startAnim() {
        animationDrawable.start();
    }

    public void stopAnim() {
        animationDrawable.stop();
    }


    public void showFault() {
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(VISIBLE);
        mEmptyLayout.setVisibility(GONE);
        hideTargetView();
        animationDrawable.stop();
    }

    public void showFault(Exception ex) {
        if (!AppUtils.isNetworkConnected(getContext()) || NetworkUtils.isNoConnection(ex)) {
            mTvFault.setText(R.string.fault_nonet);
            mIvFault.setImageResource( R.drawable.img_nonetwork );
        } else if (NetworkUtils.isTimeout(ex)) {
            mIvFault.setImageResource( R.drawable.img_overtime );
            mTvFault.setText(R.string.fault_timeout);
        } else {
            mIvFault.setImageResource( R.drawable.img_noservice );
            mTvFault.setText(R.string.fault_server);
        }
        showFault();
    }

    public void showCustomFault(String description) {
        mIvFault.setImageResource( R.drawable.img_noservice );
        mTvFault.setText(description);
        showFault();
    }

    public void showServerFault() {
        mIvFault.setImageResource( R.drawable.img_noservice );
        mTvFault.setText(R.string.fault_server);
        showFault();
    }


    public void showEmpty() {
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(VISIBLE);
        hideTargetView();
        animationDrawable.stop();
    }

    private void hideTargetView() {
        if (mTargetView != null) {
            mTargetView.setVisibility(GONE);
        }
    }

    private void showTargetView() {
        if (mTargetView != null) {
            mTargetView.setVisibility(VISIBLE);
        }
    }

    public void showEmpty(int resId) {
        String text = getResources().getString(resId);
        showEmpty(text);
    }

    public void showEmpty(String text) {
        mTvEmpty.setText(text);
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(VISIBLE);
        hideTargetView();
        animationDrawable.stop();
    }

    public void showLoading() {
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(VISIBLE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(GONE);
        hideTargetView();
        animationDrawable.start();
    }

    public void showTarget() {
        mainLayout.setVisibility(GONE);
        //        mLoadingLayout.setVisibility(GONE);
        //        mFaultLayout.setVisibility(GONE);
        //        mEmptyLayout.setVisibility(GONE);
        showTargetView();
        animationDrawable.stop();
    }

    public void hideall() {
        mainLayout.setVisibility(GONE);
        //        mLoadingLayout.setVisibility(GONE);
        //        mFaultLayout.setVisibility(GONE);
        //        mEmptyLayout.setVisibility(GONE);
        showTargetView();
        animationDrawable.stop();
    }

    /**
     * 绑定目标控件
     *
     * @param view
     */
    public void setTargetView(View view) {
        mTargetView = view;
        hideTargetView();
    }

    /**
     * 绑定点击事件
     *
     * @param listener
     */
    public void setListener(OnClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick(v);
        }
    }


    public void setEmptyText(String text) {
        mTvEmpty.setText(text);
    }
    public void setEmptyText(Spannable spannable) {
        mTvEmpty.setText(spannable);
    }
    public void setEmptyText(int resId) {
        mTvEmpty.setText(resId);
    }

    public void setWhite() {
        setBgColor(R.color.white);
    }

    public void setBgColor(int color) {
        mFaultLayout.setBackgroundResource(color);
        mEmptyLayout.setBackgroundResource(color);
        mLoadingLayout.setBackgroundResource(color);
    }
}
