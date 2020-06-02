package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 知识诊断表盘
 */
public class KnowDiagnoseDialView extends View{

    private int DISTANCE = 20;     //弧形距离边框的距离

    private Paint mPaint = new Paint();         // 覆盖画笔
    private Paint arcPaint = new Paint();       // 覆盖画笔

    //背景区域
    private Rect bgSrcRect;     //背景区域范围
    private Rect bgDstRect;     //背景目标区域范围

    private RectF arcRect;   //圆弧范围

    //指针区域
    private Rect pointerSrcRect = new Rect();
    private Rect pointerDstRect = new Rect();

    private Bitmap bgDialBitmap; // 背景表盘
    private Bitmap indicatorBitmap; //指针

    private float mRateValue;

    //动画
    private boolean bAnimating = false;
    private float animateValue;
    private float animateStep;

    public KnowDiagnoseDialView(Context context) {
        super(context);
        initView();
    }

    public KnowDiagnoseDialView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public KnowDiagnoseDialView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if( visibility == 0 ){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnimate();
                }
            },500);  //延时
        }
    }

    public void setKnowledgeRate(float rate ){
        mRateValue = rate;
    }

    //----------------------------------------------------------------------------
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化可视范围及框体大小
        bgDstRect.set(0, 0, right-left, bottom-top);
        arcRect.set( DISTANCE, DISTANCE, right-left-DISTANCE, bottom-top-DISTANCE);

        //动态计算目标大小
        int src_width = indicatorBitmap.getWidth(), src_height = indicatorBitmap.getHeight();
        int dst_height = bgDstRect.centerY() - DISTANCE;
        int dst_width = dst_height * src_width / src_height;
        pointerDstRect.set( bgDstRect.centerX()-dst_width/2, DISTANCE, bgDstRect.centerX()+dst_width/2, bgDstRect.centerY());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        float value = mRateValue;
        if( bAnimating ){
            value = animateValue;
        }
        //先绘制背景
        canvas.drawBitmap( bgDialBitmap, bgSrcRect, bgDstRect, mPaint);

        //绘制半圆背景  0度从水平向右开始
        float sweepAngle = value * 240;
        float startAngle = 150f;
        canvas.drawArc( arcRect, startAngle, sweepAngle, true, arcPaint );

        //绘制指针
        float cx = bgDstRect.centerX();
        float cy = bgDstRect.centerY();
        canvas.drawCircle( cx, cy, bgDstRect.width()/4, mPaint );

        //计算旋转的角度
        float angle = startAngle+sweepAngle+90;     //0度从竖直向上开始
        canvas.rotate( angle, cx, cy );
        canvas.drawBitmap( indicatorBitmap, pointerSrcRect, pointerDstRect, mPaint );

        canvas.restore();
    }
    //----------------------------------------------------------------------------------------------

    void initView() {

        bgDialBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.biaopan);
        indicatorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zhizhen);

        mPaint.setColor( Color.WHITE );
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        arcPaint.setColor( Color.argb( 0xC0, 0xFF, 0xED, 0x07 ) );
        arcPaint.setAntiAlias(true);//使用抗锯齿功能
        arcPaint.setStyle(Paint.Style.FILL);//设置画笔类型为填充

        bgDstRect = new Rect();
        arcRect = new RectF();

        pointerSrcRect.set( 0, 0, indicatorBitmap.getWidth(), indicatorBitmap.getHeight() );
        bgSrcRect = new Rect( 0, 0, bgDialBitmap.getWidth(), bgDialBitmap.getHeight() );

        DISTANCE = GlobalData.isPad()?24:10;
    }

    //开始动画
    private void startAnimate(){
        bAnimating = true;
        animateStep = mRateValue/10;

        //先从0到1,的满刻度动画 1s内完成
        animateValue = 0;
        startFirstAnimate();
    }

    //先从0到1,的满刻度动画 1s内完成
    private void startFirstAnimate(){
        animateValue += 0.1f;
        if( animateValue > 1.05f ){
            animateValue = 1.0f;
            startSecondAnimate();
        }else{
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startFirstAnimate();
                }
            }, 100);
        }
        invalidate();
    }
    //先从1到0,的动画 1s内完成
    private void startSecondAnimate(){
        animateValue -= 0.1f;
        if( animateValue < 0 ){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateValue = 0;
                    startThirdAnimate();
                }
            }, 100);
        }else{
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSecondAnimate();
                }
            }, 100);
        }
        invalidate();
    }

    //先从0到mRateValue,的动画 1s内完成
    private void startThirdAnimate(){
        animateValue += animateStep;
        if( animateValue < mRateValue ){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startThirdAnimate();
                }
            }, 100);
        }else{
            bAnimating = false;
        }
        invalidate();
    }
}
