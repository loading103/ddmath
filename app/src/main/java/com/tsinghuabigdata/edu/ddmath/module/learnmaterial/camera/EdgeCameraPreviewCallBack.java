package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.camera;

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
 * 边缘检测实时预览
 */
public class EdgeCameraPreviewCallBack implements Camera.PreviewCallback, Camera.PictureCallback {

    private DetechAynscTask mDetechAynscTask;

    private Rect mCropRect;     //图片裁剪框
    private EdgeDetectListener mDetectListener;

    //private static final double CARITY = 1.0;

    private static final int ST_DETECTFLAG           = 0;      //检测四个点的标志
    private static final int ST_FOUCS_CAMERA         = 1;      //对焦拍照
    private static final int ST_FINISHED_SUCCESS     = 2;      //流程成功完成
    private static final int ST_WAIT_FOCUS           = 3;      //等待对焦完成

    private int runStatus = ST_DETECTFLAG;

    private int mCameraPreviewWidth;            //相机预览大小
    private int mCamreaPriviewHeight;

    private final EdgeCameraPreviewCallBack instance;
    private Camera mCamera;

    private boolean bPreviewStatus = false;
    private boolean bStop = false;

    private boolean bForceCamera = false;

    //拍照循环的次数
    private int tryTime = 0;

    public void setForceCamera(boolean bForceCamera) {
        this.bForceCamera = bForceCamera;
    }

    public EdgeCameraPreviewCallBack(){
        initStatus();
        instance = this;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCamera = camera;

        if( /*!startStatus || succesStatus*/ runStatus == ST_FINISHED_SUCCESS || runStatus==ST_WAIT_FOCUS || runStatus == ST_FOUCS_CAMERA )      //没有开始处理，或者已经处理成功
            return;

        if( null != mDetechAynscTask ){
            if( !mDetechAynscTask.isFinish() )
                return;                 //没有结束识别
            else
                mDetechAynscTask.cancel( false );
        }

        //
//        mDetechAynscTask = new DetechAynscTask( data, camera );
//        mDetechAynscTask.execute();
    }

    public boolean enableCamera(){ return mCamera!=null; }

    //private int index = 0;
    private long time;
    private boolean pictureRunning = false;
    @Override
    public void onPictureTaken(final byte[] data, final Camera mCamera) {
        if (data == null || mCamera == null || bStop ){
            //AppLog.d("onPictureTaken ,mCamera = "+mCamera );
            return;
        }
        if( pictureRunning ){
            //AppLog.d("onPictureTaken have runing. " );
            return;
        }
        //AppLog.d("onPictureTaken start runing.  ");
        pictureRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                AppLog.d(" dsfdsfsdgfgf picture start time = " + ( System.currentTimeMillis()-time ) );
                AppLog.d("onPictureTaken start 0000  ");
                //旋转
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options );

                if( bitmap != null && mDetectListener!=null&& mCropRect!=null ){

                    // 默认取camera0 的角度
                    int degree[] = new int[1];
                    mDetectListener.cameraRorate( degree );

                    if( degree[0] != 0 ){
                        Matrix matrix = new Matrix();
                        matrix.postRotate( degree[0] );
                        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        bitmap.recycle();
                        bitmap = bmp;
                    }

                    //从 bitmap 里面 按比例裁剪出 目标图片
                    float w_rate = bitmap.getWidth()  * 1.0f / mCameraPreviewWidth;       //预览与成像的比例关系
                    float h_rate = bitmap.getHeight() * 1.0f / mCamreaPriviewHeight;

                    int left    = (int)(mCropRect.left * w_rate);
                    int top     = (int)(mCropRect.top  * h_rate);
                    int right   = (int)(mCropRect.right * w_rate);
                    int bottom  = (int)(mCropRect.bottom  * h_rate);

                    Bitmap dstBitmap = Bitmap.createBitmap(bitmap, left, top, right-left, bottom-top );
                    bitmap.recycle();
                    bitmap = dstBitmap;

//                        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/1/test-"+ (index++) +".jpg";
//                        //drawLineInBitmap( bitmap, ret );
//                        path = BitmapUtils.saveImage( path, bitmap );
//                        AppLog.d("dsfdsfsdgfgfsklk DetechAynscTask picture path = " + path );
                    
                    bForceCamera = bForceCamera || tryTime >= 2;
                    AppLog.d(" dsfdsfsdgfgf create time = " + ( System.currentTimeMillis()-time ) );
                    int ret[] = OpenCVHelper.detectFourEdge( bitmap, bForceCamera );
                    AppLog.d(" dsfdsfsdgfgf picture time = " + ( System.currentTimeMillis()-time ) );
                    AppLog.d(" dsfdsfsdgfgf detectFourEdge = " + ret[0] );

                    //更新到界面显示 同时判断 是否满足条件
                    boolean success = mDetectListener.detectResult( ret, false );

                    //强拍时赋值默认识别框
                    if( (!success || ret[0]!=0 )&& bForceCamera ){
                        ret = getDefaultquArea( bitmap );
                    }

//                    if( !success ){
//                        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/1/test-"+ (index++) +".jpg";
//                        //drawLineInBitmap( bitmap, ret );
//                        path = BitmapUtils.saveImage( path, bitmap );
//                        AppLog.d("dsfdsfsdgfgfsklk DetechAynscTask picture path = " + path );
//                    }
                    bitmap.recycle();

                    AppLog.d("onPictureTaken start 3333  success="+success );
                    //double clarity = OpenCVHelper.clarityMat(MODE_CLARITY_1);
                    //boolean clarity = true/*OpenCVHelper.fuzzyDetection()>0*/;
                    if( success || bForceCamera ){

                        //第二阶段，图片生成阶段
                        String imagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;
                        success = OpenCVHelper.saveImage( imagepath );
                        //保存图片成功
                        if( success ){
                            mDetectListener.detectFile( imagepath, ret );
                            pictureRunning = false;
                            return;
                        }
                    }
                }else{
                    if(bitmap != null)bitmap.recycle();
                }

                if( bStop ) return;

                //AppLog.d("onPictureTaken start 44444");
                //修改状态，重新启动预览重新对焦
                runStatus = ST_DETECTFLAG;
                mCamera.startPreview();
                if(mDetectListener!=null){
                    mDetectListener.resetTakePicture();
                    mDetectListener.nextAutoFocus();

                    tryTime++;
//                    //启动强制拍摄的按钮
//                    if( tryTime >= 1 ){
//                        if(mDetectListener!=null)mDetectListener.forceCamera();
//                    }
                }

                //AppLog.d("onPictureTaken start 99999999");
                pictureRunning = false;
            }
        }).start();
    }

    public void setData(Rect croprect, EdgeDetectListener listener, int viewWidth, int viewHaight ){
        mCropRect = croprect;
        //mFixRect = fixrect;
        mDetectListener = listener;

        mCameraPreviewWidth = viewWidth;
        mCamreaPriviewHeight = viewHaight;
    }
    public void setPreviewStatus( boolean status ){
        bPreviewStatus = status;
    }

    public void initStatus(){
        runStatus = ST_DETECTFLAG;
        tryTime = 0;
        bForceCamera = false;
        bStop = false;
    }
    public void startCamera(){
        synchronized (instance){
            if( runStatus == ST_WAIT_FOCUS ) {
                runStatus = ST_FOUCS_CAMERA;
                time = System.currentTimeMillis();
                if( bPreviewStatus) mCamera.takePicture( null, null, instance);
                if( mDetectListener!=null )mDetectListener.startTakePicture();
            }
        }

    }

    public void startForceCamera(){
        synchronized (instance){
            time = System.currentTimeMillis();
            //bForceCamera = true;
            if( runStatus == ST_FOUCS_CAMERA )
                return;
            runStatus = ST_FOUCS_CAMERA;
            time = System.currentTimeMillis();
            mCamera.takePicture( null, null, instance);
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

    //--------------------------------------------------------------------------------------
    //强制拍摄时的默认识别框
    private int[] getDefaultquArea( Bitmap bitmap ){

        int left = bitmap.getWidth() / 10,  right = left*9;     //10%   90%
        int top  = bitmap.getHeight() / 10, bottom= top*9;      //10%   90%

        int ret[] = new int[9];
        ret[0] = 0;
        ret[1] = left;
        ret[2] = top;
        ret[3] = right;
        ret[4] = top;
        ret[5] = left;
        ret[6] = bottom;
        ret[7]= right;
        ret[8] = bottom;
        return ret;
    }
    private int index = 0;

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

            //AppLog.d(" DetechAynscTask exec start ");
            time = System.currentTimeMillis();

            bFinish = false;

            if (mCamera == null || bStop ) return false;

            Camera.Size size = mCamera.getParameters().getPreviewSize(); //获取预览大小
            //Camera.Size psize = mCamera.getParameters().getPictureSize();

            final int w = size.width;  //宽度
            final int h = size.height;
            //AppLog.d(" DetechAynscTask PreviewSize w = " + w + ",,, h = " + h + ",,, PictureSize w = " + psize.width + ",, h = " + psize.height );
            final YuvImage image = new YuvImage(mData, mCamera.getParameters().getPreviewFormat(), w, h, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
            if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                return false;
            }
            byte[] bytes = os.toByteArray();

            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inPreferredConfig = Bitmap.Config.RGB_565;          //默认 8888
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length/*,options*/);
            //AppLog.d("asdasdsad DetechAynscTask preview bitmap w = " +  bitmap.getWidth() + ",, h = " + bitmap.getHeight() );

            if( mDetectListener!=null && mCropRect!=null ){

                // 默认取camera0 的角度
                int degree[] = new int[1];
                mDetectListener.cameraRorate( degree );

                //AppLog.d(" DetechAynscTask bitmap degree = " + degree[0] );
                if( degree[0] != 0 ){
                    Matrix matrix = new Matrix();

                    matrix.postRotate( degree[0] );
                    Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    bitmap.recycle();
                    bitmap = bmp;
                }

                //从 bitmap 里面 按比例裁剪出 目标图片
                float w_rate = bitmap.getWidth()  * 1.0f / mCameraPreviewWidth;       //预览与成像的比例关系
                float h_rate = bitmap.getHeight() * 1.0f / mCamreaPriviewHeight;

                int left    = (int)(mCropRect.left * w_rate);
                int top     = (int)(mCropRect.top  * h_rate);
                int right   = (int)(mCropRect.right * w_rate);
                int bottom  = (int)(mCropRect.bottom  * h_rate);

                Bitmap dstBitmap = Bitmap.createBitmap(bitmap, left, top, right-left, bottom-top );
                bitmap.recycle();
                bitmap = dstBitmap;
//                AppLog.d("asdasdsad DetechAynscTask preview mCameraPreviewWidth=" + mCameraPreviewWidth + ",,, mCamreaPriviewHeight="+mCamreaPriviewHeight);
//                AppLog.d("asdasdsad DetechAynscTask preview w_rate=" + w_rate + ",,, h_rate="+h_rate);
//                AppLog.d("asdasdsad DetechAynscTask sss bitmap left = " + left + ",,, top = " + top + ",,, width = " + (right-left) + ",,,height = " + (bottom-top) );

                int ret[] = OpenCVHelper.detectFourEdge( bitmap, bForceCamera );
//                AppLog.d(" dsfdsfsdgfgf priview time = " + ( System.currentTimeMillis()-time ) );

                //更新到界面显示 同时判断 是否满足条件
                boolean success = mDetectListener.detectResult( ret, true );

//                if( success ){
//                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/1/test-"+ (index++) +".jpg";
//                    drawLineInBitmap( bitmap, ret );
//                    path = BitmapUtils.saveImage( path, bitmap );
//                    AppLog.d("dsfdsfsdgfgfsklk DetechAynscTask preview path = " + path );
//                }
                bitmap.recycle();

                //AppLog.d("DetechAynscTask sdsds  success = " + success + ",,,runStatus = " + runStatus );
                if( success && runStatus == ST_DETECTFLAG ){       //第一识别标志阶段，等待对焦完成再识别
                    runStatus = ST_WAIT_FOCUS;
                    return true;
                }

                //检测失败
            }else{
                bitmap.recycle();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if( runStatus == ST_WAIT_FOCUS ){
                //AppLog.d("----DetechAynscTask  ready next autofocus ");
                // mDetectListener.nextAutoFocus(); 取消对焦过程
                startCamera();
            }else if( runStatus == ST_FOUCS_CAMERA ){
                if( result ){   //处理成功
                    runStatus = ST_FINISHED_SUCCESS;
                }else{          //处理失败，重复对焦拍照流程
                    runStatus = ST_WAIT_FOCUS;
                    if(mDetectListener!=null)mDetectListener.nextAutoFocus();
                }
            }/*else{      //ST_DETECTFLAG 识别点查找失败，继续
                AppLog.d("----DetechAynscTask detect fail, next progress ");
            }*/

            bFinish = true;
        }
    }

//    private void drawLineInBitmap( Bitmap bitmap, int data[] ){
//
//        Canvas canvas = new Canvas( bitmap );
//        path path = new path();
//        path.moveTo( data[1], data[2] );
//        path.lineTo( data[3], data[4] );
//        path.lineTo( data[7], data[8] );
//        path.lineTo( data[5], data[6] );
//        path.lineTo( data[1], data[2] );
//        Paint paint = new Paint();
//        paint.setColor(Color.RED );
//        paint.setStyle(Paint.Style.STROKE);
//
//        canvas.drawPath( path, paint );
//    }

}
