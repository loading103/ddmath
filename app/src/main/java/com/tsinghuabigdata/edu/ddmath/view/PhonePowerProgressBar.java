package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;


/**
 * 我的学力仪表盘 手机版
 * Created by Administrator on 2017/9/7.
 */

public class PhonePowerProgressBar extends View {

    private static final String TAG = "MyView";

    /**
     * 矩形宽度 测试使用
     */
    private float mRectWidth = dp2px(4);

    private int[] colors = {0xffFFB545, 0xffF8F711, 0xff4DFF48, 0xffFFB545};
    //    private int[] colors = {0xff000000, 0xffF8F711, 0xff4DFF48, 0xffffffff};

    private Paint[] mArcPaints = new Paint[3];
    private Paint mRectPaint;
    private Paint mFirstRectPaint;
    private Paint mSecondRectPaint;
    private Paint mThirdRectPaint;
    private Paint mFourthRectPaint;
    private Paint mFifthRectPaint;
    private Paint mWhiteRectPaint;

    private RectF mArcRect;
    private RectF mFirstArcRect;
    private RectF mSecondArcRect;
    private RectF mThirdRArcRect;
    private RectF mFourthArcRect;
    private RectF mFifthArcRect;
    private RectF mWhiteArcRect;

    private RectF mTextRect1;
    private RectF mTextRect2;

    private float mProgress;
    private float mArcCenterX;
    private float mArcCenterY;
    private float mArcRadius; // 圆弧半径

    private Paint mTextPaint1;
    private Paint mTextPaint2;
    private Paint mTextPaint3;
    private Paint mTextPaint4;

    private int curValue;
    private int startValue = 0;
    private int endValue   = 9999;

    public PhonePowerProgressBar(Context context) {
        this(context, null, 0);
    }

    public PhonePowerProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhonePowerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiAttributes(context, attrs);
        initView();
    }

    private void intiAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PowerProgress);
        //        mArcBgColor = a.getColor(R.styleable.ArcProgressBar_arcBgColor, mArcBgColor);
        //        mDottedDefaultColor = a.getColor(R.styleable.ArcProgressBar_dottedDefaultColor, mDottedDefaultColor);
        //        mDottedRunColor = a.getColor(R.styleable.ArcProgressBar_dottedRunColor, mDottedRunColor);
        a.recycle();
    }

    private void initView() {
        // 外层矩形的画笔
        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(true);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(mRectWidth);
        mRectPaint.setColor(Color.WHITE);
        mRectPaint.setStrokeCap(Paint.Cap.ROUND);

        // 最外层白色画笔
        mWhiteRectPaint = new Paint();
        mWhiteRectPaint.setAntiAlias(true);
        mWhiteRectPaint.setStyle(Paint.Style.STROKE);
        mWhiteRectPaint.setStrokeWidth(dp2px(35));
        mWhiteRectPaint.setColor(Color.argb(97, 255, 255, 255));
        mWhiteRectPaint.setStrokeCap(Paint.Cap.ROUND);

        // 紫色弧形的画笔
        mFirstRectPaint = new Paint();
        mFirstRectPaint.setAntiAlias(true);
        mFirstRectPaint.setStyle(Paint.Style.STROKE);
        mFirstRectPaint.setStrokeWidth(dp2px(16));
        mFirstRectPaint.setColor(getResources().getColor(R.color.color_E2E2EB));
        mFirstRectPaint.setStrokeCap(Paint.Cap.ROUND);

        // 白色弧形的画笔
        mSecondRectPaint = new Paint();
        mSecondRectPaint.setAntiAlias(true);
        mSecondRectPaint.setStyle(Paint.Style.STROKE);
        mSecondRectPaint.setStrokeWidth(dp2px(14));
        mSecondRectPaint.setColor(getResources().getColor(R.color.color_FFFEF0));
        mSecondRectPaint.setStrokeCap(Paint.Cap.ROUND);

        // 渐变弧形的画笔
        mThirdRectPaint = new Paint();
        mThirdRectPaint.setAntiAlias(true);
        mThirdRectPaint.setStyle(Paint.Style.STROKE);
        mThirdRectPaint.setStrokeWidth(dp2px(10));
        mThirdRectPaint.setStrokeCap(Paint.Cap.ROUND);

        // 小指针画笔
        mFourthRectPaint = new Paint();
        mFourthRectPaint.setAntiAlias(true);
        mFourthRectPaint.setStyle(Paint.Style.STROKE);
        mFourthRectPaint.setStrokeWidth(dp2px(4));
        mFourthRectPaint.setColor(getResources().getColor(R.color.color_979797));

        // 大指针画笔
        mFifthRectPaint = new Paint();
        mFifthRectPaint.setAntiAlias(true);
        mFifthRectPaint.setStyle(Paint.Style.STROKE);
        mFifthRectPaint.setStrokeWidth(dp2px(10));
        mFifthRectPaint.setColor(getResources().getColor(R.color.color_6B749B));

        mTextPaint1 = new Paint();
        mTextPaint1.setAntiAlias(true);
        mTextPaint1.setTextSize(dp2px(8));
        mTextPaint1.setColor(getResources().getColor(R.color.color_666666));

        mTextPaint2 = new Paint();
        mTextPaint2.setAntiAlias(true);
        mTextPaint2.setTextSize(dp2px(8));
        mTextPaint2.setColor(getResources().getColor(R.color.color_666666));

        mTextPaint3 = new Paint();
        mTextPaint3.setAntiAlias(true);
        mTextPaint3.setTextSize(dp2px(14));
        mTextPaint3.setColor(Color.parseColor("#555555"));

        mTextPaint4 = new Paint();
        mTextPaint4.setAntiAlias(true);
        mTextPaint4.setTextSize(dp2px(40));
        mTextPaint4.setColor(getResources().getColor(R.color.color_FF9D00));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        LogUtils.i(TAG, "w=" + w + "h=" + h + "oldw=" + oldw + "oldh=" + oldh);
        super.onSizeChanged(w, h, oldw, oldh);
        float maxHeight = h - dp2px(10);
        float maxWidth = w * 300f / 350;
        LogUtils.i(TAG, "maxHeighth=" + maxHeight + " maxWidth=" + maxWidth);
        float allWidth;
        if (maxHeight / maxWidth > 0.5) {
            allWidth = maxWidth;
        } else {
            allWidth = 2 * maxHeight;
        }
        mArcRadius = allWidth / 2;
        LogUtils.i(TAG, "allWidth=" + allWidth + "mArcRadius=" + mArcRadius);
        mArcCenterX = w / 2f;
        mArcCenterY = allWidth / 2f;
        LogUtils.i(TAG, "mArcCenterX=" + mArcCenterX + "mArcCenterY=" + mArcCenterY);

        mArcRect = new RectF();
        float distance = mArcRadius - dp2px(0);
        mArcRect.top = mArcCenterY - distance;
        mArcRect.left = mArcCenterX - distance;
        mArcRect.right = mArcCenterX + distance;
        mArcRect.bottom = mArcCenterY + distance;
        LogUtils.i(TAG, "top=" + mArcRect.top + "bottom  =" + mArcRect.bottom);
        LogUtils.i(TAG, "left=" + mArcRect.left + "right  =" + mArcRect.right);
        //mArcRect.inset(mArcWidth / 2, mArcWidth / 2);

        mWhiteArcRect = new RectF();
        float distance7 = mArcRadius - dp2px(32);
        mWhiteArcRect.top = mArcCenterY - distance7;
        mWhiteArcRect.left = mArcCenterX - distance7;
        mWhiteArcRect.right = mArcCenterX + distance7;
        mWhiteArcRect.bottom = mArcCenterY + distance7;

        mFirstArcRect = new RectF();
        float distance1 = mArcRadius - dp2px(32);
        mFirstArcRect.top = mArcCenterY - distance1;
        mFirstArcRect.left = mArcCenterX - distance1;
        mFirstArcRect.right = mArcCenterX + distance1;
        mFirstArcRect.bottom = mArcCenterY + distance1;

        mSecondArcRect = new RectF();
        float distance2 = mArcRadius - dp2px(32);
        mSecondArcRect.top = mArcCenterY - distance2;
        mSecondArcRect.left = mArcCenterX - distance2;
        mSecondArcRect.right = mArcCenterX + distance2;
        mSecondArcRect.bottom = mArcCenterY + distance2;

        mThirdRArcRect = new RectF();
        float distance3 = mArcRadius - dp2px(32);
        mThirdRArcRect.top = mArcCenterY - distance3;
        mThirdRArcRect.left = mArcCenterX - distance3;
        mThirdRArcRect.right = mArcCenterX + distance3;
        mThirdRArcRect.bottom = mArcCenterY + distance3;

        mFourthArcRect = new RectF();
        float distance4 = mArcRadius - dp2px(18);
        mFourthArcRect.top = mArcCenterY - distance4;
        mFourthArcRect.left = mArcCenterX - distance4;
        mFourthArcRect.right = mArcCenterX + distance4;
        mFourthArcRect.bottom = mArcCenterY + distance4;

        mFifthArcRect = new RectF();
        float distance5 = mArcRadius - dp2px(17);
        mFifthArcRect.top = mArcCenterY - distance5;
        mFifthArcRect.left = mArcCenterX - distance5;
        mFifthArcRect.right = mArcCenterX + distance5;
        mFifthArcRect.bottom = mArcCenterY + distance5;

        mTextRect1 = new RectF();
        float textDis1 = mArcRadius - dp2px(10);
        mTextRect1.top = mArcCenterY - textDis1;
        mTextRect1.left = mArcCenterX - textDis1;
        mTextRect1.right = mArcCenterX + textDis1;
        mTextRect1.bottom = mArcCenterY + textDis1;

        mTextRect2 = new RectF();
        float textDis2 = mArcRadius - dp2px(12);
        mTextRect2.top = mArcCenterY - textDis2;
        mTextRect2.left = mArcCenterX - textDis2;
        mTextRect2.right = mArcCenterX + textDis2;
        mTextRect2.bottom = mArcCenterY + textDis2;

        float position[] = {0, 0.25f, 0.5f, 0.8f};
        SweepGradient sweepGradient = new SweepGradient(mArcCenterX, mArcCenterY, colors, position);
        Matrix matrix = new Matrix();
        matrix.setRotate(180, mArcCenterX, mArcCenterY);
        sweepGradient.setLocalMatrix(matrix);
        //        SweepGradient sweepGradient = new SweepGradient(radius, radius, new int[]{Color.parseColor("#ff0000"), Color.parseColor("#00ff00")}, null);
        mThirdRectPaint.setShader(sweepGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //LogUtils.i(TAG, "mProgress=" + mProgress);
        //最外层白色边框 测试使用
        //canvas.drawArc(mArcRect, 180, 180, false, mRectPaint);
        // 白色圆环
        canvas.drawArc(mWhiteArcRect, 180, 180, false, mWhiteRectPaint);
        //紫色圆环
        canvas.drawArc(mFirstArcRect, 180, 180, false, mFirstRectPaint);
        //白色圆环
        canvas.drawArc(mSecondArcRect, 180, 180, false, mSecondRectPaint);
        //彩色圆环
        //float progress = curValue * 180 / 10000;
        float progress = (float) (curValue - startValue) / (endValue - startValue);
        LogUtils.i(TAG, "progress=" + progress);
        if (progress > 0 && progress <= 1) {
            canvas.drawArc(mThirdRArcRect, 180, progress * 180, false, mThirdRectPaint);
        }
        int p = 180;
        //画小指针
        for (int i = 0; i < p + 1; i++) {
            canvas.drawArc(mFourthArcRect, 180 + i - 0.15f, 0.3f, false, mFourthRectPaint); // 绘制间隔快
        }
        //画大指针
        for (int i = 0; i < 5; i++) {
            canvas.drawArc(mFifthArcRect, 180 + i * 45 - 0.5f, 1, false, mFifthRectPaint); // 绘制间隔快
        }
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        //画数值
        float everyDegrees = 45;
        float startDegress = 180;
        float value = (float) (endValue - startValue);
        float count = 4;
        float single = value / count;
        for (int i = 0; i < count + 1; i++) {
            float degrees = startDegress + i * everyDegrees;
            int stageValue = Math.round(startValue + i * single);
            float textWidth = mTextPaint1.measureText(stageValue + "");
            float angle = (float) ((textWidth * 180) / (Math.PI * mArcRadius));
            Path path = new Path();
            path.addArc(mTextRect1, degrees - angle / 2, everyDegrees);//一个弧线的路径
            canvas.drawTextOnPath(stageValue + "", path, 0, 0, mTextPaint1);
        }

        float textEveryDegrees = 45;
        float textStartDegress = 202.5f;

        String[] stages = {"起步", "初燃", "全速", "MAX"};
        for (int i = 0; i < 4; i++) {
            float degrees = textStartDegress + i * textEveryDegrees;
            String str = stages[i];
            float textWidth = mTextPaint2.measureText(str);
            float angle = (float) ((textWidth * 180) / (Math.PI * mArcRadius));
            Path path = new Path();
            path.addArc(mTextRect2, degrees - angle / 2, everyDegrees);//一个弧线的路径
            canvas.drawTextOnPath(str, path, 0, 0, mTextPaint2);
        }


        String title = "总学力";
        float titleWidth = mTextPaint3.measureText(title);
        canvas.drawText(title, mArcCenterX - titleWidth / 2, mArcCenterY - dp2px(40), mTextPaint3);

        String power = curValue + "";
        float textWidth = mTextPaint4.measureText(power);
        canvas.drawText(power, mArcCenterX - textWidth / 2, mArcCenterY, mTextPaint4);
    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }


    public void setStartValue(int value) {
        startValue = value;
    }

    public void setEndValue(int value) {
        endValue = value;
        if (value <= startValue) {
            endValue = startValue = 100;
        }
    }

    public void setPower(int value) {
        //LogUtils.i(TAG, "value=" + value + "startValue=" + startValue + "endValue=" + endValue);
        curValue = value;
        invalidate();
    }
}
