package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.utils.NetworkUtils;

/**
 * Created by Administrator on 2017/11/24.
 */

public class HonourDialogLoadingPager extends FrameLayout implements View.OnClickListener{

    private View mainLayout;
    private LinearLayout mFaultLayout;
    private TextView mTvFault;
    private Button mBtnRetry;
    private LinearLayout mEmptyLayout;
    //    private ImageView    mIvEmpty;
    private TextView     mTvEmpty;
    private LinearLayout mLoadingLayout;
    private TextView     mTvLoading;

    private View              mTargetView;
    private OnClickListener   mListener;

    public HonourDialogLoadingPager(Context context) {
        this(context, null);
    }

    public HonourDialogLoadingPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()) {
            mainLayout = View.inflate(context, R.layout.view_dialog_loaing_pager, this);
        } else {
            mainLayout = View.inflate(context, R.layout.view_dialog_loading_pager_phone, this);
        }
        initView();
    }

    /**
     * 初始化界面元素
     */
    private void initView() {
        mFaultLayout = (LinearLayout) findViewById(R.id.fault_layout);
        mTvFault = (TextView) findViewById(R.id.tv_fault);
        mBtnRetry = (Button) findViewById(R.id.btn_retry);
        mEmptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        mTvLoading = (TextView) findViewById(R.id.tv_loading);
        mBtnRetry.setOnClickListener(this);

    }

    private void showFault() {
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(VISIBLE);
        mEmptyLayout.setVisibility(GONE);
        hideTargetView();
    }

    public void showFault(Exception ex) {
        if (!AppUtils.isNetworkConnected(getContext()) || NetworkUtils.isNoConnection(ex)) {
            mTvFault.setText(R.string.fault_nonet);
        } else if (NetworkUtils.isTimeout(ex)) {
            mTvFault.setText(R.string.fault_timeout);
        } else {
            mTvFault.setText(R.string.fault_server);
        }
        showFault();
    }

    public void showServerFault() {
        mTvFault.setText(R.string.fault_server);
        showFault();
    }


    public void showEmpty() {
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(VISIBLE);
        hideTargetView();
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


    public void showEmpty(String text) {
        mTvEmpty.setText(text);
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(VISIBLE);
        hideTargetView();
    }

    public void showLoading() {
        mainLayout.setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(VISIBLE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(GONE);
        hideTargetView();
    }

    public void showTarget() {
        mainLayout.setVisibility(GONE);
        //        mLoadingLayout.setVisibility(GONE);
        //        mFaultLayout.setVisibility(GONE);
        //        mEmptyLayout.setVisibility(GONE);
        showTargetView();
    }

    public void hideall() {
        mainLayout.setVisibility(GONE);
        //        mLoadingLayout.setVisibility(GONE);
        //        mFaultLayout.setVisibility(GONE);
        //        mEmptyLayout.setVisibility(GONE);
        showTargetView();
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

    public void setWhite() {
        setBgColor(R.color.white);
    }

    public void setBgColor(int color) {
        mFaultLayout.setBackgroundResource(color);
        mEmptyLayout.setBackgroundResource(color);
        mLoadingLayout.setBackgroundResource(color);
    }
}
