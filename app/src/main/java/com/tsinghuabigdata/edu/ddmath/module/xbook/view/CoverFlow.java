package com.tsinghuabigdata.edu.ddmath.module.xbook.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

public class CoverFlow extends Gallery {

    // mCamera是用来做类3D效果处理，比如Z轴方向上的平移，绕Y轴的旋转等
    private Camera mCamera = new Camera();
    // mMaxRotationAngle是图片绕Y轴最大旋转角度，也就是屏幕最边上那两张图片的旋转角度
//    private int mMaxRotationAngle = 50;  //50
    // mMaxZoom是图片在Z轴平移的距离，视觉上看上进心来就是放大缩小的效果
//    private int mMaxZoom = -200;
//    private int mCoveflowCenter;
//    private boolean mAlphaMode = true;
//    private boolean mCircleMode = true;
    private int curSlection = 2;

    private boolean isPad = false;
//    private int measureWidth = 108*9;
//    private int screenCenter = 540;

    public CoverFlow(Context context) {
        super(context);
        this.setStaticTransformationsEnabled(true);
        init();
    }

    public CoverFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setStaticTransformationsEnabled(true);
        init();
    }

    public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setStaticTransformationsEnabled(true);
        init();
    }

//    public int getMaxRotationAngle() {
//        return mMaxRotationAngle;
//    }
//
//    public void setMaxRotationAngle(int maxRotationAngle) {
//        mMaxRotationAngle = maxRotationAngle;
//    }
//
//    public boolean getCircleMode() {
//        return mCircleMode;
//    }
//
//    public void setCircleMode(boolean isCircle) {
//        mCircleMode = isCircle;
//    }
//
//    public boolean getAlphaMode() {
//        return mAlphaMode;
//    }
//
//    public void setAlphaMode(boolean isAlpha) {
//        mAlphaMode = isAlpha;
//    }
//
//    public int getMaxZoom() {
//        return mMaxZoom;
//    }
//
//    public void setMaxZoom(int maxZoom) {
//        mMaxZoom = maxZoom;
//    }

    //-----------------------------------------------------------------------------------

    private void init(){
        isPad = GlobalData.isPad();
    }

//    private int getCenterOfCoverflow() {
//        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
//                + getPaddingLeft();
//    }
//
//    // 获取视图中心
//    private static int getCenterOfView(View view) {
//        return view.getLeft() + view.getWidth() / 2;
//    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }


    // 重写Garray方法 ，产生层叠和放大效果
//    @Override
//    protected boolean getChildStaticTransformation(View child, Transformation t) {
//        //return super.getChildStaticTransformation( child, t);
//
//        final int childCenter = getCenterOfView(child);
//        final int childWidth = child.getWidth();
//        int rotationAngle = 0;
//        t.clear();
//        t.setTransformationType(Transformation.TYPE_BOTH);
//
//        //Log.d("tttt", "childCenter = " + childCenter + ",,,mCoveflowCenter = " + mCoveflowCenter + ",,,childWidth = " + childWidth + ",,,height=" + child.getHeight() );
//        if (childCenter == mCoveflowCenter) {
//            transformImageBitmap(child, t, 0, 0);
//        } else {
//
//            float rate = (float) (mCoveflowCenter - childCenter) / childWidth;
//            Log.d("tttt", "childCenter = " + childCenter + ",,,mCoveflowCenter = " + mCoveflowCenter + ",,,childWidth = " + childWidth + ",,,rate=" + rate );
//
//            //透明度
//            if( rate < 0 ) rate = -rate;
//            t.setAlpha( 1-rate );
//            rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
//            //
//            transformImageBitmap( child, t, rotationAngle, childCenter - mCoveflowCenter );
//        }
//        return true;
//    }

    // 重写Garray方法 ，改变alpha
//    @Override
//    protected boolean getChildStaticTransformation(View child, Transformation t) {
//
//        final int childCenter = getCenterOfView(child);
//        int childWidth = child.getWidth();
//        int childleft = child.getLeft();
//
//        //t.clear();
//        t.setTransformationType(Transformation.TYPE_ALPHA);
//
//        //Log.d("tttt", "childCenter = " + childCenter + ",,,childleft = " + childleft );
//        if (childCenter == mCoveflowCenter) {
//            t.setAlpha( 1f );
//            alphaImageBitmap(child, t);
//        } else {
//
//            if( childCenter < screenCenter ){
//                if( screenCenter - childCenter > measureWidth ){
//                    t.setAlpha( 0f );
//                }else{
//                    t.setAlpha( 1.0f*(measureWidth - screenCenter + childCenter) / measureWidth );
//                }
//            }else{
//                if( childCenter - screenCenter > measureWidth ){
//                    t.setAlpha( 0f );
//                }else{
//                    t.setAlpha( 1.0f*(measureWidth + screenCenter - childCenter) / measureWidth );
//                }
//            }
//            alphaImageBitmap(child, t);
//        }
//        return true;
//    }


    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Aphla
     */
//    private void alphaImageBitmap( View child, Transformation t) {
//        mCamera.save();
//        child.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
//        Matrix imageMatrix = t.getMatrix();
//        mCamera.getMatrix(imageMatrix);
//        mCamera.restore();
//    }

    /**
     * Transform the Image Bitmap by the Angle passed
     */
//    private void transformImageBitmap( View child, Transformation t, int rotationAngle, int offX ) {
//        mCamera.save();
//
//        child.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
//
//        final Matrix imageMatrix = t.getMatrix();
//        final int imageHeight = child.getLayoutParams().height;
//        final int imageWidth = child.getLayoutParams().width;
//        final int rotation = Math.abs(rotationAngle);
//
//        // mCamera.translate(0.0f, 0.0f, 100.0f);
//        // As the angle of the view gets less, zoom in
//        // if (rotation <= mMaxRotationAngle) {
////        float zoomAmount = (float) (-140 + (rotation * 2));
////        if(rotationAngle != 0) {
////            if( rotationAngle < 0 )
////                mCamera.translate( 50/*(float) (-rotation * 0.5)*/,  /*(float) (-rotation * 0.3) + 5*/-150, 40+Math.abs(zoomAmount) );
////            else
////                mCamera.translate( 50/*(float) (rotation*1.5)*/,  /*(float) (-rotation * 0.3) + 5*/-150, 40+Math.abs(zoomAmount) );
////        } else {
////            mCamera.translate( 50/*(float) rotation*/, /*(float) (-rotation * 0.3) + 5*/-150, 40 /* zoomAmount*/);
////        }
////        Log.i("info", "----------------------> = " + rotationAngle + ",,, zoomAmount = " + zoomAmount );
//
//        //float zoomAmount = (float) (-140 + (rotation * 2));
//        //改变 Z轴 和 X轴
//        if( offX != 0) {
//            if( rotationAngle < 0 )
//                mCamera.translate( 50/*(float) (-rotation * 0.5)*/,  /*(float) (-rotation * 0.3) + 5*/-150, 40+Math.abs(offX) );
//            else
//                mCamera.translate( 50/*(float) (rotation*1.5)*/,  /*(float) (-rotation * 0.3) + 5*/-150, 40+Math.abs(offX) );
//        } else {
//            mCamera.translate( 50/*(float) rotation*/, /*(float) (-rotation * 0.3) + 5*/-150, 40 /*  zoomAmount*/);
//        }
//        Log.i("info", "----------------------> = " + rotationAngle + ",,, offX = " + offX );
//
//
//
//        if (mCircleMode) {
//            //          if (rotation < 40) {
//            //              mCamera.translate(0.0f, (100 - rotation * 2.5f), 0.0f);
//            //          } else {
//            mCamera.translate(0.0f, (50 - rotation * 1.5f), 0.0f);
//            //          }
//        }
//        // }
//        // mCamera.rotateY(rotationAngle);
//        mCamera.getMatrix(imageMatrix);
//        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
//        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
//        mCamera.restore();
//    }

    // 重载视图显示顺序让左到中间显示，再到右到中间显示
    protected int getChildDrawingOrder(int childCount, int i) {
        // Current selected index.
        int selectedIndex = getSelectedItemPosition() - getFirstVisiblePosition();
        //播放声音
        //Log.e("ssss","curSlection="+curSlection+" getSelectedItemPosition()="+getSelectedItemPosition());
        if(curSlection != getSelectedItemPosition()){
            curSlection = getSelectedItemPosition();
        }

        if (selectedIndex < 0)
        {
            return i;
        }

        if (i < selectedIndex)
        {
            return i;
        }
        else if (i >= selectedIndex)
        {
            return childCount - 1 - i + selectedIndex;
        }
        else
        {
            return i;
        }
            /*long t = getSelectedItemId();
            int h = getSelectedItemPosition();
            Log.e("getChildDrawingOrder","i="+i+"  childCount="+childCount);
            if (i < childCount / 2 ) {
                return i;
            }
            return childCount - i - 1 + childCount / 2;*/
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_UP && onScrollFinishListener!=null ){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    onScrollFinishListener.onScrollFinish();
                }
            },1000 );
        }
        return super.onTouchEvent( event );
    }

    private OnScrollFinishListener onScrollFinishListener=null;
//    public void setOnScrollFinishListener( OnScrollFinishListener listener ){
//        onScrollFinishListener = listener;
//    }
    public interface OnScrollFinishListener{
        void onScrollFinish();
    }


    //--------------------------------------------------------------------------
    private Matrix mMatrix = new Matrix();
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret;
        final float offset = calculateOffsetOfCenter(child);
        getTransformationMatrix(child, offset);

        child.setAlpha(1 - Math.abs(offset));

        final int saveCount = canvas.save();
        canvas.concat(mMatrix);
        ret = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(saveCount);
        return ret;
    }
    void getTransformationMatrix(View child, float offset) {
        final int halfWidth = child.getLeft() + (child.getMeasuredWidth() >> 1);
        final int halfHeight = child.getMeasuredHeight() >> 1;

        mCamera.save();
        float os = offset * 50;
        if( isPad ) os = offset * 200;
        mCamera.translate(-os, 0.0f , Math.abs(offset) * 200);

        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-halfWidth, -halfHeight);
        mMatrix.postTranslate(halfWidth, halfHeight);
    }
    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        return false;
    }
    //获取父控件中心点 X 的位置
    protected int getCenterOfCoverflow() {
        return ((getWidth() - getPaddingLeft() - getPaddingRight()) >> 1) + getPaddingLeft();
    }
    //获取 child 中心点 X 的位置
    protected  int getCenterOfView(View view) {
        return view.getLeft() + (view.getWidth() >> 1);
    }
    //计算 child 偏离 父控件中心的 offset 值， -1 <= offset <= 1
    protected float calculateOffsetOfCenter(View view){
        final int pCenter = getCenterOfCoverflow();
        final int cCenter = getCenterOfView(view);

        float offset = (cCenter - pCenter) / (pCenter * 1.0f);
        offset = Math.min(offset, 0.8f);
        offset = Math.max(offset, -0.8f);
        return offset;
    }

    void transformViewRoom(View child, Transformation t, float race){
        Camera mCamera = new Camera();
        mCamera.save();
        final Matrix matrix = t.getMatrix();
        final int halfHeight = child.getMeasuredHeight() >> 1;
        final int halfWidth = child.getMeasuredWidth() >> 1;

        // 平移 X、Y、Z 轴已达到立体效果
        mCamera.translate(-race * 50, 0.0f , Math.abs(race) * 200);
        //也可设置旋转效果
        mCamera.getMatrix(matrix);
        //以 child 的中心点变换
        matrix.preTranslate(-halfWidth, -halfHeight);
        matrix.postTranslate(halfWidth, halfHeight);
        mCamera.restore();
        //设置 alpha 变换
        t.setAlpha(1 - Math.abs(race));
    }

    int getChildIndex( View child ){
        int count = getChildCount();
        for( int i=0; i<count; i++ ){
            if( getChildAt(i).equals( child ) )
                return i;
        }
        return -1;
    }
}

