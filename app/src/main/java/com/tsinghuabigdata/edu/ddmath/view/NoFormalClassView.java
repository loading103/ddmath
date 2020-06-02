package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * 未加入正式班级
 * Created by Administrator on 2016/12/23.
 */

public class NoFormalClassView extends LinearLayout implements View.OnClickListener {


    private TextView mTvNotJion;
    private TextView mTextView;


    public NoFormalClassView(Context context) {
        this(context, null);
    }

    public NoFormalClassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()){
            View.inflate(context, R.layout.view_no_class, this);
        }else {
            View.inflate(context, R.layout.view_no_class_phone, this);
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
        mTvNotJion = (TextView) findViewById(R.id.tv_not_join);
        mTextView = (TextView) findViewById(R.id.tv_join_class);
        mTvNotJion.setText("你还没有加入正式班级哦!");
        mTextView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        AccountUtils.checkJionSchoolClass(getContext());
    }


}
