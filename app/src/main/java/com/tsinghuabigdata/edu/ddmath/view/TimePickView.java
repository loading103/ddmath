package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


public class TimePickView extends LinearLayout implements OnClickListener {

    private String Tag = "TimePickView";
    private Context    mContext;
    private TimeSelect mTimeSelect;

    private int selectedPosition;

    private TextView     mTvTime1;
    private TextView     mTvTime2;
    private TextView     mTvTime3;
    private TextView     mTvTime4;
    private TextView     mTvTime5;
    private LinearLayout mLlTime;
    private int          mCurPosition;


    public TimePickView(Context context) {
        this(context, null);
    }

    public TimePickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()){
            View.inflate(context, R.layout.view_time_picker, this);
        }else {
            View.inflate(context, R.layout.view_time_picker_phone, this);
        }
        mContext = context;
        initView();
    }


    private void initView() {
        mLlTime = (LinearLayout) findViewById(R.id.ll_time);
        mTvTime1 = (TextView) findViewById(R.id.tv_time1);
        mTvTime2 = (TextView) findViewById(R.id.tv_time2);
        mTvTime3 = (TextView) findViewById(R.id.tv_time3);
        mTvTime4 = (TextView) findViewById(R.id.tv_time4);
        mTvTime5 = (TextView) findViewById(R.id.tv_time5);
        mTvTime1.setActivated(true);
        mTvTime1.setOnClickListener(this);
        mTvTime2.setOnClickListener(this);
        mTvTime3.setOnClickListener(this);
        mTvTime4.setOnClickListener(this);
        mTvTime5.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_time1:
                choose(0);
                break;
            case R.id.tv_time2:
                choose(1);
                break;
            case R.id.tv_time3:
                choose(2);
                break;
            case R.id.tv_time4:
                choose(3);
                break;
            case R.id.tv_time5:
                choose(4);
                break;
            default:
                break;
        }
    }

    private void choose(int position) {
        if (mCurPosition == position) {
            return;
        }
        for (int i = 0; i < mLlTime.getChildCount(); i++) {
            TextView textView = (TextView) mLlTime.getChildAt(i);
            textView.setActivated(position == i);
        }
        if (mTimeSelect != null) {
            mTimeSelect.select(position);
        }
        mCurPosition = position;
    }

    public void setTimeSelect(TimeSelect timeSelect) {
        mTimeSelect = timeSelect;
    }

    public void justSize(int rootWidth) {
        int width = rootWidth * 140 / (140 * 5 + 32 * 4);
        int margin = rootWidth * 32 / (140 * 5 + 32 * 4);
        for (int i = 0; i < mLlTime.getChildCount(); i++) {
            TextView textView = (TextView) mLlTime.getChildAt(i);
            LinearLayout.LayoutParams params = (LayoutParams) textView.getLayoutParams();
            params.width = width;
            if (i == 0) {
                params.leftMargin = 0;
            } else {
                params.leftMargin = margin;
            }
            textView.requestLayout();
        }
    }

    public void justPhoneSize(int rootWidth) {
        int width = rootWidth * 96 / (96 * 5 + 29 * 4);
        int margin = rootWidth * 29 / (96 * 5 + 29 * 4);
        for (int i = 0; i < mLlTime.getChildCount(); i++) {
            TextView textView = (TextView) mLlTime.getChildAt(i);
            LinearLayout.LayoutParams params = (LayoutParams) textView.getLayoutParams();
            params.width = width;
            if (i == 0) {
                params.leftMargin = 0;
            } else {
                params.leftMargin = margin;
            }
            //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    public void justPhoneSize() {
        for (int i = 0; i < mLlTime.getChildCount(); i++) {
            TextView textView = (TextView) mLlTime.getChildAt(i);
            LinearLayout.LayoutParams params = (LayoutParams) textView.getLayoutParams();
            params.width = DensityUtils.dp2px(getContext(), 100);
            if (i == 0) {
                params.leftMargin = 0;
            } else {
                params.leftMargin = DensityUtils.dp2px(getContext(), 20);
            }
            //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    public interface TimeSelect {
        void select(int postion);
    }
}
