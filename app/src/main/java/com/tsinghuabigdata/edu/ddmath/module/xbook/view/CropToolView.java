package com.tsinghuabigdata.edu.ddmath.module.xbook.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;


/**
 * 剪切工具View
 */
public class CropToolView extends LinearLayout {

    //private LinearLayout mCropToolLayout;
    private ImageView qustionAreaImage;
    private ImageView dotlineImage;
    private ImageView answerAreaImage;
    private ImageView scissorImage;

    private CaptureView mCaptureView;
    //private View mRelateView;

    private int initHeight = 0;
    private int showStatus = 0;

    public CropToolView(Context context) {
        super(context);
        init();
    }

    public CropToolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CropToolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CropToolView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

//    public void setRelateView(View view){
//        mRelateView = view;
//    }
    public void setCaptureView(CaptureView view){
        mCaptureView = view;
    }

    public void changePosistion( boolean init ){

        if( mCaptureView == null ){
            return;
        }

        Rect cropRect = mCaptureView.getCaptureRect();

        Rect gtoolRect = new Rect();
        getGlobalVisibleRect( gtoolRect );
        Rect gcropRect = new Rect();
        mCaptureView.getGlobalVisibleRect( gcropRect );

        int croptool_pos = (gtoolRect.top + gtoolRect.bottom )/2;
        if( init ){
            if( croptool_pos <= (gcropRect.top+cropRect.top) ){
                float dy = gcropRect.top+cropRect.top/*-(gtoolRect.top + gtoolRect.bottom )/2*/ ;
                dy -= gcropRect.height()/8;
                setY(getY() + dy);
                setShow( 0 );
            }else if( croptool_pos >= (gcropRect.top+cropRect.bottom) ){
                float dy = (gtoolRect.top + gtoolRect.bottom )/2 - gcropRect.top - cropRect.bottom ;
                setY(getY() - dy);
                setShow( 1 );
            }else{
                setShow( 0 );
            }
        }else if( croptool_pos <= (gcropRect.top+cropRect.top) ){
            float dy = gcropRect.top+cropRect.top-(gtoolRect.top + gtoolRect.bottom )/2 ;
            setY(getY() + dy);
            setShow( -1 );
        }else if( croptool_pos >= (gcropRect.top+cropRect.bottom) ){
            float dy = (gtoolRect.top + gtoolRect.bottom )/2 - gcropRect.top - cropRect.bottom ;
            setY(getY() - dy);
            setShow( 1 );
        }else{
            setShow( 0 );
        }
    }

    public int getInitHeight(){ return initHeight; }
    public int getShowStatus(){ return showStatus; }
//    public void changeLayout(){
//        Rect rect = mCaptureView.getCaptureRect();
//        if( rect!=null ){
//            int top = (rect.height() - getHeight())/2;
//            setTop( top );
//
//            AppLog.d("xxx dfdfdfsdas height = " + rect.height() + ",,,, getHeight =  " + getHeight() );
//            AppLog.d("xxx dfdfdfsdas top = " + top );
//        }
//    }
//    //---------------------------------------------------------------------------------------
//
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//
//        AppLog.d("dfdfdfsdas top = " + top );
//        AppLog.d("dfdfdfsdas left = " + left );
//        AppLog.d("dfdfdfsdas bottom = " + bottom );
//        AppLog.d("dfdfdfsdas right = " + right );
//
//    }

    //---------------------------------------------------------------------------------------
    private void init(){

        inflate(getContext(), R.layout.view_xbook_croptool, this);
        qustionAreaImage = (ImageView) findViewById(R.id.xbook_crop_question);
        dotlineImage = (ImageView) findViewById(R.id.xbook_crop_dotline);
        answerAreaImage = (ImageView) findViewById(R.id.xbook_crop_answer);
        scissorImage    = (ImageView) findViewById(R.id.xbook_crop_scissor);

        //
        Drawable qdrawable = getResources().getDrawable( R.drawable.ico_subject );
        Drawable ddrawable = getResources().getDrawable( R.drawable.xbook_dotline );
        Drawable adrawable = getResources().getDrawable( R.drawable.ico_answer );

        initHeight = ( qdrawable.getIntrinsicHeight()+ddrawable.getIntrinsicHeight()+adrawable.getIntrinsicHeight() )/2;
    }

    private boolean setXY( float dx, float dy ){

        if( mCaptureView!=null ){

//            //不要移除裁剪框的上下边界
//            Rect gtoolRect = new Rect();
//            getGlobalVisibleRect( gtoolRect );
//            Rect gcropRect = new Rect();
//            mCaptureView.getGlobalVisibleRect( gcropRect );
//
//            if( gtoolRect.top==gcropRect.top && dy<0 && (gtoolRect.bottom - gtoolRect.top < initHeight)
//                    || gtoolRect.bottom==gcropRect.bottom && dy>0 && (gtoolRect.bottom - gtoolRect.top < initHeight) ){
//                return false;
//            }else{
//                setY(getY() + dy);
//                changePosistion();
//                return true;
//            }

            Rect cropRect = mCaptureView.getCaptureRect();

            Rect gtoolRect = new Rect();
            getGlobalVisibleRect( gtoolRect );
            Rect gcropRect = new Rect();
            mCaptureView.getGlobalVisibleRect( gcropRect );

            int croptool_pos = (gtoolRect.top + gtoolRect.bottom )/2 + (int)dy;
            if( gtoolRect.top<=gcropRect.top ){
                croptool_pos = gtoolRect.bottom - initHeight + (int)dy;
            }else if(  gtoolRect.bottom >= gcropRect.bottom ){
                croptool_pos = gtoolRect.top + initHeight + (int)dy;
            }

            //AppLog.i("fdfdsfd top=" + gtoolRect.top + ",, bottom = " + gtoolRect.bottom + ",,,, ctop = " + gcropRect.top + ",,cbottom = " + gcropRect.bottom + ",,,initHeight = " + initHeight );
            if( croptool_pos <= (gcropRect.top+cropRect.top) ){
                //dy = gcropRect.top+cropRect.top-(gtoolRect.top + gtoolRect.bottom )/2 ;
                dy = initHeight + gcropRect.top+cropRect.top - gtoolRect.bottom;
                //if( dy > 0 )
                    setY(getY() + dy);

                setShow( -1 );
                return false;
            }else if( croptool_pos >= (gcropRect.top+cropRect.bottom) ){
                dy = gtoolRect.top + initHeight - gcropRect.top - cropRect.bottom;
                //if( dy < 0 )
                    setY(getY() - dy);
                setShow( 1 );
                return false;
            }else{
                setY(getY() + dy);
                setShow( 0 );
                return true;
            }

        }else{
            setY(getY() + dy);
            changePosistion( false );
            return true;
        }
    }

    private void setShow( int status ){
        showStatus = status;
        if( status < 0 ){
            qustionAreaImage.setVisibility( INVISIBLE );
            answerAreaImage.setVisibility( VISIBLE );
        }else if( status == 0 ){
            qustionAreaImage.setVisibility( VISIBLE );
            answerAreaImage.setVisibility( VISIBLE );
        }else{
            qustionAreaImage.setVisibility( VISIBLE );
            answerAreaImage.setVisibility( INVISIBLE );
        }
    }

    /* 设置图片 显示位置,移动图片时使用
    * @param: x
    * @param: y
    */

    /** 用于记录开始时候的坐标位置 */
    private PointF startPoint = new PointF();
    private PointF endPoint = new PointF();

    //private boolean move_status = false;
    //private long startPressTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:{
                startPoint.set(event.getRawX(), event.getRawY());
                break;
            }
            case MotionEvent.ACTION_MOVE:{

                //float dx = event.getRawX() - startPoint.x; // 得到x轴的移动距离
                float dy = event.getRawY() - startPoint.y; // 得到x轴的移动距离
                //避免和双击冲突,大于10f才算是拖动
                if(Math.abs(dy) < 10){
                    //移动距离过小，不处理
                    break;
                }

                if( setXY( 0, dy ) ){
                    startPoint.set(event.getRawX(), event.getRawY());
                }
            }
            break;
            case MotionEvent.ACTION_UP: {
                endPoint.set( event.getX(), event.getY() );
                break;
            }
            default:
                break;
        }
        return true;//super.onTouchEvent(event);
    }
}
