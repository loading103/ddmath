package com.tsinghuabigdata.edu.ddmath.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

@SuppressLint("DrawAllocation")
public class CaptureView extends View {
    private static final String TAG = "CaptureView";

    // 触摸位置及动作
    public static final int GROW_NONE = (1 << 0);//框体外部
    public static final int GROW_POINT_MOVE = (1 << 1);//框体角点
    //	public static final int GROW_RIGHT_EDGE = (1 << 2);//框体右边缘
//	public static final int GROW_TOP_EDGE = (1 << 3);//框体上边缘
//	public static final int GROW_BOTTOM_EDGE = (1 << 4);//框体下边缘
    public static final int GROW_MOVE = (1 << 2);//框体移动

    public static final int POINT_RADIO = 10;//角半径

    private Paint outsideCapturePaint = new Paint(); // 捕获框体外部画笔
    private Paint lineCapturePaint = new Paint(); // 边框画笔

    //选择区域端点
    private ArrayList<Point> mPointList = new ArrayList<>();
    //正在移动的点
    private Point currPoint;

    private Rect viewRect; // 可视范围

    private Rect border;  //限制移动范围

    private Rect maxRect;

    private int mMotionEdge; // 触摸的边缘

    private float mLastX, mLastY; // 上次触摸的坐标
    private CaptureView mCaptureView;
    float minh, minw, maxh, maxw;

    private boolean rectCoverMode = false;


    private enum ActionMode { // 枚举动作类型：无、移动、拉伸
        None, Move, Grow;
    }

    private ActionMode mMode = ActionMode.None;

    public CaptureView(Context context) {
        super(context);
        initView();
    }

    public CaptureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public CaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setRectCoverMode(){
        outsideCapturePaint.setARGB(255, 255, 255, 255);
        rectCoverMode = true;
    }

    void initView() {
        lineCapturePaint.setStrokeWidth(1); // 捕获框边框画笔大小
        lineCapturePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        lineCapturePaint.setAntiAlias(true); // 抗锯齿
        lineCapturePaint.setColor(Color.RED); // 画笔颜色

        setFullScreen(true); // 默认为全屏模式
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化可视范围及框体大小
        viewRect = new Rect(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (border == null) {
            border = new Rect(getLeft(), getTop(), getRight(), getBottom());
        }
        canvas.save();

        Path tpath = getCurrPath();

        Region rgn = new Region();

        rgn.set(viewRect);
        boolean b = rgn.setPath(tpath, rgn);
        b = rgn.op(viewRect, Region.Op.XOR);

        RegionIterator iter = new RegionIterator(rgn);
        Rect rf = new Rect();
        while (iter.next(rf)) {
            canvas.drawRect(rf, outsideCapturePaint);
        }
        //绘制框体
//        canvas.drawPath(tpath, lineCapturePaint);

        canvas.restore();
    }

    public void setFullScreen(boolean full) {
        if (full) { // 全屏，则把外部框体颜色设为透明
            outsideCapturePaint.setARGB(125, 0, 0, 0);
        } else { // 只显示框体区域，框体外部为全黑
            outsideCapturePaint.setARGB(255, 0, 0, 0);
        }
    }

    private Path getCurrPath() {
        Path tpath = new Path();
        if( rectCoverMode ){
            tpath.addRoundRect( new RectF(getLeft(), getTop(), getRight(), getBottom()), 20f, 20f, Path.Direction.CW );
        }else{
            float minw = Math.min(viewRect.right - viewRect.left, viewRect.bottom - viewRect.top);
            float rad = (float) (minw / 2.0 - 20);
            tpath.addCircle(viewRect.centerX(), viewRect.centerY(), rad, Path.Direction.CW);
        }
        tpath.close();
        return tpath;
    }
}
