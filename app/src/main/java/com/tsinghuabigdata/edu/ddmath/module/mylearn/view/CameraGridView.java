package com.tsinghuabigdata.edu.ddmath.module.mylearn.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.AttributeSet;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;


public class CameraGridView extends View {

    private int cornerColor,cornerLenght;
    public CameraGridView(Context context) {
        super(context);
        init();
    }

    public CameraGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CameraGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        cornerColor = getContext().getResources().getColor( R.color.color_ADFF31);
        cornerLenght= WindowUtils.dpToPixels(getContext(), GlobalData.isPad()?64:32 );
    }
//    private boolean port = false;
//    public void setPortFlag( boolean b ){
//        port = b;
//    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        
        Paint paint = new Paint();
        paint.setColor( Color.WHITE );

        int width  = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingTop() - getPaddingBottom();

        int paddingLeft = getPaddingLeft();
        int paddingTop  = getPaddingTop();
        
        canvas.save();

        //先画边框
        paint.setStrokeWidth(4);
        canvas.drawLine( paddingLeft, paddingTop, width+paddingLeft, paddingTop, paint);                                //上横线
        canvas.drawLine( paddingLeft, height+paddingTop, width+paddingLeft, height+paddingTop, paint);                  //下横线
        canvas.drawLine( paddingLeft, paddingTop, paddingLeft, height+paddingTop, paint);                               //左竖线
        canvas.drawLine( width+paddingLeft, paddingTop, width+paddingLeft, height+paddingTop, paint);                   //右竖线

        paint.setStrokeWidth(2);

        //内部 横线
        float yy = (float)(height*0.33);
        canvas.drawLine( paddingLeft, yy+paddingTop, width+paddingLeft, yy+paddingTop, paint);
        yy = (float)(height*0.67);
        canvas.drawLine( paddingLeft, yy+paddingTop, width+paddingLeft, yy+paddingTop, paint);

        //内部 竖线
        float xx = (float)(width*0.33);
        canvas.drawLine( xx+paddingLeft, paddingTop, xx+paddingLeft, height+paddingTop, paint);
        xx = (float)(width*0.67);
        canvas.drawLine( xx+paddingLeft, paddingTop, xx+paddingLeft, height+paddingTop, paint);

        //再画 四个角
        paint.setStrokeWidth(4);
        paint.setColor( cornerColor );

        canvas.drawLine( paddingLeft, paddingTop, paddingLeft+cornerLenght, paddingTop, paint);                    //左上角
        canvas.drawLine( paddingLeft, paddingTop, paddingLeft, paddingTop+cornerLenght, paint);

        canvas.drawLine( paddingLeft+width-cornerLenght, paddingTop, paddingLeft+width, paddingTop, paint);        //右上角
        canvas.drawLine( paddingLeft+width, paddingTop, paddingLeft+width, paddingTop+cornerLenght, paint);

        canvas.drawLine( paddingLeft, paddingTop+height, paddingLeft+cornerLenght, paddingTop+height, paint);      //左下角
        canvas.drawLine( paddingLeft, paddingTop+height-cornerLenght, paddingLeft, paddingTop+height, paint);

        canvas.drawLine( paddingLeft+width-cornerLenght, paddingTop+height, paddingLeft+width, paddingTop+height, paint);                    //右下角
        canvas.drawLine( paddingLeft+width, paddingTop+height-cornerLenght, paddingLeft+width, paddingTop+height, paint);

        //框体外背景色
        Paint outsideCapturePaint = new Paint();// 全屏，则把外部框体颜色设为透明
        outsideCapturePaint.setARGB(75, 0, 0, 0);

        Region rgn = new Region();
        rgn.set(new Rect( 0, 0, getWidth(), getHeight()));      //整个View占据的大小
        rgn.op(new Rect( paddingLeft, paddingTop, width+paddingLeft, height+paddingTop), Region.Op.DIFFERENCE );         //内容占据的大小
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();
        while (iter.next(r)) {
            canvas.drawRect(r, outsideCapturePaint );
        }

        canvas.restore();
    }

}
