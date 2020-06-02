package com.tsinghuabigdata.edu.ddmath.module.learnmaterial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BaseActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkManager;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.UserAdjustView;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * 用户确认图谱识别效果的界面
 */
public class LMUserAdjustActivity extends BaseActivity implements OnClickListener {

    private final static int MODE_EDIT = 101;    //图片编辑界面返回
    private static final String PARAM_POINTS    = "list";
    private static final String PARAM_PAGEINFO  = "pageInfo";
    private static final String PARAM_IMAGEPATH = "imagepath";
    private static final String PARAM_TEACHER   = "teacher";
    private static final String PARAM_BOOKRATE  = "bookrate";

    public static void openActivityForResult(Activity activity, ArrayList<Point> list, LocalPageInfo pageInfo, String imagePath, int resultCode, boolean teacher, float bookRate ){
        if( activity==null || pageInfo==null || TextUtils.isEmpty(imagePath) ) return;
        Intent intent = new Intent( activity, LMUserAdjustActivity.class);
        intent.putExtra( PARAM_POINTS, list );
        intent.putExtra( PARAM_PAGEINFO, true );
        intent.putExtra( PARAM_IMAGEPATH, imagePath );
        intent.putExtra( PARAM_TEACHER, teacher );
        intent.putExtra( PARAM_BOOKRATE, bookRate );
        activity.startActivityForResult( intent, resultCode );
    }
    //---------------------------------------------------

    private RelativeLayout mainLayout;

    //图片
    private ImageView targetImage;

    //选择框
    private UserAdjustView mCaptureView;


    private Activity mActivity;

    private Context mContext;

    //传递过来的参数
    private LocalPageInfo localPageInfo;
    private String srcImagePath;            //图片路径
    private boolean isTeacher;
    private ArrayList<Point> mDetectPoints;
    private float bookRate;

    private Bitmap mSrcBitmap;              //拍照原图
    private long useTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;
        mActivity= this;

        if( !parseIntent() ){
            return;
        }
        if( bookRate > 1 ){     //横版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(GlobalData.isPad()?R.layout.activity_lm_useradjust_land:R.layout.activity_lm_useradjust_land_phone);
        }else{      //竖版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView( GlobalData.isPad()?R.layout.activity_lm_useradjust:R.layout.activity_lm_useradjust_phone );
        }

        initView();

        loadImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        useTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLog.caermaLog( useTime, "LmAdjust", localPageInfo );
    }

    @Override
    public void onClick(View v) {
        switch ( v.getId() ){

            //重新拍照
            case R.id.lm_useradjust_recamera:{
                redo();
                break;
            }

            //确认使用
            case R.id.lm_useradjust_enter:{
                String imagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;
                boolean success = OpenCVHelper.saveImageEdge( imagepath, mCaptureView.getData() );
                if( success ){
                    LMPreviewActivity.openActivityForResult( mActivity, localPageInfo, imagepath, MODE_EDIT, isTeacher, bookRate );
                }else{
                    ToastUtils.show( mContext, "图片处理异常，请重新拍照" );
                    redo();
                }
                break;
            }
            default:
                    break;
        }
    }
    @Override
    public void onBackPressed(){
        redo();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if( mSrcBitmap!=null )
            mSrcBitmap.recycle();
        mSrcBitmap = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch( requestCode ) {
            case  MODE_EDIT:{
                if(data != null ) {
                    if (data.hasExtra("redo")) {    //
                        //直接关闭
                        Intent intent = new Intent();
                        intent.putExtra( "redo", true );
                        setResult( MODE_EDIT, intent );

                        finish();
                    }else if (data.hasExtra("close")) {     //通知关闭,也要关闭拍照界面
                        Intent intent = new Intent();
                        intent.putExtra( "close", true );
                        setResult( MODE_EDIT, intent );
                        finish();
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    //UI
    private boolean parseIntent(){

        Intent intent = getIntent();
        boolean has = intent.getBooleanExtra( PARAM_PAGEINFO, false );
        if( has )
            localPageInfo = AccountUtils.getLocalPageInfo();
        srcImagePath  = intent.getStringExtra( PARAM_IMAGEPATH );
        isTeacher   = intent.getBooleanExtra( PARAM_TEACHER, false );
        mDetectPoints = intent.getParcelableArrayListExtra( PARAM_POINTS );
        bookRate = intent.getFloatExtra( PARAM_BOOKRATE, -1 );

        return localPageInfo!=null && !TextUtils.isEmpty(srcImagePath) && mDetectPoints!=null && bookRate>0;
    }

    //返回重拍
    private void redo(){

        //先删除当前图片
        File file = new File(srcImagePath);

        boolean b = file.delete();
        AppLog.d("delete b = " + b );

        Intent intent = new Intent();
        intent.putExtra( "redo", true );
        setResult( RESULT_OK, intent );

        finish();
    }

    private void initView() {

        mainLayout = (RelativeLayout)findViewById( R.id.lm_useradjust_mainlayout );
        targetImage = (ImageView) findViewById(R.id.lm_useradjust_photo);

        mCaptureView = (UserAdjustView) findViewById(R.id.lm_useradjust_captureview);
        mCaptureView.setRelateView( targetImage );
        mCaptureView.setLocalPageInfo( localPageInfo );

        TextView textView = (TextView) findViewById(R.id.lm_useradjust_recamera);
        textView.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.lm_useradjust_enter);
        textView.setOnClickListener(this);
    }


    private void loadImage(){
        new MyTask().execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Object> {

        @Override
        protected Object doInBackground(Void... params) {

            Bitmap target_bitmap = null;
            if( srcImagePath != null && (new File( srcImagePath )).exists() ){
                target_bitmap = decodeBitmap( srcImagePath/*, getWindowManager().getDefaultDisplay()*/ );
            }else if( mSrcBitmap != null ){
                target_bitmap = mSrcBitmap;
            }
            return target_bitmap;
        }

        @Override
        protected void onPostExecute(Object object) {
            super.onPostExecute(object);
            if(object!=null) {

                Bitmap bitmap = (Bitmap)object;
                //等待
                while ( mainLayout.getWidth() == 0 ) SystemClock.sleep(10);


                if(mCaptureView!=null)mCaptureView.setVisibility(View.VISIBLE);
                targetImage.setVisibility(View.VISIBLE);
                mCaptureView.setBitmap( bitmap );

                //等比大小  居中显示
                int maxw = mainLayout.getWidth();
                int maxh = mainLayout.getHeight();
                int height = BitmapUtils.showBestMaxBitmap( bitmap, maxw, maxh, targetImage );
                targetImage.setImageBitmap( bitmap );

                //显示可调整框
                mCaptureView.setData( mDetectPoints, height*1f/bitmap.getHeight() );

            } else {
                mCaptureView.setVisibility(View.GONE);
                targetImage.setVisibility(View.GONE);

                AlertManager.toast( mContext, "图片获取出错" );
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
//    /**
//     * 完成剪切，界面跳转
//     */
//    private void finishCrop() {
//
//        //关闭相机页面
//        Intent intent = new Intent();
//        intent.putExtra("close",true);
//        setResult( RESULT_OK,intent );
//        finish();
//    }

//    /**
//     * 监听缩放、平移
//     */
//    private class TounchListener implements OnTouchListener {
//
//        private PointF startPoint = new PointF();
//
//        private Matrix currentMaritx = new Matrix();
//
//        private int mode = 0;// 用于标记模式
//        private static final int DRAG = 1;// 拖动
//        private static final int ZOOM = 2;// 放大
//        private float startDis = 0;
//        private PointF midPoint;// 中心点
//
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                mode = DRAG;
//                currentMaritx.set(targetImage.getImageMatrix());// 记录ImageView当期的移动位置
//                startPoint.set(event.getX(), event.getY());// 开始点
//                break;
//
//            case MotionEvent.ACTION_MOVE:// 移动事件
//                if (mode == DRAG) {// 图片拖动事件
//                    float dx = event.getX() - startPoint.x;// x轴移动距离
//                    float dy = event.getY() - startPoint.y;
//
//                    int x = (int) (dx / 50);
//                    int y = (int) (dy / 50);
//
//                    int[] start = new int[2];
//                    targetImage.getLocationInWindow(start);
//                    matrix.set(currentMaritx);// 在当前的位置基础上移动
//                    matrix.postTranslate(dx, dy);
//                    // }
//                } else if (mode == ZOOM) {// 图片放大事件
//                    float endDis = distance(event);// 结束距离
//                    float[] t_f = new float[9];
//                    currentMaritx.getValues(t_f);
//                    if (endDis > 10f) {
//                        float scale = endDis / startDis;// 放大倍数
//                        matrix.set(currentMaritx);
//                        if (t_f[0] * scale <= MAX_ZOOM) {
//                            matrix.postScale(scale, scale, midPoint.x, midPoint.y);
//                        }else {
//                            float n_s = MAX_ZOOM/t_f[0];
//                            matrix.postScale(n_s, n_s, midPoint.x, midPoint.y);
//                        }
//                    }
//                }
//
//                break;
//
//            case MotionEvent.ACTION_UP:
//                float dx = event.getX() - startPoint.x;// x轴移动距离
//                float dy = event.getY() - startPoint.y;
//                mode = 0;
//                break;
//            // 有手指离开屏幕，但屏幕还有触点(手指)
//            case MotionEvent.ACTION_POINTER_UP:
//                float[] f = new float[9];
//                matrix.getValues(f);
//               // zoomDegree = f[0];
//                mode = 0;
//                break;
//            // 当屏幕上已经有触点（手指）,再有一个手指压下屏幕
//            case MotionEvent.ACTION_POINTER_DOWN:
//                mode = ZOOM;
//                startDis = distance(event);
//
//                if (startDis > 10f) {// 避免手指上有两个茧
//                    midPoint = mid(event);
//                    currentMaritx.set(targetImage.getImageMatrix());// 记录当前的缩放倍数
//                }
//                break;
//            default:
//                break;
//            }
//            targetImage.setImageMatrix(matrix);
//            return true;
//        }
//    }
//    /**
//     * 两点之间的距离
//     *
//     * @param event
//     * @return
//     */
//    private static float distance(MotionEvent event) {
//        // 两根线的距离
//        float dx = event.getX(1) - event.getX(0);
//        float dy = event.getY(1) - event.getY(0);
//        return (float)Math.sqrt(dx * dx + dy * dy);
//    }

//    /**
//     * 获得屏幕中心点
//     *
//     * @param event
//     * @return
//     */
//    private static PointF mid(MotionEvent event) {
//        return new PointF(width / 2, height / 2);
//    }


    //---------------------------------------------------------------------------------------------
//    private Rect getImageRect( Bitmap target_bitmap ){
//
//        Rect rect = new Rect();
//
//        int margin = WindowUtils.dpToPixels(mContext,4);     //4dp的margin
//
//        int width = targetImage.getWidth();
//        int height = targetImage.getHeight();
//
//        int cwidth = mCaptureView.getWidth();
//        int cheight = mCaptureView.getHeight();
//
//        int image_width = target_bitmap.getWidth();
//        int image_height = target_bitmap.getHeight();
//
//        if( width == image_width && height == image_height ){  //两边都不满
//            targetImage.getHitRect( rect );
//
//            image_rate = 1.0;
//        }else if( cwidth == width+2*margin ){    //宽度方向撑满，上下有偏移
//            int offx = (cheight - height)/2;
//            rect.left = margin;
//            rect.top  = offx ;
//            rect.right = cwidth - margin;
//            rect.bottom = cheight - offx;
//
//            image_rate = image_width *1.0 / width;
//        }else{
//            int offx = (cwidth - width)/2;
//            rect.left = offx;
//            rect.top  = 0 ;
//            rect.right = cwidth - offx;
//            rect.bottom = cheight;
//
//            image_rate = image_height *1.0 / height;
//
//        }
//        return rect;
//    }

//    /**
//     * 剪切图片
//     * @return
//     */
//    private boolean cropImage() {
//
//        if( targetImage==null )
//            return false;
//
//        Bitmap bitmap = targetImage.getImageBitmap();
//        if( bitmap == null ){
//            return false;
//        }
//
//        //剪切框大小
//        Rect cropRect = mCaptureView.getCaptureRect();
//
//        //图片区域
//        Rect cropRect2 = getImageRect(bitmap);
//        //adjuectRect( cropRect, bitmap );
//
//        if( !cropRect.intersect(cropRect2) ){    //没有交集
//            AlertManager.toast( mContext, "请正确选择图片区域" );
//            return false;
//        }
//
//        //相对整个屏幕的位置
//        Rect gtoolRect = new Rect();
//        mCropToolView.getGlobalVisibleRect( gtoolRect );
//        Rect gcropRect = new Rect();
//        mCaptureView.getGlobalVisibleRect( gcropRect );
//
//        int croptool_pos = (gtoolRect.top + gtoolRect.bottom )/2;
//        if( gtoolRect.top == gcropRect.top || mCropToolView.getShowStatus() < 0 ){
//            croptool_pos = gtoolRect.bottom - mCropToolView.getInitHeight();
//        }else if( gtoolRect.bottom == gcropRect.bottom || mCropToolView.getShowStatus() > 0  ){
//            croptool_pos = gtoolRect.top + mCropToolView.getInitHeight();
//        }
//        if( croptool_pos <= (gcropRect.top+cropRect.top) ){
//            croptool_pos = -1;
//        }else if( croptool_pos >= (gcropRect.top+cropRect.bottom) ){
//            croptool_pos = 10000;
//        }else{
//            croptool_pos = croptool_pos - gcropRect.top - cropRect.top;
//        }
//
//        //处理成相对图像的位置
//        Rect dstRect = new Rect();
//        dstRect.left = cropRect.left - cropRect2.left;
//        dstRect.top = cropRect.top - cropRect2.top;
//
//        dstRect.right = dstRect.left + cropRect.width();
//        dstRect.bottom = dstRect.top + cropRect.height();
//
//        String cropimagepath = ContextUtils.getCacheDir(AppConst.IMAGE_DIR) + "/" + System.currentTimeMillis()+AppConst.IMAGE_SUFFIX_NAME;
//
//        if( AppConst.TYPE_EDIT.equals(croptype) ){      //没有剪切线
//
//            boolean success = cropRectImage( dstRect, bitmap, cropimagepath );
//            if( success ){
//                mCropImagePath1 = cropimagepath;
//            }else{
//                mCropImagePath1 = null;
//            }
//            mCropImagePath2 = null;
//            return success;
//        }else{
//
//            if( croptool_pos < 0 ){   //只有答案
//                boolean success = cropRectImage( dstRect, bitmap, cropimagepath );
//                if( success ){
//                    mCropImagePath2 = cropimagepath;
//                }else{
//                    mCropImagePath2 = null;
//                }
//                mCropImagePath1 = null;
//                return success;
//
//            }else if( croptool_pos >= 10000 ){   //只有题干
//                boolean success = cropRectImage( dstRect, bitmap, cropimagepath );
//                if( success ){
//                    mCropImagePath1 = cropimagepath;
//                }else{
//                    mCropImagePath1 = null;
//                }
//                mCropImagePath2 = null;
//                return success;
//            }else{  //题干和答案都有
//
//                //题干区域
//                Rect stemRect = new Rect();
//                stemRect.top = dstRect.top;
//                stemRect.left = dstRect.left;
//                stemRect.bottom = croptool_pos+dstRect.top;
//                stemRect.right = dstRect.right;
//                boolean success = cropRectImage( stemRect, bitmap, cropimagepath );
//                if( success ){
//                    mCropImagePath1 = cropimagepath;
//                }else{
//                    mCropImagePath1 = null;
//                    return false;
//                }
//
//                //我的答案区域
//                Rect answerRect = new Rect();
//                answerRect.top = croptool_pos+dstRect.top;
//                answerRect.left = dstRect.left;
//                answerRect.bottom = dstRect.bottom;
//                answerRect.right = dstRect.right;
//                cropimagepath = ContextUtils.getCacheDir(AppConst.IMAGE_DIR) + "/" + System.currentTimeMillis()+AppConst.IMAGE_SUFFIX_NAME;
//                success = cropRectImage( answerRect, bitmap, cropimagepath );
//                if( success ){
//                    mCropImagePath2 = cropimagepath;
//                }else{
//                    mCropImagePath2 = null;
//                }
//                return success;
//            }
//        }
//    }

//    /**
//     * 根据目标区域，从图片上面剪切指定图片
//     */
//    private boolean cropRectImage( Rect dstRect, Bitmap bitmap, String cropimagepath ){
//
//        //映射到实际图片的位置
//        int x1 = (int)( dstRect.left*image_rate );
//        int y1 = (int)( dstRect.top*image_rate );
//        int width = (int)( dstRect.width()*image_rate );
//        int height = (int)( dstRect.height()*image_rate );
//
//        if (x1 < 0)
//            x1 = 0;
//        else if( x1 > bitmap.getWidth() ){
//            x1 = bitmap.getWidth();
//        }
//
//        if (y1 < 0)
//            y1 = 0;
//        else if( y1 > bitmap.getHeight() ){
//            y1 = bitmap.getHeight();
//        }
//
//        if( x1 + width > bitmap.getWidth() ){
//            width = bitmap.getWidth() -x1;
//        }
//        if( y1 + height > bitmap.getHeight() ){
//            height = bitmap.getHeight() -y1;
//        }
//
//        //剪切图
//        Bitmap tmp = Bitmap.createBitmap(bitmap, x1, y1, width, height);
//        return compressImage( cropimagepath, tmp );
//    }

    /*
     * 压缩图片  对图片尺寸和大小进行处理
     */
//    private boolean compressImage( String cropimagepath, Bitmap bitmap ) {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//
//        int fsize = baos.toByteArray().length / 1024;
//        AppLog.d( "----compressImage fsize =" + fsize + ",,,max_size = " + LMUserAdjustActivity.OLD_SIZE );
//
//        int options;
//        if( fsize > 500) {        //>
//            options = 80;
//        }else if( fsize > 300) {        //>
//            options = 85;
//        }else if( fsize > 200) {        //>
//            options = 90;
//        }else {
//            options = 100;
//        }
//
//        int tc = 0;
//        //用于上传显示的图片
//        if( fsize > LMUserAdjustActivity.OLD_SIZE ){
//            while ( baos.toByteArray().length / 1024 > LMUserAdjustActivity.OLD_SIZE) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
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
//        FileOutputStream fout = null;
//        try {
//            fout = new FileOutputStream( cropimagepath );
//            if( !bitmap.compress(Bitmap.CompressFormat.JPEG, options, fout) ){
//                AppLog.d( "----compressImage save fail " );
//                return false;
//            }
//        } catch (Exception e) {
//            AppLog.i( "", e );
//            AlertManager.toast( mContext, "磁盘空间不足，无法保存图片。" );
//            return false;
//        }finally {
//            if(fout != null) {
//                try {
//                    fout.flush();
//                    fout.close();
//                } catch (IOException e) {
//                    AppLog.i( "", e );
//                }
//            }
//        }
//        return true;
//    }

    
    private static Bitmap decodeBitmap( String imageFile/*,Display currentDisplay*/ ){
        
        //屏幕大小
        //Display currentDisplay = getWindowManager().getDefaultDisplay();
//        int dw = currentDisplay.getWidth();
//        int dh = currentDisplay.getHeight();


        // 得到图片大小但是不加载图片
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, opts);
        
        //int heightRatio = (int) Math.ceil(opts.outHeight / (float) dh);
        //int widthRatio = (int) Math.ceil(opts.outWidth / (float) dw);

        if( opts.outHeight > 6000 || opts.outWidth > 6000 ){
            opts.inSampleSize = 2;
        }/*else if( opts.outHeight > 2000 || opts.outWidth > 2000 ){
            opts.inSampleSize = 2;
        }*/

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
