package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

/**
 * 滚动到底部监听
 */

public class ScrollBottomScrollView extends ScrollView {

    private OnScrollBottomListener listener;
    private int calCount;

    public ScrollBottomScrollView(Context context) {
        super(context);
    }
    public ScrollBottomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(OnScrollBottomListener l) {
        listener = l;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = this.getChildAt(0);
        if (this.getHeight() + this.getScrollY() == view.getHeight()) {
            calCount++;
            if (calCount == 1) {
                if (listener != null) {
                    listener.srollToBottom();
                }
            }
            AppLog.d(" bbbbbb " + calCount );
        } else {
            calCount = 0;
        }
    }

    //接口
    interface OnScrollBottomListener {
        void srollToBottom();
    }
}