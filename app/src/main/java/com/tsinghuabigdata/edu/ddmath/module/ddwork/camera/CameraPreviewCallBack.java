package com.tsinghuabigdata.edu.ddmath.module.ddwork.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkManager;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import java.io.ByteArrayOutputStream;

/**
 * 实时预览处理
 */
public class CameraPreviewCallBack implements Camera.PreviewCallback, Camera.PictureCallback {

    private DetechAynscTask mDetechAynscTask;

    private Rect mFixRect;     //定位识别框
    private Rect mCropRect;     //图片裁剪框
    private DetectListener mDetectListener;

    //private boolean startStatus = false;      //是否开始处理成功的状态，false: 还没有开始处理，true: 已开始处理
    //private boolean succesStatus = false;      //是否处理成功的状态， true:处理成功，不在处理

    //private Context mContext;

    private static final int ST_DETECTFLAG           = 0;      //检测四个点的标志
    private static final int ST_FOUCS_CAMERA         = 1;      //对焦拍照
    private static final int ST_FINISHED_SUCCESS     = 2;      //流程成功完成
    private static final int ST_WAIT_FOCUS           = 3;      //等待对焦完成

    private int runStatus = ST_DETECTFLAG;

    private int mCameraPreviewWidth;            //相机预览大小
    private int mCamreaPriviewHeight;

    private CameraPreviewCallBack instance;
    private Camera mCamera;

    private boolean bPreviewStatus = false;
    private boolean bStop = false;

    //输出图片的宽高
    private int outWidth = 1200;
    private int outHeight= 1840;

    public CameraPreviewCallBack(){
        initStatus();
        instance = this;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCamera = camera;

        AppLog.d("dfdsfsfd start Preview runing.  ");
        if( /*!startStatus || succesStatus*/ runStatus == ST_FINISHED_SUCCESS || runStatus==ST_WAIT_FOCUS || runStatus == ST_FOUCS_CAMERA )      //没有开始处理，或者已经处理成功
            return;

        if( null != mDetechAynscTask ){
            if( !mDetechAynscTask.isFinish() )
                return;                 //没有结束识别
            else
                mDetechAynscTask.cancel( false );
        }

        mDetechAynscTask = new DetechAynscTask( data, camera );
        mDetechAynscTask.execute();
    }

    //int index = 0;
    private long time;
    private boolean pictureRunning = false;
    @Override
    public void onPictureTaken(final byte[] data, final Camera mCamera) {
        if (data == null || mCamera == null || bStop ){
            AppLog.d("dfdsfsfd ,mCamera = "+mCamera );
            return;
        }
        if( pictureRunning ){
            AppLog.d("dfdsfsfd have runing. " );
            return;
        }
        AppLog.d("dfdsfsfd start runing.  ");
        pictureRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                time = System.currentTimeMillis();

                //旋转
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options );

                if( mFixRect!=null && mDetectListener!=null&& mCropRect!=null ){

                    // 默认取camera0 的角度
                    int degree[] = new int[1];
                    mDetectListener.cameraRorate( degree );

                    AppLog.d(" DetechAynscTask bitmap degree = " + degree[0] );
                    if( degree[0] != 0 ){
                        Matrix matrix = new Matrix();
                        matrix.postRotate( degree[0] );
                        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        bitmap.recycle();
                        bitmap = bmp;
                    }

                    //AppLog.d(" DetechAynscTask exec mRect.top = " + mFixRect.top + ",,, left = " + mFixRect.left + ",,, width = " + mFixRect.width() + ",,, height = " + mFixRect.height()  );

                    AppLog.d(" DetechAynscTask mCameraPreviewWidth = " + mCameraPreviewWidth + ",,, mCamreaPriviewHeight = " + mCamreaPriviewHeight);

                    //从 bitmap 里面 按比例裁剪出 目标图片
                    float w_rate = bitmap.getWidth()  * 1.0f / mCameraPreviewWidth;       //预览与成像的比例关系
                    float h_rate = bitmap.getHeight() * 1.0f / mCamreaPriviewHeight;

                    int left    = (int)(mCropRect.left * w_rate);
                    int top     = (int)(mCropRect.top  * h_rate);
                    int right   = (int)(mCropRect.right * w_rate);
                    int bottom  = (int)(mCropRect.bottom  * h_rate);

                    AppLog.d(" DetechAynscTask PPP w_rate = " + w_rate + ",,, h_rate = " + h_rate );

                    //对 mFixRect 进行比例变换
                    int dw = (int)(mFixRect.width() * w_rate * 1.2);
                    int dh = (int)(mFixRect.height() * h_rate * 1.2);

                    Rect dstFixRect = new Rect();
                    dstFixRect.left = (int)(mFixRect.left * w_rate);
                    dstFixRect.top  = (int)(mFixRect.top * h_rate);
                    dstFixRect.right= dstFixRect.left + dw;
                    dstFixRect.bottom= dstFixRect.top + dh;

                    AppLog.d(" DetechAynscTask sss  dstFixRect.left = " + dstFixRect.left + ",,, dstFixRect.top = " + dstFixRect.top + ",,, width = " + dstFixRect.width() + ",,,height = " + dstFixRect.height() );

                    Bitmap dstBitmap = Bitmap.createBitmap(bitmap, left, top, right-left, bottom-top );
                    bitmap.recycle();
                    bitmap = dstBitmap;

                    //AppLog.d(" DetechAynscTask sss bitmap left = " + left + ",,, top = " + top + ",,, width = " + (right-left) + ",,,height = " + (bottom-top) );

                    int ret = OpenCVHelper.detectBitmapColour( bitmap, outWidth, outHeight );
                    AppLog.d(" DetechAynscTask exec ret = " + ret );
                    ret = ret>0?ret:0;

                    if( ret > 15 ){
                        ret = 0;
                        mDetectListener.showCenterToast("请将同一页的四个标识符放在对应的提示框内，并保持手机水平拍照。");
//                    }else if( ret < 15 ){
                        //mDetectListener.showCenterToast("拍照时请保持手机稳定");
                        //testImage( bitmap, dstFixRect, ret );
                    }
                    mDetectListener.detectResult( ret );

                    String imagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;

                    bitmap.recycle();

                    if( (ret & 15) == 15 ){

                        //第二阶段，图片生成阶段
                        boolean success = OpenCVHelper.getDetectImage( imagepath );
                        AppLog.d(" DetechAynscTask exec success = " + success + ",,, imagepath = " + imagepath );
                        if( success ){
                            mDetectListener.detectFile( imagepath );
                            pictureRunning = false;
                            AppLog.d("dfdsfsfdsadsd pppp time = "+(System.currentTimeMillis()-time));
                            return;
                        }
                    }
                }else{
                    bitmap.recycle();
                }

                if( bStop ) return;

                //修改状态，重新启动预览重新对焦
                runStatus = ST_DETECTFLAG;
                mCamera.startPreview();
                mDetectListener.resetTakePicture();
                mDetectListener.nextAutoFocus();

                pictureRunning = false;

                AppLog.d("dfdsfsfdsadsd pppp time = "+(System.currentTimeMillis()-time));
                AppLog.d("dfdsfsfd stop next runing.  ");
            }
        }).start();
    }

    public void setPaperDefaultWidthHeight(float rate){
        if( rate>0 ){
            outHeight = (int)(outWidth / rate);
        }
    }
//    private void testImage( Bitmap bitmap, Rect dstFixRect, int ret ){
//
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setColor(Color.RED);
//
//        //左上
//        canvas.drawRect( dstFixRect, paint );
//
//
//        BitmapUtils.saveImage( "/storage/emulated/0/cache/"+ret+"_"+(index++)+".jpg", bitmap );
//    }


    public void setData(Rect croprect, Rect fixrect, DetectListener listener, int viewWidth, int viewHaight ){
        mCropRect = croprect;
        mFixRect = fixrect;
        mDetectListener = listener;

        mCameraPreviewWidth = viewWidth;
        mCamreaPriviewHeight = viewHaight;
    }
    public void setPreviewStatus( boolean status ){
        bPreviewStatus = status;
    }

    public void initStatus(){
        runStatus = ST_DETECTFLAG;
        bStop = false;
    }
    public void startCamera(){
        if( runStatus == ST_WAIT_FOCUS ) {
            runStatus = ST_FOUCS_CAMERA;
            if( bPreviewStatus) mCamera.takePicture( null, null, instance);
            if( mDetectListener!=null )mDetectListener.startTakePicture();
        }
    }

    public boolean isWaitNextFocus(){
        return runStatus == ST_WAIT_FOCUS;
    }

    public void stopDetechAynscTask(){
        if( null != mDetechAynscTask && !mDetechAynscTask.isFinish() ){
            mDetechAynscTask.cancel( true );
        }
        bStop = true;
    }

    private class DetechAynscTask extends AsyncTask<Void,Void,Boolean> {

        private byte[] mData;
        private Camera mCamera;

        private boolean bFinish;

        DetechAynscTask(byte[] data, Camera camera) {
            mData = data;
            mCamera = camera;
            bFinish = false;
        }

        public boolean isFinish() {
            return bFinish;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            AppLog.d(" DetechAynscTask exec start ");

            bFinish = false;
            time = System.currentTimeMillis();

            if (mCamera == null || bStop ) return false;

            Camera.Size size = mCamera.getParameters().getPreviewSize(); //获取预览大小
            Camera.Size psize = mCamera.getParameters().getPictureSize();


            final int w = size.width;  //宽度
            final int h = size.height;
            AppLog.d(" DetechAynscTask PreviewSize w = " + w + ",,, h = " + h + ",,, PictureSize w = " + psize.width + ",, h = " + psize.height );
            final YuvImage image = new YuvImage(mData, mCamera.getParameters().getPreviewFormat(), w, h, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
            if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                return false;
            }
            byte[] bytes = os.toByteArray();

            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.RGB_565;          //默认 8888
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length/*,options*/);
            AppLog.d(" DetechAynscTask bitmap w = " +  bitmap.getWidth() + ",, h = " + bitmap.getHeight() );

            //原图
            //String path = DDWorkManager.getImagePath()+"10.jpg";
            //BitmapUtils.saveImage( path, bitmap );

            if( mFixRect!=null && mDetectListener!=null&& mCropRect!=null ){

                // 默认取camera0 的角度
                int degree[] = new int[1];
                mDetectListener.cameraRorate( degree );

                AppLog.d(" DetechAynscTask bitmap degree = " + degree[0] );
                if( degree[0] != 0 ){
                    Matrix matrix = new Matrix();
                    //int width = bmp.getWidth(), height = bmp.getHeight();
//                    if( bitmap.getWidth() > 2000 || bitmap.getHeight() > 2000 ){
//                        matrix.postScale( 0.5f, 0.5f );
//                    }
                    matrix.postRotate( degree[0] );
                    Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    bitmap.recycle();
                    bitmap = bmp;
                }

                //AppLog.d(" DetechAynscTask exec mRect.top = " + mFixRect.top + ",,, left = " + mFixRect.left + ",,, width = " + mFixRect.width() + ",,, height = " + mFixRect.height()  );

                AppLog.d(" DetechAynscTask mCameraPreviewWidth = " + mCameraPreviewWidth + ",,, mCamreaPriviewHeight = " + mCamreaPriviewHeight);

                //从 bitmap 里面 按比例裁剪出 目标图片
                float w_rate = bitmap.getWidth()  * 1.0f / mCameraPreviewWidth;       //预览与成像的比例关系
                float h_rate = bitmap.getHeight() * 1.0f / mCamreaPriviewHeight;

                int left    = (int)(mCropRect.left * w_rate);
                int top     = (int)(mCropRect.top  * h_rate);
                int right   = (int)(mCropRect.right * w_rate);
                int bottom  = (int)(mCropRect.bottom  * h_rate);

                AppLog.d(" DetechAynscTask w_rate = " + w_rate + ",,, h_rate = " + h_rate );

                //对 mFixRect 进行比例变换
                int dw = (int)(mFixRect.width() * w_rate * 1.2);
                int dh = (int)(mFixRect.height() * h_rate * 1.2);

                Rect dstFixRect = new Rect();
                dstFixRect.left = (int)(mFixRect.left * w_rate);
                dstFixRect.top  = (int)(mFixRect.top * h_rate);
                dstFixRect.right= dstFixRect.left + dw;
                dstFixRect.bottom= dstFixRect.top + dh;

                AppLog.d(" DetechAynscTask sss bitmap left = " + left + ",,, top = " + top + ",,, width = " + (right-left) + ",,,height = " + (bottom-top) );

                Bitmap dstBitmap = Bitmap.createBitmap(bitmap, left, top, right-left, bottom-top );
                bitmap.recycle();
                bitmap = dstBitmap;

                AppLog.d(" DetechAynscTask sss bitmap left = " + left + ",,, top = " + top + ",,, width = " + (right-left) + ",,,height = " + (bottom-top) );

                int ret = OpenCVHelper.detectBitmapColour( bitmap,outWidth, outHeight );
                AppLog.d(" DetechAynscTask exec ret = " + ret );
                ret = ret>0?ret:0;
                mDetectListener.detectResult( ret );

                //String imagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;

//                //原图
//                String path = DDWorkManager.getImagePath()+"0.jpg";
//                BitmapUtils.saveImage( path, bitmap );

                bitmap.recycle();

                //AppLog.d(" DetechAynscTask exec dstFixRect left = " + dstFixRect.left + ",,, top = " + dstFixRect.top + ",,, width = " + dstFixRect.width() + ",,, height = " + dstFixRect.height() );

                if( (ret & 15) == 15 ){

                    if( runStatus == ST_DETECTFLAG ){       //第一识别标志阶段，等待对焦完成再识别
                        runStatus = ST_WAIT_FOCUS;
                        return true;
                    }

//                    Bitmap ttBitmap = Bitmap.createBitmap(bitmap, dstFixRect.left, dstFixRect.top, dstFixRect.width(), dstFixRect.height() );
//                    BitmapUtils.saveImage( path, ttBitmap );

//                    long tt = System.currentTimeMillis();
//                    OpenCVHelper.enhanceImageMat( OpenCVHelper.MODE_ENHANCE_EXP|/*OpenCVHelper.MODE_REMOVE_NOISE|*/OpenCVHelper.MODE_GUASS_BLUR, OpenCVHelper.DEF_BLOCKSIZE-2 );
//                    AppLog.d(" DetechAynscTask exec enhance time = " + (System.currentTimeMillis()-tt) );


                    //第二阶段，图片生成阶段
//                    boolean success = OpenCVHelper.getDetectImage( imagepath );
//                    AppLog.d(" DetechAynscTask exec success = " + success + ",,, imagepath = " + imagepath );
//                    if( success ){
//                        mDetectListener.detectFile( imagepath );
//                        return true;
//                    }
                }
            }else{
                bitmap.recycle();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if( runStatus == ST_WAIT_FOCUS ){
                AppLog.d("----DetechAynscTask  ready next autofocus ");
                mDetectListener.nextAutoFocus();
            }else if( runStatus == ST_FOUCS_CAMERA ){
                if( result ){   //处理成功
                    runStatus = ST_FINISHED_SUCCESS;
                }else{          //处理失败，重复对焦拍照流程
                    runStatus = ST_WAIT_FOCUS;
                    mDetectListener.nextAutoFocus();
                }
            }else{      //ST_DETECTFLAG 识别点查找失败，继续
                AppLog.d("----DetechAynscTask detect fail, next progress ");
            }

            bFinish = true;
            AppLog.d(" DetechAynscTask exec result = " + result + ",,,dfdsfsfdsadsd  time = " + (System.currentTimeMillis() - time));
        }
    }


}
