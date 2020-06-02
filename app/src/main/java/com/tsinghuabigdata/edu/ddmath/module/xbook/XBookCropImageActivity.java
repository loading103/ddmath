package com.tsinghuabigdata.edu.ddmath.module.xbook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BaseActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.xbook.view.CaptureView;
import com.tsinghuabigdata.edu.ddmath.module.xbook.view.CropToolView;
import com.tsinghuabigdata.edu.ddmath.module.xbook.view.RotateImageView;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.ContextUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 */
public class XBookCropImageActivity extends BaseActivity implements OnClickListener {


    public static final int MAX_WIDTH = 2000;

    //压缩后图片大小
    public static final int OLD_SIZE = 200;
    //public static final int SMALL_SIZE = 50;

    //---------------------------------------------------

    private RelativeLayout mainLayout;

    //图片
    private RotateImageView targetImage;

    //选择框
    private CaptureView mCaptureView;
    //加载
    //private ProgressBar mLoadProgressBar;
    private TextView tipTextView;
    
    //操作按钮
    private RelativeLayout mRedoTextView;
    private RelativeLayout enterLayout;
//    private TextView mCancelTextView;

    //剪切工具
    private CropToolView mCropToolView;

    private double image_rate = 0;      //图片缩放比例

    private static final int MAX_ZOOM = 4;
    private static int height;
    private static int width;
    private Matrix matrix = new Matrix();
    private float zoomDegree = 1;
    
    private Context mContext;
    
//    private final int SHOW_TOAST = 0;
//    private final int CROP_TOAST = 1;
//    private final int ROTATE_TOAST = 2;
    
//    private int cropimage_width = 0;
//    private int cropimage_height = 0;
//
//    private int smallimage_width = 0;
//    private int smallimage_height = 0;

    //Intent 传递一些参数
    private String mSubject;
    private String image_from;        //0:相机  1:相册
    private String croptype;          //0:有剪刀，需要裁剪为题干和答案  1：没有裁剪功能
    private String qtype;             //题干部分

    //原图地址
    private String srcimagepath;
    private Bitmap mSrcBitmap;

    //剪切图地址  最多两张图
    private String mCropImagePath1;         //当有有两张图时，1：默认对应题干， 2： 默认对应答案
    private String mCropImagePath2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;

        if( !setScreenOrient() ){
            return;
        }
        initView();

        initParam();

        loadImage();
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.xbook_crop_redo:{
                Intent it = new Intent();
                it.putExtra("from", image_from);
                setResult(RESULT_OK, it);
                finish();
                break;
            }
            case R.id.xbook_crop_enter:{
                //不应许点击
                enterLayout.setEnabled( false );
                if( cropImage() ){
                    finishCrop();
                }else{
                    enterLayout.setEnabled( true );
                }
                break;
            }
//            case R.id.xbook_crop_cancel:{
//
//                break;
//            }
            default:
                    break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if( mSrcBitmap!=null )
            mSrcBitmap.recycle();
        mSrcBitmap = null;
    }

    //---------------------------------------------------------------------------------------------------------------
    //UI
    private boolean setScreenOrient(){

        Intent intent = getIntent();
        if( !intent.hasExtra("picturePath") && !intent.hasExtra("session") ){
            AlertManager.toast( mContext, "参数错误");
            finish();
            return false;
        }

        if( intent.hasExtra("picturePath") ){       //
            srcimagepath = intent.getStringExtra( "picturePath" );

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcimagepath, opts);

//            if( opts.outHeight > opts.outWidth ){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                setContentView( GlobalData.isPad()?R.layout.activity_xbook_cropimage_port:R.layout.activity_xbook_cropimage_port_phone );
//            }else{
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//                setContentView(R.layout.activity_xbook_cropimage_land);
//            }
        }else{
            int session = intent.getIntExtra("session",-1);
            mSrcBitmap = XBookUtils.getBitmap( session );
            XBookUtils.removeBitmap( session );

            if( mSrcBitmap!=null ){
//                if( mSrcBitmap.getHeight() > mSrcBitmap.getWidth() ){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    setContentView( GlobalData.isPad()?R.layout.activity_xbook_cropimage_port:R.layout.activity_xbook_cropimage_port_phone );
//                }else{
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//                    setContentView(R.layout.activity_xbook_cropimage_land);
//                }
            }else{
                AlertManager.toast( mContext, "参数错误");
                finish();
                return false;
            }
        }
        return true;
    }

    private void initView() {

        mainLayout = (RelativeLayout)findViewById( R.id.xbook_crop_mainlayout );
        targetImage = (RotateImageView) findViewById(R.id.xbook_crop_image);
        targetImage.setOnTouchListener(new TounchListener());

        tipTextView = (TextView) findViewById( R.id.xbook_crop_tiptext );

        mCaptureView = (CaptureView) this.findViewById(R.id.xbook_crop_capture);

        //mLoadProgressBar = (ProgressBar)findViewById( R.id.xbook_crop_loading );

        mRedoTextView = (RelativeLayout) findViewById(R.id.xbook_crop_redo);
        mRedoTextView.setOnClickListener(this);

        enterLayout = (RelativeLayout) findViewById(R.id.xbook_crop_enter);
        enterLayout.setOnClickListener(this);

//        mCancelTextView = (TextView) findViewById(R.id.xbook_crop_cancel);
//        mCancelTextView.setOnClickListener(this);

        //mRotateButton = (ImageView) findViewById(R.id.s8s_edit_rotate_image);
        //mRotateButton.setOnClickListener(this);

        //剪切工具
        mCropToolView = (CropToolView)findViewById( R.id.xbook_crop_tools);
        mCropToolView.setCaptureView( mCaptureView );
        //mCropToolView.setRelateView( targetImage );
        mCaptureView.setCropToolView( mCropToolView );
        mCaptureView.setRelateView( targetImage );
    }

    private void initParam(){

        Intent intent = getIntent();
        image_from = intent.getStringExtra("from");
        mSubject = intent.getStringExtra( "subject" );
        croptype = intent.getStringExtra("ctype");
        qtype    = intent.getStringExtra("qtype");

        //
        //if( AppConst.FROM_CAMERA.equals(image_from) ){
        //    mRedoTextView.setText("重拍");
        //}else{
        //    mRedoTextView.setText("重选");
        //}

        //剪切功能
        if( !TextUtils.isEmpty(qtype) ){
            mCropToolView.setVisibility( View.GONE );
            tipTextView.setText( getResources().getString(R.string.xbook_crop_tips1) );
        }else{
            mCropToolView.setVisibility( View.VISIBLE );
        }
    }

    private void loadImage(){
        
        new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {

                Bitmap target_bitmap = null;
                if( srcimagepath != null && (new File( srcimagepath )).exists() ){
                    target_bitmap = decodeBitmap( srcimagepath, getWindowManager().getDefaultDisplay() );
                }else if( mSrcBitmap != null ){
                    target_bitmap = mSrcBitmap;
                }
                return target_bitmap;
            }
            
            @Override
            protected void onPostExecute(Object bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null) {
                    
                    //if(mLoadProgressBar!=null)mLoadProgressBar.setVisibility(View.GONE);
                    if(mCaptureView!=null)mCaptureView.setVisibility(View.VISIBLE);

                    targetImage.setVisibility(View.VISIBLE);
                    
                    Bitmap target_bitmap = (Bitmap)bitmap;
                    targetImage.setImageBitmap(target_bitmap,0);

                    //mCaptureView 限制在图片范围内

                    //size = getSize( target_bitmap );
                } else {
                    //mLoadProgressBar.setVisibility(View.GONE);
                    mCaptureView.setVisibility(View.VISIBLE);
                    targetImage.setVisibility(View.VISIBLE);

                    AlertManager.toast( mContext, "图片获取出错" );
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }.execute();
    }

//    private float getSize( Bitmap target_bitmap ) {
//        float size = 1;
//        wh = getWidthHeight();
//
//        int bw = target_bitmap.getWidth();
//        int bh = target_bitmap.getHeight();
//
//        if (wh[0] < bw || wh[1] < bh) {
//            if (wh[0] / (wh[1] + 0.0f) > bw / (bh + 0.0f)) {
//                size = bh / (wh[1] + 0.0f);
//                //rx = (int) ((wh[0] - bw / size) / 2);
//            } else {
//                size = bw / (wh[0] + 0.0f);
//                //ry = (int) ((wh[1] - bh / size) / 2);
//            }
//        } else {
//            size = 1;
//        }
//        return size;
//    }

//    private int[] getWidthHeight() {
//        Display dis = getWindowManager().getDefaultDisplay();
//        int[] wh = new int[] { dis.getWidth(), dis.getHeight() };
//        return wh;
//    }

    /**
     * 完成剪切，界面跳转
     */
    private void finishCrop() {

        if( !TextUtils.isEmpty(qtype) ){     //来自错题编辑界面  通过广播返回剪切的数据
            Intent it = new Intent( AppConst.ACTION_XBOOK_ADD );
            it.putExtra("subject", mSubject );
            it.putExtra("imagepath", mCropImagePath1 );
            it.putExtra("qtype", qtype );
            sendBroadcast( it );
//        }else{      //相机相册直接启动 编辑界面
//            Intent it = new Intent( mContext, XBookEditActivity.class);
//            it.putExtra("subject", mSubject );
//            it.putExtra("imagepath", mCropImagePath1 );
//            it.putExtra("imagepath1", mCropImagePath2 );
//            startActivity( it );
        }

//        if( AppConst.FROM_CAMERA.equals(image_from) ){
//            (new File(srcimagepath)).delete();      //删除拍照生成的文件
//        }

        //关闭相机页面
        Intent intent = new Intent();
        intent.putExtra("close",true);
        setResult( RESULT_OK,intent );
        finish();
    }

    /**
     * 监听缩放、平移
     * 
     */
    private class TounchListener implements OnTouchListener {

        private PointF startPoint = new PointF();

        private Matrix currentMaritx = new Matrix();

        private int mode = 0;// 用于标记模式
        private static final int DRAG = 1;// 拖动
        private static final int ZOOM = 2;// 放大
        private float startDis = 0;
        private PointF midPoint;// 中心点

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                currentMaritx.set(targetImage.getImageMatrix());// 记录ImageView当期的移动位置
                startPoint.set(event.getX(), event.getY());// 开始点
                break;

            case MotionEvent.ACTION_MOVE:// 移动事件
                if (mode == DRAG) {// 图片拖动事件
                    float dx = event.getX() - startPoint.x;// x轴移动距离
                    float dy = event.getY() - startPoint.y;

                    int x = (int) (dx / 50);
                    int y = (int) (dy / 50);

                    int[] start = new int[2];
                    targetImage.getLocationInWindow(start);
                    matrix.set(currentMaritx);// 在当前的位置基础上移动
                    matrix.postTranslate(dx, dy);
                    // }
                } else if (mode == ZOOM) {// 图片放大事件
                    float endDis = distance(event);// 结束距离
                    float[] t_f = new float[9];
                    currentMaritx.getValues(t_f);
                    if (endDis > 10f) {
                        float scale = endDis / startDis;// 放大倍数
                        matrix.set(currentMaritx);
                        if (t_f[0] * scale <= MAX_ZOOM) {
                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        }else {
                            float n_s = MAX_ZOOM/t_f[0];
                            matrix.postScale(n_s, n_s, midPoint.x, midPoint.y);
                        }
                    }
//                    float[] t_f2 = new float[9];
//                    matrix.getValues(t_f2);
//                    Log.e("Gon","scale:"+t_f2[0]);
                }

                break;

            case MotionEvent.ACTION_UP:
                float dx = event.getX() - startPoint.x;// x轴移动距离
                float dy = event.getY() - startPoint.y;
                // startX -= dx / zoomDegree;
                // startY -= dy / zoomDegree;
                mode = 0;
                break;
            // 有手指离开屏幕，但屏幕还有触点(手指)
            case MotionEvent.ACTION_POINTER_UP:
                float[] f = new float[9];
                matrix.getValues(f);
                zoomDegree = f[0];
                mode = 0;
                break;
            // 当屏幕上已经有触点（手指）,再有一个手指压下屏幕
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                startDis = distance(event);

                if (startDis > 10f) {// 避免手指上有两个茧
                    midPoint = mid(event);
                    currentMaritx.set(targetImage.getImageMatrix());// 记录当前的缩放倍数
                }
                break;
            default:
                break;
            }
            targetImage.setImageMatrix(matrix);
            return true;
        }
    }
    /**
     * 两点之间的距离
     * 
     * @param event
     * @return
     */
    private static float distance(MotionEvent event) {
        // 两根线的距离
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 获得屏幕中心点
     * 
     * @param event
     * @return
     */
    private static PointF mid(MotionEvent event) {
        return new PointF(width / 2, height / 2);
    }


    //---------------------------------------------------------------------------------------------
    private Rect getImageRect( Bitmap target_bitmap ){

        Rect rect = new Rect();

        int margin = WindowUtils.dpToPixels(mContext,4);     //4dp的margin

        int width = targetImage.getWidth();
        int height = targetImage.getHeight();

        int cwidth = mCaptureView.getWidth();
        int cheight = mCaptureView.getHeight();

        int image_width = target_bitmap.getWidth();
        int image_height = target_bitmap.getHeight();

        if( width == image_width && height == image_height ){  //两边都不满
//            int offx = (cheight - height)/2;
//            rect.left = margin;
//            rect.top  = offx ;
//            rect.right = cwidth - margin;
//            rect.bottom = cheight - offx;
            targetImage.getHitRect( rect );

            image_rate = 1.0;
        }else if( cwidth == width+2*margin ){    //宽度方向撑满，上下有偏移
            int offx = (cheight - height)/2;
            rect.left = margin;
            rect.top  = offx ;
            rect.right = cwidth - margin;
            rect.bottom = cheight - offx;

            image_rate = image_width *1.0 / width;
        }else{
            int offx = (cwidth - width)/2;
            rect.left = offx;
            rect.top  = 0 ;
            rect.right = cwidth - offx;
            rect.bottom = cheight;

            image_rate = image_height *1.0 / height;

        }
        return rect;
    }

    /**
     * 剪切图片
     * @return
     */
    private boolean cropImage() {

        if( targetImage==null )
            return false;

        Bitmap bitmap = targetImage.getImageBitmap();
        if( bitmap == null ){
            return false;
        }

        //剪切框大小
        Rect cropRect = mCaptureView.getCaptureRect();

        //图片区域
        Rect cropRect2 = getImageRect(bitmap);
        //adjuectRect( cropRect, bitmap );

        if( !cropRect.intersect(cropRect2) ){    //没有交集
            AlertManager.toast( mContext, "请正确选择图片区域" );
            return false;
        }

        //相对整个屏幕的位置
        Rect gtoolRect = new Rect();
        mCropToolView.getGlobalVisibleRect( gtoolRect );
        Rect gcropRect = new Rect();
        mCaptureView.getGlobalVisibleRect( gcropRect );

        int croptool_pos = (gtoolRect.top + gtoolRect.bottom )/2;
        if( gtoolRect.top == gcropRect.top || mCropToolView.getShowStatus() < 0 ){
            croptool_pos = gtoolRect.bottom - mCropToolView.getInitHeight();
        }else if( gtoolRect.bottom == gcropRect.bottom || mCropToolView.getShowStatus() > 0  ){
            croptool_pos = gtoolRect.top + mCropToolView.getInitHeight();
        }
        if( croptool_pos <= (gcropRect.top+cropRect.top) ){
            croptool_pos = -1;
        }else if( croptool_pos >= (gcropRect.top+cropRect.bottom) ){
            croptool_pos = 10000;
        }else{
            croptool_pos = croptool_pos - gcropRect.top - cropRect.top;
        }

        //处理成相对图像的位置
        Rect dstRect = new Rect();
        dstRect.left = cropRect.left - cropRect2.left;
        dstRect.top = cropRect.top - cropRect2.top;

        dstRect.right = dstRect.left + cropRect.width();
        dstRect.bottom = dstRect.top + cropRect.height();

        String cropimagepath = ContextUtils.getCacheDir(AppConst.IMAGE_DIR) + "/" + System.currentTimeMillis()+AppConst.IMAGE_SUFFIX_NAME;

        if( AppConst.TYPE_EDIT.equals(croptype) ){      //没有剪切线

            boolean success = cropRectImage( dstRect, bitmap, cropimagepath );
            if( success ){
                mCropImagePath1 = cropimagepath;
            }else{
                mCropImagePath1 = null;
            }
            mCropImagePath2 = null;
            return success;
        }else{

            if( croptool_pos < 0 ){   //只有答案
                boolean success = cropRectImage( dstRect, bitmap, cropimagepath );
                if( success ){
                    mCropImagePath2 = cropimagepath;
                }else{
                    mCropImagePath2 = null;
                }
                mCropImagePath1 = null;
                return success;

            }else if( croptool_pos >= 10000 ){   //只有题干
                boolean success = cropRectImage( dstRect, bitmap, cropimagepath );
                if( success ){
                    mCropImagePath1 = cropimagepath;
                }else{
                    mCropImagePath1 = null;
                }
                mCropImagePath2 = null;
                return success;
            }else{  //题干和答案都有

                //题干区域
                Rect stemRect = new Rect();
                stemRect.top = dstRect.top;
                stemRect.left = dstRect.left;
                stemRect.bottom = croptool_pos+dstRect.top;
                stemRect.right = dstRect.right;
                boolean success = cropRectImage( stemRect, bitmap, cropimagepath );
                if( success ){
                    mCropImagePath1 = cropimagepath;
                }else{
                    mCropImagePath1 = null;
                    return false;
                }

                //我的答案区域
                Rect answerRect = new Rect();
                answerRect.top = croptool_pos+dstRect.top;
                answerRect.left = dstRect.left;
                answerRect.bottom = dstRect.bottom;
                answerRect.right = dstRect.right;
                cropimagepath = ContextUtils.getCacheDir(AppConst.IMAGE_DIR) + "/" + System.currentTimeMillis()+AppConst.IMAGE_SUFFIX_NAME;
                success = cropRectImage( answerRect, bitmap, cropimagepath );
                if( success ){
                    mCropImagePath2 = cropimagepath;
                }else{
                    mCropImagePath2 = null;
                }
                return success;
            }
        }
    }

    /**
     * 根据目标区域，从图片上面剪切指定图片
     */
    private boolean cropRectImage( Rect dstRect, Bitmap bitmap, String cropimagepath ){

        //映射到实际图片的位置
        int x1 = (int)( dstRect.left*image_rate );
        int y1 = (int)( dstRect.top*image_rate );
        int width = (int)( dstRect.width()*image_rate );
        int height = (int)( dstRect.height()*image_rate );

        if (x1 < 0)
            x1 = 0;
        else if( x1 > bitmap.getWidth() ){
            x1 = bitmap.getWidth();
        }

        if (y1 < 0)
            y1 = 0;
        else if( y1 > bitmap.getHeight() ){
            y1 = bitmap.getHeight();
        }

        if( x1 + width > bitmap.getWidth() ){
            width = bitmap.getWidth() -x1;
        }
        if( y1 + height > bitmap.getHeight() ){
            height = bitmap.getHeight() -y1;
        }

        //剪切图
        Bitmap tmp = Bitmap.createBitmap(bitmap, x1, y1, width, height);
        return compressImage( cropimagepath, tmp );
    }

    /*
     * 压缩图片  对图片尺寸和大小进行处理
     */
    private boolean compressImage( String cropimagepath, Bitmap bitmap ) {
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        
        int fsize = baos.toByteArray().length / 1024;
        AppLog.d( "----compressImage fsize =" + fsize + ",,,max_size = " + XBookCropImageActivity.OLD_SIZE );

        int options;
        
        /*if( fsize > 3000){
            options = 20;
        }else if( fsize > 2048 ){
            options = 30;
        }else if( fsize > 1024) {        //>1M
            options = 40;
        }else if( fsize > 700) {        //>
            options = 50;
        }else */if( fsize > 500) {        //>
            options = 80;
        }else if( fsize > 300) {        //>
            options = 85;
        }else if( fsize > 200) {        //>
            options = 90;
        }else {
            options = 100;
        }
        
        int tc = 0;
        //用于上传显示的图片
        if( fsize > XBookCropImageActivity.OLD_SIZE ){
            while ( baos.toByteArray().length / 1024 > XBookCropImageActivity.OLD_SIZE) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10  
                if( options <= 80 ){
                    options = 80;
                    break;
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                tc++;
            }
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream( cropimagepath );
            if( !bitmap.compress(Bitmap.CompressFormat.JPEG, options, fout) ){
                AppLog.d( "----compressImage save fail " );
                return false;
            }
        } catch (Exception e) {
            AppLog.i( "", e );
            AlertManager.toast( mContext, "磁盘空间不足，无法保存图片。" );
            return false;
        }finally {
            if(fout != null) {
                try {
                    fout.flush();
                    fout.close();
                } catch (IOException e) {
                    AppLog.i( "", e );
                }
            }
        }
        return true;
    }
    
//    private static int computeSampleSize(BitmapFactory.Options options,
//            int minSideLength, int maxNumOfPixels) {
//        int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);
//
//        int roundedSize;
//        if (initialSize <= 8 ) {
//            roundedSize = 1;
//            while (roundedSize < initialSize) {
//                roundedSize <<= 1;
//            }
//        } else {
//            roundedSize = (initialSize + 7) / 8 * 8;
//        }
//
//        return roundedSize;
//    }

//    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
//        double w = options.outWidth;
//        double h = options.outHeight;
//
//        int lowerBound = (maxNumOfPixels == -1) ? 1 :
//                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
//        int upperBound = (minSideLength == -1) ? 128 :
//                (int) Math.min(Math.floor(w / minSideLength),
//                Math.floor(h / minSideLength));
//
//        if (upperBound < lowerBound) {
//            // return the larger one when there is no overlapping zone.
//            return lowerBound;
//        }
//
//        if ((maxNumOfPixels == -1) &&
//                (minSideLength == -1)) {
//            return 1;
//        } else if (minSideLength == -1) {
//            return lowerBound;
//        } else {
//            return upperBound;
//        }
//    }
    
    private static Bitmap decodeBitmap( String imageFile,Display currentDisplay ){
        
        //屏幕大小
        //Display currentDisplay = getWindowManager().getDefaultDisplay();
        int dw = currentDisplay.getWidth();
        int dh = currentDisplay.getHeight();


        // 得到图片大小但是不加载图片
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, opts);
        
        //int heightRatio = (int) Math.ceil(opts.outHeight / (float) dh);
        //int widthRatio = (int) Math.ceil(opts.outWidth / (float) dw);

        if( opts.outHeight > 6000 || opts.outWidth > 6000 ){
            opts.inSampleSize = 4;
        }else if( opts.outHeight > 2000 || opts.outWidth > 2000 ){
            opts.inSampleSize = 2;
        }

        // 解析图片
        opts.inJustDecodeBounds = false;
        //opts.inSampleSize = widthRatio;
        //opts.inSampleSize = 1;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(imageFile, opts);
        } catch (OutOfMemoryError err) {
            AppLog.i( "", err.toString() );
            err.printStackTrace();
        }
        return bmp;
    }
}
