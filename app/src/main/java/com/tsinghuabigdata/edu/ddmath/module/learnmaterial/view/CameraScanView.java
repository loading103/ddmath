package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 相机扫描效果
 */
public class CameraScanView extends RelativeLayout {

    private ImageView scanView;

    //扫描动画时.动画位置 及 步长
    private final static int TOTAL = 40;
    private final static int TIMES = 30;
    private int currTop = 0;
    private int step = 0;

    public CameraScanView(Context context) {
        super(context);
        initView();
    }

    public CameraScanView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public CameraScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public UserAdjustView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initView();
//    }

    public void reset(){
        currTop = 0;
    }

    public void startScan(){

        scanView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if( !isShown()) return;     //不显示就不在刷新

                int height = getHeight();
                if( height != 0 ){
                    if( step == 0 ) step = height / TOTAL;
                    currTop += step;

                    if( currTop+scanView.getHeight() > height ){        //底部超出范围
                        currTop = 0; //从头开始
                    }

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)scanView.getLayoutParams();
                    layoutParams.topMargin = currTop;
                    scanView.setLayoutParams( layoutParams );
                }
                startScan();
            }
        }, TIMES );
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return true;     //拦截事件
//    }

    //-----------------------------------------------------------------------------------
    private void initView() {
        inflate( getContext(), GlobalData.isPad()? R.layout.view_lm_camerascan:R.layout.view_lm_camerascan_phone, this );
        scanView = (ImageView)findViewById( R.id.iv_lm_scan );
    }

}
