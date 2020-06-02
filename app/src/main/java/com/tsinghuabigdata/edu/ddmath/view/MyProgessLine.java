package com.tsinghuabigdata.edu.ddmath.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * 自定义进度
 *
 * @author Rock Lee
 * @date 2016年4月18日
 */
public class MyProgessLine extends View {

    //需要执行动画的参数名
    private static final String PROGRESS_PROPERTY = "progress";

    private Paint paint;// 画笔
    private Paint dividePaint;// 画笔

    RectF inRectF;
    RectF outRectF;

    private int         color;// 进度条颜色
    private float       bottom;// 进度条底部
    private AnimatorSet mAnimatorSet;


    protected float progress;
    protected float progressWith;

    public void setColor(int color) {
        this.color = color;
    }

    public MyProgessLine(Context context) {
        this(context, null);
    }

    public MyProgessLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgessLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        paint = new Paint();
        dividePaint = new Paint();
        inRectF = new RectF();
        outRectF = new RectF();

        TypedArray mTypedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.MyProgressLine, defStyleAttr, 0);

        color = mTypedArray.getColor(R.styleable.MyProgressLine_progress_color,
                Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bottom = getHeight() - getPaddingBottom();
        //        Log.i("sky", "bottom=" + bottom);
        //        Log.i("sky", "getPaddingTop()=" + getPaddingTop());
        //        paint.setColor(0xD7D7D7);
        //        paint.setColor(0x33000000);
        paint.setColor(getResources().getColor(R.color.color_D7D7D7));
        paint.setStrokeCap(Paint.Cap.SQUARE);// 圆角
        // paint.setStyle(Paint.Style.FILL); // 设置实心
        paint.setStrokeWidth(bottom); // 设置笔画的宽度
        paint.setAntiAlias(true); // 消除锯齿

        dividePaint.setColor(getResources().getColor(R.color.color_018ABB));
        dividePaint.setStrokeCap(Paint.Cap.SQUARE);// 圆角
        // paint.setStyle(Paint.Style.FILL); // 设置实心
        dividePaint.setStrokeWidth(bottom); // 设置笔画的宽度
        dividePaint.setAntiAlias(true); // 消除锯齿
        outRectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), bottom);
        int divdie = DensityUtils.dp2px(getContext(), 1);
        inRectF.set(getPaddingLeft() + divdie, getPaddingTop() + divdie, getWidth() - getPaddingRight() - divdie, bottom - divdie);
        int radius;
        if (GlobalData.isPad()) {
            radius = DensityUtils.dp2px(getContext(), 100);
        } else {
            radius = DensityUtils.dp2px(getContext(), 70);
        }
        canvas.drawRoundRect(outRectF, radius, radius, dividePaint);
        canvas.drawRoundRect(inRectF, radius, radius, paint);

        //        paint.setColor(0x00CCFC);
        //        paint.setColor(color);
        paint.setColor(getResources().getColor(R.color.color_00CCFC));
        paint.setStrokeWidth(bottom); // 设置笔画的宽度
        outRectF.set(getPaddingLeft(), getPaddingTop(), progressWith + getPaddingLeft(), bottom);
        inRectF.set(getPaddingLeft() + divdie, getPaddingTop() + divdie, progressWith + getPaddingLeft() - divdie, bottom - divdie);
        canvas.drawRoundRect(outRectF, radius, radius, dividePaint);
        canvas.drawRoundRect(inRectF, radius, radius, paint);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        this.progressWith = progress * (getWidth() - getPaddingLeft() - getPaddingRight());
        invalidate();
    }

    /**
     * 赋值+执行动画
     */
    public void dodo(float start, float end, int time) {
        this.progress = end;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        mAnimatorSet = new AnimatorSet();
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(this, PROGRESS_PROPERTY,
                start, progress);
        progressAnimation.setDuration(time);//动画耗时
        progressAnimation.setInterpolator(new LinearInterpolator());

        mAnimatorSet.playTogether(progressAnimation);
        mAnimatorSet.start();
    }

    public void end() {
        if (mAnimatorSet != null) {
            mAnimatorSet.end();
        }
    }

    public void cancel() {
        if (mAnimatorSet != null) {
            mAnimatorSet.cancel();
        }
    }

    /**
     * 赋值+执行动画
     */
    public void dodo(float start, float end, long time, Animator.AnimatorListener animatorListener) {
        this.progress = end;
        if (progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        mAnimatorSet = new AnimatorSet();
        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(this, PROGRESS_PROPERTY,
                start, progress);
        progressAnimation.setDuration(time);//动画耗时
        progressAnimation.setInterpolator(new LinearInterpolator());

        mAnimatorSet.playTogether(progressAnimation);
        if (animatorListener != null) {
            mAnimatorSet.addListener(animatorListener);
        }
        mAnimatorSet.start();
    }


}