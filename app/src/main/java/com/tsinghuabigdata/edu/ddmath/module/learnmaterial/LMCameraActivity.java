package com.tsinghuabigdata.edu.ddmath.module.learnmaterial;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.BaseCameraActivity;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.HoleBean;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.NewbieGuide;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.NewbieGuideManager;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.FocusTipsView;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.camera.EdgeCameraPreviewCallBack;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.camera.EdgeDetectListener;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.CameraScanView;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.OutlineBorderView;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.MoveImageView;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.CameraUtil;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * 原版教辅作业拍照界面
 */
public class LMCameraActivity extends BaseCameraActivity implements View.OnClickListener, EdgeDetectListener, NewbieGuide.OnGuideChangedListener{

    public static void openActivity( Context context, LocalPageInfo pageInfo, float rate, boolean teacher, boolean isExam ){
        if( context == null || pageInfo ==null )
            return;

        Intent intent = new Intent( context, LMCameraActivity.class );
        AccountUtils.setLocalPageInfo( pageInfo );
        AccountUtils.setExamFlag( isExam );
        intent.putExtra( PARAM_PAGEINFO, true );
        intent.putExtra( PARAM_RATE, rate );
        intent.putExtra( PARAM_TEACHER, teacher );
        context.startActivity( intent );
    }

    //纸张框的比例宽度
    public static final String PARAM_PAGEINFO  = "pageinfo";       //原版教辅页信息
    public static final String PARAM_RATE      = "rate";          //原版教辅宽度/高比
    public static final String PARAM_TEACHER   = "teacher";       //是否老师布置的作业

    //纸张框的比例宽度
    public static final int PAPER_WIDTH = 210;     //A4,210
    public static final int PAPER_HEIGHT= 297;     //A4,297

    private LinearLayout mainLayout;
    //上面提示框区域
    private LinearLayout tipsLayout;
    private TextView tipsTextView;
    private LinearLayout animationLayout;
    private FocusTipsView focusTipLayout;
    //
    private OutlineBorderView outlineBorderView;

    //操作区
    private RelativeLayout toolsLayout;

    //闪光灯
    private RelativeLayout mFlashLedLayout;
    private ImageView mFlashLedView;
    private ImageView forceCameraView;
    //private RelativeLayout mCancelLayout;

    //对焦效果
    private MoveImageView mFocusImageView;
    private TextView dealCameraTextView;

    private View topEdgeView;
    private View leftEdgeView;
    private View rightEdgeView;
    private View bottomEdgeView;

    private Context mContext;
    private Activity mActivity;
    private EdgeDetectListener mDetectListener;
    private Camera camera = null;

    private CameraScanView cameraScanView;

    private boolean autofocus = false;

    private boolean bStartPreview = false;
    private boolean exit = true;
    private static final Object lock = new Object();

    private boolean bAutoFocus = false;
    private boolean bCameraReady = false;
    private boolean froceFocus = false;         //强制对焦拍张

    private int offset_margin = 0;
    private boolean bNoAutoFocus = false;

    //记录屏幕与预览图的比例  成像比例
    private float rate_width_pre,rate_width_picture;
    private float rate_height_pre,rate_height_picture;

    //private final static int MODE_PICS = 100;    //相册返回
    private final static int MODE_EDIT = 101;    //图片编辑界面返回

    //传递过来的参数
    private LocalPageInfo localPageInfo;        //作业页内信息
    private float bookRate;                      //
    private boolean isTeacher = false;          //

    //隐藏虚拟键后的宽高
    private int mScreenWidth;
    private int mScreenHeight;

    private Point previewPoint;
    private Point picturePoint;

    private Point mFocusPoint;      //对焦位置
    private Point mFocusViewPoint;  //对焦位置指示

    private EdgeCameraPreviewCallBack mPreviewCallBack = new EdgeCameraPreviewCallBack();
    private boolean bPause = false;
    private boolean bCreate = false;

    private FocusManager mFocusManager;
    private boolean bLandCamera = false;

    private long useTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme( R.style.Theme_Custom_AppCompat );
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if( !parseIntent() ){
            ToastUtils.show( this, "参数错误", Toast.LENGTH_SHORT );
            finish();
            return;
        }
        if( bookRate > 1 ){     //横版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            bLandCamera = true;
            setContentView(GlobalData.isPad()?R.layout.activity_lmmaterial_camera_land:R.layout.activity_lmmaterial_camera_land_phone);
        }else{      //竖版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(GlobalData.isPad()?R.layout.activity_lmmaterial_camera:R.layout.activity_lmmaterial_camera_phone);
        }

        mContext = this;
        mActivity = this;
        bStartPreview = false;
        mDetectListener = this;
        bCreate = true;

        initView();

        initAutofocus();
        //
        startSetParamThread();
        initCommonGudie();
        //startTipAnimation();
    }

    @Override
    protected void onPause() {
        bPause = true;
        mSensorManager.unregisterListener(mSensorListener);
        if( camera!=null ) {
            stopPreview();
        }
        mFocusManager.stop();
        super.onPause();
        AppLog.caermaLog( useTime, "LmCamera", localPageInfo );
    }

    @Override
    protected void onResume() {
        super.onResume();
        bPause = false;

        useTime = System.currentTimeMillis();
        hideNavigationBar();

        bAutoFocus = false;
        bNoAutoFocus = false;

        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        resetPreviewCallbackStatus();

        localPageInfo.initCacheData();
        //此页没有拍照时，默认全对, 已拍照，默认上次的结果
        if( TextUtils.isEmpty(localPageInfo.getLocalpath()) && localPageInfo.getUploadType() == AppConst.UPLOAD_TYPE_MARKED) localPageInfo.setDeflautCorrectCache();

        if( camera!=null ) {
            startPreview();
            camera.setPreviewCallback( mPreviewCallBack );

            //延时500ms后开始自动对焦
            startAutoFouse( bCreate?2100:300 );
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {// 屏幕触摸事件

        if (event.getAction() == MotionEvent.ACTION_DOWN) {// 按下时自动对焦
            autofocus = true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP && autofocus ) {// 放开后拍照
//            if( focusTipLayout.isShown() ){
//                //强制启动拍照
//                mPreviewCallBack.startForceCamera();
//            }else{
                try {
                    Rect rect = new Rect();
                    outlineBorderView.getGlobalVisibleRect( rect );
                    if( rect.contains( (int)event.getRawX(), (int)event.getRawY()) )
                        autoFocus( true, event.getX() - offset_margin, event.getY()-tipsLayout.getHeight() );
                } catch (Exception e) {
                    AppLog.i( "", e );
                }
//            }
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
    }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.ddwork_testBtn:{
                //mPreviewCallBack.setForceCamera( true );
                //forceCameraView.setEnabled( false );
                if( camera!=null ){
                    //先对焦
                    froceFocus = true;
                    AppLog.d("fsdfsafsdfs  force camera btn");
                    if( mPreviewCallBack.enableCamera() )camera.autoFocus( autoFocusCallback );
                    startCameraScan();
                }
                break;
            }
            case R.id.ddwork_layout_cameraledbtn:{       //手电筒
                int mode = getCameraFlashMode();
                mode = (mode+1)%2;        //改变
                setFlashMode( mode );
                break;
            }
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
            case  MODE_EDIT:{
                if(data != null ) {
                    if (data.hasExtra("redo")) {    //
                        resetPreviewCallbackStatus();
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
    public void forceCamera(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                forceCameraView.setVisibility( View.VISIBLE );
            }
        });
    }
    @Override
    public boolean detectResult( int detectResult[], boolean preview ) {

        if( detectResult == null || detectResult[0] != 0 || detectResult.length != 9 ){
            //识别框复位
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //显示检测到的四边形
                    int [] arr = new int[9];
                    Arrays.fill( arr,0);
                    outlineBorderView.showDetectRect( arr );
                }
            });
            return false;
        }

        //先变换到屏幕比例
        final int result[] = new int[9];
        result[0] = detectResult[0];
        for( int i=0; i<4; i++ ){
            result[1+i*2] = (int)(detectResult[1+i*2] * (preview?rate_width_pre : rate_width_picture) );
            result[2+i*2] = (int)(detectResult[2+i*2] * (preview?rate_height_pre: rate_height_picture) );
        }

        //判断四个点是否在要求的范围内
        boolean succ = true;
        Rect outer = outlineBorderView.getViewRect();
        Rect inner = outlineBorderView.getInnerRect();

        for( int i=0; i<4; i++ ){
            int x = result[1+i*2];
            int y = result[2+i*2];
            if( !outer.contains( x, y ) || inner.contains( x, y) ){
                succ = false;
                break;
            }
        }

        //AppLog.d("asdasdsad DetechAynscTask sss view left = " + outlineBorderView.getLeft() + ",,, top = " + outlineBorderView.getTop() + ",,, width = " +outlineBorderView.getWidth() + ",,,height = " + outlineBorderView.getHeight() );
        //Rect rect = new Rect();
        //outlineBorderView.getGlobalVisibleRect( rect );
        //AppLog.d("asdasdsad DetechAynscTask sss pos left = " + rect.left + ",,, top = " + rect.top + ",,, width = " +rect.width() + ",,,height = " + rect.height() );


        //更新界面
        final boolean sc = succ;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //显示检测到的四边形
                outlineBorderView.showDetectRect( result );

                //符合条件，提醒用户
                if( sc ){
                    mFocusManager.stop();
                    bCreate = false;
                    showTipInfo( View.INVISIBLE, View.GONE, View.GONE );
                }
            }
        });
        return succ;
    }

    @Override
    public void showCenterToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show(mContext,msg);
            }
        });
    }

    @Override
    public void detectFile(String filepath) {

        stopCameraScan();
        mPreviewCallBack.setForceCamera( false );
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                forceCameraView.setEnabled( true );
            }
        });

        LMPreviewActivity.openActivityForResult( mActivity, localPageInfo, filepath, MODE_EDIT, isTeacher, bookRate );
    }

    //返回的是原图和原图上面的四个点
    @Override
    public void detectFile(String filepath, int[] result){

        if( result == null || result[0] != 0 || result.length != 9 ){
            //识别框复位
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //显示检测到的四边形
                    outlineBorderView.showDetectRect( new int[9] );
                }
            });
            return;
        }

        mPreviewCallBack.setForceCamera( false );
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                forceCameraView.setEnabled( true );
            }
        });

        //左上
        ArrayList<Point> list = new ArrayList<>();
        Point point = new Point();
        point.x = result[1];
        point.y = result[2];
        list.add( point );

        //右上
        point = new Point();
        point.x = result[3];
        point.y = result[4];
        list.add( point );

        //右下
        point = new Point();
        point.x = result[7];
        point.y = result[8];
        list.add( point );

        //左下
        point = new Point();
        point.x = result[5];
        point.y = result[6];
        list.add( point );
        stopCameraScan();
        LMUserAdjustActivity.openActivityForResult( mActivity, list, localPageInfo, filepath, MODE_EDIT, isTeacher, bookRate );
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
//        focusFailCount = 0;

        stopCameraScan();
        //识别框复位
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //显示检测到的四边形
                //outlineBorderView.showDetectRect( new int[9] );
                ToastUtils.show( mContext, "没有检测到书页边框,请重新拍照。");
            }
        });
    }

    //-------------------------------------------------------------------------------------------------
    private void initView() {

        mainLayout = findViewById( R.id.ddwork_camera_mainlayout );
        tipsLayout = findViewById( R.id.ddwork_tips_layout );
        tipsTextView=findViewById( R.id.ddwork_tips_textview );
        animationLayout = findViewById( R.id.ddwork_animtips_layout );
        outlineBorderView =  findViewById( R.id.ddwork_outerlineBorderView );
        focusTipLayout=findViewById( R.id.ddwork_camera_focustiplayout );
        //focusTipLayout.setTextView( "点击屏幕强制拍照" );对焦
        toolsLayout= findViewById( R.id.ddwork_tools_layout );

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
        mFlashLedView = findViewById(R.id.ddwork_camera_flashimg);
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

//        lefttopFixedView = (FixedPiontView)findViewById( R.id.ddwork_lefttop_fixpointview );
//        righttopFixedView = (FixedPiontView)findViewById( R.id.ddwork_righttop_fixpointview );
//        leftbottomFixedView = (FixedPiontView)findViewById( R.id.ddwork_leftbottom_fixpointview );
//        rightbottomFixedView = (FixedPiontView)findViewById( R.id.ddwork_rightbottom_fixpointview );

        mFocusManager = new FocusManager();

        leftEdgeView = findViewById( R.id.ddwork_camera_leftedge );
        topEdgeView = findViewById( R.id.ddwork_camera_topedge );
        rightEdgeView = findViewById( R.id.ddwork_camera_rightedge );
        bottomEdgeView = findViewById( R.id.ddwork_camera_bottomedge );

        forceCameraView = findViewById( R.id.ddwork_testBtn );
        forceCameraView.setOnClickListener( this );

        cameraScanView = findViewById( R.id.ddwork_lm_scanview );
    }

    //根据显示情况，设置取景框高宽，及 定位框
    private void startSetParamThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while( true ){

                    SystemClock.sleep(50);
                    if(bPause) break;

                    if( tipsLayout.getHeight() == 0 || camera == null || toolsLayout.getHeight() == 0 || mScreenWidth == 0 )
                        continue;

                    //设置相机参数
                    if( previewPoint ==null || picturePoint == null ){
                        boolean succ = setCameraParams();
                        if( !succ ){
                            showCenterToast("相机参数设置失败!");
                            mActivity.finish();
                        }
                    }


                    //先判断 相机参数 与 屏幕的关系，调整surfaceView的边距
                    int camera_width = bLandCamera?previewPoint.x:previewPoint.y;
                    int camera_height= bLandCamera?previewPoint.y:previewPoint.x;

                    //  预览显示到屏幕的变化比例
                    float rate_w = mScreenWidth*1f / camera_width;         //  预览
                    float rate_h = mScreenHeight*1f / camera_height;       //
                    AppLog.d("dsfsdfsdfdsfsdaafsda rate_w = "+rate_w + ",,,rate_h="+rate_h + ",,,mScreenWidth = "+mScreenWidth + ",,, mScreenHeight = " + mScreenHeight );

                    rate_width_pre = rate_w;
                    rate_height_pre = rate_h;

                    //成像
                    rate_width_picture  = mScreenWidth*1f  / (bLandCamera?picturePoint.x:picturePoint.y);
                    rate_height_picture = mScreenHeight*1f / (bLandCamera?picturePoint.y:picturePoint.x);
                    AppLog.d("dsfsdfsdfdsfsdaafsda rate_width_pre = "+rate_width_pre + ",,,rate_height_pre="+rate_height_pre + ",,,rate_width_picture = "+rate_width_picture + ",,, rate_height_picture = " + rate_height_picture );

                    // 调整拍照裁剪框的边距

                    //可用宽高
                    int usewidth = mScreenWidth;
                    int useheight= mScreenHeight;
                    if( bLandCamera ){
                        usewidth = usewidth - tipsLayout.getWidth() - toolsLayout.getWidth();
                    }else{
                        useheight = useheight - tipsLayout.getHeight() - toolsLayout.getHeight();
                    }

                    //裁剪框的目标宽高(变换比例)
                    int dstWidth = (int)((useheight * bookRate * rate_w) / ( rate_h) );      //默认 高度撑满
                    int dstHeight= useheight;
                    if( dstWidth > usewidth ){
                        dstWidth = usewidth;                   //宽度撑满，
                        dstHeight= (int)((usewidth * rate_h) / (bookRate * rate_w) );
                    }
                    AppLog.d("dsfsdfsdfdsfsdaafsda dstWidth = "+dstWidth + ",,, dstHeight = " + dstHeight );

                    //计算上下 左右的偏移距离
                    int offset_width = ( usewidth - dstWidth ) / 2;
                    int offset_height= ( useheight - dstHeight ) / 2;

                    adjustViewPostion( offset_width,offset_height, dstWidth, dstHeight );

                    //裁剪框（相对surfaceView ）
                    final Rect cropRect = new Rect();
                    cropRect.left = bLandCamera?(tipsLayout.getWidth()+offset_width):offset_width;
                    cropRect.top  = bLandCamera?(offset_height):tipsLayout.getHeight()+offset_height;
                    cropRect.right= cropRect.left + dstWidth;
                    cropRect.bottom= cropRect.top + dstHeight;

                    if( mFocusPoint == null ){
                        mFocusPoint = new Point();
                        mFocusViewPoint = new Point();
                    }
                    mFocusManager.calFocusPosition( /*localPageInfo.getQuestions(),*/ cropRect, mFocusPoint );

                    //变换相对预览图的位置
                    cropRect.left = (int)(cropRect.left / rate_w);
                    cropRect.top = (int)(cropRect.top / rate_h);
                    cropRect.right = (int)(cropRect.right / rate_w);
                    cropRect.bottom = (int)(cropRect.bottom / rate_h);

                    mPreviewCallBack.setData( cropRect, mDetectListener, camera_width, camera_height );
                    break;
                }
            }
        }).start();
    }

    private boolean parseIntent(){
        Intent intent = getIntent();
        boolean has = intent.getBooleanExtra( PARAM_PAGEINFO, false );
        if( has ){
            localPageInfo = AccountUtils.getLocalPageInfo();
        }
        bookRate  = intent.getFloatExtra( PARAM_RATE, -1 );
        if( bookRate < 0.1 )
            bookRate = PAPER_WIDTH * 1f/PAPER_HEIGHT;
        isTeacher = intent.getBooleanExtra( PARAM_TEACHER, false );
        return !( localPageInfo ==null );
    }

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

    private void adjustViewPostion( final int offset_width, final int offset_height, final int dw, final int dh ){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //A4
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)outlineBorderView.getLayoutParams();
                layoutParams.setMargins( offset_width, offset_height, offset_width, offset_height );
                layoutParams.width = dw;
                layoutParams.height= dh;
                outlineBorderView.setLayoutParams( layoutParams );
                outlineBorderView.setMargins( offset_width, offset_height );

//                int linewidth = WindowUtils.dpToPixels(mContext,4);
//                if( !GlobalData.isPad() ) linewidth = WindowUtils.dpToPixels(mContext,2);
                offset_margin = offset_width;

                //调整 三个定位框的位置
//                lefttopFixedView.setFixedWidth( offset_width );
//                leftbottomFixedView.setFixedWidth( offset_width );
//                righttopFixedView.setFixedWidth( offset_width );
//                rightbottomFixedView.setFixedWidth( offset_width );

//                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams)lefttopFixedView.getLayoutParams();
//                layoutParams1.setMargins( offset_width+linewidth, linewidth, offset_width+linewidth, linewidth );
//                lefttopFixedView.setLayoutParams( layoutParams1 );
//
//                layoutParams1 = (RelativeLayout.LayoutParams)righttopFixedView.getLayoutParams();
//                layoutParams1.setMargins( offset_width+linewidth, linewidth, offset_width+linewidth, linewidth );
//                righttopFixedView.setLayoutParams( layoutParams1 );
//
//                layoutParams1 = (RelativeLayout.LayoutParams)leftbottomFixedView.getLayoutParams();
//                layoutParams1.setMargins( offset_width+linewidth, linewidth, offset_width+linewidth, offset_height*2+linewidth );
//                leftbottomFixedView.setLayoutParams( layoutParams1 );
//
//                layoutParams1 = (RelativeLayout.LayoutParams)rightbottomFixedView.getLayoutParams();
//                layoutParams1.setMargins( offset_width+linewidth, linewidth, offset_width+linewidth, offset_height*2+linewidth );
//                rightbottomFixedView.setLayoutParams( layoutParams1 );
            }
        });
    }

    private void setFlashMode(int mode) {
        SharedPreferences sharep = getSharedPreferences("ask_camera", 0);
        Editor editor = sharep.edit();
        editor.putInt("flashmode", mode);
        editor.apply();

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
        String c_mode;
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

            //mFocusImageView.setMoveMargin( centerX - mFocusImageView.getWidth()/2, centerY - mFocusImageView.getHeight()/2 );
            showTipInfo( View.VISIBLE, View.GONE, View.GONE );
            mFocusImageView.startZoomAndFadeOut();
//            if( mFocusPoint != null ){      //设置指定对焦位置
//                mFocusManager.setFocusArea( (int)centerX, (int)centerY, camera );
//            }
            startAutoFocus();

            if( !only )bAutoFocus = true;
        }
    }

    private boolean onfocusing = false;
    private void startAutoFocus(){
        if(bPause) return;
        if( camera.getParameters().isZoomSupported() ){
            if( onfocusing ) return;
            onfocusing = true;
            AppLog.d("fsdfsafsdfs  auto camera btn");
            if( mPreviewCallBack.enableCamera() )
                camera.autoFocus(autoFocusCallback);
        }else{
            autoFocusCallback.onAutoFocus( true, camera );
        }
    }

    private int getCameraDegree() {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees;
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
            degrees = 0;
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
//    private int focusFailCount = 0;     //大于1次，直接取成像图

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            onfocusing = false;
            AppLog.d("fsdfsafsdfs  focus end");
            mFocusImageView.sopZoomAndFadeOut();
            //AppLog.d("dfdfdfdfd  auto focus = " + success );
            if( froceFocus ){
                froceFocus = false;
                playCameraSound();
                mPreviewCallBack.startForceCamera();
            } else /*if( success ) */{
                showTipInfo( View.GONE, View.GONE, View.VISIBLE );
                if (mPreviewCallBack != null){
                     mPreviewCallBack.startCamera();
                }
                // mFocusManager.start();
//                focusFailCount = 0;
            }/*else{
                focusFailCount++;
                if( focusFailCount >= 2 && mPreviewCallBack.isWaitNextFocus() ){    //
                    if (mPreviewCallBack != null){
                        mPreviewCallBack.startCamera();
                    }
                    // mFocusManager.start();
                    focusFailCount = 0;
                }else{
                    mFocusManager.show( true );
                }
            }*/
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

            AppLog.d("fsafdsfds surfaceCreated");

            //默认打开后置摄像头
            int cameraCount;
            CameraInfo cameraInfo = new CameraInfo();
            cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
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
            AppLog.d("fsafdsfds surfaceChanged");
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
                startAutoFouse( bCreate?2100:500 );
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                synchronized (lock) {
                    exit = true;
                }
                camera.setPreviewCallback(null) ;
                stopPreview();
                mPreviewCallBack.stopDetechAynscTask();
                camera.release();
                camera = null;
            }
        }
    };

    private void startAutoFouse( final int delaytime ){
        toolsLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                bCameraReady = true;
                if(bPause) return;
                showTipInfo( View.INVISIBLE, View.GONE, View.GONE );
                if( mFocusViewPoint != null )
                    autoFocus( true, mFocusViewPoint.x, mFocusViewPoint.y  );
            }
        }, delaytime );
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

        //-----------------------------------------------------------------------------------------------
        //方案1: 以成像参数为基础，300万左右， 成像，预览，屏幕 同比例去设置成像参数
        float screenRate = CameraUtil.getScreenRate(mActivity);
        float bestRate;
        AppLog.d(" setCameraParam screenRate = " + screenRate );

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

        //
        int screen_width = mainLayout.getWidth(), screen_height = mainLayout.getHeight();
        if( screen_height < screen_width ){
            int tmp = screen_height;
            screen_height = screen_width;
            screen_width = tmp;
        }

        if( screen_height*bestRate > screen_width ){        //width 不够，则width占满，高度方向 添加黑
            int dst = ((int)(screen_height - screen_width/bestRate))/2;
            setEdgeView( 0, dst, 0, dst );

            if( bLandCamera ){
                mScreenWidth = screen_height - 2*dst;
                mScreenHeight= screen_width;
            }else{
                mScreenWidth = screen_width;
                mScreenHeight= screen_height - 2*dst;
            }
        }else{                  //height 不够，height，宽度方向 添加黑
            int dst = (screen_width - (int)(screen_height*bestRate))/2;
            setEdgeView( dst, 0, dst, 0 );

            if( bLandCamera ){
                mScreenWidth = screen_height;
                mScreenHeight= screen_width - 2*dst;
            }else{
                mScreenWidth = screen_width - 2*dst;
                mScreenHeight= screen_height;
            }
            //AppLog.d(" setCameraParam adjuestUI xxx " +dst);
        }
    }

    //设置四边的宽度或者高度
    private void setEdgeView( final int left, final int top, final int right, final int bottom ){

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ViewGroup.LayoutParams layoutParams = leftEdgeView.getLayoutParams();
                layoutParams.width = bLandCamera?top:left;
                leftEdgeView.setLayoutParams( layoutParams );

                layoutParams = rightEdgeView.getLayoutParams();
                layoutParams.width = bLandCamera?bottom:right;
                rightEdgeView.setLayoutParams( layoutParams );

                layoutParams = topEdgeView.getLayoutParams();
                layoutParams.height = bLandCamera?left:top;
                topEdgeView.setLayoutParams( layoutParams );

                layoutParams = bottomEdgeView.getLayoutParams();
                layoutParams.height = bLandCamera?right:bottom;
                bottomEdgeView.setLayoutParams( layoutParams );
            }
        });
    }
    //保证对焦，手动对焦提示，稳定提示同时 只显示一个
    private void showTipInfo( int focusImage, int handTips, int handStable ){

        handTips = View.GONE;
        mFocusImageView.setVisibility( focusImage );
        focusTipLayout.setVisibility( handTips );
        dealCameraTextView.setVisibility( handStable );         //对焦完成才显示
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
                    showTipInfo( View.INVISIBLE, show?View.VISIBLE:View.GONE, View.GONE );

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
//            Parameters p = mCamera.getParameters();
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
//            p.setFocusMode(Parameters.FOCUS_MODE_AUTO);
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

//        //指定范围内取值, 保证坐标必须在min到max之内，否则异常
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
     //    * @param qlist 题目答案区分布信息
         * @param mRect A4取景框
         */
        void calFocusPosition(/*ArrayList<LocalQuestionInfo> qlist,*/ Rect mRect, Point focusPoint ){

            //只考虑上下，左右居中处理,寻找距离中心最近的题干区域
            final int centerX = mRect.width() / 2;
            final int centerY = mRect.height() / 2;

//            int diff = 10000;
//            //int focusX = centerX;
//            int focusY = centerY;

//            int preBottom = 0;      //前一个区域的bottom

//            for( LocalQuestionInfo qustionInfo : qlist ){
//
//                QuestionRect questionRect = qustionInfo.getQuestionRect();      //答案区域，反推出题干区域
//                if( questionRect != null ){
//                    //题目区域
//                    int qtop     = preBottom;
//                    int qbottom  = (int) (questionRect.getY() * mRect.height());
//                    preBottom = qbottom + (int)(questionRect.getHeight() * mRect.height());
//
//                    if( qbottom < centerY ){        //题目区域偏上
//                        if( centerY-qbottom < diff ){
//                            diff = centerY-qbottom;
//                            focusY = (qtop+qbottom)/2;
//                        }
//                    }else if( qtop > centerY ){     //题目区域偏下
//                        if( qtop - centerY < diff ){
//                            diff = qtop - centerY;
//                            focusY = (qtop+qbottom)/2;
//                        }
//                    }else{                          //题目区域夸中心区域
//                        focusY = (qtop+qbottom)/2;
//                        break;
//                    }
//                }
//            }
            focusPoint.x = centerX + mRect.left;     //调整相对相机预览的位置
            focusPoint.y = centerY + mRect.top;
            mFocusViewPoint.x = centerX;
            mFocusViewPoint.y = centerY;
            AppLog.d("seetvbdsdsd focusPoint x= "+focusPoint.x + ",,y = "+focusPoint.y );
        }

    }
    private void hideNavigationBar()
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

    //重置预览回调的状态
    private void resetPreviewCallbackStatus(){
        bAutoFocus = false;
        mPreviewCallBack.initStatus();
        //forceCameraView.setVisibility( View.INVISIBLE );

        //状态恢复
        outlineBorderView.showDetectRect( new int[9] );
    }

    //中间停留 1S,然后1S内回到顶部
//    private void startTipAnimation(){
//        tipsTextView.setVisibility( View.INVISIBLE );
//        animationLayout.setVisibility( View.VISIBLE );
//
//        int width = ScreenUtils.getScreenWidth(mContext);
//        int height = ScreenUtils.getScreenHeight(mContext);
//
//        int fromX = 0, toX = 0, fromY = width<height? width/2 : height/2, toY = 0;
//        if( bLandCamera ){
//            fromX = width>height? width/2 : height/2;
//            fromY = 0;
//        }
//        final TranslateAnimation animation = new TranslateAnimation( fromX, toX, fromY, toY );
//        animation.setDuration(1000);//设置动画持续时间
//        //animation.setRepeatCount(2);//设置重复次数
//        animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
//        animationLayout.setAnimation( animation );
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//            @Override
//            public void onAnimationEnd(Animation action) {
//                //animation.clearAnimation();
//                tipsTextView.setVisibility( View.VISIBLE );
//                animationLayout.setVisibility( View.INVISIBLE );
//            }
//        });
//        animation.setStartTime(2000+ SystemClock.uptimeMillis());
//    }

    /**
     *   播放系统拍照声音
     */
    private MediaPlayer mediaPlayer;
    public void playCameraSound(){
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if( audioManager == null )return;
        int volume = audioManager.getStreamVolume( AudioManager.STREAM_NOTIFICATION);
        if (volume != 0){
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer.create(mContext, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mediaPlayer != null){
                mediaPlayer.start();
            }
        }
    }

    private void startCameraScan(){

        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                //扫描效果
                cameraScanView.setVisibility( View.VISIBLE );
                cameraScanView.reset();
                cameraScanView.startScan();
                //提示文本显示
                dealCameraTextView.setText( getResources().getText(R.string.lmcamera_handtips ) );
                //按钮不能点击
                mFlashLedLayout.setEnabled( false );
                //mCancelLayout.setEnabled( false );
                forceCameraView.setEnabled( false );
            }
        });
    }
    private void stopCameraScan(){
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                cameraScanView.setVisibility( View.GONE );
                dealCameraTextView.setText( getResources().getText( bLandCamera? R.string.lmcamera_dealtips1 : R.string.lmcamera_dealtips ) );
                //按钮恢复点击
                mFlashLedLayout.setEnabled( true );
                //mCancelLayout.setEnabled( true );
                forceCameraView.setEnabled( true );
            }
        });
    }

    //--------------------------------------------------------------------------------------
    //增加浮层提示
    private NewbieGuideManager mGuideManager;
    //private int guideViewIndex = 0;

    private void initCommonGudie() {
        String cname = LMCameraActivity.class.getName();

        //没有显示过 且 手机号是空的 提示
        if ( NewbieGuideManager.isNeverShowed(this, cname, "_cameraguide_") ) {
            mGuideManager = new NewbieGuideManager(this, cname, "_cameraguide_");
            mGuideManager.showWithListener(this);
            mainLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addGuideView();
                }
            }, 1000);
        }
    }

    private void addGuideView() {
        Rect rect = new Rect();
        forceCameraView.getGlobalVisibleRect(rect);

//        if (guideViewIndex != 0)
//            return;

        final NewbieGuide mNewbieGuide = mGuideManager.getNewbieGuide();
        //高亮区域
        mNewbieGuide.addHighLightView(forceCameraView, HoleBean.TYPE_CIRCLE);

        //指示图
        if( bLandCamera ){      //横版布局
            if (GlobalData.isPad()) {       //平板
                int w = 288 * 3/2;
                int h = 56 * 3/2;
                int left = rect.left + get(-w-15);
                int top  = (rect.top+rect.bottom)/2 + get(-h/2);
                mNewbieGuide.addIndicateImg( R.drawable.pic_notice, left, top, get(w), get(h));
                //我知道了
                w = 93 * 3/2;
                h = 46 * 3/2;
                mNewbieGuide.addBtnImage(R.drawable.pic_iknow, rect.left + get(-w-30), get(-30), get(w), get(h));
            } else {        //手机
                int w = 288;
                int h = 56;
                int left = rect.left + get(-w);
                int top  = (rect.top+rect.bottom)/2 + get(-h/2-10);
                mNewbieGuide.addIndicateImg( R.drawable.pic_notice, left, top, get(w), get(h));
                //我知道了
                w = 93;
                h = 46;
                mNewbieGuide.addBtnImage(R.drawable.pic_iknow, rect.left + get(-w-30), get(-30), get(w), get(h));
            }
        }else{                  //竖版布局

            if (GlobalData.isPad()) {       //平板
                //箭头
                int w = 194 * 3 / 2;
                int h = 152 * 3 / 2;
                int left = (rect.left+rect.right)/2 + get(-w/2-15);
                int top  = rect.top + get(-h-45);
//                mNewbieGuide.addIndicateImg( R.drawable.pic_notice1, left, top, get(w), get(h));
                mNewbieGuide.addIndicateImg( R.drawable.pic_notice1, left, top -get(45), get(w), get(h));
                //我知道了
                w = 93 * 3 / 2;
                h = 46 * 3 / 2;
//                mNewbieGuide.addBtnImage(R.drawable.pic_iknow, -get(45), get(30), get(w), get(h));
                mNewbieGuide.addBtnImage(R.drawable.pic_iknow, -get(45), top+get(45), get(w), get(h));
            } else {        //手机
                int left = (rect.left+rect.right)/2 + get(-194/2-10);
                int top  = rect.top + get(-152-16);

//                mNewbieGuide.addIndicateImg( R.drawable.pic_notice1, left, top, get(194), get(152));
//                //我知道了
//                mNewbieGuide.addBtnImage(R.drawable.pic_iknow, -get(30), get(20), get(93), get(46));

                mNewbieGuide.addIndicateImg( R.drawable.pic_notice1, left, top-get(20), get(194), get(152));
                //我知道了
                mNewbieGuide.addBtnImage(R.drawable.pic_iknow, -get(30),top+get(45+6), get(93), get(46));
            }
        }


        mGuideManager.show();
    }

    private int get(int dis) {
        return DensityUtils.dp2px(mContext, dis);
    }

    @Override
    public void onShowed() {
        //guideViewIndex++;
    }

    @Override
    public void onRemoved() {
        //addGuideView();
    }

}
