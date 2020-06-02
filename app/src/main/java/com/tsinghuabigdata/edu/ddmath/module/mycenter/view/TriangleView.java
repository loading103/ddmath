package com.tsinghuabigdata.edu.ddmath.module.mycenter.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


/**
 * 三角形
 */

public class TriangleView extends View {

    //三角形方向
    public final static int DIRECT_UP = 0;
    public final static int DIRECT_DOWN = 1;
    public final static int DIRECT_LEFT = 2;
    public final static int DIRECT_RIGHT = 3;

    private Paint mPaint = new Paint();
    private Path path = new Path();
    private int mColor = Color.WHITE;
    private int mDirect = DIRECT_UP;

    public TriangleView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    public void setColor(int mColor) {
        this.mColor = mColor;
        invalidate();
    }

    public void setDirect(int mDirect) {
        this.mDirect = mDirect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor( mColor );
        mPaint.setStyle(Paint.Style.FILL);

        int width = getWidth();
        int height = getHeight();
        int min = width<height?width:height;
        int offx = (width-min)/2;
        int offy = (height-min)/2;

        int triangle_height = min * 3/4;

        path.reset();
        //实例化路径
        if( mDirect == DIRECT_UP ){
            path.moveTo( offx, offy+min );// 此点为多边形的起点
            path.lineTo( offx+min/2, offy + min/4 );
            path.lineTo( offx+min, offy+min);
        }else if( mDirect == DIRECT_DOWN ){
            path.moveTo( offx, offy );// 此点为多边形的起点
            path.lineTo( offx+min/2, offy+min*3/4 );
            path.lineTo( offx+min, offy);
        }else if( mDirect == DIRECT_LEFT ){
            path.moveTo( offx+min, offy );// 此点为多边形的起点
            path.lineTo( offx+min/4, offy+min/2 );
            path.lineTo( offx+min, offy+min);
        }else {
            path.moveTo( offx, offy);// 此点为多边形的起点
            path.lineTo( offx+min*3/4, offy + min/2 );
            path.lineTo( offx, offy+min);
        }

        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, mPaint);
    }

    //-------------------------------------------------------------------------------------
    private void init(Context context, AttributeSet attrs, int defStyle) {

    }

}
