package com.tsinghuabigdata.edu.ddmath.module.errorbook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

/**
 * 带椭圆外框的文本
 */

public class OvalTextSpan extends ReplacementSpan {
    //private Context mContext;
    private int mBgColor = Color.BLUE; //Icon背景颜色
    private String mText; //Icon内文字
    private float mBgHeight; //Icon背景高度
    private float mBgWidth; //Icon背景宽度
    //private float mRadius; //Icon圆角半径
    //private float mRightMargin; //右边距
    private float mTextSize; //文字大小

    private int mTextColor = Color.BLUE; //文字颜色
    private Paint mBgPaint; //icon背景画笔
    private Paint mTextPaint; //icon文字画笔

    /*public*/ OvalTextSpan(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        //初始化默认数值
        initDefaultValue(context, text);
        //计算背景的宽度
        this.mBgWidth = caculateBgWidth(text);
        //初始化画笔
        initPaint();
    }

    /**
     * 设置宽度，宽度=背景宽度+右边距
     */
    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        return (int) (mBgWidth);
    }

    /**
     * draw
     *
     * @param text   完整文本
     * @param start  setSpan里设置的start
     * @param end    setSpan里设置的start
     * @param x      开始位置
     * @param top    当前span所在行的上方y
     * @param y      y其实就是metric里baseline的位置
     * @param bottom 当前span所在行的下方y(包含了行间距)，会和下一行的top重合
     * @param paint  使用此span的画笔
     */
    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

        //画背景
        //mBgPaint.setColor(mBgColor);
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        //算出背景开始画的y坐标
        float bgStartY = y + (textHeight - mBgHeight) / 2 + metrics.ascent;

        //画背景
        RectF bgRect = new RectF(x, bgStartY, x + mBgWidth, bgStartY + mBgHeight);
        canvas.drawRoundRect(bgRect, mBgHeight/2, mBgHeight/2, mBgPaint);

        //把字画在背景中间
        //mTextPaint.setColor(mTextColor);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float textRectHeight = fontMetrics.bottom - fontMetrics.top;
        canvas.drawText(mText, x + mBgWidth / 2, bgStartY + (mBgHeight - textRectHeight) / 2 - fontMetrics.top, mTextPaint);
    }

    public void setColor( int bgColor, int textColor, float textSize){
        this.mBgColor = bgColor;
        this.mTextColor = textColor;
        this.mTextSize = textSize;

        this.mBgWidth = caculateBgWidth(mText);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);

        mBgPaint.setColor(bgColor);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        //初始化背景画笔
        mBgPaint = new Paint();
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStrokeWidth(4);
        //初始化文字画笔
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 初始化默认数值
     */
    private void initDefaultValue(Context context, String text) {
        Context mContext = context.getApplicationContext();
        this.mText = text;
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        this.mBgHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, displayMetrics);
        //this.mRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);
        //this.mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mContext.getResources().getDisplayMetrics());
        this.mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, displayMetrics);

        //mBgHeight += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, displayMetrics);
    }

    /**
     * 计算icon背景宽度
     * @param text icon内文字
     */
    private float caculateBgWidth(String text) {
        if (text.length() > 1) {
            //多字，宽度=文字宽度+padding
            Rect textRect = new Rect();
            Paint paint = new Paint();
            paint.setTextSize(mTextSize);
            paint.getTextBounds(text, 0, text.length(), textRect);
            //float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
            return textRect.width() + /*padding * 2 +*/ mTextSize;
        } else {
            //单字，宽高一致为正方形
            return mBgHeight;
        }
    }

//    /**
//     * 设置右边距
//     */
//    public void setRightMarginDpValue(int rightMarginDpValue) {
//        this.mRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightMarginDpValue, mContext.getResources().getDisplayMetrics());
//    }


}

