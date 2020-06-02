package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;

/**
 * 自定义RadioButton
 */
@SuppressLint("AppCompatCustomView")
public class ChooseRadioButtonView extends RadioButton{

    private Bitmap mBitmap;
    private Paint mPaint;
    private Rect mSrcRect;
    private Rect mDstRect;

    public ChooseRadioButtonView(Context context) {
        super(context);
        init();
    }

    public ChooseRadioButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChooseRadioButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if( mDstRect==null ) mDstRect = new Rect();
        mDstRect.set( 0,0, right-left, bottom-top );
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw( canvas );

        //绘制选中，则绘制选中图片
        if( isChecked() && mBitmap!=null ){
            canvas.drawBitmap( mBitmap, mSrcRect, mDstRect, mPaint );
        }
    }

    //-------------------------------------------------------------------------
    private void init(){
        Drawable drawable = getResources().getDrawable( R.drawable.ic_choice_black);
        mBitmap = BitmapUtils.drawableToBitmap( drawable );

        mPaint = new Paint();

        mSrcRect = new Rect(0,0,mBitmap.getWidth(), mBitmap.getHeight());
    }

}
