package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 *
 */

public class MyRelativeLayout extends RelativeLayout {
    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean intercept = false;
    public void setInterceptEvent(boolean b){
        intercept = b;
    }
    //把所有事件都消费了
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return intercept || super.onTouchEvent( event );
    }
}
