package com.tsinghuabigdata.edu.ddmath.module.mylearn;

import android.content.Context;
import android.content.Intent;
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
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.entrance.LocalImageManager;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.CaptureView;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.RotateImageView;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 */
public class ScWorkCropImageActivity extends ScWorkBaseActivity implements OnClickListener {


    public static final int MAX_WIDTH = 2000;

    //压缩后图片大小
    public static final int OLD_SIZE = 500;
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
    //private RelativeLayout mRedoTextView;
    private RelativeLayout enterLayout;
    private RelativeLayout cancelLayout;

    //剪切工具
    //private CropToolView mCropToolView;

    private double image_rate = 0;      //图片缩放比例

    private static final int MAX_ZOOM = 4;
    private static int height;
    private static int width;
    private Matrix matrix = new Matrix();

    private Context mContext;

    //Intent 传递一些参数
    //传递过来的参数
    private boolean isNextImage;
    private String mTaskId;
    private boolean isBroadCast;
    private String  reqfrom;

    private String srcImagePath;            //图片路径
    private Bitmap mSrcBitmap;              //拍照原图

    //剪切图地址  最多两张图
    private String mCropImagePath1;         //当有有两张图时，1：默认对应题干， 2： 默认对应答案
    private String mCropImagePath2;

    private boolean isCroping = false;
    private final Object mlock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( GlobalData.isPad()?R.layout.activity_scwork_cropimage_port:R.layout.activity_scwork_cropimage_port_phone );
        mContext = this;

        if( !parseIntent() ){
            return;
        }
        initView();
        loadImage();
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.xbook_crop_redo:{
                finish();
                break;
            }
            case R.id.xbook_crop_enter:{
                //不应许点击
                synchronized (mlock){
                    if( isCroping ) return;
                    isCroping = true;
                }
                if( cropImage() ){
                    finishCrop();
                }else{
                    isCroping = false;
                }
                break;
            }
            case R.id.xbook_croplayout_cancel:{
                //先关闭相机页面   回到
                Intent intent = new Intent();
                intent.putExtra("close",true);
                setResult( RESULT_OK,intent );
                finish();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        isCroping = false;
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
    private boolean parseIntent(){

        Intent intent = getIntent();
        isNextImage = intent.getBooleanExtra( ScWorkUtils.IS_NEXT_IMAGE, false );
        mTaskId     = intent.getStringExtra( ScWorkUtils.TASK_ID );
        isBroadCast = intent.getBooleanExtra( ScWorkUtils.PARAM_BROADCAST, false );
        reqfrom     = intent.getStringExtra( ScWorkUtils.PARAM_FROM );

        if( !intent.hasExtra( ScWorkUtils.IMAGE_TYPE) || !intent.hasExtra(ScWorkUtils.IMAGE_HANDLE) ){
            AlertManager.toast( mContext, "参数错误");
            finish();
            return false;
        }
        String imageType = intent.getStringExtra( ScWorkUtils.IMAGE_TYPE );

        if( ScWorkUtils.TYPE_BITMAP.equals( imageType )  ){
            int session = intent.getIntExtra(ScWorkUtils.IMAGE_HANDLE,-1);
            mSrcBitmap = ScWorkUtils.getBitmap( session );
            ScWorkUtils.removeBitmap( session );
        }else {
            srcImagePath = intent.getStringExtra( ScWorkUtils.IMAGE_HANDLE );

//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            opts.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(srcImagePath, opts);
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

        RelativeLayout mRedoTextView = (RelativeLayout) findViewById(R.id.xbook_crop_redo);
        mRedoTextView.setOnClickListener(this);

        enterLayout = (RelativeLayout) findViewById(R.id.xbook_crop_enter);
        enterLayout.setOnClickListener(this);

        cancelLayout = (RelativeLayout) findViewById(R.id.xbook_croplayout_cancel);
        cancelLayout.setOnClickListener(this);

        //mRotateButton = (ImageView) findViewById(R.id.s8s_edit_rotate_image);
        //mRotateButton.setOnClickListener(this);

        //剪切工具
//        mCropToolView = (CropToolView)findViewById( R.id.xbook_crop_tools);
//        mCropToolView.setCaptureView( mCaptureView );
//        //mCropToolView.setRelateView( targetImage );
//        mCaptureView.setCropToolView( mCropToolView );
        mCaptureView.setRelateView( targetImage );
    }

    private void loadImage(){

        new AsyncTask<Void, Void, Object>() {

            @Override
            protected Object doInBackground(Void... params) {

                Bitmap target_bitmap = null;
                if( srcImagePath != null && (new File( srcImagePath )).exists() ){
                    target_bitmap = zoomInBitmap(decodeBitmap( srcImagePath, getWindowManager().getDefaultDisplay() ));
                }else if( mSrcBitmap != null ){
                    target_bitmap = zoomInBitmap(mSrcBitmap);
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

//        if( !TextUtils.isEmpty(qtype) ){     //来自错题编辑界面  通过广播返回剪切的数据
//            Intent it = new Intent( AppConst.ACTION_SCWORK_ADD );
//            it.putExtra("subject", mSubject );
//            it.putExtra("imagepath", mCropImagePath1 );
//            it.putExtra("qtype", qtype );
//            sendBroadcast( it );
//        }else{      //相机相册直接启动 编辑界面
//            Intent it = new Intent( mContext, ScWorkPreviewActivity.class);
//            it.putExtra("subject", mSubject );
//            it.putExtra("imagepath", mCropImagePath1 );
//            it.putExtra("imagepath1", mCropImagePath2 );
//            startActivity( it );
//        }

        Intent intent = new Intent(mContext, ScWorkPreviewActivity.class);

        intent.putExtra( ScWorkUtils.IS_NEXT_IMAGE, isNextImage );
        intent.putExtra( ScWorkUtils.PARAM_BROADCAST, isBroadCast );
        if( !TextUtils.isEmpty(mTaskId) )
            intent.putExtra( ScWorkUtils.TASK_ID, mTaskId );
        if( !TextUtils.isEmpty(reqfrom) )
            intent.putExtra( ScWorkUtils.PARAM_FROM, reqfrom );

        //可优化，不保存，其他页面进行保存出来
        intent.putExtra( ScWorkUtils.IMAGE_TYPE, ScWorkUtils.TYPE_LOCAL );
        intent.putExtra( ScWorkUtils.IMAGE_HANDLE, mCropImagePath1 );
        startActivity( intent );

        //关闭相机页面
//        intent = new Intent();
//        intent.putExtra("close",true);
//        setResult( RESULT_OK,intent );
//        finish();
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

        int margin = WindowUtils.dpToPixels(mContext,16);     //4dp的margin  left right

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


        //处理成相对图像的位置
        Rect dstRect = new Rect();
        dstRect.left = cropRect.left - cropRect2.left;
        dstRect.top = cropRect.top - cropRect2.top;

        dstRect.right = dstRect.left + cropRect.width();
        dstRect.bottom = dstRect.top + cropRect.height();

        String cropimagepath = LocalWorkManager.getImagePath();
        if( AppConst.TYPE_FROM_ENTRANCE.equals(reqfrom) ){
            cropimagepath = LocalImageManager.getImagePath();
        }
        cropimagepath += System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;
        boolean success = cropRectImage( dstRect, bitmap, cropimagepath );
        if( success ){
            mCropImagePath1 = cropimagepath;
        }else{
            mCropImagePath1 = null;
        }
        mCropImagePath2 = null;
        return success;
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
//        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, x1, y1, width, height);
//        for( int i = 70; i<=100; i=i+5 ){
//            compressImage( cropimagepath, bitmap1, i );
//        }

        //剪切图
        return compressImage( cropimagepath, Bitmap.createBitmap(bitmap, x1, y1, width, height) );
    }

    /*
     * 压缩图片  对图片尺寸和大小进行处理
     */
    private boolean compressImage( String cropimagepath, Bitmap bitmap ) {
        AppLog.d( "----compressImage cropimagepath =" + cropimagepath );
        bitmap = zoomInBitmap( bitmap );//先进行大小缩放

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

        int fsize = baos.size() / 1024;
        AppLog.d( "----compressImage fsize =" + fsize + ",,,width = " + bitmap.getWidth() + ",,,, height = " + bitmap.getHeight() );

        int options;

        if( fsize > 500) {        //>
            options = 80;
        }else if( fsize > 300) {        //>
            options = 85;
        }else if( fsize > 200) {        //>
            options = 90;
        }else {
            options = 100;
        }

//        int tc = 0;
//        //用于上传显示的图片
//        if( fsize > ScWorkCropImageActivity.OLD_SIZE ){
//            while ( baos.toByteArray().length / 1024 > ScWorkCropImageActivity.OLD_SIZE) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
//                baos.reset();//重置baos即清空baos
//                options -= 10;//每次都减少10
//                if( options <= 80 ){
//                    options = 80;
//                    break;
//                }
//                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//                tc++;
//            }
//        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream( cropimagepath );
            if( !bitmap.compress(Bitmap.CompressFormat.JPEG, options, fout) ){
                AppLog.d( "----compressImage save fail " );
                return false;
            }
        } catch (Exception e) {
            if( cropimagepath!=null && cropimagepath.contains("null") ){
                AlertManager.toast( mContext, "出现异常，请先退出APP，重新操作。" );
            }else
                AlertManager.toast( mContext, "磁盘空间不足，无法保存图片。" );
            AppLog.i( "", e );
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
            bitmap.recycle();
        }
        return true;
    }

    private Bitmap zoomInBitmap( Bitmap bmp ){
        Matrix matrix = new Matrix();
        if( bmp.getWidth() > 2000 ){
            matrix.postScale( 0.5f, 0.5f );
            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            AppLog.d( "----zoomInBitmap  ,,,width = " + bmp.getWidth() + ",,,, height = " + bmp.getHeight() );
            bmp.recycle();
            bmp = bitmap;
        }
        return bmp;
    }

//    private boolean compressImage( String cropimagepath, Bitmap bitmap, int options ) {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//
//        String path = cropimagepath.replace(".jpg","-q"+options+".jpg");
//        FileOutputStream fout = null;
//        try {
//            fout = new FileOutputStream( path );
//            if( !bitmap.compress(Bitmap.CompressFormat.JPEG, options, fout) ){
//                AppLog.d( "----compressImage save fail " );
//                return false;
//            }
//        } catch (Exception e) {
//            AlertManager.toast( mContext, "磁盘空间不足，无法保存图片。" );
//            AppLog.i( "", e );
//        }finally {
//            if(fout != null) {
//                try {
//                    fout.flush();
//                    fout.close();
//                } catch (IOException e) {
//                    AppLog.i( "", e );
//                }
//            }
//            //bitmap.recycle();
//        }
//        AppLog.d( "----compressImage quality = "+options + ",,path = " + path );
//        return true;
//    }

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
            AppLog.e( "", "", err );
        }
        return bmp;
    }
}
