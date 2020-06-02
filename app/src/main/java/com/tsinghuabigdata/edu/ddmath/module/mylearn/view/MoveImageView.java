package com.tsinghuabigdata.edu.ddmath.module.mylearn.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;


/**
 * 移动匹配图
 */
@SuppressLint("AppCompatCustomView")
public class MoveImageView extends ImageView {

    private static final int MSG_FADEIN = 2;
    private static final int MSG_FADEOUT = 3;
    private static final int MSG_MOVE = 4;
    private static final int MSG_JUMP = 5;
    private static final int MSG_SCALE = 6;

    //动画时间 ms
    private static final int animate_time = 500;

    //显示的初始位置
    private float initailX = 0;
    private float initailY = 0;


    public MoveImageView(Context context) {
        super(context);
        initialize();

    }

    public MoveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MoveImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MoveImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    //-------------------------------------------------------

    public void setInitailPostion(){
        this.initailX = getX();
        this.initailY = getY();
    }
    public void setInitailPostion( int x, int y ){
        this.initailX = x;
        this.initailY = y;
        setX( x );
        setY( y );
    }

    //-------------------------------------------------------
    private void initialize() {
//        instance = this;
        //mLongClickMonitor = new LongClickMonitor();
    }


    /* 设置图片 显示位置,移动图片时使用
    * @param: x
    * @param: y
    */
    public boolean setXY( float dx, float dy ){
        setX(getX() + dx);
        setY(getY() + dy);
        return true;
    }

    /**
     * 淡入的动画
     */
    public void showFadeIn() {
        AlphaAnimation anim = new AlphaAnimation(0, 1);
        anim.setDuration( animate_time );
        anim.setFillAfter(true);
        startAnimation(anim);
        anim.setAnimationListener( new MyAnimationListener(MSG_FADEIN) );
    }

    /**
     * 淡出的动画
     */
    public void showFadeOut() {
        AlphaAnimation anim = new AlphaAnimation(1, 0);
        anim.setDuration( animate_time );
        anim.setFillAfter(true);
        startAnimation(anim);
        anim.setAnimationListener( new MyAnimationListener(MSG_FADEOUT) );
    }

    /**
     * 回到初始位置动画
     */
    public void showMoveInitailPos(){
        TranslateAnimation anim = new TranslateAnimation( 0, initailX-getX(), 0, initailY-getY() );
        anim.setDuration( animate_time );
        //设置动画结束后保留结束状态
        anim.setFillAfter( true );
        anim.setFillBefore( false );
        anim.setAnimationListener( new MyAnimationListener(MSG_MOVE) );
        startAnimation(anim);
    }

    class MyAnimationListener implements Animation.AnimationListener{

        private int type = 0;

        public MyAnimationListener( int type ){
            this.type = type;
        }
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {

            if( type == MSG_MOVE ){
                clearAnimation();
                setX( initailX );
                setY( initailY );

            }else if( type == MSG_FADEIN ){
                clearAnimation();
            }else if( type == MSG_FADEOUT ){
                clearAnimation();
            }else if( type == MSG_JUMP ){
                if( bJumpAnimation ){
                    orientation = !orientation;
                    startJumpAnimation();
                }
            }else if( type == MSG_SCALE ){
                clearAnimation();
                if(isShown()){
                    startZoomAndFadeOut();
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    //----------------------------------------------------------------------------------------------------------
    //跳动动画
    private boolean orientation = false;        //false: 下--》上   true：上-->下
    private boolean bJumpAnimation = false;
    public void startJumpAnimation(){

        bJumpAnimation = true;
        float rate = 0.16f;
        float originalY = 0;
        float finalY = 0;
        if( orientation ){
            originalY = getHeight()*rate;
            finalY    = 0 - getHeight()*rate;
        }else{
            originalY = 0 - getHeight()*rate;
            finalY    = getHeight()*rate;
        }
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new TranslateAnimation(0, 0, originalY, finalY));

        animationSet.setDuration(800);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new MyAnimationListener(MSG_JUMP));
        startAnimation( animationSet );
    }
    public void stopJumpAnimation(){
        bJumpAnimation = false;
    }

    //--------------------------------------------------------------------------------------------------------
    //缩小 淡出  相机焦点效果
    public void setMoveMargin( float left, float top ){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)getLayoutParams();
        layoutParams.leftMargin = (int)left;
        layoutParams.topMargin = (int)top;
        setLayoutParams( layoutParams );
    }
    public void startZoomAndFadeOut(){

//        int screenWidth = WindowUtils.getScreenWidth( getContext() );
//        int screenHeight = WindowUtils.getScreenHeight( getContext() );


        ScaleAnimation anim = new ScaleAnimation( 1, 0.5f, 1, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f );  //getX(), getX()+getWidth()/2, getY(), getY()+getHeight()/2 );
//        float centerx = (getX()+getWidth()/2)*1f / screenWidth;
//        float centery = (getY()+getHeight()/2)*1f / screenHeight;
//
//        AppLog.d("ddjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj   centerx = " + centerx + ",,centerx = " + centery );

        //ScaleAnimation anim = new ScaleAnimation( 1, 0.3f, 1, 0.3f, Animation.ABSOLUTE, -getX(), Animation.ABSOLUTE,-getY() );  // );
        anim.setDuration( 300 );
        anim.setFillAfter(true);
        anim.setAnimationListener( new MyAnimationListener(MSG_SCALE) );
        startAnimation(anim);
    }
    public void sopZoomAndFadeOut(){
        //stop
        setVisibility( GONE );
    }

}
