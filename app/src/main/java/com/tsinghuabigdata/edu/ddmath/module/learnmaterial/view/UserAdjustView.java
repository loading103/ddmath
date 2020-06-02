package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.ConvexUtil;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.util.ArrayList;

/**
 * 用户可以任意调整的View
 */
@SuppressLint("DrawAllocation")
public class UserAdjustView extends View {

    // 触摸位置及动作
    public static final int GROW_NONE = 0;//框体外部
    public static final int GROW_LEFT_TOP = (1 << 1);//框体左上角
    public static final int GROW_RIGHT_TOP = (1 << 2);//框体右上角
    public static final int GROW_LEFT_BOTTOM = (1 << 3);//框体左下角
    public static final int GROW_RIGHT_BOTTOM = (1 << 4);//框体右下角
    //public static final int GROW_MOVE = (1 << 5);//框体移动

    private Paint outsideCapturePaint = new Paint(); // 捕获框体外部画笔
    private Paint lineCapturePaint = new Paint(); // 边框画笔

    private RectF viewRect; // 可视范围
    //private Rect captureRect; // 框体范围

    private int mMotionEdge; // 触摸的边缘
    private float mLastX, mLastY; // 上次触摸的坐标


    private Drawable circleArrows; // 左上角拉伸箭头
    private int cornerArrowsHalfWidth; // 拉伸箭头的宽
    private int cornerArrowsHalfHeight;// 拉伸箭头的高

    private UserAdjustView mCaptureView;
    //private CropToolView cropToolView;

    private ImageView relateView;

    private Path path;
    private Matrix matrix;
    private Paint paint;

    private ArrayList<Point> mPointList = new ArrayList<>();

    //外边框可以分为8个矩形，4个三角形
    ArrayList<Rect> rectlist = new ArrayList<>();
    ArrayList<Path> pathlist = new ArrayList<>();

    private Bitmap mBitmap;
    private boolean actionMoving = false;

    private enum ActionMode { // 枚举动作类型：无、移动、拉伸
        None, Move, Grow
    }

    private ActionMode mMode = ActionMode.None;

    public UserAdjustView(Context context) {
        super(context);
        initView();
    }

    public UserAdjustView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public UserAdjustView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public UserAdjustView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initView();
//    }

    public void setRelateView( ImageView view ){
        relateView = view;
    }

    public void setBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    private Path path1 = new Path();
    private float zoomRate = 1f;
    public void setData( ArrayList<Point> list, float rate ){

        zoomRate = rate;

        mPointList.clear();
        for( Point point : list ){
            Point np = new Point();
            np.x = (int)(point.x * rate);
            np.y = (int)(point.y * rate);
            AppLog.d(" ----- np.x = " + np.x + ",,, npy = " + np.y );
            mPointList.add( np );
        }

        path1.reset();
        if( mPointList!=null && mPointList.size()>=4 ){
            Point point = mPointList.get(0);
            path1.moveTo( point.x, point.y );
            point = mPointList.get(1);
            path1.lineTo( point.x, point.y );
            point = mPointList.get(2);
            path1.lineTo( point.x, point.y );
            point = mPointList.get(3);
            path1.lineTo( point.x, point.y );
            point = mPointList.get(0);
            path1.lineTo( point.x, point.y );
            path1.close();
        }
    }

    private LocalPageInfo localPageInfo;
    public void setLocalPageInfo( LocalPageInfo pageInfo ){
        localPageInfo = pageInfo;
    }

    public ArrayList<Point> getData(){

        for( Point point : mPointList ){
            point.x = (int)(point.x / zoomRate);
            point.y = (int)(point.y / zoomRate);
        }
        return mPointList;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int ttop         = Math.max( top, relateView.getTop() );
        int tleft        = Math.max( left, relateView.getLeft() );
        int tbottom      = Math.min( bottom, relateView.getBottom() );
        int tright      = Math.min( right, relateView.getRight() );

        //AppLog.d( "changed = " + changed + " top = " + top + ",, left = " + left + ", right = "+ right + ",, bottom="+bottom );
        //AppLog.d( "changed = " + changed + " ttop = "+ttop + ",, tleft = "+ tleft +", tright ="+ tright + ",,tbottom="+tbottom );

        // 初始化可视范围及框体大小  调整后的大小
        viewRect = new RectF(tleft, ttop, tright, tbottom);
        //lineCapturePaint.setShader( new LinearGradient( tleft, ttop, tright, tbottom, Color.argb(40,255,0,0), Color.argb(40,255,0,0), Shader.TileMode.CLAMP) )
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        //绘制检测出的四边形
        if( mPointList==null || mPointList.size()!=4 ) return;

        int margin_left=0, margin_top = 0;
        if( relateView!=null ){
            margin_left = relateView.getLeft();
            margin_top  = relateView.getTop();
        }

        //框体外背景色
        drawOuterBg( canvas, margin_left, margin_top );

        int linewidth = (int)lineCapturePaint.getStrokeWidth()/2;
        //上边线
        Point p1 = mPointList.get(0);
        Point p2 = mPointList.get(1);
        canvas.drawLine( p1.x+margin_left, p1.y+margin_top, p2.x+margin_left, p2.y+margin_top, lineCapturePaint);
        //左边线
        p2 = mPointList.get(3);
        canvas.drawLine( p1.x+margin_left, p1.y-linewidth+margin_top, p2.x+margin_left, p2.y+linewidth+margin_top, lineCapturePaint);
        //下边线
        p1 = mPointList.get(2);
        canvas.drawLine( p1.x+margin_left, p1.y+margin_top, p2.x+margin_left, p2.y+margin_top, lineCapturePaint);
        //右边线
        p2 = mPointList.get(1);
        canvas.drawLine( p1.x+margin_left, p1.y+linewidth+margin_top, p2.x+margin_left, p2.y-linewidth+margin_top, lineCapturePaint);

        canvas.restore();

        // 框体左上的箭头
        drawCircleArrow( canvas, mPointList.get(0), margin_left, margin_top );

        // 框体右上角的箭头
        drawCircleArrow( canvas, mPointList.get(1), margin_left, margin_top );

        // 框体左下角的箭头
        drawCircleArrow( canvas, mPointList.get(3), margin_left, margin_top );

        // 框体右下角的箭头
        drawCircleArrow( canvas, mPointList.get(2), margin_left, margin_top );

        //绘制局部放大图
        drawZoomView( canvas );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            int grow = getGrow(event.getX(), event.getY());
            if (grow != GROW_NONE) {
                // 锁定当前触摸事件的操作对象，直到ACTION_UP，如果没有锁定，有grow为前次操作的值。
                mCaptureView = UserAdjustView.this;
                mMotionEdge = grow;
                mLastX = event.getX();
                mLastY = event.getY();
                mCaptureView.setMode( ActionMode.Grow );
            }
            actionMoving = false;
            break;
        case MotionEvent.ACTION_UP:
            if (mCaptureView != null) {
                setMode(ActionMode.None);
                mCaptureView = null; // 释放当前锁定的操作对象
            }
            actionMoving = false;
            break;
        case MotionEvent.ACTION_MOVE: // 框体移动
            if (mCaptureView != null) {
                handleMotion(mMotionEdge, event.getX() - mLastX, event.getY()
                        - mLastY);
                mLastX = event.getX();
                mLastY = event.getY();

                if( localPageInfo != null)localPageInfo.setMoveFlagCache();
            }
            actionMoving = true;
            break;
        default:
            break;
        }
        return true;
    }

    //-----------------------------------------------------------------------------------
    private void initView() {
        lineCapturePaint.setStrokeWidth(4F); // 捕获框边框画笔大小
        lineCapturePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        lineCapturePaint.setAntiAlias(true); // 抗锯齿
        lineCapturePaint.setColor( getResources().getColor(R.color.color_48B8FF) ); // 画笔颜色

        Resources resources = getResources();

        circleArrows            = resources.getDrawable(R.drawable.ic_white_dot );
        cornerArrowsHalfWidth   = circleArrows.getIntrinsicWidth() / 2;
        cornerArrowsHalfHeight  = circleArrows.getIntrinsicHeight() / 2;
        setFullScreen(true); // 默认为全屏模式

        path = new Path();
        path.addCircle(RADIUS/2, RADIUS/2, RADIUS/2, Path.Direction.CW);
        matrix = new Matrix();
        matrix.setScale(FACTOR, FACTOR);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);

        //初始化
        for( int i=0; i<8; i++ ){
            rectlist.add( new Rect() );
        }
        for( int i=0; i<4; i++ ){
            pathlist.add( new Path() );
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void drawOuterBg( Canvas canvas, int marginLeft, int marginTop ) {

//        path path1 = new path();
//        Point point = mPointList.get(0);
//        path1.moveTo(  point.x, point.y );
//        point = mPointList.get(1);
//        path1.moveTo(  point.x, point.y );
//        point = mPointList.get(2);
//        path1.moveTo(  point.x, point.y );
//        point = mPointList.get(3);
//        path1.moveTo(  point.x, point.y );
//        path1.close();
//
//        path path2 = new path();
//        path2.addRect( viewRect, path.Direction.CW);
//        path2.close();
//
//        path1.op( path2, path.Op.XOR );
//        canvas.drawPath(path1, outsideCapturePaint);



        //---------------------------------------------------------------------------
        //上边线
        Point p1 = mPointList.get(0);
        Point p2 = mPointList.get(1);

        Rect rect = rectlist.get(0);
        rect.set( 0, 0, p1.x + marginLeft, p1.y + marginTop );
        rect = rectlist.get(1);
        rect.set( p2.x + marginLeft, 0, (int)viewRect.right + marginLeft, p2.y + marginTop );
        rect = rectlist.get(2);
        rect.set( p1.x + marginLeft, 0, p2.x + marginLeft, (p1.y < p2.y?p1.y:p2.y) + marginTop );

        //三角形
        Path path  = pathlist.get(0);
        path.reset();
        path.moveTo( p1.x + marginLeft, p1.y + marginTop );
        path.lineTo( p2.x + marginLeft, p2.y + marginTop );
        if( p1.y < p2.y ){
            path.lineTo( p2.x + marginLeft, p1.y + marginTop );
        }else{
            path.lineTo( p1.x + marginLeft, p2.y + marginTop );
        }
        path.close();

        //---------------------------------------------------------------------------
        //下边线
        p1 = mPointList.get(3);
        p2 = mPointList.get(2);
        rect = rectlist.get(3);
        rect.set( p1.x + marginLeft, (p1.y < p2.y?p2.y:p1.y) + marginTop, p2.x + marginLeft, (int)viewRect.bottom + marginTop );

        //三角形
        path  = pathlist.get(1);
        path.reset();
        path.moveTo( p1.x + marginLeft, p1.y + marginTop );
        path.lineTo( p2.x + marginLeft, p2.y + marginTop );
        if( p1.y < p2.y ){
            path.lineTo( p1.x + marginLeft, p2.y + marginTop );
        }else{
            path.lineTo( p2.x + marginLeft, p1.y + marginTop );
        }
        path.close();
        //---------------------------------------------------------------------------
        //左边线
        p1 = mPointList.get(0);
        p2 = mPointList.get(3);

        rect = rectlist.get(4);
        rect.set( 0/*(int)viewRect.left + marginLeft*/, p2.y + marginTop, p2.x + marginLeft, (int)viewRect.bottom + marginTop );
        rect = rectlist.get(5);
        rect.set( 0/*(int)viewRect.left + marginLeft*/, p1.y + marginTop, (p1.x < p2.x?p1.x:p2.x) + marginLeft, p2.y + marginTop );

        //三角形
        path  = pathlist.get(2);
        path.reset();
        path.moveTo( p1.x + marginLeft, p1.y + marginTop );
        path.lineTo( p2.x + marginLeft, p2.y + marginTop );
        if( p1.x < p2.x ){
            path.lineTo( p1.x + marginLeft, p2.y + marginTop );
        }else{
            path.lineTo( p2.x + marginLeft, p1.y + marginTop );
        }
        path.close();

        //---------------------------------------------------------------------------
        //右边线
        p1 = mPointList.get(1);
        p2 = mPointList.get(2);

        rect = rectlist.get(6);
        rect.set( p2.x + marginLeft, p2.y + marginTop, (int)viewRect.right + marginLeft, (int)viewRect.bottom + marginTop );
        rect = rectlist.get(7);
        rect.set( (p1.x<p2.x?p2.x:p1.x) + marginLeft, p1.y + marginTop, (int)viewRect.right + marginLeft, p2.y + marginTop );

        //三角形
        path  = pathlist.get(3);
        path.reset();
        path.moveTo( p1.x + marginLeft, p1.y + marginTop );
        path.lineTo( p2.x + marginLeft, p2.y + marginTop );
        if( p1.x < p2.x ){
            path.lineTo( p2.x + marginLeft, p1.y + marginTop );
        }else{
            path.lineTo( p1.x + marginLeft, p2.y + marginTop );
        }
        path.close();

        //---------------------------------------------------------------------
        //绘制
        for( Rect rect1 : rectlist ){
            canvas.drawRect( rect1, outsideCapturePaint);
        }
        for( Path path1 : pathlist ){
            canvas.drawPath( path1, outsideCapturePaint);
        }
    }




    private void setFullScreen(boolean full) {
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
        int grow = GROW_NONE;

        if( mPointList==null || mPointList.size()!=4 ) return grow;

        //左上角点
        Point point = mPointList.get(0);
        if( isAroundPoint( point, x, y ) ){
            grow = GROW_LEFT_TOP;
        }

        //右上角
        point = mPointList.get(1);
        if( isAroundPoint( point, x, y ) ){
            grow = GROW_RIGHT_TOP;
        }

        //右下角
        point = mPointList.get(2);
        if( isAroundPoint( point, x, y ) ){
            grow = GROW_RIGHT_BOTTOM;
        }

        //左下角
        point = mPointList.get(3);
        if( isAroundPoint( point, x, y ) ){
            grow = GROW_LEFT_BOTTOM;
        }
        return grow;
    }

    private boolean isAroundPoint(Point point, float x, float y ){
        final float effectiveRange = 60F; // 触摸的有效范围大小

        int margin_left=0, margin_top = 0;
        if( relateView!=null ){
            margin_left = relateView.getLeft();
            margin_top  = relateView.getTop();
        }

        RectF rectF = new RectF();
        rectF.left = point.x - effectiveRange + margin_left;
        rectF.right= point.x + effectiveRange + margin_left;
        rectF.top  = point.y - effectiveRange + margin_top;
        rectF.bottom=point.y + effectiveRange + margin_top;
        return  rectF.contains( x, y);
    }


    // 处理触摸事件，判断移动框体还是伸缩框体
    private void handleMotion(int grow, float dx, float dy) {
            growBy( grow, dx, dy);
    }

    private void growBy(int grow, float dx, float dy){

        if( mPointList==null || mPointList.size()!=4 ) return;

        if( (grow & GROW_LEFT_TOP) == GROW_LEFT_TOP   ){
            controlPointArea( mPointList.get(0), dx, dy );
        }
        else if( (grow&GROW_RIGHT_TOP) == GROW_RIGHT_TOP  ){
            controlPointArea( mPointList.get(1), dx, dy );
        }
        else if( (grow&GROW_LEFT_BOTTOM) == GROW_LEFT_BOTTOM ){
            controlPointArea( mPointList.get(3), dx, dy );
        }
        else if( (grow&GROW_RIGHT_BOTTOM) == GROW_RIGHT_BOTTOM ){
            controlPointArea( mPointList.get(2), dx, dy );
        }

        invalidate();
    }

    //控制四个点显示的范围
    private void controlPointArea( Point point, float dx, float dy ){

        int xbak = point.x, ybak = point.y;

        int margin_left=0, margin_top = 0;
        if( relateView!=null ){
            margin_left = relateView.getLeft();
            margin_top  = relateView.getTop();
        }

        int nx = (int)(point.x + dx + 0.5f) + margin_left;
        int ny = (int)(point.y + dy + 0.5f) + margin_top;

        if( nx < viewRect.left ) nx = (int)viewRect.left;
        else if( nx > viewRect.right ) nx = (int)viewRect.right;

        if( ny < viewRect.top ) ny = (int)viewRect.top;
        else if( ny > viewRect.bottom ) ny = (int)viewRect.bottom;

        point.x = nx - margin_left;
        point.y = ny - margin_top;

        //判断四个点能否构成凸四边形, 不能构成的话，恢复原样
        //long time = System.currentTimeMillis();
        boolean success = judgeFourPointConvex();
        //AppLog.d(" judgeFourPointConvex success = " + success + " time = " + (System.currentTimeMillis()-time) );
        if( !success ){
            point.x = xbak;
            point.y = ybak;
        }
    }

    ConvexUtil convexUtil = new ConvexUtil();
    private ArrayList<Point> points = new ArrayList<>();
    private boolean judgeFourPointConvex() {

        if( points.size() == 0 ){
            points.add( mPointList.get(0) );
            points.add( mPointList.get(3) );
            points.add( mPointList.get(2) );
            points.add( mPointList.get(1) );
        }
        return convexUtil.judgeConvex( points );
    }


    private static final int RADIUS = 120;// 放大镜半径
    private static final int FACTOR = 2; // 放大系数
    private static final int STROKE = 4; // 附加线条宽度
    private void drawZoomView( Canvas canvas ){
        //
        if( !actionMoving ) return;

        Point point = null;
        if( (mMotionEdge & GROW_LEFT_TOP) == GROW_LEFT_TOP   ){
            point = mPointList.get(0);
        }
        else if( (mMotionEdge&GROW_RIGHT_TOP) == GROW_RIGHT_TOP  ){
            point = mPointList.get(1);
        }
        else if( (mMotionEdge&GROW_LEFT_BOTTOM) == GROW_LEFT_BOTTOM ){
            point = mPointList.get(3);
        }
        else if( (mMotionEdge&GROW_RIGHT_BOTTOM) == GROW_RIGHT_BOTTOM ){
            point = mPointList.get(2);
        }
        if( point == null || relateView == null) return;

        Bitmap bitmap = getTargetBitmap( point );
        if( bitmap==null ) return;

        // 移动到path剪切部分显示
        Point tp = getZoomViewPoint( point, 10, 10 );
        canvas.translate( tp.x, tp.y );
        canvas.drawBitmap( bitmap, matrix, null);

        //画十字星
        Path line = new Path();
        line.moveTo(RADIUS/2, RADIUS);
        line.lineTo(RADIUS/2+RADIUS, RADIUS);
        line.moveTo(RADIUS, RADIUS/2);
        line.lineTo(RADIUS, RADIUS/2+RADIUS );
        canvas.drawPath(line, paint);

        //画圆
        canvas.drawCircle(RADIUS, RADIUS, RADIUS - STROKE, paint);

    }

    //取出的图进行放大2倍处理
    private Bitmap dstBitmap = Bitmap.createBitmap( RADIUS, RADIUS, Bitmap.Config.ARGB_8888 );
    private Rect   dstRect = new Rect(0,0,dstBitmap.getWidth(), dstBitmap.getHeight() );
    private Rect   srcRect = new Rect();

    private Bitmap getTargetBitmap( Point point ){
        if( mBitmap == null ) return null;

        Canvas canvas = new Canvas( dstBitmap );
        int x = (int)(point.x/zoomRate) - RADIUS/2;
        int y = (int)(point.y/zoomRate) - RADIUS/2;
        if( x < 0 ) x = 0;
        if( x+RADIUS > mBitmap.getWidth() ) x = mBitmap.getWidth() - RADIUS;
        if( y < 0 ) y = 0;
        if( y+RADIUS > mBitmap.getHeight() ) y = mBitmap.getHeight() - RADIUS;

        srcRect.set( x, y, x+RADIUS, y+RADIUS );

        canvas.clipPath(path);
        canvas.drawBitmap( mBitmap, srcRect, dstRect, null );
        return dstBitmap;
    }

    private void drawCircleArrow( Canvas canvas, Point point, int margin_left, int margin_top ){
        circleArrows.setBounds(
                point.x - circleArrows.getIntrinsicWidth() + cornerArrowsHalfWidth+margin_left,
                point.y - cornerArrowsHalfHeight+margin_top,//
                point.x + cornerArrowsHalfWidth+margin_left,//
                point.y + circleArrows.getIntrinsicHeight() - cornerArrowsHalfHeight+margin_top);
        circleArrows.draw(canvas);
    }

    private Point getZoomViewPoint( Point point, int x, int y ){
        Point tp = new Point();
        if( (point.x < 2*RADIUS && point.y<2*RADIUS) || (x > point.x - RADIUS && x < point.x+RADIUS && y>point.y-RADIUS && y<point.y+RADIUS) ){
            x = getWidth() - 2*RADIUS;
        }
        tp.set( x, y );
        return tp;
    }
}
