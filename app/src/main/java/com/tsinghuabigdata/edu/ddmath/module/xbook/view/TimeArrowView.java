package com.tsinghuabigdata.edu.ddmath.module.xbook.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * 剪切工具View
 */
@SuppressLint("AppCompatCustomView")
public class TimeArrowView extends ImageView {


    private TimeFilterView.ArrowImageChangeListener mListener;

    public TimeArrowView(Context context) {
        super(context);
        init();
    }

    public TimeArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimeArrowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setListener( TimeFilterView.ArrowImageChangeListener listener){
        mListener = listener;
    }

    //---------------------------------------------------------------------------------------
    private void init(){

    }

    private boolean setXY( float dx, float dy ){

        setX( getX() + dx);

        if( mListener!=null ){
            mListener.positionChage();
        }
        return true;
    }


    /* 设置图片 显示位置,移动图片时使用
    * @param: x
    * @param: y
    */

    /** 用于记录开始时候的坐标位置 */
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();

    private float startx;
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getActionMasked()) {
//
//            case MotionEvent.ACTION_DOWN:{
//                startPoint.set(event.getRawX(), event.getRawY());
//                startx = event.getRawX();
//                break;
//            }
//            case MotionEvent.ACTION_MOVE:{
//
//                float dx = event.getRawX() - startPoint.x; // 得到x轴的移动距离
//                //float dy = event.getRawY() - startPoint.y; // 得到x轴的移动距离
//                //避免和双击冲突,大于10f才算是拖动
//                if(Math.abs(dx) < 10){
//                    //移动距离过小，不处理
//                    break;
//                }
//
//                if( setXY( dx, 0 ) ){
//                    startPoint.set(event.getRawX(), event.getRawY());
//                }
//            }
//            break;
//            case MotionEvent.ACTION_UP: {
//                endPoint.set( event.getX(), event.getY() );
//                if( mListener!=null ){
//                    mListener.onTouchUp( startx, event.getX() );
//                }
//                break;
//            }
//            default:
//                break;
//        }
//        return true;//super.onTouchEvent(event);
//    }

    public void moveAnimation( int startx, final int stopx ){

        TranslateAnimation anim = new TranslateAnimation( 0, stopx-startx, 0, 0 );
        anim.setDuration( 300 );
        //设置动画结束后保留结束状态
        //anim.setFillAfter( true );
        //anim.setFillBefore( false );
        anim.setAnimationListener( new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clearAnimation();
                setX( stopx );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        } );
        startAnimation(anim);

    }
}
