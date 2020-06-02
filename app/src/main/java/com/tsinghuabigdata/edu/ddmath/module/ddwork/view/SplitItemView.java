package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

@SuppressLint("DrawAllocation")
public class SplitItemView extends View implements View.OnClickListener{

    //显示模型
    public static final int MODE_BLACK      = 0;      //框体外部
    public static final int MODE_WHITE      = 1;      //框体外部
    public static final int MODE_ACTIVIE    = 2;    //框体外部

    //移动模式
    public static final int MOVE_NONE           = 0;//框体外部
    public static final int MOVE_TOP_EDGE       = 1;   //框体上边
    public static final int MOVE_BOTTOM_EDGE    = 2;//框体下边

    private Paint coverPaint = new Paint(); // 覆盖画笔
    private Paint borderPaint = new Paint(); // 白色边框画笔
    private Paint activePaint = new Paint(); // 激活边框画笔

    private Rect viewRect; // 可视范围
    private int mMotionEdge; // 触摸的边缘
    private int showMode = MODE_BLACK;


    private PorterDuffXfermode pdf;

    private Drawable upDownArrow; // 上下拉伸箭头
    private WorkSplitView.ItemData mItemData;

    public SplitItemView(Context context) {
        super(context);
        initView();
    }

    public SplitItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public SplitItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setShowMode( WorkSplitView.ItemData itemData ){
        mItemData = itemData;
        showMode = itemData.mode;
        if( MODE_BLACK == showMode ){
            //默认 0.4 #232323
            coverPaint.setARGB(100, 35, 35, 35);
        }else{
            pdf = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            coverPaint.setXfermode(pdf);
            coverPaint.setStyle(Paint.Style.STROKE);
//            if( itemData.select ){
//                showMode = MODE_ACTIVIE;
//            }
        }
        initViewRect();
    }

    @Override
    public void onClick(View v) {
        if( showMode == MODE_WHITE ){
            showMode = MODE_ACTIVIE;
            //invalidate();
            initViewRect();
            invalidate();
        }
    }
    //----------------------------------------------------------------------------
    void initViewRect(){

        int offset_height = 0;
        if( showMode == MODE_ACTIVIE ){
            offset_height = upDownArrow.getIntrinsicHeight();
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if( layoutParams == null ){
            layoutParams = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        }
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height= mItemData.bottom-mItemData.top + offset_height;
        setLayoutParams( layoutParams );

        setY( mItemData.top - offset_height/2  );
    }

    void initView() {
        borderPaint.setStrokeWidth(2); // 捕获框边框画笔大小
        borderPaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        borderPaint.setAntiAlias(true); // 抗锯齿
        borderPaint.setColor( Color.rgb(0xFF,0xE9,0x63)); // 画笔颜色

        activePaint.setStrokeWidth(3); // 捕获框边框画笔大小
        activePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        activePaint.setAntiAlias(true); // 抗锯齿
        activePaint.setColor( Color.rgb(0x58,0xE6,0xFF)); // 画笔颜色

        //默认 0.4 #232323
        coverPaint.setARGB(100, 35, 35, 35);

        Resources resources = getResources();
        upDownArrow = resources.getDrawable(R.drawable.ic_up_down );

        setOnClickListener( this );
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化可视范围及框体大小
        viewRect = new Rect(left, top, right, bottom);
        AppLog.d(" SplitItemView dd left = " + left + ",,, top="+top+",,, right="+right+",,bottom="+bottom );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        if( MODE_BLACK == showMode ){
            //绘制半透明覆盖层
            canvas.drawRect(viewRect, coverPaint);
        }else if( MODE_WHITE == showMode ){
            //先绘制边框
            canvas.drawRect( getLeft(), getTop(), getRight(),getBottom(), borderPaint );

            //绘制透明覆盖层
            canvas.drawRect( getLeft(), getTop(), getRight(),getBottom(), coverPaint );

        }else{

            int offset_height = upDownArrow.getIntrinsicHeight()/2;

            //先绘制边框
            canvas.drawRect( getLeft(), getTop()+offset_height, getRight(),getBottom()-offset_height, activePaint );

            //绘制透明覆盖层
            canvas.drawRect( getLeft(), getTop()+offset_height, getRight(),getBottom()-offset_height, coverPaint );

            offset_height = upDownArrow.getIntrinsicHeight();

            //绘制上箭头
            upDownArrow.setBounds(//
                    (getLeft()+getRight())/2 - upDownArrow.getIntrinsicWidth()/2,//
                    getTop(),//
                    (getLeft()+getRight())/2 + upDownArrow.getIntrinsicWidth()/2,
                    getTop() + offset_height );
            upDownArrow.draw(canvas);

            //绘制下箭头
            upDownArrow.setBounds(//
                    (getLeft()+getRight())/2 - upDownArrow.getIntrinsicWidth()/2,//
                    getBottom() - offset_height,//
                    (getLeft()+getRight())/2 + upDownArrow.getIntrinsicWidth()/2,
                    getBottom() );
            upDownArrow.draw(canvas);
        }
        canvas.restore();
    }

    private float mLastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if( showMode != MODE_ACTIVIE )
            return super.onTouchEvent( event );

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                int grow = getGrow( (int)event.getRawX(), (int)event.getRawY() );
                AppLog.i("---------down  grow = " + grow );
                if (grow != MOVE_NONE) {
                    // 锁定当前触摸事件的操作对象，直到ACTION_UP，如果没有锁定，有grow为前次操作的值。
                    //mCaptureView = CaptureView.this;
                    mMotionEdge = grow;
                    mLastY = event.getY();
//                    mCaptureView.setMode((grow == GROW_MOVE) ? ActionMode.Move
//                            : ActionMode.Grow);
                }
                break;
            case MotionEvent.ACTION_UP:
                AppLog.i("---------up  " );
                mMotionEdge = MOVE_NONE;
//                if (mCaptureView != null) {
//                    setMode(ActionMode.None);
//                    mCaptureView = null; // 释放当前锁定的操作对象
//                }

                break;
            case MotionEvent.ACTION_MOVE: // 框体移动
                AppLog.i("---------move grow = " + mMotionEdge  );
                //if (mCaptureView != null) {
                growBy( mMotionEdge, 0, event.getY() - mLastY);
                mLastY = event.getY();
                //}
                break;
            default:
                break;
        }
        return true;
    }

    // 确定触摸位置及动作，分别为触摸框体 框体上、下
    private Rect mRect = new Rect();
    private Rect gRect = new Rect();
    private int getGrow(int x, int y) {

        final int effectiveRange = 20; // 触摸的有效范围大小
        int grow = MOVE_NONE;

        getGlobalVisibleRect( gRect );

        //上箭头
        int left = (gRect.left+gRect.right)/2 - upDownArrow.getIntrinsicWidth()/2 - effectiveRange;
        int top = gRect.top -  effectiveRange;
        int right = (gRect.left+gRect.right)/2 + upDownArrow.getIntrinsicWidth()/2 + effectiveRange;
        int bottom = gRect.top+upDownArrow.getIntrinsicHeight() + effectiveRange;

        mRect.set( left, top, right, bottom );
        if( mRect.contains( x, y ) ){
            return MOVE_TOP_EDGE;
        }

        //下箭头
        left = (gRect.left+gRect.right)/2 - upDownArrow.getIntrinsicWidth()/2 - effectiveRange;
        top = gRect.bottom - upDownArrow.getIntrinsicHeight() - effectiveRange;
        right = (gRect.left+gRect.right)/2 + upDownArrow.getIntrinsicWidth()/2 + effectiveRange;
        bottom = gRect.bottom+effectiveRange;

        mRect.set( left, top, right, bottom );
        if( mRect.contains( x, y ) ){
            return MOVE_BOTTOM_EDGE;
        }
        //
        return grow;
    }

    private void growBy(int grow, float dx, float dy){

        float widthCap = 50F;        //captureRect最小宽度
        float heightCap = 50F;      //captureRect最小高度

        //Rect r = new Rect(viewRect);

        if( grow == MOVE_TOP_EDGE ){
            //if( dy < 0 && r.height() - dy >= viewRect.height() )//上边向上拉升
            //    dy = 0f;
            //r.top+= dy;
            mItemData.top += dy;
        }
        if( grow == MOVE_BOTTOM_EDGE ){
            //if( dy > 0 && r.height() + dy >= viewRect.height() )    //下边向下拉升
            //    dy = 0f;
            mItemData.bottom+= dy;
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if( layoutParams == null ){
            layoutParams = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        }
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height= (mItemData.bottom-mItemData.top+upDownArrow.getIntrinsicHeight());
        setLayoutParams( layoutParams );

        setY( mItemData.top - upDownArrow.getIntrinsicHeight()/2 );

        //当captureRect缩小到宽度 = widthCap时

        //同上
//        if (r.height() <= heightCap) {
//            r.inset(0F, -(heightCap - r.height()) / 2F);
//        }
//
//        if (r.left < viewRect.left) {
//            r.offset(viewRect.left - r.left, 0F);
//        } else if (r.right > viewRect.right) {
//            r.offset(-(r.right - viewRect.right), 0);
//        }
//        if (r.top < viewRect.top) {
//            r.offset(0F, viewRect.top - r.top);
//        } else if (r.bottom > viewRect.bottom) {
//            r.offset(0F, -(r.bottom - viewRect.bottom));
//        }
//
//        viewRect.set((int) r.left, (int) r.top, (int) r.right,
//                (int) r.bottom);
        invalidate();

    }
}
