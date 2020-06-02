package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.LMPreviewActivity;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AnswerAreaBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.AreaInfoBean;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.engine.util.TFProvider;

import java.util.ArrayList;
import java.util.List;

public class AnswerSplitView extends View {

    private static final float zoom_rate = 0.50f;

    //显示模型
    public static final int MODE_NORMAL     = 0;      //普通模式
    public static final int MODE_ACTIVIE    = 1;      //选中模式

    private static final int COLOR_1 = Color.rgb( 0x00, 0xEF, 0xD0 );
    private static final int COLOR_2 = Color.rgb( 0xEE, 0x77, 0x85 );

    private Paint borderPaint = new Paint(); // 白色边框画笔
    //private Paint activePaint = new Paint(); // 激活边框画笔
    private Paint textPaint   = new Paint(); //绘制文本
    private Paint coverPaint = new Paint(); // 覆盖画笔

    private Rect mRect = new Rect();

    //点击调整时使用
    private ItemData currItemData;          //调整时的对象
    private Rect zoomRect;                  //扩大20%的rect

    private LMPreviewActivity mActivity;

    //对象
    private ArrayList<ItemData> mList = new ArrayList<>();

	public AnswerSplitView(Context context) {
		super(context);
		init();
	}

	public AnswerSplitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AnswerSplitView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

//    public ItemData getItemData( int index ){
//        if( index < 0 || index >= mList.size() ) return null;
//        return mList.get( index );
//    }
    public void setActivity( LMPreviewActivity activity){
        mActivity = activity;
    }

    //
    private boolean rectColor = false;

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

    //------------------------------------------------------------------------------------
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //先绘制
        for( ItemData itemData : mList ){
            draw( canvas, itemData );
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
                mLastY = event.getY();
                startPressTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP: // 框体移动
                if( System.currentTimeMillis() - startPressTime < 300 && Math.abs(mLastY-event.getY()) < 5 ){
                    dealOnClick( event.getX(), event.getY() );
                }
                mLastY = event.getY();
                break;
            default:
                break;
        }
        return true;
    }

    //设置显示方框数据
    public void setData(ArrayList<LocalQuestionInfo> qlist ){

        mList.clear();

        int viewWidth = getWidth();
        int viewHeight= getHeight();

        //先把区域信息集中，进行处理
        ArrayList<AnswerAreaBean> answerAreaList = new ArrayList<>();
        ArrayList<AnswerAreaBean> figureAreaList = new ArrayList<>();
        for( LocalQuestionInfo qustionInfo : qlist ) {

            AreaInfoBean areaInfo = qustionInfo.getAreaInfoBean();
            if( areaInfo == null ) continue;

            if( areaInfo.getAnswerAreas() != null ){
                qustionInfo.setAnswerAreaList( areaInfo.getAnswerAreas() );
                answerAreaList.addAll( areaInfo.getAnswerAreas() );
            }
            if( areaInfo.getFigureAreas()!=null ){
                qustionInfo.setFigureAreaList( areaInfo.getFigureAreas() );
                figureAreaList.addAll( areaInfo.getFigureAreas() );
            }
        }

        //先处理答题区域
        long time = System.currentTimeMillis();
        ArrayList<RectF> areaList   = OpenCVHelper.getAnswerAreas( answerAreaList, figureAreaList );
        AppLog.d(" answerAreaList time = " + ( System.currentTimeMillis()-time) );
        AppLog.d(" answerAreaList size =  " + answerAreaList.size() + " figureAreaList size =  " + figureAreaList.size() + ",,,, areaList size =  "+(areaList==null?0:areaList.size()) );

        //个数必须保持一致
        if( areaList==null || ( areaList.size() != (answerAreaList.size()+figureAreaList.size()) ) )
            return;
        //分拆数据
        ArrayList<RectF> figureList = new ArrayList<>();
        int count = figureAreaList.size();
        if( count > 0 ){
            for( int i=0; i<count; i++ ){
                RectF rectF = areaList.get( areaList.size()-1 );//最后一个
                figureList.add( 0, rectF );
                areaList.remove( rectF );
            }
        }
        AppLog.d(" answerAreaList size =  " + answerAreaList.size() + ",,,, areaList size =  "+(areaList.size()) );
        AppLog.d(" figureAreaList size =  " + figureAreaList.size() + ",,,, figureList size =  "+(figureList.size()) );

        if( answerAreaList.size()!=areaList.size() || figureList.size()!=figureAreaList.size() )
            return;

        int indexAnswerArea = 0;
        int indexFigureArea = 0;
        for( LocalQuestionInfo qustionInfo : qlist ){

            int mode = qustionInfo.isSelectCache()?MODE_ACTIVIE:MODE_NORMAL;

            //先处理答题区域
            answerAreaList = qustionInfo.getAnswerAreaList();
            if( answerAreaList != null ){

                for( AnswerAreaBean areaBean : answerAreaList ){
                    int serialNum = areaBean.getSerialNum();
                    RectF rectF = areaList.get( indexAnswerArea++ );
                    int left    = (int)(rectF.left * viewWidth);
                    int top     = (int)(rectF.top * viewHeight);
                    int right   = (int)(rectF.right * viewWidth);
                    int bottom  = (int)(rectF.bottom * viewHeight);
                    ItemData itemData = new ItemData( left, top, right, bottom, mode, qustionInfo.getNumInPaper(), qustionInfo.getIndexInPaper(), serialNum );
                    itemData.setColor( rectColor?COLOR_1:COLOR_2 );
                    itemData.setLocalQuestionInfo( qustionInfo );
                    mList.add( itemData );
                    rectColor = !rectColor;
                }
            }

            //再处理辅助图区域
            answerAreaList = qustionInfo.getFigureAreaList();
            if( answerAreaList != null ){
                for( AnswerAreaBean areaBean : answerAreaList ){
                    int serialNum = areaBean.getSerialNum();
                    RectF rectF = figureList.get( indexFigureArea++ );
                    int left    = (int)(rectF.left * viewWidth);
                    int top     = (int)(rectF.top * viewHeight);
                    int right   = (int)(rectF.right * viewWidth);
                    int bottom  = (int)(rectF.bottom * viewHeight);

                    ItemData itemData = new ItemData( left, top, right, bottom, mode, qustionInfo.getNumInPaper(), qustionInfo.getIndexInPaper(), serialNum, ItemData.TYPE_FIGURE );
                    itemData.setColor( rectColor?COLOR_1:COLOR_2 );
                    itemData.setLocalQuestionInfo( qustionInfo );
                    mList.add( itemData );
                    rectColor = !rectColor;
                }
            }
        }
        invalidate();
    }

    public void updateSelectItem( String numInPaper, boolean selected ){
        if(TextUtils.isEmpty(numInPaper)) return;

        for( ItemData itemData : mList ){
            if( numInPaper.equals( itemData.getNums() ) ){
                itemData.mode = selected?MODE_ACTIVIE:MODE_NORMAL;
            }
        }
        invalidate();
    }

    /**
     * 更新方框
     * @param rectF 相对zoomRect的比例
     */
    //
    public void updateData( RectF rectF ){

        if( currItemData == null || zoomRect == null ) return;

        currItemData.rect.left  = (int)(rectF.left * zoomRect.width() );
        currItemData.rect.right = (int)(rectF.right * zoomRect.width() );
        currItemData.rect.top   = (int)(rectF.top * zoomRect.height() );
        currItemData.rect.bottom= (int)(rectF.bottom * zoomRect.height() );

        currItemData.rect.offset( zoomRect.left, zoomRect.top );
        //触发更新
        invalidate();
    }

    /**
     * 获取到指定的对象列表
     */
    public ArrayList<ItemData> getItemList( String chapterName, String sectionName, int indexInPaper, int type ){
        ArrayList<ItemData> list = new ArrayList<>();
        for( ItemData itemData : mList ){
            if( itemData.type == type && itemData.indexInPaper == indexInPaper && itemData.localQuestionInfo.getChapterName().equals(chapterName) && itemData.localQuestionInfo.getSectionName().equals(sectionName) ){
                list.add( itemData );
            }
        }
        return list;
    }

    /**
     * 获得当前对象 对应的题号
     */
    public String getQuestionNumInPaper(){
        if( currItemData != null){
            return currItemData.getNums();
        }
        return "";
    }

    /**
     * 查询是否有上一个显示框
     */
//    public boolean hasPreRect() {
//        if( currItemData != null){
//            int index = mList.indexOf( currItemData );
//            if( index <=0 ) return false;
//            for( int i=index-1; i>=0; i-- ){
//                if( mList.get(i).mode == MODE_ACTIVIE )
//                    return true;
//            }
//        }
//        return false;
//    }
    public synchronized boolean doPreRect(){
        if( currItemData != null){
            int index = mList.indexOf( currItemData );
            if( index <=0 ) return false;
            for( int i=index-1; i>=0; i-- ){
                if( mList.get(i).mode == MODE_ACTIVIE ){
                    currItemData = mList.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 查询是否有下一个显示框
     */
//    public boolean hasNextRect() {
//        if( currItemData != null){
//            int index = mList.indexOf( currItemData );
//            if( index <0 || index >= mList.size()-1 ) return false;
//            for( int i=index+1; i<mList.size(); i++ ){
//                if( mList.get(i).mode == MODE_ACTIVIE )
//                    return true;
//            }
//        }
//        return false;
//    }
    public synchronized boolean doNextRect() {
        if( currItemData != null){
            int index = mList.indexOf( currItemData );
            if( index <0 || index >= mList.size()-1 ) return false;
            for( int i=index+1; i<mList.size(); i++ ){
                if( mList.get(i).mode == MODE_ACTIVIE ){
                    currItemData = mList.get(i);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     */
    public void showEditView(){
        if( currItemData != null ){

            Rect rect = currItemData.getRect();
            //放大后的区域
            zoomRect = new Rect();
            //目标区域相对新截图的区域
            RectF dstRect  = new RectF();

            //对长宽各扩大 20% ，生成新的区域，
            int dw = (int)(rect.width() * zoom_rate );
            if( dw < 25 ) dw = 25;
            //计算,取小，边缘情况 按实际情况进行放大处理
            int dleft = Math.min( rect.left, dw );
            int dright= Math.min( getWidth()-rect.right, dw );

            //
            zoomRect.left = rect.left - dleft;
            zoomRect.right= rect.right + dright;
            dstRect.left = dleft * 1f / zoomRect.width();
            dstRect.right= 1 - dright * 1f / zoomRect.width();

            //同理
            int dh = (int)(rect.height() * zoom_rate );
            if( dh < 25 ) dh = 25;
            int dtop   = Math.min( rect.top, dh );
            int dbottom= Math.min( getHeight()-rect.bottom, dh );

//            if( dbottom <= dtop )
//                dbottom = dtop + 25;

            //
            zoomRect.top = rect.top - dtop;
            zoomRect.bottom= rect.bottom + dbottom;
            dstRect.top = dtop * 1f / zoomRect.height();
            dstRect.bottom= 1 - dbottom * 1f / zoomRect.height();
            if( dstRect.bottom <= dstRect.top ){
                dstRect.top = 0.0f;
                dstRect.bottom = 1f;
            }

            if( mActivity!=null ) mActivity.adjustAnswerRect( zoomRect, dstRect, currItemData );
        }
    }

    //-------------------------------------------------------------------------
	private void init(){

        borderPaint.setStrokeWidth( GlobalData.isPad()?6:4); // 捕获框边框画笔大小
        borderPaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        borderPaint.setAntiAlias(true); // 抗锯齿
        borderPaint.setColor( Color.rgb(0xFF,0x95,0x23 ) ); // 画笔颜色

        //activePaint.setStrokeWidth(4); // 捕获框边框画笔大小
        //activePaint.setStyle(Paint.Style.STROKE);// 画笔风格:空心
        //activePaint.setAntiAlias(true); // 抗锯齿
        //activePaint.setColor( Color.rgb(0x48,0xB8,0xFF ) ); // 画笔颜色

        textPaint.setTextSize(DensityUtils.sp2px(getContext(), GlobalData.isPad()?32:24));
        textPaint.setColor( Color.rgb(0x48,0xB8,0xFF ) );

        coverPaint.setARGB(100, 166, 234, 251);
	}

	Rect tRect = new Rect();
    private void draw( Canvas canvas, ItemData itemData ){

        //没有选中的不显示
        if( MODE_NORMAL == itemData.mode )
            return;

        Rect rect = itemData.getRect();
        mRect.set( rect.left+1, rect.top+1, rect.right-2, rect.bottom-2 );

        //canvas.drawRect(mRect, coverPaint);

        //绘制边框
        borderPaint.setColor( itemData.getColor() );
        canvas.drawRect(mRect, borderPaint );

        //绘制文本
        String txt = itemData.getNums();
        textPaint.getTextBounds( txt, 0, txt.length(), tRect);

        //计算文本的位置
        textPaint.setColor( itemData.getColor() );
        int x = rect.left + (rect.width()-tRect.width()) / 2;
        int y = rect.top + (rect.height()-tRect.height()) / 2 + tRect.height();
        canvas.drawText( txt, x, y , textPaint );
    }
    private void draw(Canvas canvas, TFProvider.Recognition recognition){
        RectF rectF = recognition.getLocation();
        mRect.set( (int)(rectF.left*getWidth()), (int)(rectF.top*getHeight()), (int)(rectF.right*getWidth()), (int)(rectF.bottom*getHeight()) );
        //绘制边框
        borderPaint.setColor( Color.RED );
        canvas.drawRect(mRect, borderPaint );
    }
    private synchronized void dealOnClick( float x, float y){

        //先找到最合适的框
        currItemData = null;
        for( ItemData item : mList ){
            Rect rect = item.getRect();
            if( item.mode == MODE_ACTIVIE && rect.contains( (int)x, (int)y ) ){

                if( currItemData == null )
                    currItemData = item;
                //如果多个框，寻找面积最小的那个
                else{
                    int area = rect.width() * rect.height();
                    int area1= currItemData.rect.width() * currItemData.rect.height();
                    if( area1 > area ){
                        currItemData = item;
                    }
                }
            }
        }
        showEditView();
    }

    //-------------------------------------------------------------------------

    public class ItemData{

        public static final int TYPE_ANSWER = 0;
        public static final int TYPE_FIGURE = 1;

        Rect rect = new Rect();

        int mode;           //显示模式
        String nums;       //题目序号
        int type = TYPE_ANSWER;           //0:答题框   1:辅助图框
        int serialNum;
        int indexInPaper;
        int color = 0x48B8FF;
        LocalQuestionInfo localQuestionInfo;

        ItemData( int left, int top, int right, int bottom, int mode, String num, int indexInPaper, int serialNum ){
            rect.set( left, top, right, bottom );
            this.mode = mode;
            this.nums = num;
            this.indexInPaper = indexInPaper;
            this.serialNum = serialNum;
        }
        ItemData( int left, int top, int right, int bottom, int mode, String num, int indexInPaper, int serialNum, int type ){
            rect.set( left, top, right, bottom );
            this.mode = mode;
            this.nums = num;
            this.type = type;
            this.indexInPaper = indexInPaper;
            this.serialNum = serialNum;
        }
        public Rect getRect() {
            return rect;
        }

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        String getNums() {
            return nums;
        }

        public RectF getRateRect(){
            RectF rectF = new RectF();
            rectF.left   = rect.left*1f / getWidth();
            rectF.right  = rect.right*1f / getWidth();
            rectF.top    = rect.top*1f / getHeight();
            rectF.bottom = rect.bottom*1f / getHeight();

            if( rectF.left < 0 ) rectF.left = 0;
            if( rectF.right > 1 ) rectF.right = 1;
            if( rectF.left >= rectF.right ){
                if( rectF.right > 0.02f )
                    rectF.left = rectF.right - 0.02f;
                else{
                    rectF.left = 0;
                    rectF.right = 0.02f;
                }
            }

            if( rectF.top < 0 ) rectF.top = 0;
            if( rectF.bottom > 1 ) rectF.bottom = 1;
            if( rectF.top >= rectF.bottom ){
                if( rectF.bottom > 0.02f )
                    rectF.top = rectF.bottom - 0.02f;
                else{
                    rectF.top = 0;
                    rectF.bottom = 0.02f;
                }
            }
            return rectF;
        }

        public int getSerialNum() {
            return serialNum;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public LocalQuestionInfo getLocalQuestionInfo() {
            return localQuestionInfo;
        }

        public void setLocalQuestionInfo(LocalQuestionInfo localQuestionInfo) {
            this.localQuestionInfo = localQuestionInfo;
        }
    }
}
