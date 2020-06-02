package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


public class SortView extends LinearLayout implements OnClickListener {

    private String Tag = "SortView";
    private Context mContext;

    private LinearLayout mLlUploadTime;
    private TextView     mTvUploadTime;
    private ImageView    mIvUploadTime;
    private LinearLayout mLlWrongCount;
    private TextView     mTvWrongCount;
    private ImageView    mIvWrongCount;

    private int        mCurPosition;
    private SortSelect mSortSelect;


    public SortView(Context context) {
        this(context, null);
    }

    public SortView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()) {
            View.inflate(context, R.layout.view_sort, this);
        }else {
            View.inflate(context, R.layout.view_sort_phone, this);
        }
        mContext = context;
        initView();
    }


    private void initView() {
        mLlUploadTime = (LinearLayout) findViewById(R.id.ll_upload_time);
        mTvUploadTime = (TextView) findViewById(R.id.tv_upload_time);
        mIvUploadTime = (ImageView) findViewById(R.id.iv_upload_time);
        mLlWrongCount = (LinearLayout) findViewById(R.id.ll_wrong_count);
        mTvWrongCount = (TextView) findViewById(R.id.tv_wrong_count);
        mIvWrongCount = (ImageView) findViewById(R.id.iv_wrong_count);
        mTvUploadTime.setActivated(true);
        mIvUploadTime.setImageResource(R.drawable.ic_arrow1);
        mLlUploadTime.setOnClickListener(this);
        mLlWrongCount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_upload_time:
                choose(0);
                break;
            case R.id.ll_wrong_count:
                choose(1);
                break;
            default:
                break;
        }
    }

    private void choose(int kind) {
        if (kind == 0) {
            if (mCurPosition == 0) {
                mCurPosition = 1;
            } else {
                mCurPosition = 0;
            }
        } else {
            if (mCurPosition == 2) {
                mCurPosition = 3;
            } else {
                mCurPosition = 2;
            }
        }
        updateView();
        if (mSortSelect != null) {
            mSortSelect.select(mCurPosition);
        }
    }

    private void updateView() {
        if (mCurPosition == 0) {
            mTvUploadTime.setActivated(true);
            mTvWrongCount.setActivated(false);
            mIvUploadTime.setImageResource(R.drawable.ic_arrow1);
            mIvWrongCount.setImageResource(R.drawable.ic_arrow3);
        } else if (mCurPosition == 1) {
            mTvUploadTime.setActivated(true);
            mTvWrongCount.setActivated(false);
            mIvUploadTime.setImageResource(R.drawable.ic_arrow2);
            mIvWrongCount.setImageResource(R.drawable.ic_arrow3);
        } else if (mCurPosition == 2) {
            mTvUploadTime.setActivated(false);
            mTvWrongCount.setActivated(true);
            mIvUploadTime.setImageResource(R.drawable.ic_arrow3);
            mIvWrongCount.setImageResource(R.drawable.ic_arrow1);
        } else {
            mTvUploadTime.setActivated(false);
            mTvWrongCount.setActivated(true);
            mIvUploadTime.setImageResource(R.drawable.ic_arrow3);
            mIvWrongCount.setImageResource(R.drawable.ic_arrow2);
        }
    }

    public void setSortSelect(SortSelect sortSelect) {
        mSortSelect = sortSelect;
    }

    public void setIndex(int i) {
        mCurPosition = i;
        updateView();
    }

    public interface SortSelect {
        void select(int postion);
    }
}
