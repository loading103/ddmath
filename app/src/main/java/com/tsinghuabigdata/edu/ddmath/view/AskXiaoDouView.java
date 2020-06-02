package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * Created by Administrator on 2017/9/7.
 */

public class AskXiaoDouView extends LinearLayout {

    private TextView mTvPeopleCount;

    public AskXiaoDouView(Context context) {
        this(context, null);
    }

    public AskXiaoDouView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, GlobalData.isPad()?R.layout.v_ask_xiaodou:R.layout.v_ask_xiaodou_phone, this);
        initView();
    }

    private void initView() {
        mTvPeopleCount = (TextView) findViewById(R.id.tv_people_count);
    }

    public void setCount(CharSequence text) {
        mTvPeopleCount.setText(text);
    }

    public void setCount(int count) {
        mTvPeopleCount.setText(String.valueOf(count));
    }

}
