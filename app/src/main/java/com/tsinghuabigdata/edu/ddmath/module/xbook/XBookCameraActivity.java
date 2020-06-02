package com.tsinghuabigdata.edu.ddmath.module.xbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BaseCameraActivity;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.ScreenUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.MoveImageView;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.CameraUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XBookCameraActivity extends BaseCameraActivity implements View.OnClickListener {

    //闪光灯
    private RelativeLayout mFlashLedLayout;
    private ImageView mFlashLedView;
    //对焦效果
    private MoveImageView mFocusImageView;
    private Drawable mFocusDrawable;

    //
    private RelativeLayout mainTipLayout;
    private TextView tipTextView;
    private TextView tip2TextView;
    private LinearLayout tip3Layout;

    private RelativeLayout cameraEnterLayout;

    private Context mContext;
    private Activity mActivity;
    private Camera camera = null;

    private boolean autofocus = false;

    private boolean bStartPreview = false;
    private boolean exit = true;
    private static final Object lock = new Object();
    
    private boolean bAutoFocus = false;
    private boolean bCameraReady = false;

    private final static int MODE_PICS = 100;    //相册返回
    private final static int MODE_EDIT = 101;    //图片编辑界面返回

    private boolean onlyFocuse = false;
    private int centerScreenX;
    private int centerScreenY;

    //传递过来的参数
    private String mSubjectName;    //学科
    private String croptype;        //拍照类型
    private String qtype;           //

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Custom_AppCompat);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if( getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ) {
            setContentView(R.layout.activity_xbook_camera_land);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView( GlobalData.isPad()?R.layout.activity_xbook_camera_port:R.layout.activity_xbook_camera_port_phone );
        }

        mContext = this;
        mActivity= this;
        bStartPreview = false;

        initView();

        initParams();

        initAutofocus();

        startTipAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(mSensorListener);

        if( camera!=null )
            camera.stopPreview();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {// 屏幕触摸事件

        if (event.getAction() == MotionEvent.ACTION_DOWN) {// 按下时自动对焦

            autofocus = true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP && autofocus ) {// 放开后拍照
            try {
                autoFocus( true, event.getRawX(), event.getRawY() );
            } catch (Exception e) {
                AppLog.i( "", e );
            }
            autofocus = false;
        }
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();

        mFocusImageView.setVisibility( View.GONE );
        bAutoFocus = false;

        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
        //mOrientationListener.enable();

        if( camera!=null )
            camera.startPreview();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
            camera = null;
        }
        //cameraInstance = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig ){
        super.onConfigurationChanged( newConfig );

        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            setContentView( GlobalData.isPad()?R.layout.activity_xbook_camera_port:R.layout.activity_xbook_camera_port_phone );
            initView();
            if( !TextUtils.isEmpty(qtype) ){
                tipTextView.setText( getResources().getString( R.string.camera_tips4) );
                tip3Layout.setVisibility( View.VISIBLE );
                tip2TextView.setVisibility( View.GONE );
            }
        }else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.activity_xbook_camera_land);
            initView();
            if( !TextUtils.isEmpty(qtype) ){
                tipTextView.setText( getResources().getString( R.string.camera_tips4) );
                tip3Layout.setVisibility( View.VISIBLE );
                tip2TextView.setVisibility( View.GONE );
            }
        }

        if( camera!=null )
            camera.startPreview();
    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.xbook_layout_cameraledbtn:{       //手电筒
                int mode = getCameraFlashMode();
                mode = (mode+1)%2;        //改变
                setFlashMode( mode );
                break;
            }

            case R.id.xbook_iv_photobtn:{       //相册
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*" );
                startActivityForResult( intent, MODE_PICS );
                break;
            }

            case R.id.xbook_camera_enter:{       //拍照
                while( !bCameraReady ){
                    SystemClock.sleep(50);
                }
                autoFocus( false, centerScreenX, centerScreenY  );
                break;
            }

            case R.id.xbook_layout_cameracancel:{       //取消
                finish();
                break;
            }
            default:
                break;
        }
    }

    //    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        if( newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch( requestCode ) {
            case MODE_PICS: {             //相册返回
                if (resultCode == RESULT_OK) {
                    try {
                        Uri originalUri = data.getData();        //获得图片的uri

                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String path = cursor.getString(column_index);

                        startEditPhoto(path, AppConst.FROM_PICS );
                    } catch (Exception e) {
                        AppLog.i("",e);
                    }
                }
                break;
            }
            case  MODE_EDIT:{

                if(data != null ) {
                    if (data.hasExtra("from")) {    //

                        bAutoFocus = false;

                    } else if (data.hasExtra("close")) {     //通知关闭
                        finish();
                    }
//                    if (data.getBooleanExtra("redo", false)) {
//
//                    }
                }
                break;
            }
            default:
                break;
        }
    }

    //-------------------------------------------------------------------------------------------------

    //中间停留 2S,然后1S内回到顶部
    private void startTipAnimation(){

        final TranslateAnimation animation = new TranslateAnimation(0, 0,ScreenUtils.getScreenContentHeight(mContext)/2-80,0);
        animation.setDuration(1000);//设置动画持续时间
        //animation.setRepeatCount(2);//设置重复次数
        animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
        mainTipLayout.setAnimation( animation );
        animation.setStartTime(2000+ SystemClock.uptimeMillis());
        //animation.startNow();
    }
    private void initView() {

        int wh[] = WindowUtils.getScreenWHForCamera( mActivity );
        //boolean navbar = WindowUtils.checkDeviceHasNavigationBar( mContext );
        int screen_width = wh[1];//WindowUtils.getScreenWidthForCamera( mContext );
        int screen_height= wh[0];//WindowUtils.getScreenHeightForCamera( mContext )  + (navbar?WindowUtils.getBottomBarHeight(mContext):0);       //竖屏使用
        if( screen_height < screen_width ){
            int tmp = screen_height;
            screen_height = screen_width;
            screen_width = tmp;
        }
        centerScreenX = screen_width / 2;
        centerScreenY = screen_height / 2;

        mFocusDrawable= getResources().getDrawable(R.drawable.ico_focus);

        SurfaceView surfaceView = (SurfaceView)findViewById( R.id.xbook_camera_preview );

        //
        SurfaceHolder surfaceHolder = surfaceView.getHolder(); // Camera interface to instantiate components
        surfaceHolder.addCallback(surfaceCallback); // Add a callback for the SurfaceHolder

        LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
        surfaceView.setLayoutParams(params);

        mFlashLedLayout = (RelativeLayout)findViewById( R.id.xbook_layout_cameraledbtn );
        mFlashLedLayout.setOnClickListener(this);

        //手电筒效果
        mFlashLedView = (ImageView) findViewById(R.id.xbook_camera_flashimg);
        mFlashLedView.setImageResource(getImageFlashMode());
        boolean flash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if(!flash){
            mFlashLedLayout.setVisibility(View.GONE);
        }

        //CameraGridView gridView = (CameraGridView)findViewById( R.id.xbook_camera_gridview );
        //gridView.setPortFlag( true );

        int mode = getCameraFlashMode();
        setFlashMode( mode );

        mFocusImageView = (MoveImageView) findViewById(R.id.xbook_iv_focusimg);

        TextView photoTextView = (TextView)findViewById( R.id.xbook_iv_photobtn );
        photoTextView.setOnClickListener(this);

        RelativeLayout mCancelLayout = (RelativeLayout)findViewById(R.id.xbook_layout_cameracancel);
        mCancelLayout.setOnClickListener( this );

        cameraEnterLayout = (RelativeLayout) findViewById(R.id.xbook_camera_enter);
        cameraEnterLayout.setOnClickListener(this);
//        cameraEnterLayout.setLongClickable(true);
//        cameraEnterLayout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                if (camera != null && !prompt_status) {
//                    autoFocus();
//                }
//                return true;
//            }
//        });
        //
        Intent intent = getIntent();
        if( intent!=null ){
            mSubjectName = intent.getStringExtra("subject");
        }

        mainTipLayout = (RelativeLayout)findViewById( R.id.xbook_camera_tiplayout );
        tipTextView = (TextView)findViewById( R.id.xbook_camera_tiptext );
        tip2TextView= (TextView)findViewById( R.id.xbook_camera_tiptext2 );
        tip3Layout  = (LinearLayout)findViewById( R.id.xbook_camera_tiptext3 );

    }

    private void initParams(){

        Intent intent = getIntent();
        if( intent==null ) return;

        mSubjectName = intent.getStringExtra( "subject" );
        croptype     = intent.getStringExtra( "ctype" );
        qtype        = intent.getStringExtra("qtype");

        if( TextUtils.isEmpty(mSubjectName) && TextUtils.isEmpty(qtype)  ){
            AlertManager.toast( mContext, "参数错误，缺少学科" );
            finish();
        }

        //仅仅拍照，没有剪刀，提示语改变
        if( !TextUtils.isEmpty(qtype) ){
            tipTextView.setText( getResources().getString( R.string.camera_tips4) );
            tip3Layout.setVisibility( View.VISIBLE );
            tip2TextView.setVisibility( View.GONE );
        }
    }

    //private OrientationEventListener mOrientationListener;
    private SensorEventListener mSensorListener;
    private SensorManager mSensorManager;
    private float mX = 0.0f;
    private float mY = 0.0f;
    private float mZ = 0.0f;
    private float mOrX = 0.0f;
    private float mOrY = 0.0f;
    private float mOrZ = 0.0f;
    private long lastTime = 0;  
    private long curTime = 0;  
    private Boolean isMove = false;    
    
    //根据重力感应自动对焦
    private void initAutofocus(){
            
        //
//        mOrientationListener = new OrientationEventListener(this) {
//            @Override
//            public void onOrientationChanged(int orientation) {
//                if (orientation == ORIENTATION_UNKNOWN) return;
//                
//                if (camera != null){
//                    //mPreview.onConfigurationChanged(null);
//                }
//            }
//        };

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new SensorEventListener() {
            
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor == null)
                    return;

                //获取当前时刻的毫秒数  
                curTime = System.currentTimeMillis();  
                //100毫秒检测一次  
                if ((curTime - lastTime) > 350) {
                    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                        final float x = event.values[0];
                        final float y = event.values[1];
                        final float z = event.values[2];
                        final float px = Math.abs(mX - x);
                        final float py = Math.abs(mY - y);
                        final float pz = Math.abs(mZ - z);
                        
                        mX = x;
                        mY = y;
                        mZ = z;
                        float h = Math.max(Math.max(px, py), pz);//Math.max(px, py);
                        final float hz = (float) Math.abs(z - 9.81);
                        if ((h > 2.0f || (hz > 0.6f && pz > 0.8f))  && camera != null){
                           isMove = true;
                        } else {
                           if( camera != null && isMove)
                           {
                               isMove = false;
                               if(!bAutoFocus&&bCameraReady) {
                                   if( camera.getParameters().isZoomSupported() ){
                                       camera.autoFocus( null );
                                   }
                               }
                           }
                        }
                    }
                    if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
                    {
                        final float x = event.values[0];
                        final float y = event.values[1];
                        final float z = event.values[2];
                        final float px = Math.abs(mOrX - x);
                        final float py = Math.abs(mOrY - y);
                        final float pz = Math.abs(mOrZ - z);
                        
                        mOrX = x;
                        mOrY = y;
                        mOrZ = z;
                        if (Math.max(Math.max(px, py), pz) > 30.0f && camera != null){
                               isMove = true;
                        }
                    }
                    
                    lastTime = curTime;  
                }
            }
            
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
    }    
        

    /**
     * 相册返回 图片地址方式
     * @param path        图片地址
     * @param from        =camera 相机，pics：相册
     */
    private void startEditPhoto(String path, String from) {

        Intent intent = new Intent(this, XBookCropImageActivity.class);
        intent.putExtra("picturePath", path );
        intent.putExtra("from", from);

        intent.putExtra("subject", mSubjectName );
        intent.putExtra("ctype", croptype);
        intent.putExtra("qtype", qtype);

        //intent.putExtra("orient", orient );
        startActivityForResult(intent, MODE_EDIT);
    }

    /**
     * 拍照直接返回Bitmap
     * @param bitmap        图片
     * @param from        =camera 相机，pics：相册
     */
    private void startEditPhoto(Bitmap bitmap, String from) {

        Intent intent = new Intent(this, XBookCropImageActivity.class);
        int session = XBookUtils.setBitmap( bitmap );
        intent.putExtra("session", session );
        intent.putExtra("from", from);

        intent.putExtra("subject", mSubjectName );
        intent.putExtra("ctype", croptype);
        intent.putExtra("qtype", qtype);

        //intent.putExtra("orient", orient );
        startActivityForResult(intent, MODE_EDIT);
    }

    private void setFlashMode(int mode) {
        SharedPreferences sharep = getSharedPreferences("ask_camera", 0);
        Editor editor = sharep.edit();
        editor.putInt("flashmode", mode);
        editor.commit();

        if (camera == null)
            return;

        mFlashLedView.setImageResource(getImageFlashMode());

        try {
            Parameters parameters = camera.getParameters();
            if( mode == 1 ){
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            }else{
                parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            }
            camera.setParameters(parameters);
        } catch (Exception e) {
            AppLog.i( "", e );
        }
        startPreview();
    }
    
    private int getCameraFlashMode() {
        SharedPreferences sharep = getSharedPreferences("ask_camera", 0);
        return sharep.getInt("flashmode", 0);
    }
    
    private String getCameraFlashModeStr() {
        int mode = getCameraFlashMode();
        String c_mode = "";
        if (mode == 1) {
            c_mode = Parameters.FLASH_MODE_TORCH;
        } else {
            c_mode = Parameters.FLASH_MODE_OFF;
        }
        return c_mode;
    }

    private int getImageFlashMode() {
        SharedPreferences sharep = getSharedPreferences("ask_camera", 0);
        int mode = sharep.getInt("flashmode", 0);
        int img_id;
        if (mode == 1) {
            img_id = R.drawable.ic_flashlight_orange;
        } else {
            img_id = R.drawable.ic_flashlight_white;
        }
        return img_id;
    }

    private void startPreview() {
        if (camera != null && !bStartPreview) {
            camera.startPreview();
            bStartPreview = true;
        }
    }

    private void stopPreview() {
        if (camera != null && bStartPreview) {
            camera.stopPreview();
            bStartPreview = false;
        }
    }

    private void autoFocus( boolean only, float centerX, float centerY )  {
        //
        if (camera != null&&!bAutoFocus&&bCameraReady) {
            onlyFocuse = only;
            int width = mFocusImageView.getWidth();
            width = width!=0?width:mFocusDrawable.getIntrinsicWidth();
            mFocusImageView.setMoveMargin( centerX - width/2, centerY - width/2 );
            mFocusImageView.setVisibility(View.VISIBLE);
            if( camera.getParameters().isZoomSupported() ){
                camera.autoFocus(autoFocusCallback);
            }else{
                autoFocusCallback.onAutoFocus( true, camera );
            }
            mFocusImageView.startZoomAndFadeOut();

            if( !only )bAutoFocus = true;
        }else {
            cameraEnterLayout.setEnabled( true );
        }
    }

    private void takePic() {
        try {
            if (camera != null)
                camera.takePicture(shutterCallback, null, pictureJpegCallback);
        } catch (Exception e) {
            AppLog.i( "", e );
        }
    }

    private int getCameraDegree() {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
        case Surface.ROTATION_0:
            degrees = 0;
            break;
        case Surface.ROTATION_90:
            degrees = 90;
            break;
        case Surface.ROTATION_180:
            degrees = 180;
            break;
        case Surface.ROTATION_270:
            degrees = 270;
            break;
        default:
            break;
        }

        int degree;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            degree = (info.orientation + degrees) % 360;
            degree = (360 - degree) % 360; // compensate the mirror
        } else { // back-facing
            degree = (info.orientation - degrees + 360) % 360;
        }

        return degree;
    }


    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            mFocusImageView.sopZoomAndFadeOut();
            if( !onlyFocuse )
                takePic();
            else {
                bAutoFocus = false;
                cameraEnterLayout.setEnabled( true );
            }
        }
    };

    // Photo call back
    Camera.PictureCallback pictureJpegCallback = new Camera.PictureCallback() {
        // @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePictureTask().execute(data);
            stopPreview();
        }
    };

    // Photo call back
//    Camera.PictureCallback pictureRawCallback = new Camera.PictureCallback() {
//        // @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//            // DDebug.debugLog( "ttttttttttttt 00100" );
//            // new SavePictureTask().execute(data);
//            // camera.startPreview();
//        }
//    };

    // Photo call back
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {

        @Override
        public void onShutter() {
        }
    };
    
//    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
//        long time = 0;
//        int old_degree = 0;
//        @Override
//        public void onPreviewFrame(byte[] data, Camera camera1) {
//            if (camera != null) {
//                try {
//                    int degree = getCameraDegree();
//                    long curr = System.currentTimeMillis();
//                    if (curr - time > 500 && old_degree != degree) {
//                        old_degree = degree;
//                        camera.setDisplayOrientation(degree);
//                        time = curr;
//                    }
//                } catch (Exception e) {
//                    AppLog.i( e.toString() );
//                }
//            }
//        }
//    };

    //------------------------------------------------------------------------------------------------------
    class CameraDeamonThread extends Thread {
        long time = 0;
        int old_degree = 0;

        public void run() {
            while (true) {
                synchronized (lock) {
                    if (exit)
                        return;
                }
                try {
                    sleep(500);
                    if (camera != null) {
                        int degree = getCameraDegree();
                        long curr = System.currentTimeMillis();
                        if (curr - time > 500 && old_degree != degree) {
                            old_degree = degree;
                            camera.setDisplayOrientation(degree);
                            time = curr;
                        }
                    }
                } catch (Exception e) {
                    AppLog.i( "", e );
                }
            }
        }
    }



    // save pic
    class SavePictureTask extends AsyncTask<byte[], String, String> {

        @Override
        protected String doInBackground(byte[]... params) {
            byte[] data = null;
            if (params!=null) {
                data = params[0];
            }
            if (data == null || camera == null)
                return null;

            // 默认取camera0 的角度
            int degree = getCameraDegree();

            //旋转
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options );
            if( degree!=0 ){
                Matrix matrix = new Matrix();
                //int width = bmp.getWidth(), height = bmp.getHeight();
                if( bmp.getWidth() > 2000 || bmp.getHeight() > 2000 ){
                    matrix.postScale( 0.5f, 0.5f );
                    //width /= 2;
                    //height /= 2;
                }
                matrix.postRotate(degree);
                Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

//                Bitmap bitmap = Bitmap.createBitmap( width, height, Bitmap.Config.RGB_565 );
//                Paint paint = new Paint();
//                Canvas canvas = new Canvas( bitmap );
//                canvas.drawBitmap( bmp, matrix, paint );

                bmp.recycle();
                bmp = bitmap;
            }

            startEditPhoto( bmp, AppConst.FROM_CAMERA );
            return "";
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {

            //默认打开后置摄像头
            CameraInfo cameraInfo = new CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
            camera = null;

            for(int i = 0; i < cameraCount; i++   ) {
                Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
                if(cameraInfo.facing  == CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    try{
                        camera = Camera.open(i);//打开当前选中的摄像头
                    }catch (Exception e){
                        AppLog.i( "", e );
                    }
                    break;
                }
            }

            if (camera == null) {
                Toast.makeText(mContext, "无法连接到相机,请检查相机权限是否打开", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
            try {
                setPicturePix();
                camera.setPreviewDisplay(holder);
                synchronized (lock) {
                    exit = false;
                }
                new CameraDeamonThread().start();
            } catch (IOException e) {
                AppLog.i( "", e );
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (camera != null) {
                int degree = getCameraDegree();
                camera.setDisplayOrientation(degree);

                Parameters parameters;
                try {
                    parameters = camera.getParameters();
                    parameters.setPictureFormat(ImageFormat.JPEG); // 设置图片格式
                    parameters.setFlashMode(getCameraFlashModeStr()); // 设置开启闪光灯
                    parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO); // 自动对焦

                    camera.setParameters(parameters);
                } catch (Exception e) {
                    AppLog.i( "", e );
                }
                
                startPreview();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if( TextUtils.isEmpty(camera.getParameters().getFlashMode()) ){
                            mFlashLedLayout.setVisibility( View.GONE );
                        }
                    }
                });

                new Thread( null,new Runnable() {
                    
                    @Override
                    public void run() {
                        SystemClock.sleep(2500);
                        bCameraReady = true;

                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                autoFocus( true, centerScreenX, centerScreenY  );
                            }
                        });
                    }
                },"").start();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                synchronized (lock) {
                    exit = true;
                }
                stopPreview();
                camera.release();
                camera = null;
            }
        }
    };


    //---------------------------------------------------------------------------------------
    /**
     * 设置拍照分辨率
     */
    private boolean setPicturePix() {
        if( camera == null )
            return false;

        // 获取照相机的参数
        Parameters params = camera.getParameters();

        //先求出预览与成像交集的列表集合
//        List<Camera.Size> dstlist = getIntersectList( params.getSupportedPreviewSizes(), params.getSupportedPictureSizes() );
//        List<Camera.Size> ll = params.getSupportedPreviewSizes();
//        // 得到最大分辨率的
//        int pindex =  getMaxSrceen( dstlist );
//
//        int w = dstlist.get(pindex).width;
//        int h = dstlist.get(pindex).height;
//
//        AppLog.i(" DetechAynscTask caamera set PictureSize w = " + w + ",,, h = " + h );
//
//        //设置参数  预览与成像参数一样
//        //params.setPictureSize( w, h );
//        params.setPreviewSize( 1280, 960 );

        //先求出屏幕的比例
        float screenRate = CameraUtil.getScreenRate( mActivity );
        AppLog.i(" setCameraParam screenRate = " + screenRate );

        //最佳预览参数
        Point point = CameraUtil.getBestCameraSize( params.getSupportedPreviewSizes(), screenRate );
        params.setPreviewSize( point.x, point.y );
        AppLog.i(" setCameraParam PreviewSize w = " + point.x + ",,,, h = " + point.y );

        point = CameraUtil.getBestCameraSize( params.getSupportedPictureSizes(), screenRate );
        params.setPictureSize( point.x, point.y );
        AppLog.i(" setCameraParam Picture w = " + point.x + ",,,, h = " + point.y );

        camera.setParameters(params);

        return true;
    }

    /**
     * 获取拍照之后的尺寸
     */
    private int getPictureSize(List<Camera.Size> list) {

        //sizes 默认是横屏，这里使用的是竖屏拍照 --手机
        // 屏幕的宽度
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        if( screenWidth < screenHeight ){
            int tmp = screenWidth;
            screenWidth = screenHeight;
            screenHeight = tmp;
        }

        //int w = WindowUtils.getScreenWidth( mContext );
        //int h = WindowUtils.getScreenHeight( mContext );

        //先查找与手机屏幕大小匹配的尺寸
        int index = findScreenSize( list, screenWidth, screenHeight );

//        //当未找到与手机分辨率相等的数值,且手机分辨率大于2000，缩小 2 倍来查找
//        if(index == -1 && screenHeight > 2000) {
//            index = findScreenSize( list, screenWidth/2, screenHeight/2 );
//        }
//
//        //如果还没有找到，按屏幕比例来
//        if( index == -1 ){
//            float rate = screenHeight * 1f / screenWidth;
//            for (int i = 0; i < list.size(); i++) {
//                Camera.Size size = list.get(i);
//                if( rate > 1 ){
//                    if( (size.width > size.height && size.width*1f/size.height==rate) || (size.width < size.height && size.height*1f/size.width==rate) )
//                        return i;
//                }else{
//                    if( (size.width < size.height && size.width*1f/size.height==rate) || (size.width > size.height && size.height*1f/size.width==rate) )
//                        return i;
//                }
//            }
//        }

        //设置的预览大小应该是大于或者等于自定义的预览大小的。因此，在获得所有的预览值大小之后，可以将每一组值与自定义的预览大小值做比较，
        // 得到宽的差的绝对值和高的差的绝对值之和，然后从中选出“宽的差的绝对值和高的差的绝对值之和”中最小的值，并且该组的宽和高都是大于
        // 或者等于自定义预览大小的宽和高的值。
        if( index == -1 ) index = findNearScreenSize(  list, screenWidth, screenHeight  );

        //还是没有找到，取中间
        if( index == -1 ) index = list.size() / 2;

        return index;
    }

    private int findScreenSize( List<Camera.Size> list, int screenWidth, int screenHeight  ){
        AppLog.i("gghjkjjkjk screenWidth = " + screenWidth + ",,, screenHeight = " + screenHeight );
        for (int i = 0; i < list.size(); i++) {
            Camera.Size size = list.get(i);
            AppLog.i("gghjkjjkjk i = " + i + ",,, width = " + size.width + ",,, height = " + size.height );
            if( (screenHeight == size.width && screenWidth == size.height) || (screenWidth == size.width && screenHeight == size.height) ) {
                return i;
            }
        }
        return -1;
    }

    private int findNearScreenSize( List<Camera.Size> sizes, int width, int height  ){

        //先
        ArrayList<WrapCameraSize> wrapCameraSizes = new ArrayList<>(sizes.size());
        for (int i = 0; i < sizes.size(); i++) {
            Camera.Size size = sizes.get(i);

            WrapCameraSize wrapCameraSize = new WrapCameraSize();
            wrapCameraSize.setWidth(size.width);
            wrapCameraSize.setHeight(size.height);
            wrapCameraSize.setD(Math.abs((size.width - width)) + Math.abs((size.height - height)));
            wrapCameraSize.setIndex( i );

            wrapCameraSizes.add(wrapCameraSize);
        }

        //排序
        WrapCameraSize minCameraSize = Collections.min(wrapCameraSizes);

        //找到最小，且
        while (!(minCameraSize.getWidth() >= width && minCameraSize.getHeight() >= height)) {
            wrapCameraSizes.remove(minCameraSize);
            //minCameraSize = null;
            minCameraSize = Collections.min(wrapCameraSizes);
        }

        return minCameraSize.getIndex();
    }
    /**
     * Created by wangjiang on 2016/1/28.用于存储得到的相机预览大小的值，以及自定义预览大小与相机预览大小差的绝对值，以便从相机预览大小所有值中找出大于或者等于自定义预览大小最合适的值。
     */
    public class WrapCameraSize implements Comparable<WrapCameraSize> {
        private int width;//宽
        private int height;//高
        private int d;//宽的差的绝对值和高的差的绝对值之和
        private int index;      //原来的序号

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }


        @Override
        public int compareTo(WrapCameraSize another) {
            if (this.d > another.d) {
                return 1;
            } else if (this.d < another.d) {
                return -1;
            }
            return 0;
        }
    }

}
