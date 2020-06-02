package com.tsinghuabigdata.edu.ddmath.module.mycenter.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * Created by Administrator on 2017/2/13.
 */

@SuppressLint("AppCompatCustomView")
public class TagView extends ImageView {


    private Paint[] mPaints = new Paint[3];
    private Paint mTextPaint;
    /**
     * 文字颜色
     */
    private int mTextColor = Color.WHITE;
    /**
     * 文字大小
     */
    private float mTextSize;


    private int mColor1;
    private int mColor2;
    private int mColor3;
    private String mContent = "";

    public TagView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    public void setColor1(int color1) {
        mColor1 = color1;
    }

    public void setColor2(int color2) {
        mColor2 = color2;
    }

    public void setColor3(int color3) {
        mColor3 = color3;
    }

    public void setContent(String content, int color) {
        mContent = content;
        mTextPaint.setColor( color );
    }

    public void updatePaint() {
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                mPaints[i].setColor(mColor1);
            } else if (i == 1) {
                mPaints[i].setColor(mColor2);
            } else {
                mPaints[i].setColor(mColor3);
            }
        }
        invalidate();
    }

    //-------------------------------------------------------------------------------------
    private void init(Context context, AttributeSet attrs, int defStyle) {
        initPaint(context, attrs);
    }

    private void initPaint(Context context, AttributeSet attrs) {
       /* mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);*/

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagView);
        mColor1 = a.getColor(R.styleable.TagView_color1, Color.YELLOW);
        mColor2 = a.getColor(R.styleable.TagView_color1, Color.RED);
        mColor3 = a.getColor(R.styleable.TagView_color1, Color.BLUE);
        mTextSize = a.getInt(R.styleable.TagView_textSize, (int) dp2px(GlobalData.isPad()?20:16));
        a.recycle();

        for (int i = 0; i < 3; i++) {
            mPaints[i] = new Paint();
            mPaints[i].setAntiAlias(true);
            mPaints[i].setStyle(Paint.Style.FILL);
            if (i == 0) {
                mPaints[i].setColor(mColor1);
            } else if (i == 1) {
                mPaints[i].setColor(mColor2);
            } else {
                mPaints[i].setColor(mColor3);
            }
        }
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
    }

    private void drawRect(Canvas canvas) {
        int w = getWidth();
        float lineWidth = getWidth() / 20;
        float startY = w / 4f;
        float startX = w - startY;
        float startY2 = startY + lineWidth;
        float startX2 = w - startY2;
        float startY3 = startY2 + lineWidth;
        float startX3 = w - startY3;

        float startX4 = lineWidth;
        float startY4 = w - startX4;
        float startXT = lineWidth + w / 5f;
        float startYT = w - startXT;
//        Log.i(Tag, startX + " " + startX2 + " " + startX3 + " " + startX4);
//        Log.i(Tag, startY + " " + startY2 + " " + startY3 + " " + startY4);

        Path path1 = new Path();
        path1.moveTo(startX, 0);
        path1.lineTo(w, startY);
        path1.lineTo(w, startY2);
        path1.lineTo(startX2, 0);
        path1.close();
        canvas.drawPath(path1, mPaints[0]);

        Path path2 = new Path();
        path2.moveTo(startX2, 0);
        path2.lineTo(w, startY2);
        path2.lineTo(w, startY3);
        path2.lineTo(startX3, 0);
        path2.close();
        canvas.drawPath(path2, mPaints[1]);

        Path path3 = new Path();
        path3.moveTo(startX3, 0);
        path3.lineTo(w, startY3);
        path3.lineTo(w, startY4);
        path3.lineTo(startX4, 0);
        path3.close();
        canvas.drawPath(path3, mPaints[2]);

        Path path4 = new Path();
        path4.moveTo(startX4, 0);
        path4.lineTo(w, startY4);
        path4.lineTo(w, w);
        path4.lineTo(0, 0);
        path4.close();
        canvas.drawPath(path4, mPaints[0]);

        //绘制文本
        if( !TextUtils.isEmpty(mContent)){
            Path textPath = new Path();
            textPath.moveTo(startXT, 0);
            textPath.lineTo(w, startYT);
            float dis = (float) (Math.sqrt(2) * startYT);
            float textWidth = mTextPaint.measureText(mContent);
            float start = (dis - textWidth) / 2;
            canvas.drawTextOnPath(mContent, textPath, start, 0, mTextPaint);
        }

    }

    private float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale;
    }

}
