package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;

/**
 * Created by Administrator on 2018/4/25.
 */

public class MyClickableSpan extends ClickableSpan {

    Context              mContext;
    View.OnClickListener mOnClickListener;

    public MyClickableSpan(String str, Context context, View.OnClickListener onClickListener){
        super();
        this.mContext = context;
        mOnClickListener = onClickListener;
    }


    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mContext.getResources().getColor(R.color.color_1BD9DA));
    }


    @Override
    public void onClick(View widget) {
        mOnClickListener.onClick(widget);
    }

}
