/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.GridView;

import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

/**
 * Created by yanshen on 2016/3/31.
 */
public class MultiGridView extends GridView {

    //解决多层嵌套的事件冲突问题
    private AbsListView mParentView;

    public MultiGridView(Context context) {
        super(context);
    }

    public MultiGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);// 注意这里,这里的意思是直接测量出GridView的高度
    }

    public void setParentView( AbsListView parentView ){
        mParentView = parentView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtils.i("w=" + w);
    }

    //    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch(ev.getAction()){
//
//            case MotionEvent.ACTION_DOWN:{
//
//                if( mParentView!=null ) mParentView.requestDisallowInterceptTouchEvent(true); //不允许ScrollView拦截事件
//
//                break;
//            }
//            case MotionEvent.ACTION_MOVE:{
//                int moveX = (int)ev.getX();
//                int moveY = (int) ev.getY();
//
//                //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
////                if(!isTouchInItem(mStartDragItemView, moveX, moveY)){
////                    mHandler.removeCallbacks(mLongClickRunnable);
////                    //    if( mParentView!=null ) mParentView.requestDisallowInterceptTouchEvent(false); //不允许ScrollView拦截事件
////                }
//                break;
//            }
//            case MotionEvent.ACTION_UP:{
//
//                if( mParentView!=null ) mParentView.requestDisallowInterceptTouchEvent(false); //不允许ScrollView拦截事件
//                break;
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
}
