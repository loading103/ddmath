package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;


/**
 * 拍照时外框限制
 */
public class OutlineBorderView extends View {

    private Paint outsideCapturePaint = new Paint(); // 捕获框体外部画笔
    private Paint detectPaint = new Paint();         // 探测结果
    private Paint innerLinePaint = new Paint();      // 内框线
    private Paint outerLinePaint = new Paint();      // 外框线
    private Paint refercnceLinePaint = new Paint();  //对齐参考线

    private Rect viewRect = new Rect(); // 可视范围
    private Rect innerRect = new Rect(); // 内框范围

    private int rightcolor = Color.rgb(0x00,0xF8,0xFB);
    private int wrongcolor = Color.rgb(0xFF,0x38,0x38);

    //RegionIterator regionIterator;

    private int marginStart,marginTop;

    //private View.OnClickListener focusListener;

    private ArrayList<Point> pointList;

    public OutlineBorderView(Context context) {
        super(context);
        initView();
    }

    public OutlineBorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public OutlineBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public OutlineBorderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initView();
//    }

    /**
     * 显示检测到的四边形  第一个 值无用
     * @param points 点数据
     */
    public void showDetectRect( int points[] ){

        //AppLog.d("dsaddsad view ------------------------ ");
        //AppLog.d("dsaddsad view width = " + getWidth() + ",,, height = " + getHeight() );
        //左上
        Point point = pointList.get(0);
        point.x = points[1];
        point.y = points[2];
        //AppLog.d("dsaddsad left top x = " + point.x + ",,, y = " + point.y );
        //右上
        point = pointList.get(1);
        point.x = points[3];
        point.y = points[4];
        //AppLog.d("dsaddsad rirht top x = " + point.x + ",,, y = " + point.y );

        //右下
        point = pointList.get(2);
        point.x = points[7];
        point.y = points[8];
        //AppLog.d("dsaddsad right bottom x = " + point.x + ",,, y = " + point.y );

        //左下
        point = pointList.get(3);
        point.x = points[5];
        point.y = points[6];
        //AppLog.d("dsaddsad left bottom x = " + point.x + ",,, y = " + point.y );

//        path1.reset();
//        path1.moveTo( points[1], points[2] );
//        path1.lineTo( points[3],points[4] );
//        path1.lineTo( points[7],points[8] );
//        path1.lineTo( points[5],points[6] );
//        path1.lineTo( points[1], points[2] );

//        path1.quadTo( points[1], points[2], points[3],points[4]);
//        path1.quadTo( points[3], points[4], points[7],points[8]);
//        path1.quadTo( points[7], points[8], points[5],points[6]);
//        path1.quadTo( points[5], points[6], points[1],points[2]);

//        Region rgn2 = new Region();
//        rgn2.set( viewRect );
//        Region rgn1 = new Region();
//        rgn1.setPath( path1, rgn2 );
//        rgn1.op( viewRect, Region.Op.INTERSECT );
//        regionIterator = new RegionIterator(rgn1);

        //触发刷新
        invalidate();
    }

    public Rect getViewRect(){  return viewRect; }

    public Rect getInnerRect(){ return innerRect; }

    public void setMargins( int offset_width, int offset_height ){
        marginStart = offset_width;
        marginTop   = offset_height;
    }

//    public void setFocusListener( View.OnClickListener listener ){
//        focusListener = listener;
//    }

    //拦截事件，启动自动对焦
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        if (event.getAction() == MotionEvent.ACTION_UP ) {// 放开后拍照
//            if( focusListener!=null ) focusListener.onClick( this );
//        }
//        return true;
//    }
    //--------------------------------------------------------------------------------

    void initView() {
//        lineCapturePaint.setStrokeWidth(4F); // 捕获框边框画笔大小
//        lineCapturePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
//        lineCapturePaint.setAntiAlias(true); // 抗锯齿
//        lineCapturePaint.setColor( Color.RED ); // 画笔颜色

        outsideCapturePaint.setARGB( 40, 0x02,0xA9,0xAB);

        outerLinePaint.setColor( Color.rgb(0xFB,0x69,0) );
        outerLinePaint.setStrokeWidth( GlobalData.isPad()?12:12 );

        innerLinePaint.setColor( Color.rgb(0x02,0xA9,0xAB) );
        innerLinePaint.setStyle(Paint.Style.STROKE);
        innerLinePaint.setAntiAlias(true);
        innerLinePaint.setStrokeWidth(  GlobalData.isPad()?6:4  );
        //DashPathEffect是Android提供的虚线样式API，具体的使用可以参考下面的介绍
        innerLinePaint.setPathEffect(new DashPathEffect(new float[] { 16, 16 }, 0));

        detectPaint.setColor( Color.rgb(0x48,0xB8,0xFF) );
        detectPaint.setStrokeWidth(GlobalData.isPad()?6:4 );
        detectPaint.setAntiAlias(true); // 抗锯齿

        refercnceLinePaint.setColor( Color.WHITE );
        refercnceLinePaint.setStrokeWidth(GlobalData.isPad()?2:1 );
        refercnceLinePaint.setAntiAlias(true); // 抗锯齿
        
        pointList = new ArrayList<>();
        pointList.add( new Point() );
        pointList.add( new Point() );
        pointList.add( new Point() );
        pointList.add( new Point() );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化可视范围及框体大小
        viewRect.set( left, top, right, bottom );
        viewRect.offset( -marginStart, -marginTop );

        //内框大小
        int dw = (right - left)*4/20;
        int dh = (bottom - top)*4/20;
        innerRect.set( left+dw, top+dh, right-dw, bottom-dh );
        innerRect.offset( -marginStart, -marginTop );
    }

    //private Region rgn = new Region();
    //private Rect rect = new Rect();
    //private path path1 = new path();
    //private RegionIterator iter = new RegionIterator(rgn);
//    private RectF rectF = new RectF();
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        //绘制外框线
        int line_len = 80;
        int magrin = 24;
        int linewidth = (int)outerLinePaint.getStrokeWidth()/2;
        //左上角线
        canvas.drawLine( magrin, magrin, magrin+line_len, magrin, outerLinePaint );
        canvas.drawLine( magrin, magrin-linewidth, magrin, magrin+line_len+linewidth, outerLinePaint );

        //右上角线
        canvas.drawLine( viewRect.right-magrin, magrin, viewRect.right-magrin-line_len, magrin, outerLinePaint );
        canvas.drawLine( viewRect.right-magrin, magrin-linewidth, viewRect.right-magrin, magrin+line_len+linewidth, outerLinePaint );

        //左下角线
        canvas.drawLine( magrin, viewRect.bottom-magrin, magrin+line_len, viewRect.bottom-magrin, outerLinePaint );
        canvas.drawLine( magrin, viewRect.bottom-magrin+linewidth, magrin, viewRect.bottom-magrin-line_len-linewidth, outerLinePaint );

        //右下角线
        canvas.drawLine( viewRect.right-magrin, viewRect.bottom-magrin, viewRect.right-magrin-line_len, viewRect.bottom-magrin, outerLinePaint );
        canvas.drawLine( viewRect.right-magrin, viewRect.bottom-magrin+linewidth, viewRect.right-magrin, viewRect.bottom-magrin-line_len-linewidth, outerLinePaint );

        //绘制对齐参考线
        int y = viewRect.height() / 3 + viewRect.top;
        canvas.drawLine( viewRect.left, y, viewRect.right, y, refercnceLinePaint );
        y = viewRect.height()*2 / 3 + viewRect.top;
        canvas.drawLine( viewRect.left, y, viewRect.right, y, refercnceLinePaint );

        //绘制内框
        //canvas.drawRect(innerRect, innerLinePaint );
        //canvas.drawRect( innerRect, outsideCapturePaint );

        //暂时不显示检测出的四边
        //绘制检测出的四边形
//        linewidth = (int)detectPaint.getStrokeWidth()/2;
//        //上边线
//        Point p1 = pointList.get(0);
//        Point p2 = pointList.get(1);
//        detectPaint.setColor( checkLineRight(p1,p2) );
//        canvas.drawLine( p1.x, p1.y, p2.x, p2.y, detectPaint);
//        //左边线
//        p2 = pointList.get(3);
//        detectPaint.setColor( checkLineRight(p1,p2) );
//        canvas.drawLine( p1.x, p1.y-linewidth, p2.x, p2.y+linewidth, detectPaint);
//        //下边线
//        p1 = pointList.get(2);
//        detectPaint.setColor( checkLineRight(p1,p2) );
//        canvas.drawLine( p1.x, p1.y, p2.x, p2.y, detectPaint);
//        //右边线
//        p2 = pointList.get(1);
//        detectPaint.setColor( checkLineRight(p1,p2) );
//        canvas.drawLine( p1.x, p1.y+linewidth, p2.x, p2.y-linewidth, detectPaint);

        //检测出的四边形背景色
//        if( regionIterator!=null )
//        while (regionIterator.next(rect)) {
//            canvas.drawRect(rect, outsideCapturePaint );
//        }
//        canvas.drawPath( path1, outsideCapturePaint );
        canvas.restore();
    }

    private int checkLineRight( Point p1, Point p2 ){
        boolean st =  viewRect.contains( p1.x, p1.y ) && !innerRect.contains( p1.x, p1.y ) &&  //点在大的范围内 不在小框的范围内
               viewRect.contains( p2.x, p2.y ) && !innerRect.contains( p2.x, p2.y ) &&
               viewRect.intersects( p1.x, p1.y, p2.x, p2.y ) && !innerRect.intersects( p1.x, p1.y, p2.x, p2.y );  //矩形范围重叠
        return st?rightcolor:wrongcolor;
    }

}
