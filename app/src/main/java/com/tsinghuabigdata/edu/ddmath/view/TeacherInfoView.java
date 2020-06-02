package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

public class TeacherInfoView extends LinearLayout {

    //
    private CircleImageView headimg;
    private TextView tvName;
    private Context mContext;

    public TeacherInfoView(Context context) {
        super(context);
        initialize(context);
    }

    public TeacherInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public TeacherInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    /**
     * 初始化界面元素
     */
    private void initialize(Context context) {
        mContext = context;
        if (GlobalData.isPad()) {
            inflate(getContext(), R.layout.layout_teacherinfo, this);
        } else {
            inflate(getContext(), R.layout.layout_teacherinfo_mobile, this);
        }

        headimg = (CircleImageView) findViewById(R.id.cir_teacherimg);
        tvName = (TextView) findViewById(R.id.tv_name);
    }

    public TeacherInfoView setImg(int resid) {
        headimg.setImageResource(resid);
        return this;
    }

    public TeacherInfoView setImg(String headImage) {
        HeadImageUtils.setHeadImage(headimg, headImage, R.drawable.img_teacher);
        return this;
    }

    public TeacherInfoView setText(String s) {
        tvName.setText(s);
        return this;
    }
}
