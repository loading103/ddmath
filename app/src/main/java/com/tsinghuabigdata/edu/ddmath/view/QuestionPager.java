package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
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
 * 适配错题本页面的LoadingPager
 * Created by Administrator on 2016/12/23.
 */

public class QuestionPager extends FrameLayout implements View.OnClickListener {

    private LinearLayout mFaultLayout;
    private ImageView mIvFault;
    private TextView mTvFault;
    //private Button mBtnRetry;
    private LinearLayout mEmptyLayout;
    private ImageView mIvEmpty;
    private TextView mTvEmpty;
    private LinearLayout mLoadingLayout;
    //private ImageView mGivLoading;
    //private TextView mTvLoading;

    private View              mTargetView;
    private OnClickListener   mListener;
    private AnimationDrawable animationDrawable;

    public QuestionPager(Context context) {
        this(context, null);
    }

    public QuestionPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()){
            View.inflate(context, R.layout.view_loading_pager, this);
        }else {
            View.inflate(context, R.layout.view_loading_pager_question, this);
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
        Button mBtnRetry = (Button) findViewById(R.id.btn_retry);
        mEmptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        mIvEmpty = (ImageView) findViewById(R.id.iv_empty);
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        ImageView mGivLoading = (ImageView) findViewById(R.id.giv_loading);
        //mTvLoading = (TextView) findViewById(R.id.tv_loading);
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
        setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(VISIBLE);
        mEmptyLayout.setVisibility(GONE);
        mTargetView.setVisibility(GONE);
        animationDrawable.stop();
    }

    public void showFault(Exception ex) {
        setVisibility(VISIBLE);
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

    public void showEmpty() {
        setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(VISIBLE);
        mTargetView.setVisibility(GONE);
        animationDrawable.stop();
    }

    public void showEmpty( String text) {
        setVisibility(VISIBLE);
        mTvEmpty.setText(text);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(VISIBLE);
        mTargetView.setVisibility(GONE);
        animationDrawable.stop();
    }

    public void showLoading() {
        setVisibility(VISIBLE);
        mLoadingLayout.setVisibility(VISIBLE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(GONE);
        mTargetView.setVisibility(GONE);
        animationDrawable.start();
    }

    public void showTarget() {
        setVisibility(GONE);
        mLoadingLayout.setVisibility(GONE);
        mFaultLayout.setVisibility(GONE);
        mEmptyLayout.setVisibility(GONE);
        mTargetView.setVisibility(VISIBLE);
        animationDrawable.stop();
    }

    /**
     * 绑定目标控件
     */
    public void setTargetView(View view) {
        mTargetView = view;
        mTargetView.setVisibility(GONE);
    }

    /**
     * 绑定点击事件
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

//    public void setEmptyImage(int resid) {
//        mIvEmpty.setImageResource(resid);
//    }
//
//    public void setEmptyText(String text) {
//        mTvEmpty.setText(text);
//    }
//
//    public void justSize() {
//        LinearLayout.LayoutParams ivParams = (LinearLayout.LayoutParams) mIvFault.getLayoutParams();
//        ivParams.width =  DensityUtils.dp2px(getContext(), 120);
//        ivParams.height =  DensityUtils.dp2px(getContext(), 120);
//        mIvFault.requestLayout();
//
//        LinearLayout.LayoutParams btnParams = (LinearLayout.LayoutParams) mBtnRetry.getLayoutParams();
//        btnParams.height = DensityUtils.dp2px(getContext(), 44);
//        btnParams.topMargin = DensityUtils.dp2px(getContext(), 8);
//        mBtnRetry.requestLayout();
//    }
}
