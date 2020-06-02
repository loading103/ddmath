package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionRect;
import com.tsinghuabigdata.engine.util.TFProvider;

import java.util.ArrayList;
import java.util.List;


public class WorkSplitView extends View {

    //显示模型
    public static final int MODE_BLACK      = 0;      //框体外部
    public static final int MODE_WHITE      = 1;      //框体外部
    public static final int MODE_ACTIVIE    = 2;    //框体外部

    //移动模式
    public static final int TOUCH_NONE           = -1;//框体外部
    public static final int TOUCH_TOP_EDGE = -2;   //框体上边
    public static final int TOUCH_BOTTOM_EDGE    = -3;//框体下边


    private Paint coverPaint = new Paint(); // 覆盖画笔
    //private Paint coverPaint1 = new Paint(); // 覆盖画笔
    private Paint borderPaint = new Paint(); // 白色边框画笔
    private Paint activePaint = new Paint(); // 激活边框画笔

    private Rect mRect = new Rect();
    private Rect gRect = new Rect();
    
    private Rect upArrowRect = new Rect();
    private Rect downArrowRect = new Rect();

    private int mMotionEdge; // 触摸的边缘

//    private PorterDuffXfermode pdf;

    private Drawable upDownArrow; // 上下拉伸箭头

    //对象
    private ArrayList<ItemData> mList = new ArrayList<>();

	public WorkSplitView(Context context) {
		super(context);
		init();
	}

	public WorkSplitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public WorkSplitView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

    public ItemData getItemData( int index ){
        if( index < 0 || index >= mList.size() ) return null;
        return mList.get( index );
    }


    private List<TFProvider.Recognition> resultList;
    public void setTestData(List<TFProvider.Recognition> resultList){
        this.resultList = resultList;
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //先绘制
        for( ItemData itemData : mList ){
            draw( canvas, itemData );
        }
        //后绘制拖动区域
        for( ItemData itemData : mList ){
            drawDragImage( canvas, itemData );
        }
        if( resultList!=null ){
            for( TFProvider.Recognition recognition:resultList ){
                draw( canvas, recognition );
            }
        }
        canvas.restore();
    }

    private float mLastY;
    private long startPressTime = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mMotionEdge = getGrow( (int)event.getRawX(), (int)event.getRawY() );
                mLastY = event.getY();
                startPressTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                if( System.currentTimeMillis() - startPressTime < 300 ){
                    dealOnClick();
                }
                mMotionEdge = TOUCH_NONE;
                break;
            case MotionEvent.ACTION_MOVE: // 框体移动
                growBy( mMotionEdge, (int)(event.getY() - mLastY) );
                mLastY = event.getY();
                break;
            default:
                break;
        }
        return true;
    }


    public void setSplitData(ArrayList<LocalQuestionInfo> qlist, int imageHeight ){

        boolean firstAnswer = true;
        int preBottom = 0;      //前一个区域的bottom

        for( LocalQuestionInfo qustionInfo : qlist ){

            QuestionRect answerRect = qustionInfo.getQuestionRect();
            boolean chooseQuestion  = "choice".equals( qustionInfo.getQuestionType() );         //选择题不能调整选择框位置
            if( answerRect != null ){
                //题目区域
                int qtop     = preBottom;
                int qbottom  = (int) (answerRect.getY() * imageHeight);
                //int atop     = qbottom;
                int abottom  = qbottom + (int)(answerRect.getHeight() * imageHeight);

                preBottom = abottom;
                //
                mList.add( new ItemData( qtop, qbottom, answerRect.getX(), answerRect.getWidth(), SplitItemView.MODE_BLACK ) );         //题干区域
                if( firstAnswer ){
                    firstAnswer = false;
                    ItemData itemData = new ItemData( qbottom, abottom, answerRect.getX(), answerRect.getWidth(), SplitItemView.MODE_ACTIVIE, !chooseQuestion );
                    itemData.setQuestionInfo( qustionInfo );
                    mList.add( itemData ); //答案区域，激活才可以调整
                }else{
                    ItemData itemData = new ItemData( qbottom, abottom, answerRect.getX(), answerRect.getWidth(), SplitItemView.MODE_WHITE, !chooseQuestion );
                    itemData.setQuestionInfo( qustionInfo );
                    mList.add( itemData );   //答案区域
                }
            }
        }

        //下面未使用区域遮盖
        mList.add( new ItemData( preBottom, imageHeight, 0, 1, SplitItemView.MODE_BLACK ) );

        invalidate();
    }
	//-------------------------------------------------------------------------
	private void init(){

//        pdf = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);


        //coverPaint1.setARGB(255, 35, 35, 35);
        //coverPaint1.setXfermode(pdf);
        //coverPaint1.setStyle(Paint.Style.STROKE);

        borderPaint.setStrokeWidth(8); // 捕获框边框画笔大小
        borderPaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        borderPaint.setAntiAlias(true); // 抗锯齿
        borderPaint.setColor( getResources().getColor(R.color.color_FFE963) ); // 画笔颜色

        activePaint.setStrokeWidth(8); // 捕获框边框画笔大小
        activePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        activePaint.setAntiAlias(true); // 抗锯齿
        activePaint.setColor( getResources().getColor(R.color.color_58E6FF)); // 画笔颜色

        //默认 0.4 #232323
        coverPaint.setARGB(40/* 100*/, 35, 35, 35);
        coverPaint.setStyle(Paint.Style.FILL );

        Resources resources = getResources();
        upDownArrow = resources.getDrawable(R.drawable.ic_up_down );


//        for( int i=0; i<mList.size(); i++ ){
//            SplitItemView itemView = new SplitItemView(getContext());
//            ItemData itemData = mList.get(i);
//            itemView.setShowMode( itemData );
//            addView( itemView );
//        }
	}


    private void draw( Canvas canvas, ItemData itemData ){

        //setShowMode( itemData );

        mRect.set( 2, itemData.top+1, getRight()-2, itemData.bottom-1 );
        if( MODE_BLACK == itemData.mode ){
            //绘制半透明覆盖层
            canvas.drawRect(mRect, coverPaint);
        }else if( MODE_WHITE == itemData.mode ){

            //绘制透明覆盖层
            //canvas.drawRect(mRect, coverPaint1 );

            //先绘制边框
            canvas.drawRect(mRect, borderPaint );

        }else{
            //先绘制边框
            canvas.drawRect(mRect, activePaint );
        }
    }

    private void draw(Canvas canvas, TFProvider.Recognition recognition){
        RectF rectF = recognition.getLocation();
        mRect.set( (int)(rectF.left*getWidth()), (int)(rectF.top*getHeight()), (int)(rectF.right*getWidth()), (int)(rectF.bottom*getHeight()) );
        //绘制边框
        borderPaint.setColor( Color.RED );
        canvas.drawRect(mRect, borderPaint );
    }
    //绘制
    private void drawDragImage( Canvas canvas, ItemData itemData ){

        //setShowMode( itemData );

        mRect.set( 2, itemData.top+1, getRight()-2, itemData.bottom-1 );
        if( MODE_ACTIVIE == itemData.mode && itemData.enable ){

            int offset_height = upDownArrow.getIntrinsicHeight()/2+2;

            //绘制上箭头
            upArrowRect.set((mRect.left+ mRect.right)/2 - upDownArrow.getIntrinsicWidth()/2,//
                    mRect.top - offset_height,//
                    (mRect.left+ mRect.right)/2 + upDownArrow.getIntrinsicWidth()/2,
                    mRect.top + offset_height );
            upDownArrow.setBounds( upArrowRect );
            upDownArrow.draw(canvas);

            //绘制下箭头
            downArrowRect.set((mRect.left+ mRect.right)/2 - upDownArrow.getIntrinsicWidth()/2,//
                    mRect.bottom - offset_height,//
                    (mRect.left+ mRect.right)/2 + upDownArrow.getIntrinsicWidth()/2,
                    mRect.bottom + offset_height );
            upDownArrow.setBounds( downArrowRect );
            upDownArrow.draw(canvas);
        }
    }

    private void dealOnClick(){
        if( mMotionEdge < 0 )
            return;

        if( mMotionEdge>=0 && mMotionEdge < mList.size() ){
            for( ItemData itemdata : mList ) {
                if( itemdata.mode == MODE_ACTIVIE )
                    itemdata.mode = MODE_WHITE;
            }
            mList.get( mMotionEdge ).mode = MODE_ACTIVIE;
            invalidate();
        }
    }

    private int getGrow(int x, int y) {

        //转换为相对本身的坐标
        getGlobalVisibleRect( gRect );
        x = x - gRect.left;
        y = y - gRect.top;

        final int effectiveRange = 20; // 触摸的有效范围大小
        //int grow = TOUCH_NONE;

        //gRect.set( x-effectiveRange, y - effectiveRange, x+effectiveRange, y+effectiveRange );      //触摸区域：箭头区域附件
        gRect.set( getLeft(), y - effectiveRange, getRight(), y+effectiveRange );        //触摸区域  上下框附件

        //上箭头
        if( upArrowRect.intersect( gRect) ){
            return TOUCH_TOP_EDGE;
        }

        //下箭头
        if( downArrowRect.intersect( gRect) ){
            return TOUCH_BOTTOM_EDGE;
        }

        //判断是不是触摸每个对象区域
        for( int i=0; i<mList.size(); i++ ){
            ItemData itemdata = mList.get(i);
            if( y >= itemdata.top && y <= itemdata.bottom ){
                if( itemdata.mode != MODE_BLACK ) return i;
                else return TOUCH_NONE;
            }
        }
        return TOUCH_NONE;
    }

    private void growBy(int grow, int dy){

        if( grow >= TOUCH_NONE )
            return;

        int index = getSelectItemData();
        if( index < 0 ) return;
        //
        if( grow == TOUCH_TOP_EDGE){

            ItemData itemData = mList.get(index);
            if( itemData.getQuestionInfo()!=null )
                itemData.getQuestionInfo().setMoveFlagCache();
            int top = itemData.top + dy;

            //向下移动 不能大于 bottom
            if( dy > 0 ){
                if( top + upDownArrow.getIntrinsicHeight() > itemData.bottom )
                    return;
                else{
                    itemData.top = top;
                    if( index > 0 )
                        mList.get(index-1).bottom = top;        //更新前一个的bottom
                }
            }else{  //向上移动，不能小于 前一个 空白模式的 的bottom

                if( index == 0 ){
                    if( top < 0 )
                        top = 0;
                    itemData.top = top;
                }else if( index == 1 ){
                    if( top < 0 ) top = 0;
                    itemData.top = top;
                    //
                    ItemData preItemData = mList.get(index-1);
                    preItemData.bottom = top;        //更新前一个的bottom
                    if( preItemData.top > preItemData.bottom )
                        preItemData.top = preItemData.bottom;
                }else{
                    ItemData preItemData = mList.get(index-2);
                    if( top - upDownArrow.getIntrinsicHeight() < preItemData.bottom )
                        return;
                    itemData.top = top;
                    mList.get(index-1).bottom = top;        //更新前一个的bottom
                }
            }


        }else if( grow == TOUCH_BOTTOM_EDGE ){

            ItemData itemData = mList.get(index);
            int bottom = itemData.bottom + dy;

            //向上移动 不能大于 本区域 top
            if( dy < 0 ){
                if( itemData.top + upDownArrow.getIntrinsicHeight() > bottom  )
                    return;
                else{
                    itemData.bottom = bottom;
                    if( index+1 < mList.size() )
                        mList.get(index+1).top = bottom;        //更新下一个的top
                }
            }else{  //向下移动，不能大于 后一个 空白模式的 的top

                if( index+1 == mList.size() ){      //是最后一个对象
                    if( bottom > getBottom() )
                        bottom = getBottom();
                    itemData.bottom = bottom;
                }else if( index+2 == mList.size() ){    //倒数第二个，  倒数第一个是不同模式，不用处理
                    if( bottom > getBottom() )
                        bottom = getBottom();
                    itemData.bottom = bottom;
                    //
                    ItemData preItemData = mList.get(index+1);
                    preItemData.top = bottom;        //更新下一个的top
                    if( preItemData.top > preItemData.bottom )
                        preItemData.top = preItemData.bottom;
                }else{      //
                    ItemData preItemData = mList.get(index+2);
                    if( bottom + upDownArrow.getIntrinsicHeight() > preItemData.top )
                        return;
                    itemData.bottom = bottom;
                    mList.get(index+1).top = bottom;        //更新前一个的bottom
                }
            }
        }
        invalidate();
    }

    private int getSelectItemData(){

        for( int i=0; i < mList.size(); i++ ){
            ItemData itemdata = mList.get(i);
            if( itemdata.mode == MODE_ACTIVIE && itemdata.enable ) return i;
        }
        return -1;
    }

    //-------------------------------------------------------------------------

    public class ItemData{
        int top;
        int bottom;
        float left;
        float width;

        int mode;           //显示模式

        boolean enable;     //是否可以移动位置
        LocalQuestionInfo questionInfo;

        /*public*/ ItemData( int top, int bottom, float l, float w, int mode ){
            this.top = top;
            this.bottom = bottom;
            this.mode = mode;
            this.left = l;
            this.width = w;
            this.enable = false;
        }
        /*public*/ ItemData( int top, int bottom, float l, float w, int mode, boolean enable ){
            this.top = top;
            this.bottom = bottom;
            this.mode = mode;
            this.left = l;
            this.width = w;
            this.enable = enable;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public float getLeft() {
            return left;
        }

        public void setLeft(float left) {
            this.left = left;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public LocalQuestionInfo getQuestionInfo() {
            return questionInfo;
        }

        public void setQuestionInfo(LocalQuestionInfo questionInfo) {
            this.questionInfo = questionInfo;
        }
    }


}
