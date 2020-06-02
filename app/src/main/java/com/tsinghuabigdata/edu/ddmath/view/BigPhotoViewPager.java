package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

/**
 * Created by Bodyplus on 2016/4/18.
 * <p/>
 * <p/>
 * 解决  photoview 与viewpager 组合时 图片缩放的错误 ；异常：.IllegalArgumentException: pointerIndex out of range
 */
public class BigPhotoViewPager extends ViewPager {


    public BigPhotoViewPager(Context context) {
        super(context);
    }

    public BigPhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            AppLog.i( "", e );
        } catch (ArrayIndexOutOfBoundsException e) {
            AppLog.i( "", e );
        }
        return false;
    }

}