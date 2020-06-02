package com.tsinghuabigdata.edu.ddmath.module.ddwork;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BaseCameraActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionRect;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.camera.CameraPreviewCallBack;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.camera.DetectListener;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.FixedPiontView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.FocusTipsView;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.ScWorkUtils;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.MoveImageView;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.CameraUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;

import java.io.IOException;
import java.util.ArrayList;

public class DDCameraActivity extends BaseCameraActivity implements View.OnClickListener, DetectListener{

    public static final String PARAM_RATE      = "rate";          //教辅宽度/高比

    public static void openActivity( Context context, LocalPageInfo pageInfo, boolean isExam, float rate ){
        if( context == null || pageInfo ==null )
            return;

        Intent intent = new Intent( context, DDCameraActivity.class );
        AccountUtils.setLocalPageInfo( pageInfo );
        AccountUtils.setExamFlag( isExam );
        intent.putExtra( PARAM_RATE, rate );
        context.startActivity( intent );
    }

    //纸张框的比例宽度
    public static int PAPER_WIDTH = 185;     //A4,210
    public static int PAPER_HEIGHT= 278;     //A4,297
    private float bookRate;

    //上面提示框区域
    private RelativeLayout tipsLayout;
    private FocusTipsView focusTipLayout;
    //
    private RelativeLayout mainLayout;

    //
    //private SurfaceView mSurfaceView;

    //闪光灯
    private RelativeLayout mFlashLedLayout;
    private ImageView mFlashLedView;
    //对焦效果
    private MoveImageView mFocusImageView;
    private TextView dealCameraTextView;

    //定位框
    private FixedPiontView lefttopFixedView;
    private FixedPiontView righttopFixedView;
    private FixedPiontView leftbottomFixedView;
    private FixedPiontView rightbottomFixedView;

    private View topEdgeView;
    private View leftEdgeView;
    private View rightEdgeView;
    private View bottomEdgeView;

    private Context mContext;
    private Activity mActivity;
    private DetectListener mDetectListener;
    private Camera camera = null;

    private boolean autofocus = false;

    private boolean bStartPreview = false;
    private boolean exit = true;
    private static final Object lock = new Object();;
    
    private boolean bAutoFocus = false;
    private boolean bCameraReady = false;
    //private boolean onlyFocuse = false;
    private int offset_margin = 0;
    private boolean bNoAutoFocus = false;

    //private final static int MODE_PICS = 100;    //相册返回
    private final static int MODE_EDIT = 101;    //图片编辑界面返回

    //传递过来的参数
//    private String workId;        //作业ID
//    private int    pageIndex;     //页序号
    private LocalPageInfo localPageInfo;

    //隐藏虚拟键后的宽高
    private int mScreenWidth;
    private int mScreenHeight;

    private Point previewPoint;
    private Point picturePoint;

    private Point mFocusPoint;      //对焦位置
    private Point mFocusViewPoint;  //对焦位置指示

    private CameraPreviewCallBack mPreviewCallBack;
    private boolean bQuit = false;
    private boolean bCreate = false;

    private FocusManager mFocusManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme( R.style.Theme_Custom_AppCompat );
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        AppLog.d(" CameraActivity onCreate ");

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //hideNavigationBar();

        setContentView(GlobalData.isPad()?R.layout.activity_ddwork_camera:R.layout.activity_ddwork_camera_phone);

        mContext = this;
        mActivity = this;
        bStartPreview = false;
        mDetectListener = this;
        bCreate = true;

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        mScreenWidth = point.x;
        mScreenHeight = point.y;

        mPreviewCallBack = new CameraPreviewCallBack();
        if( !parseIntent() ){
            ToastUtils.show( this, "参数错误", Toast.LENGTH_SHORT );
            finish();
            return;
        }
        initView();

        initAutofocus();

        //
        startSetParamThread();

        initCommonGudie();
    }

    public void hideNavigationBar()
    {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if( android.os.Build.VERSION.SDK_INT >= 19 ){
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

    @Override
    protected void onPause() {
        AppLog.d(" CameraActivity onPause 。。。。。。。 "  );
        mSensorManager.unregisterListener(mSensorListener);

        if( camera!=null ) {
            stopPreview();
        }
        mFocusManager.stop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppLog.d(" CameraActivity onResume ");
        hideNavigationBar();

        showTipInfo( View.INVISIBLE, View.GONE, View.GONE );
        bAutoFocus = false;
        bNoAutoFocus = false;
        focusFailCount = 0;

        localPageInfo.initCacheData();
        //此页没有拍照时，默认全对, 已拍照，默认上次的结果
        if( TextUtils.isEmpty(localPageInfo.getLocalpath()) && localPageInfo.getUploadType() == AppConst.UPLOAD_TYPE_MARKED) localPageInfo.setDeflautCorrectCache();

        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        AppLog.i(" PreviewCallback camera = " + camera );
        if( camera!=null ) {
            camera.startPreview();
            camera.setPreviewCallback( mPreviewCallBack );

            //延时500ms后开始自动对焦
            startAutoFouse( bCreate?500:300 );
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {// 屏幕触摸事件

        if (event.getAction() == MotionEvent.ACTION_DOWN) {// 按下时自动对焦
            autofocus = true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP && autofocus ) {// 放开后拍照
            try {
                Rect rect = new Rect();
                mainLayout.getGlobalVisibleRect( rect );
                if( rect.contains( (int)event.getRawX(), (int)event.getRawY()) )
                    autoFocus( true, event.getX() - offset_margin, event.getY() );
            } catch (Exception e) {
                AppLog.i( "", e );
            }
            autofocus = false;
            mFocusManager.stop();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPreviewCallBack.stopDetechAynscTask();
        OpenCVHelper.releaseBitmap();

        if (camera != null) {
            camera.release();
            camera = null;
        }
        bQuit = true;
        //cameraInstance = false;
    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.ddwork_layout_cameraledbtn:{       //手电筒
                int mode = getCameraFlashMode();
                mode = (mode+1)%2;        //改变
                setFlashMode( mode );
                break;
            }
//            case R.id.ddwork_iv_photobtn:{       //相册
//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*" );
//                startActivityForResult( intent, MODE_PICS );
//                break;
//            }

//            case R.id.ddwork_camera_enter:{       //拍照
//                while( !bCameraReady ){
//                    SystemClock.sleep(50);
//                }
//
//                autoFocus( false, centerScreenX, centerScreenY  );
//                break;
//            }

            case R.id.ddwork_layout_cameracancel:{       //取消
                finish();
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch( requestCode ) {
//            case MODE_PICS: {             //相册返回
//                if (resultCode == RESULT_OK) {
//                    try {
//                        Uri originalUri = data.getData();        //获得图片的uri
//
//                        String[] proj = {MediaStore.Images.Media.DATA};
//                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);
//                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                        cursor.moveToFirst();
//                        String path = cursor.getString(column_index);
//
//                        startEditPhoto(path);
//                    } catch (Exception e) {
//                        AppLog.i(e.toString());
//                    }
//                }
//                break;
//            }
            case  MODE_EDIT:{
                if(data != null ) {
                    if (data.hasExtra("from")) {    //
                        bAutoFocus = false;
                        mPreviewCallBack.initStatus();

                        //状态恢复
                        detectResult( 0 );

                    } else if (data.hasExtra("close")) {     //通知关闭
                        finish();
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void showCenterToast(final String msg ){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            ToastUtils.showToastCenter( mContext, msg );
            }
        });
    }
    @Override
    public void detectResult( final int result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //1000 代表右下角 100代表左上角,010代表右上角,001代表左下角
                if( (0x8&result) == 0x8){
                    rightbottomFixedView.setFixedStatus( FixedPiontView.ST_FIXED );
                }else{
                    rightbottomFixedView.setFixedStatus( FixedPiontView.ST_FIXING );
                }

                if( (0x4&result) == 0x4){
                    lefttopFixedView.setFixedStatus( FixedPiontView.ST_FIXED );
                }else{
                    lefttopFixedView.setFixedStatus( FixedPiontView.ST_FIXING );
                }

                if( (0x2&result) == 0x2){
                    righttopFixedView.setFixedStatus( FixedPiontView.ST_FIXED );
                }else{
                    righttopFixedView.setFixedStatus( FixedPiontView.ST_FIXING );
                }

                if( (0x1&result) == 0x1){
                    leftbottomFixedView.setFixedStatus( FixedPiontView.ST_FIXED );
                }else{
                    leftbottomFixedView.setFixedStatus( FixedPiontView.ST_FIXING );
                }

                if( result == 15 ){
                    mFocusManager.stop();
                    bCreate = false;
                    showTipInfo( View.INVISIBLE, View.GONE, View.VISIBLE );
                }
            }
        });
    }

    @Override
    public void detectFile(String filepath) {

        AppLog.i(" DetechAynscTask filepath = " + filepath );

        Intent intent = new Intent(this, DDPreviewActivity.class);
        intent.putExtra( ScWorkUtils.IMAGE_TYPE, ScWorkUtils.TYPE_LOCAL );
        intent.putExtra( ScWorkUtils.IMAGE_HANDLE, filepath );
//        intent.putExtra( DDWorkUtil.PAGEINDEX, pageIndex );
//        intent.putExtra( DDWorkUtil.WORK_ID, workId );

        startActivityForResult(intent, MODE_EDIT);
    }

    @Override
    public void cameraRorate( int degree[] ){
        degree[0] = getCameraDegree();
    }

    @Override
    public void nextAutoFocus() {
        startAutoFouse(0);  //立即开始对焦
    }

    @Override
    public void startTakePicture(){
        bNoAutoFocus = true;
    }
    @Override
    public void resetTakePicture(){
        bNoAutoFocus = false;
        focusFailCount = 0;
    }

    //-------------------------------------------------------------------------------------------------
    private void initView() {

        tipsLayout = findViewById( R.id.ddwork_tips_layout );
        mainLayout = findViewById( R.id.ddwork_mainLayout );
        focusTipLayout=findViewById( R.id.ddwork_camera_focustiplayout );

        SurfaceView mSurfaceView = findViewById( R.id.ddwork_camera_preview );

        //
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder(); // Camera interface to instantiate components
        surfaceHolder.addCallback(surfaceCallback); // Add a callback for the SurfaceHolder

        LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
        mSurfaceView.setLayoutParams(params);

        mFlashLedLayout = findViewById( R.id.ddwork_layout_cameraledbtn );
        mFlashLedLayout.setOnClickListener(this);

        if( !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ){
            mFlashLedLayout.setVisibility( View.GONE );     //没有闪光灯
        }

        //手电筒效果
        mFlashLedView =  findViewById(R.id.ddwork_camera_flashimg);
        mFlashLedView.setImageResource(getImageFlashMode());
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            mFlashLedView.setVisibility(View.GONE);
        }

        //CameraGridView gridView = (CameraGridView)findViewById( R.id.ddwork_camera_gridview );
        //gridView.setPortFlag( true );

        int mode = getCameraFlashMode();
        setFlashMode( mode );

        mFocusImageView =  findViewById(R.id.ddwork_iv_focusimg);
        dealCameraTextView=findViewById( R.id.ddwork_iv_dealimg );

//        TextView photoTextView = (TextView)findViewById( R.id.ddwork_iv_photobtn );
//        photoTextView.setOnClickListener(this);

        RelativeLayout mCancelLayout = findViewById(R.id.ddwork_layout_cameracancel);
        mCancelLayout.setOnClickListener( this );

        //RelativeLayout cameraEnterLayout = (RelativeLayout) findViewById(R.id.ddwork_camera_enter);
        //cameraEnterLayout.setOnClickListener(this);

        lefttopFixedView = findViewById( R.id.ddwork_lefttop_fixpointview );
        righttopFixedView = findViewById( R.id.ddwork_righttop_fixpointview );
        leftbottomFixedView = findViewById( R.id.ddwork_leftbottom_fixpointview );
        rightbottomFixedView = findViewById( R.id.ddwork_rightbottom_fixpointview );

        mFocusManager = new FocusManager();

        leftEdgeView = findViewById( R.id.ddwork_camera_leftedge );
        topEdgeView = findViewById( R.id.ddwork_camera_topedge );
        rightEdgeView = findViewById( R.id.ddwork_camera_rightedge );
        bottomEdgeView = findViewById( R.id.ddwork_camera_bottomedge );

    }

    //根据显示情况，设置取景框高宽，及 定位框
    private void startSetParamThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while( true ){

                    SystemClock.sleep(50);
                    if( bQuit ) break;

                    if( tipsLayout.getHeight() == 0 || camera == null/* || !bAutoFocus */ )
                        continue;

                    //设置相机参数
                    if( previewPoint ==null || picturePoint == null )
                        setCameraParams();

                    //先判断 相机参数 与 屏幕的关系，调整surfaceView的边距
                    int camera_width = previewPoint.y;
                    int camera_height= previewPoint.x;

                    //  预览显示到屏幕的变化比例
                    float rate_w = mScreenWidth*1f / camera_width;         //  预览
                    float rate_h = mScreenHeight*1f / camera_height;       //

                    //识别标志图最小宽度 图片宽度 + 8dp
                    /*int minImageWidth = detechDrawble.getIntrinsicWidth() + WindowUtils.dpToPixels(mContext,8);
                    if( !GlobalData.isPad() )*/
                    int minImageWidth = 0;

                    // 调整拍照裁剪框的边距

                    //可用宽高
                    int usewidth = mScreenWidth - minImageWidth*2;
                    int useheight= mScreenHeight - tipsLayout.getHeight();

                    //裁剪框的目标宽高(变换比例)
                    int dstWidth = (int)((useheight * bookRate * rate_w) / (rate_h) );      //默认 高度撑满
                    int dstHeight= useheight;
                    if( dstWidth > usewidth ){
                        dstWidth = usewidth;                   //宽度撑满，
                        dstHeight= (int)((usewidth * rate_h) / (bookRate * rate_w) );
                    }

                    //计算上下 左右的偏移距离
                    int offset_width = ( mScreenWidth - dstWidth ) / 2;//     + camera_marginWidth;
                    int offset_height= ( useheight - dstHeight ) / 2;//   + camera_marginHeight;

                    AppLog.i(" DetechAynscTask set MainLayout offset_width = " + offset_width + ",,, offset_height = " + offset_height );

                    adjustViewPostion( offset_width,offset_height );

                    //裁剪框（相对surfaceView ）
                    Rect cropRect = new Rect();
                    cropRect.left = offset_width;
                    cropRect.top  = offset_height;//tipsLayout.getHeight();
                    cropRect.right= cropRect.left + dstWidth;
                    cropRect.bottom= cropRect.top + dstHeight;

                    if( mFocusPoint == null ){
                        mFocusPoint = new Point();
                        mFocusViewPoint = new Point();
                    }
                    mFocusManager.calFocusPosition( localPageInfo.getQuestions(), cropRect, mFocusPoint );

                    //变换相对预览图的位置
                    cropRect.left = (int)(cropRect.left / rate_w);
                    cropRect.top = (int)(cropRect.top / rate_h);
                    cropRect.right = (int)(cropRect.right / rate_w);
                    cropRect.bottom = (int)(cropRect.bottom / rate_h);


                    //左上角 定位框 的 Rect    相对于裁剪框的位置
                    int left = WindowUtils.dpToPixels( mContext, 4 );
                    int top = WindowUtils.dpToPixels( mContext, 4 );
                    int width = WindowUtils.dpToPixels( mContext, 144 );
                    if( !GlobalData.isPad() ){
                        left = WindowUtils.dpToPixels( mContext, 2 );
                        top = WindowUtils.dpToPixels( mContext, 2 );
                        width = WindowUtils.dpToPixels( mContext, 96 );
                    }
                    int height=width;

                    //变换相对预览图的位置
                    left = (int)(left / rate_w);
                    top = (int)(top / rate_h);
                    width = (int)(width / rate_w);
                    height = (int)(height / rate_h);
                    if( width < height ){
                        width = height;
                    }

                    Rect fixRect = new Rect( left, top, left+width, top+width );

                    mPreviewCallBack.setData( cropRect, fixRect, mDetectListener, camera_width, camera_height );
                    break;
                }

            }
        }).start();
    }

    private boolean parseIntent(){
        localPageInfo = AccountUtils.getLocalPageInfo();
        bookRate  = getIntent().getFloatExtra( PARAM_RATE, -1 );
        if( bookRate < 0.1 )
            bookRate = PAPER_WIDTH * 1f/PAPER_HEIGHT;
        mPreviewCallBack.setPaperDefaultWidthHeight( bookRate );
        return !( localPageInfo.getQuestions() == null || localPageInfo.getQuestions().size() == 0 );
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
                               if(!bAutoFocus&&bCameraReady && !bNoAutoFocus) {
                                   startAutoFocus();
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

    private void adjustViewPostion( final int offset_width, final int offset_height ){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //A4
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mainLayout.getLayoutParams();
                layoutParams.setMargins( offset_width, offset_height, offset_width, offset_height );
                mainLayout.setLayoutParams( layoutParams );

                int linewidth = WindowUtils.dpToPixels(mContext,4);
                if( !GlobalData.isPad() ) linewidth = WindowUtils.dpToPixels(mContext,2);
                offset_margin = offset_width;

                //调整 三个定位框的位置
//                lefttopFixedView.setFixedWidth( offset_width );
//                leftbottomFixedView.setFixedWidth( offset_width );
//                righttopFixedView.setFixedWidth( offset_width );
//                rightbottomFixedView.setFixedWidth( offset_width );

                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams)lefttopFixedView.getLayoutParams();
                layoutParams1.setMargins( offset_width+linewidth, offset_height+linewidth, offset_width+linewidth, offset_height+linewidth );
                lefttopFixedView.setLayoutParams( layoutParams1 );

                layoutParams1 = (RelativeLayout.LayoutParams)righttopFixedView.getLayoutParams();
                layoutParams1.setMargins( offset_width+linewidth, offset_height+linewidth, offset_width+linewidth, offset_height+linewidth );
                righttopFixedView.setLayoutParams( layoutParams1 );

                layoutParams1 = (RelativeLayout.LayoutParams)leftbottomFixedView.getLayoutParams();
                layoutParams1.setMargins( offset_width+linewidth, linewidth, offset_width+linewidth, offset_height+linewidth );
                leftbottomFixedView.setLayoutParams( layoutParams1 );

                layoutParams1 = (RelativeLayout.LayoutParams)rightbottomFixedView.getLayoutParams();
                layoutParams1.setMargins( offset_width+linewidth, linewidth, offset_width+linewidth, offset_height+linewidth );
                rightbottomFixedView.setLayoutParams( layoutParams1 );
            }
        });
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
            bStartPreview = true;
            mPreviewCallBack.setPreviewStatus( true );
            camera.startPreview();
            camera.setPreviewCallback( mPreviewCallBack );
        }
    }

    private void stopPreview() {
        if (camera != null && bStartPreview) {
            bStartPreview = false;
            mPreviewCallBack.setPreviewStatus( false );
            camera.stopPreview();
            camera.setPreviewCallback( null );
        }
    }

    //only 仅仅是对焦
    private void autoFocus( boolean only, float centerX, float centerY ) {
        //
        mFocusManager.show( false );
        if (camera != null&&!bAutoFocus&&bCameraReady&&!bNoAutoFocus) {

            mFocusImageView.setMoveMargin( centerX - mFocusImageView.getWidth()/2, centerY - mFocusImageView.getHeight()/2 );
            showTipInfo( View.VISIBLE, View.GONE, View.VISIBLE );
            mFocusImageView.startZoomAndFadeOut();
            //if( mFocusPoint != null ){      //设置指定对焦位置
            //    mFocusManager.setFocusArea( (int)centerX, (int)centerY, camera );
            //}
            startAutoFocus();

            if( !only )bAutoFocus = true;
        }
    }

    private void startAutoFocus(){
        if( camera.getParameters().isZoomSupported() ){
            camera.autoFocus(autoFocusCallback);
        }else{
            autoFocusCallback.onAutoFocus( true, camera );
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

    //再次对焦失败次数
    private int focusFailCount = 0;     //大于1次，直接取成像图

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            mFocusImageView.sopZoomAndFadeOut();
            //AppLog.d("dfdfdfdfd  auto focus = " + success );
            if( success ) {
                if (mPreviewCallBack != null){
                    mPreviewCallBack.startCamera();
                }
                mFocusManager.start();
                focusFailCount = 0;
            }else{
                focusFailCount++;
                if( focusFailCount >= 2 && mPreviewCallBack.isWaitNextFocus() ){    //
                    if (mPreviewCallBack != null){
                        mPreviewCallBack.startCamera();
                    }
                    mFocusManager.start();
                    focusFailCount = 0;
                }else{
                    //ToastUtils.showShort( mContext, "对焦失败" );
                    mFocusManager.show( true );
                }
            }
        }
    };
    //------------------------------------------------------------------------------------------------------
    private class CameraDeamonThread extends Thread {
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

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {

            //默认打开后置摄像头
            int cameraCount = 0;
            CameraInfo cameraInfo = new CameraInfo();
            cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
            camera = null;

//            PackageManager pm = getPackageManager();
            // FEATURE_CAMERA - 后置相机
            // FEATURE_CAMERA_FRONT - 前置相机
//            if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
//                Log.i("camera", "non-support");
//            } else {
//                Log.i("camera", "support");
//            }

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
                setCameraParams();
                camera.setPreviewDisplay(holder);
                synchronized (lock) {
                    exit = false;
                }
                new CameraDeamonThread().start();
            } catch (IOException e) {
                AppLog.i( "", e );
                if (camera != null) {
                    mPreviewCallBack.stopDetechAynscTask();
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

                //延时500ms后开始自动对焦
                startAutoFouse( 500 );

            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                synchronized (lock) {
                    exit = true;
                }
                camera.setPreviewCallback(null) ;
                mPreviewCallBack.stopDetechAynscTask();
                stopPreview();
                camera.release();
                camera = null;
            }
        }
    };

    //
    private void startAutoFouse( final int delaytime ){
        new Thread( null,new Runnable() {

            @Override
            public void run() {
                SystemClock.sleep(delaytime);
                bCameraReady = true;

                if( bQuit ) return;
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if( mFocusViewPoint != null )
                            autoFocus( true, mFocusViewPoint.x, mFocusViewPoint.y  );
                    }
                });
            }
        },"").start();
    }

    //---------------------------------------------------------------------------------------
    /**
     * 设置拍照分辨率 / 预览分辨率
     */
    private boolean setCameraParams() {

        if( camera == null )
            return false;

        // 获取照相机的参数
        Parameters params = camera.getParameters();

        //params.setPreviewFormat(ImageFormat.NV21);

//        //先求出预览与成像交集的列表集合
//        List<Camera.Size> dstlist = getIntersectList( params.getSupportedPreviewSizes(), params.getSupportedPictureSizes() );
//
//        // 得到最大分辨率的
//        int pindex = getMaxSrceen( dstlist );
//
//        int w = dstlist.get(pindex).width;
//        int h = dstlist.get(pindex).height;
//
//        AppLog.i(" DetechAynscTask caamera set PictureSize w = " + w + ",,, h = " + h );
//
//        //设置参数  预览与成像参数一样
//        params.setPictureSize( w, h );
//        params.setPreviewSize( w, h );
//        camera.setParameters(params);

        //-----------------------------------------------------------------------------------------------
        //简化方案: 以屏幕比例为基础，屏幕与预览合一，同比例去设置成像参数
//        float screenRate = CameraUtil.getScreenRate( mContext );
//        AppLog.i(" setCameraParam screenRate = " + screenRate );
//
//        //最佳预览参数
//        previewPoint = CameraUtil.getBestCameraSize( params.getSupportedPreviewSizes(), screenRate );
//        params.setPreviewSize( previewPoint.x, previewPoint.y );
//        AppLog.i(" setCameraParam PreviewSize w = " + previewPoint.x + ",,,, h = " + previewPoint.y );
//
//        picturePoint = CameraUtil.getBestCameraSize( params.getSupportedPictureSizes(), screenRate );
//        params.setPictureSize( picturePoint.x, picturePoint.y );
//        AppLog.i(" setCameraParam Picture w = " + picturePoint.x + ",,,, h = " + picturePoint.y );

        //-----------------------------------------------------------------------------------------------
        //方案1: 以成像参数为基础，300万左右， 成像，预览，屏幕 同比例去设置成像参数
        float screenRate = CameraUtil.getScreenRate( mActivity );
        float bestRate;
        AppLog.i(" setCameraParam screenRate = " + screenRate );

        //最佳成像参数
        picturePoint = CameraUtil.getBestCameraPictureSize( params.getSupportedPictureSizes(), screenRate );
        if( picturePoint == null ){
            ToastUtils.show( this, "相机像素太低，不支持此相机拍照。", Toast.LENGTH_SHORT );
            finish();
            return false;
        }
        params.setPictureSize( picturePoint.x, picturePoint.y );
        bestRate = picturePoint.y *1f / picturePoint.x;

        AppLog.i(" setCameraParam Picture w = " + picturePoint.x + ",,,, h = " + picturePoint.y+",,,bestRate="+bestRate );

        //根据成像比例寻找 合适的预览比例
        previewPoint = CameraUtil.getBestCameraSize( params.getSupportedPreviewSizes(), bestRate );
        params.setPreviewSize( previewPoint.x, previewPoint.y );
        AppLog.i(" setCameraParam PreviewSize w = " + previewPoint.x + ",,,, h = " + previewPoint.y );

        //根据成像比例, 调整屏幕UI比例效果
        adjuestUI( bestRate );

        camera.setParameters(params);

        return true;
    }

    private void adjuestUI(float bestRate){
        int wh[] = WindowUtils.getScreenWHForCamera( mActivity );
        //boolean navbar = WindowUtils.checkDeviceHasNavigationBar( mContext );
        int screen_width = wh[1];//WindowUtils.getScreenWidthForCamera( mContext );
        int screen_height= wh[0];//WindowUtils.getScreenHeightForCamera( mContext )  + (navbar?WindowUtils.getBottomBarHeight(mContext):0);       //竖屏使用
        if( screen_height < screen_width ){
            int tmp = screen_height;
            screen_height = screen_width;
            screen_width = tmp;
        }

//        boolean navbar = WindowUtils.checkDeviceHasNavigationBar( mContext );
//        int screen_width = WindowUtils.getScreenWidthForCamera( mContext );
//        int screen_height= WindowUtils.getScreenHeightForCamera( mContext )  + (navbar?WindowUtils.getBottomBarHeight(mContext):0);       //竖屏使用

        //AppLog.d(" setCameraParam adjuestUI w = " + screen_width + ",,,, h = " + screen_height + ",,,navbar = " + navbar );
        //AppLog.d(" setCameraParam adjuestUI h = " + WindowUtils.getScreenHeight( mContext ) + ",,,, barh = " + WindowUtils.getBottomBarHeight(mContext) );

        if( screen_height*bestRate > screen_width ){        //width 不够，则width占满，高度方向 添加黑
            int dst = ((int)(screen_height - screen_width/bestRate))/2;
            setEdgeView( 0, dst, 0, dst );
            AppLog.d(" setCameraParam adjuestUI ddd " +dst);
        }else{                  //height 不够，height，宽度方向 添加黑
            int dst = (screen_width - (int)(screen_height*bestRate))/2;
            setEdgeView( dst, 0, dst, 0 );
            AppLog.d(" setCameraParam adjuestUI xxx " +dst);
        }
    }

    //设置四边的宽度或者高度
    private void setEdgeView( final int left, final int top, final int right, final int bottom ){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = leftEdgeView.getLayoutParams();
                layoutParams.width = left;
                leftEdgeView.setLayoutParams( layoutParams );

                layoutParams = rightEdgeView.getLayoutParams();
                layoutParams.width = right;
                rightEdgeView.setLayoutParams( layoutParams );

                layoutParams = topEdgeView.getLayoutParams();
                layoutParams.height = top;
                topEdgeView.setLayoutParams( layoutParams );

                layoutParams = bottomEdgeView.getLayoutParams();
                layoutParams.height = bottom;
                bottomEdgeView.setLayoutParams( layoutParams );

            }
        });
    }
    //保证对焦，手动对焦提示，稳定提示同时 只显示一个
    private void showTipInfo( int focusImage, int handTips, int handStable ){
        mFocusImageView.setVisibility( focusImage );
        focusTipLayout.setVisibility( handTips );
        dealCameraTextView.setVisibility( handStable );
    }

    //-----------------------------------------------------------------------------------------
    private class FocusManager{

        //间隔时间
        private static final int TIME = 2000;

        private static final int ST_IDLE = 0;
        private static final int ST_RUN  = 1;
        private static final int ST_STOPING = 2;
        //private static final int ST_STOP = 3;

        private int status = ST_IDLE;

        private long startTime;

        public void start(){
            show( false );
            startTime = System.currentTimeMillis();
            if( ST_IDLE == status ){
                startThread();
            }else if( ST_STOPING == status ){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(50);
                        startThread();
                    }
                }).start();
            }//else ST_RUN  not do
        }

        public void stop(){
            show( false );
            if( ST_RUN == status )
                status = ST_STOPING;
        }

        private void startThread(){
            status = ST_RUN;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while( ST_RUN == status ){
                        SystemClock.sleep(50);
                        if( ST_STOPING == status ) break;
                        if( System.currentTimeMillis()-startTime > TIME ){
                            if( bCreate ){
                                //首次进入，多对焦一次，再次自动对焦
                                bCreate = false;
                                startAutoFouse( 0 );
                            }else{
                                show( true );
                            }
                            break;
                        }
                    }
                    status = ST_IDLE;
                }
            }).start();
        }

        private void show( final boolean show ){
            if( show && bNoAutoFocus ) return;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showTipInfo( View.INVISIBLE, show?View.VISIBLE:View.GONE, show?View.GONE:View.VISIBLE );

                    if(show )focusTipLayout.start();
                    else focusTipLayout.stop();
                }
            });
        }

        //-------------------------------------------------------------------------------------
        //指定对焦位置

        //根据触摸位置设置对焦点的对焦权值：
        //（解释一下：每个对焦区域是一个具有特定权值的长方形。方向与重力感应的方向有关。而且不会受到
        // setDisplayOrientation(int)旋转画面设置的影响。矩形的坐标范围指定从-1000到1000 ，
        // （-1000,-1000）是左上角点（1000，1000）是右下角点。对焦权值的取值范围是1-1000，
        // 权值为矩形范围像素所平分，这意味着同样的权值对焦区域大的对整体的对焦影响小。
        // 理论结合实际的说就是，假如你的整个屏幕的宽度是width，那么点击某个位置算x，那么要划定对焦区域，
        // 就要告诉相机对焦的坐标，根据对焦坐标的范围为-1000到1000，所以要用公式计算对应的对焦坐标实际上是:
        // x/width*2000-1000=对焦x坐标，同样获取到对焦y坐标，然后以x，y为中心，指定对焦区域，
        // 设置对焦权值，越大表示优先对焦该位置）
//        private final int AREA_SIZE = 300;
//        private void setFocusArea(int fx, int fy, Camera mCamera ) {
//
//            boolean navbar = WindowUtils.checkDeviceHasNavigationBar( mContext );
//            int screenWidth = WindowUtils.getScreenWidthForCamera( mContext );
//            int screenHeight= WindowUtils.getScreenHeightForCamera( mContext )  + (navbar?WindowUtils.getBottomBarHeight(mContext):0);       //竖屏使用
//
//            mCamera.cancelAutoFocus();
//            Camera.Parameters p = mCamera.getParameters();
//            //计算对焦中心
//            float touchX = (fx / screenWidth) * 2000 - 1000;
//            float touchY = (fy / screenHeight) * 2000 - 1000;
//            //计算区域
//            int left = clamp((int) touchX - AREA_SIZE / 2, -1000, 1000);
//            int right = clamp(left + AREA_SIZE, -1000, 1000);
//            int top = clamp((int) touchY - AREA_SIZE / 2, -1000, 1000);
//            int bottom = clamp(top + AREA_SIZE, -1000, 1000);
//            Rect rect = new Rect(left, top, right, bottom);
//
//            if (p.getMaxNumFocusAreas() > 0) {
//                List<Camera.Area> areaList = new ArrayList<Camera.Area>();
//                areaList.add(new Camera.Area(rect, 1000));
//                p.setFocusAreas(areaList);
//            }
//            if (p.getMaxNumMeteringAreas() > 0) {
//                List<Camera.Area> areaList = new ArrayList<Camera.Area>();
//                areaList.add(new Camera.Area(rect, 1000));
//                p.setMeteringAreas(areaList);
//            }
//            p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            try {
//                mCamera.setParameters(p);
//            } catch (Exception e) {
//                AppLog.i("set Camera Parameter",e);
//            }
//        }

//        private  Rect calculateTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
//            float focusAreaSize = 300;
//            int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
//
//            boolean navbar = WindowUtils.checkDeviceHasNavigationBar( mContext );
//            int screen_width = WindowUtils.getScreenWidthForCamera( mContext );
//            int screen_height= WindowUtils.getScreenHeightForCamera( mContext )  + (navbar?WindowUtils.getBottomBarHeight(mContext):0);       //竖屏使用
//
//            int centerX = (int) (x / screen_width*2000 - 1000);
//            int centerY = (int) (y / screen_height*2000 - 1000);
//
//            int left = clamp(centerX - areaSize / 2, -1000, 1000);
//            int top  = clamp(centerY - areaSize / 2, -1000, 1000);
//
//            RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
//            return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
//        }

        //指定范围内取值, 保证坐标必须在min到max之内，否则异常
//        private int clamp(int x, int min, int max) {
//            if (x > max) {
//                return max;
//            }
//            if (x < min) {
//                return min;
//            }
//            return x;
//        }

        /**
         * 根据题目信息计算对焦位置
         * @param qlist 题目答案区分布信息
         * @param mRect A4取景框
         */
        void calFocusPosition(ArrayList<LocalQuestionInfo> qlist, Rect mRect, Point focusPoint ){

            //只考虑上下，左右居中处理,寻找距离中心最近的题干区域
            final int centerX = mRect.width() / 2;
            final int centerY = mRect.height() / 2;

            int diff = 10000;
            //int focusX = centerX;
            int focusY = centerY;

            int preBottom = 0;      //前一个区域的bottom

            for( LocalQuestionInfo qustionInfo : qlist ){

                QuestionRect questionRect = qustionInfo.getQuestionRect();      //答案区域，反推出题干区域
                if( questionRect != null ){
                    //题目区域
                    int qtop     = preBottom;
                    int qbottom  = (int) (questionRect.getY() * mRect.height());
                    preBottom = qbottom + (int)(questionRect.getHeight() * mRect.height());

                    if( qbottom < centerY ){        //题目区域偏上
                        if( centerY-qbottom < diff ){
                            diff = centerY-qbottom;
                            focusY = (qtop+qbottom)/2;
                        }
                    }else if( qtop > centerY ){     //题目区域偏下
                        if( qtop - centerY < diff ){
                            diff = qtop - centerY;
                            focusY = (qtop+qbottom)/2;
                        }
                    }else{                          //题目区域夸中心区域
                        focusY = (qtop+qbottom)/2;
                        break;
                    }
                }
            }
            focusPoint.x = centerX + mRect.left;     //调整相对相机预览的位置
            focusPoint.y = focusY + mRect.top;
            mFocusViewPoint.x = centerX;
            mFocusViewPoint.y = focusY;
            AppLog.d("seetvbdsdsd focusPoint x= "+focusPoint.x + ",,y = "+focusPoint.y );
        }

    }


    //--------------------------------------------------------------------------------------
    //增加浮层提示
    private void initCommonGudie() {
        //A3首次拍照 且 手机号是空的 提示
        boolean isA3Paper = false;
        boolean is8KPaper = false;
        if(AccountUtils.mDDWorkDetail!=null){
            String psize = AccountUtils.mDDWorkDetail.getPaperSize();
            isA3Paper = !TextUtils.isEmpty(psize)&&psize.equalsIgnoreCase("a3");
            is8KPaper = !TextUtils.isEmpty(psize)&&psize.equalsIgnoreCase("8K");
        }

        if( isA3Paper || is8KPaper){
            NewbieGuide.with( this )
                    .setLabel("guide1")
                    .addGuidePage(
                            GuidePage.newInstance()
                                    .setEverywhereCancelable(false)
                                    .setLayoutRes(R.layout.guide_ddcamera, R.id.iv_i_know)
                    )
                    //.alwaysShow( true )
                    .show();
        }
    }
}
