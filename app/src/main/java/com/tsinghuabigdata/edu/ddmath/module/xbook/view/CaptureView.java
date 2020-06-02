package com.tsinghuabigdata.edu.ddmath.module.xbook.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;

/** 
 */
@SuppressLint("DrawAllocation")
public class CaptureView extends View {

    // 触摸位置及动作
    public static final int GROW_NONE = (1 << 0);//框体外部
    public static final int GROW_LEFT_EDGE = (1 << 1);//框体左边缘
    public static final int GROW_RIGHT_EDGE = (1 << 2);//框体右边缘
    public static final int GROW_TOP_EDGE = (1 << 3);//框体上边缘
    public static final int GROW_BOTTOM_EDGE = (1 << 4);//框体下边缘
    public static final int GROW_MOVE = (1 << 5);//框体移动

    private Paint outsideCapturePaint = new Paint(); // 捕获框体外部画笔
    private Paint lineCapturePaint = new Paint(); // 边框画笔

    private Rect viewRect; // 可视范围
    private Rect captureRect; // 框体范围

    private int mMotionEdge; // 触摸的边缘
    private float mLastX, mLastY; // 上次触摸的坐标


    private Drawable circleArrows; // 左上角拉伸箭头
    //    private Drawable leftTopArrows; // 左上角拉伸箭头
//    private Drawable rightTopArrows; // 右上角拉伸箭头
//    private Drawable leftBottomArrows; // 左下角拉伸箭头
//    private Drawable rightBottomArrows; // 右下角拉伸箭头
    private int cornerArrowsHalfWidth; // 拉伸箭头的宽
    private int cornerArrowsHalfHeight;// 拉伸箭头的高
    
    private CaptureView mCaptureView;
    private CropToolView cropToolView;

    private View relateView;

    private enum ActionMode { // 枚举动作类型：无、移动、拉伸
        None, Move, Grow
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CaptureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void setRelateView( View view ){
        relateView = view;
    }
    public void setCropToolView( CropToolView view ){
        cropToolView = view;
    }
    
    void initView() {
        lineCapturePaint.setStrokeWidth(4F); // 捕获框边框画笔大小
        lineCapturePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        lineCapturePaint.setAntiAlias(true); // 抗锯齿
        lineCapturePaint.setColor( Color.WHITE ); // 画笔颜色
        
        Resources resources = getResources();

        circleArrows            = resources.getDrawable(R.drawable.ic_white_dot );
//        leftTopArrows           = resources.getDrawable(R.drawable.camframe1);
//        rightTopArrows          = resources.getDrawable(R.drawable.camframe2);
//        leftBottomArrows        = resources.getDrawable(R.drawable.camframe4);
//        rightBottomArrows       = resources.getDrawable(R.drawable.camframe3);
//        cornerArrowsHalfWidth   = leftTopArrows.getIntrinsicWidth() / 5;
//        cornerArrowsHalfHeight  = leftTopArrows.getIntrinsicHeight() / 5;
        cornerArrowsHalfWidth   = circleArrows.getIntrinsicWidth() / 2;
        cornerArrowsHalfHeight  = circleArrows.getIntrinsicHeight() / 2;
        setFullScreen(true); // 默认为全屏模式
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int ttop         = Math.max( top, relateView.getTop() );
        int tleft        = Math.max( left, relateView.getLeft() );
        int tbottom      = Math.min( bottom, relateView.getBottom() );
        int tright      = Math.min( right, relateView.getRight() );

        //初始View大小
        int viewWidth = right - left;
        int viewHeight = bottom - top;

        // 初始化可视范围及框体大小  调整后的大小
        viewRect = new Rect(tleft, ttop, tright, tbottom);

        //选择框大小
        int captureWidth = viewRect.width() * 9 / 10;
        int captureHeight = viewRect.height() * 4 / 5;
        // 将框体绘制在可视范围中间位置
        int captureX = (viewWidth - captureWidth) / 2;
        int captureY = (viewHeight - captureHeight) / 2;
        captureRect = new Rect(captureX, captureY, captureX + captureWidth,
                captureY + captureHeight);

//        cropToolView.changeLayout();
        if( cropToolView != null )
            cropToolView.changePosistion( true );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        
        //框体外背景色
        Region rgn = new Region(); 
        rgn.set(viewRect); 
        rgn.op(captureRect, Region.Op.DIFFERENCE ); 
        RegionIterator iter = new RegionIterator(rgn); 
        Rect r = new Rect(); 
        while (iter.next(r)) { 
            canvas.drawRect(r, outsideCapturePaint ); 
        }
        
        //绘制框体
        Path path1 = new Path();
        path1.addRect(new RectF(captureRect), Path.Direction.CW);
        //canvas.clipPath(path1);
        canvas.drawPath(path1, lineCapturePaint);
        //canvas.drawRect( captureRect, lineCapturePaint );

        //------------------------------------------
        //绘制网格
        //横线
        Paint paint = new Paint();
        paint.setColor( Color.WHITE );
        float yy = (float)(captureRect.height()*0.33) + captureRect.top;
        canvas.drawLine( captureRect.left, yy, captureRect.right, yy, paint);
        yy = (float)(captureRect.height()*0.67) + captureRect.top;
        canvas.drawLine( captureRect.left, yy, captureRect.right, yy, paint);

        //竖线
        float xx = (float)(captureRect.width()*0.33) + captureRect.left;
        canvas.drawLine( xx, captureRect.top, xx, captureRect.bottom, paint);
        xx = (float)(captureRect.width()*0.67) + captureRect.left;
        canvas.drawLine( xx, captureRect.top, xx, captureRect.bottom, paint);

        canvas.restore();

        // 框体左上的箭头
        circleArrows.setBounds(//
                captureRect.left - cornerArrowsHalfWidth,//
                captureRect.top - cornerArrowsHalfHeight,//
                captureRect.left + circleArrows.getIntrinsicWidth() - cornerArrowsHalfWidth,
                captureRect.top + circleArrows.getIntrinsicHeight() - cornerArrowsHalfWidth);
        circleArrows.draw(canvas);

        // 框体右上角的箭头
        circleArrows.setBounds(//
                captureRect.right - circleArrows.getIntrinsicWidth() + cornerArrowsHalfWidth,//
                captureRect.top - cornerArrowsHalfHeight,//
                captureRect.right + cornerArrowsHalfWidth,//
                captureRect.top +  + circleArrows.getIntrinsicHeight() - cornerArrowsHalfHeight);
        circleArrows.draw(canvas);

        // 框体左下角的箭头
        circleArrows.setBounds(//
                captureRect.left - cornerArrowsHalfWidth,//
                captureRect.bottom - circleArrows.getIntrinsicHeight() + cornerArrowsHalfHeight,//
                captureRect.left + circleArrows.getIntrinsicWidth() - cornerArrowsHalfWidth,//
                captureRect.bottom + cornerArrowsHalfHeight);
        circleArrows.draw(canvas);

        // 框体右下角的箭头
        circleArrows.setBounds(//
                captureRect.right - circleArrows.getIntrinsicWidth() + cornerArrowsHalfWidth,//
                captureRect.bottom - circleArrows.getIntrinsicHeight() + cornerArrowsHalfHeight,//
                captureRect.right + cornerArrowsHalfWidth,//
                captureRect.bottom + cornerArrowsHalfHeight);
        circleArrows.draw(canvas);

    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            int grow = getGrow(event.getX(), event.getY());
            if (grow != GROW_NONE) {
                // 锁定当前触摸事件的操作对象，直到ACTION_UP，如果没有锁定，有grow为前次操作的值。
                mCaptureView = CaptureView.this;
                mMotionEdge = grow;
                mLastX = event.getX();
                mLastY = event.getY();
                mCaptureView.setMode((grow == GROW_MOVE) ? ActionMode.Move
                        : ActionMode.Grow);
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mCaptureView != null) {
                setMode(ActionMode.None);
                mCaptureView = null; // 释放当前锁定的操作对象
            }

            break;
        case MotionEvent.ACTION_MOVE: // 框体移动
            if (mCaptureView != null) {
                handleMotion(mMotionEdge, event.getX() - mLastX, event.getY()
                        - mLastY);
                mLastX = event.getX();
                mLastY = event.getY();
            }
            break;
        default:
            break;
        }
        return true;
    }

    public void setFullScreen(boolean full) {
        if (full) { // 全屏，则把外部框体颜色设为透明
            outsideCapturePaint.setARGB(128, 50, 50, 50);
        } else { // 只显示框体区域，框体外部为全黑
            outsideCapturePaint.setARGB(255, 0, 0, 0);
        }
    }

    private void setMode(ActionMode mode) {
        if (mode != mMode) {
            mMode = mode;
            invalidate();
        }
    }

    // 确定触摸位置及动作，分别为触摸框体外围和框体上、下、左、右边缘以及框体内部。
    private int getGrow(float x, float y) {
        final float effectiveRange = 20F; // 触摸的有效范围大小
        int grow = GROW_NONE;
        int left = captureRect.left;
        int top = captureRect.top;
        int right = captureRect.right;
        int bottom = captureRect.bottom;
        boolean verticalCheck = (y >= top - effectiveRange)
                && (y < bottom + effectiveRange);
        boolean horizCheck = (x >= left - effectiveRange)
                && (x < right + effectiveRange);

        // 触摸了框体左边缘
        if ((Math.abs(left - x) < effectiveRange) && verticalCheck) {
            grow |= GROW_LEFT_EDGE;
        }

        // 触摸了框体右边缘
        if ((Math.abs(right - x) < effectiveRange) && verticalCheck) {
            grow |= GROW_RIGHT_EDGE;
        }

        // 触摸了框体上边缘
        if ((Math.abs(top - y) < effectiveRange) && horizCheck) {
            grow |= GROW_TOP_EDGE;
        }

        // 触摸了框体下边缘
        if ((Math.abs(bottom - y) < effectiveRange) && horizCheck) {
            grow |= GROW_BOTTOM_EDGE;
        }

        // 触摸框体内部
        if (grow == GROW_NONE && captureRect.contains((int) x, (int) y)) {
            grow = GROW_MOVE;
        }
        return grow;
    }

    // 处理触摸事件，判断移动框体还是伸缩框体
    private void handleMotion(int grow, float dx, float dy) {
        /*if (grow == GROW_NONE) {
            return;
        } else */if (grow == GROW_MOVE) {
            moveBy(dx, dy); // 移动框体
        } else {
            if (((GROW_LEFT_EDGE | GROW_RIGHT_EDGE) & grow) == 0) {
                dx = 0; // 水平不伸缩
            }

            if (((GROW_TOP_EDGE | GROW_BOTTOM_EDGE) & grow) == 0) {
                dy = 0; // 垂直不伸缩
            }

            //growBy((((grow & GROW_LEFT_EDGE) != 0) ? -1 : 1) * dx,
            //        (((grow & GROW_TOP_EDGE) != 0) ? -1 : 1) * dy);
            growBy( grow, dx, dy);
        }
    }

    private void moveBy(float dx, float dy) {
        Rect invalRect = new Rect(captureRect);
        captureRect.offset((int) dx, (int) dy);
        captureRect.offset(Math.max(0, viewRect.left - captureRect.left/*+cornerArrowsHalfWidth*/),
                Math.max(0, viewRect.top - captureRect.top/*+cornerArrowsHalfHeight*/));
        captureRect.offset(Math.min(0, viewRect.right - captureRect.right/*-cornerArrowsHalfWidth*/),
                Math.min(0, viewRect.bottom - captureRect.bottom/*-cornerArrowsHalfHeight*/));

        //清除移动滞留的痕迹
        invalRect.union(captureRect);//更新围绕本身区域和指定的区域，
        invalRect.inset(-100, -100);
        invalidate(invalRect); // 重绘指定区域

        if( cropToolView != null )
            cropToolView.changePosistion( false );
    }

    private void growBy(int grow, float dx, float dy){
        float widthCap = 50F;        //captureRect最小宽度
        float heightCap = 50F;      //captureRect最小高度
        
        RectF r = new RectF(captureRect);
        
        //DDebug.debugLog( "grow = " + grow );
        
        //当captureRect拉伸到宽度 = viewRect的宽度时，则调整dx的值为 0
        if( (grow&GROW_LEFT_EDGE) == GROW_LEFT_EDGE   ){
            if( dx < 0 && r.left + dx < viewRect.left )        //左边向左拉升
                dx = 0f;
            r.left+= dx;
        }
        if( (grow&GROW_RIGHT_EDGE) == GROW_RIGHT_EDGE  ){    
            if( dx > 0 && r.right + dx >= viewRect.right )    //右边向右拉升
                dx = 0f;
            r.right+= dx;
        }
        if( (grow&GROW_TOP_EDGE) == GROW_TOP_EDGE ){    
            if( dy < 0 && r.top + dy < viewRect.top )//上边向上拉升
                dy = 0f;
            r.top+= dy;
        }
        if( (grow&GROW_BOTTOM_EDGE) == GROW_BOTTOM_EDGE ){
            if( dy > 0 && r.bottom + dy >= viewRect.bottom )    //下边向下拉升
                dy = 0f;
            r.bottom+= dy;
        }

        //当captureRect缩小到宽度 = widthCap时
        if (r.width() <= widthCap) {
            r.inset(-(widthCap - r.width()) / 2F, 0F);
        }

        //同上
        if (r.height() <= heightCap) {
            r.inset(0F, -(heightCap - r.height()) / 2F);
        }

        if (r.left < viewRect.left) {
            r.offset(viewRect.left - r.left, 0F);
        } else if (r.right > viewRect.right) {
            r.offset(-(r.right - viewRect.right), 0);
        }
        if (r.top < viewRect.top) {
            r.offset(0F, viewRect.top - r.top);
        } else if (r.bottom > viewRect.bottom) {
            r.offset(0F, -(r.bottom - viewRect.bottom));
        }

        captureRect.set((int) r.left, (int) r.top, (int) r.right,
                (int) r.bottom);
        invalidate();

        if( cropToolView != null )
            cropToolView.changePosistion( false );
    }

    public synchronized Rect getCaptureRect() {
        return captureRect;
    }
}
