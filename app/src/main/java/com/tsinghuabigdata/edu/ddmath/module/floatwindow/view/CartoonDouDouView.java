package com.tsinghuabigdata.edu.ddmath.module.floatwindow.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.FloatWindowManager;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean.BubbleBean;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


/**
 * 卡通豆豆界面
 */
public class CartoonDouDouView extends FrameLayout implements View.OnClickListener {
    private final WindowManager mWindowManager;

    private LinearLayout ddlayout;
    private ImageView ddImageView;
    private LinearLayout helpImageView;
    private LinearLayout expectImageView;

    private CartoonBubbleView bubbleView;
    private View backgroundView;

    private long startTime;
    private float mTouchStartX;
    private float mTouchStartY;

    private WindowManager.LayoutParams mWmParams;

    //显示按钮标志
    private boolean showToolBtn = false;

    //当前UI名称
    private String currUiName;
    //是否移动
    private boolean ontouchDoudou = false;

    public CartoonDouDouView(Context context) {
        this(context, null);
    }

    public CartoonDouDouView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater.from(context).inflate( GlobalData.isPad()?R.layout.float_toolbar_layout :R.layout.float_toolbar_layout_phone, this);

        ddlayout = (LinearLayout)findViewById( R.id.cartoon_dd_layout );
        ddImageView = (ImageView)findViewById( R.id.cartoon_dd_imageview );
        AnimationDrawable animationDrawable = (AnimationDrawable)ddImageView.getDrawable();
        if( animationDrawable!=null )
            animationDrawable.start();
        helpImageView = (LinearLayout)findViewById( R.id.cartoon_dd_help );
        expectImageView = (LinearLayout)findViewById( R.id.cartoon_dd_expect );

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        //下面的这些事件，跟图标的移动无关，为了区分开拖动和点击事件
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                ontouchDoudou = touchDdImageView( event.getX(), event.getY() );
                AppLog.d("sdfdsfdsfds ontouchDoudou = " + ontouchDoudou );
                //if( ontouchDoudou ){
                    startTime = System.currentTimeMillis();
                    mTouchStartX = event.getX();
                    mTouchStartY = event.getY();
                //}
                break;
            case MotionEvent.ACTION_MOVE:
                if( ontouchDoudou ){
                    //图标移动的逻辑在这里
                    float mMoveStartX = event.getX();
                    float mMoveStartY = event.getY();
                    // 如果移动量大于3才移动
                    if (Math.abs(mTouchStartX - mMoveStartX) > 3 && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                        mWmParams.x = (int) (x - mTouchStartX);
                        mWmParams.y = (int) (y - mTouchStartY);
                        mWindowManager.updateViewLayout(this, mWmParams);

                        //更新气泡的位置
                        if( bubbleView.isShown() )
                            bubbleView.updateView();
                        return false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                if( ontouchDoudou && System.currentTimeMillis() - startTime <  300 ) {
                    triggerOnClick( mTouchStartX, mTouchStartY );
                    return false;
                }
                ontouchDoudou = false;
                break;
        }
        return true;
    }

    //设置当前UI界面名称
    public void setCurrUiName( String name ){
        currUiName = name;
        if(TextUtils.isEmpty(name)){
            FloatWindowManager.hide();
        }else{
            FloatWindowManager.show();
            bubbleView.setCurrUiName( name );
        }
        AppLog.d("dfdsfdsfdsf setCurrUiName = " + name );
    }

    public void setBubbleView( CartoonBubbleView view ){
        bubbleView = view;
    }
    public void setBackgroundView( View view ){
        backgroundView = view;
    }
    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params ) {
        mWmParams = params;

        //初始化位置
        int width = DensityUtils.dp2px( getContext(), GlobalData.isPad()? 80:60 );
        mWmParams.x = mWmParams.x/2;            //水平居中
        mWmParams.y = mWmParams.y - width;
        mWindowManager.updateViewLayout( this, mWmParams );
    }

    public void onClick(View v) {

        //点击豆豆图像
        if( v.getId() == R.id.cartoon_dd_imageview ){
            //: 2018/4/27  要判断当前界面有没有帮助功能
            if( showToolBtn ){
                hideToolButton();
            }else{
                showToolButton();
            }
        }
        
        //帮助功能
        else if( v.getId() == R.id.cartoon_dd_help ){
            //  2018/4/27
            ToastUtils.show( getContext(), "点击了Help" );
            hideToolButton();
        }
        
        //敬请期待
        else if( v.getId() == R.id.cartoon_dd_expect){
            BubbleBean currBubbleBean = new BubbleBean( BubbleBean.TYPE_COMMON, "", "我正在学习新技能，敬请期待。");
            bubbleView.showBubble( currBubbleBean );
            //隐藏操作按钮
            hideToolButton();
        }
    }

    //------------------------------------------------------------------------------------------
    private boolean touchDdImageView(float x, float y) {
        Rect rect = new Rect();
        ddlayout.getGlobalVisibleRect( rect );
        return rect.contains( (int)x, (int)y );
    }

    private void triggerOnClick(float x, float y){
        Rect rect = new Rect();
        int count = ddlayout.getChildCount();
        for( int i=0; i<count; i++ ){
            View child = ddlayout.getChildAt(i);
            child.getGlobalVisibleRect( rect );
            if( rect.contains( (int)x, (int)y ) ){
                onClick( child );
                return;
            }
        }
    }

    private void showToolButton(){
        showToolBtn = true;
        helpImageView.setVisibility( View.VISIBLE );
        expectImageView.setVisibility( View.VISIBLE );
        backgroundView.setVisibility( View.VISIBLE );
        //立刻显示气泡
        bubbleView.showBubble();
    }
    private void hideToolButton(){
        showToolBtn = false;
        helpImageView.setVisibility( View.GONE );
        expectImageView.setVisibility( View.GONE );
        backgroundView.setVisibility( View.GONE );
    }

}
