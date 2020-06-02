package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.camera2;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkManager;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.FocusTipsView;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.LMCameraActivity;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.LMPreviewActivity;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.LMUserAdjustActivity;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.camera.EdgeDetectListener;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.CameraScanView;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view.OutlineBorderView;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.view.MoveImageView;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.CameraUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@TargetApi(Build.VERSION_CODES.M)
public class Camera2BasicFragment extends Fragment implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, EdgeDetectListener {

    //----------------------------------------------------------------------------------------------
    //UI相关功能

    //private final static int MODE_PICS = 100;    //相册返回
    private final static int MODE_EDIT = 101;    //图片编辑界面返回

    //传递过来的参数
    private LocalPageInfo localPageInfo;        //作业页内信息
    private float bookRate;                      //
    private boolean isTeacher = false;          //
    private boolean bAutoFocus = false;         //进入流程开始时,做一次对焦效果，仅仅对焦

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

    //private RelativeLayout mCancelLayout;
    private ImageView forceCameraView;

    //对焦效果
    private MoveImageView mFocusImageView;
    private TextView dealCameraTextView;

    private View topEdgeView;
    private View leftEdgeView;
    private View rightEdgeView;
    private View bottomEdgeView;

    private CameraScanView cameraScanView;

    //private int offset_margin = 0;

    private boolean bPause = false;
    //隐藏虚拟键后的宽高
    private int mScreenWidth;
    private int mScreenHeight;

    private Point previewPoint;
    private Point picturePoint;

    private boolean bLandCamera = false;
    private CameraManager cameraManager;

    private Context mContext;

    private AutoFitTextureView mTextureView;

    private EdgeDetecter mEdgeDetecter = new EdgeDetecter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        View view;
        if( bookRate > 1 ){     //横版拍摄
            view = inflater.inflate(GlobalData.isPad()? R.layout.activity_lmmaterial_camera2_land: R.layout.activity_lmmaterial_camera2_land_phone, container, false);
        }else{      //竖版拍摄
            view = inflater.inflate(GlobalData.isPad()? R.layout.activity_lmmaterial_camera2: R.layout.activity_lmmaterial_camera2_phone, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        initView( view );
        //startTipAnimation();
        startSetParamThread();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        bPause = false;
        if (mTextureView.isAvailable()) {
            AppLog.d("--dsffsdfsdfs onResume 000--");
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());

            //从其他界面返回时
            if( mPreviewRequestBuilder != null && mCaptureSession!=null){
                bAutoFocus = true;
                lockFocus();
            }
        } else {
            AppLog.d("--dsffsdfsdfs onResume 111--");
            bAutoFocus = true;
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
        bPause = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OpenCVHelper.releaseBitmap();
    }

    //
    public boolean parseIntent( Intent intent ){
        boolean has = intent.getBooleanExtra( LMCameraActivity.PARAM_PAGEINFO, false );
        if( has ){
            localPageInfo = AccountUtils.getLocalPageInfo();
        }
        bookRate  = intent.getFloatExtra( LMCameraActivity.PARAM_RATE, -1 );
        if( bookRate < 0.1 )
            bookRate = LMCameraActivity.PAPER_WIDTH * 1f/ LMCameraActivity.PAPER_HEIGHT;
        if( bookRate > 1 ){     //横版拍摄
            bLandCamera = true;
         }
        isTeacher = intent.getBooleanExtra( LMCameraActivity.PARAM_TEACHER, false );
        return !( localPageInfo ==null );
    }

    public float getBookRate(){ return bookRate; }

    @Override
    public void onClick(View v) {

        switch ( v.getId() ){

            case R.id.ddwork_testBtn:{
                time = System.currentTimeMillis();
                takePicture();
                playCameraSound();
                startCameraScan();
                break;
            }
            case R.id.ddwork_layout_cameraledbtn:{       //手电筒
                int mode = getCameraFlashMode();
                mode = (mode+1)%2;        //改变
                switchFlashMode( mode );
                break;
            }
            case R.id.ddwork_layout_cameracancel:{       //取消
                getActivity().finish();
                break;
            }
            case R.id.ddwork_outerlineBorderView:{
                //点击界面，自动对焦
//                if( mPreviewRequestBuilder != null ){
//                    bAutoFocus = true;
//                    lockFocus();
//                }
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //没有获取相机权限，只能退出处理
                AlertManager.showCustomDialog( mContext, "获取相机权限失败。", "退出", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch( requestCode ) {
            case  MODE_EDIT:{
                if(data != null ) {
                    if (data.hasExtra("redo")) {    //
                        resetPreviewCallbackStatus();
                    } else if (data.hasExtra("close")) {     //通知关闭
                        getActivity().finish();
                    }
                }
                break;
            }
            default:
                break;
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {// 屏幕触摸事件
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {// 按下时自动对焦
//            autofocus = true;
//        }
//        if (event.getAction() == MotionEvent.ACTION_UP && autofocus ) {// 放开后拍照
////            if( focusTipLayout.isShown() ){
////                //强制启动拍照
////                mPreviewCallBack.startForceCamera();
////            }else{
//            try {
//                Rect rect = new Rect();
//                outlineBorderView.getGlobalVisibleRect( rect );
//                if( rect.contains( (int)event.getRawX(), (int)event.getRawY()) )
//                    autoFocus( true, event.getX() - offset_margin, event.getY()-tipsLayout.getHeight() );
//            } catch (Exception e) {
//                AppLog.i( "", e );
//            }
////            }
//            autofocus = false;
//            mFocusManager.stop();
//        }
//        return true;
//    }

    @Override
    public void forceCamera(){
        getActivity().runOnUiThread(new Runnable() {
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
            getActivity().runOnUiThread(new Runnable() {
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //显示检测到的四边形
                outlineBorderView.showDetectRect( result );

                //符合条件，提醒用户
                if( sc ){
                    showTipInfo( View.INVISIBLE, View.VISIBLE );
                }
            }
        });
        return succ;
    }

    @Override
    public void showCenterToast(final String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show(mContext,msg);
            }
        });
    }

    @Override
    public void detectFile(String filepath) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                forceCameraView.setEnabled( true );
            }
        });

        LMPreviewActivity.openActivityForResult( getActivity(), localPageInfo, filepath, MODE_EDIT, isTeacher, bookRate );
    }

    //返回的是原图和原图上面的四个点
    @Override
    public void detectFile(String filepath, int[] result){

        if( result == null || result[0] != 0 || result.length != 9 ){
            //识别框复位
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //显示检测到的四边形
                    outlineBorderView.showDetectRect( new int[9] );
                }
            });
            return;
        }

        /*mPreviewCallBack.setForceCamera( false );*/
        getActivity().runOnUiThread(new Runnable() {
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

        LMUserAdjustActivity.openActivityForResult( getActivity(), list, localPageInfo, filepath, MODE_EDIT, isTeacher, bookRate );
    }

    @Override
    public void cameraRorate( int degree[] ){
    }

    @Override
    public void nextAutoFocus() {
//        startAutoFouse(0);  //立即开始对焦

        //lockFocus();
    }

    @Override
    public void startTakePicture(){
    }
    @Override
    public void resetTakePicture(){

        //识别框复位
        mainLayout.post(new Runnable() {
            @Override
            public void run() {

                bAutoFocus = true;
                lockFocus();

                //显示检测到的四边形
                //outlineBorderView.showDetectRect( new int[9] );
                ToastUtils.show( mContext, "没有检测到书页边框,请重新拍照。");
            }
        });
    }


    public static Camera2BasicFragment newInstance() {
        return new Camera2BasicFragment();
    }

    //--------------------------------------------------------------------------------------------
    private void initView(View rootView) {

        mainLayout = (LinearLayout)rootView.findViewById( R.id.ddwork_camera_mainlayout );
        tipsLayout = (LinearLayout)rootView.findViewById( R.id.ddwork_tips_layout );
        tipsTextView=(TextView)rootView.findViewById( R.id.ddwork_tips_textview );
        animationLayout = (LinearLayout)rootView.findViewById( R.id.ddwork_animtips_layout );
        outlineBorderView = (OutlineBorderView) rootView.findViewById( R.id.ddwork_outerlineBorderView );
        //outlineBorderView.setFocusListener( this );
        focusTipLayout=(FocusTipsView)rootView.findViewById( R.id.ddwork_camera_focustiplayout );
        toolsLayout= (RelativeLayout)rootView.findViewById( R.id.ddwork_tools_layout );

        mTextureView = (AutoFitTextureView) rootView.findViewById(R.id.ddwork_camera_textureview);

        //手电筒效果
        mFlashLedView = (ImageView) rootView.findViewById(R.id.ddwork_camera_flashimg);
        mFlashLedLayout = (RelativeLayout)rootView.findViewById( R.id.ddwork_layout_cameraledbtn );
        mFlashLedLayout.setOnClickListener(this);

        if( !mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) ){
            mFlashLedLayout.setVisibility( View.GONE );     //没有闪光灯
        }
        mFlashLedView.setImageResource(getImageFlashMode());

        mFocusImageView = (MoveImageView) rootView.findViewById(R.id.ddwork_iv_focusimg);
        dealCameraTextView=(TextView)rootView.findViewById( R.id.ddwork_iv_dealimg );

        RelativeLayout mCancelLayout = (RelativeLayout)rootView.findViewById(R.id.ddwork_layout_cameracancel);
        mCancelLayout.setOnClickListener( this );

        leftEdgeView = rootView.findViewById( R.id.ddwork_camera_leftedge );
        topEdgeView = rootView.findViewById( R.id.ddwork_camera_topedge );
        rightEdgeView = rootView.findViewById( R.id.ddwork_camera_rightedge );
        bottomEdgeView = rootView.findViewById( R.id.ddwork_camera_bottomedge );

        forceCameraView = (ImageView)rootView.findViewById( R.id.ddwork_testBtn );
        forceCameraView.setOnClickListener( this );

        cameraScanView = (CameraScanView)rootView.findViewById( R.id.ddwork_lm_scanview );
    }
    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            //提示需要相机权限
            AlertManager.showCustomDialog( mContext, "需要使用相机权限。", "允许","拒绝",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Activity activity = getActivity();
                            if (activity != null) {
                                activity.finish();
                            }
                        }
                    } );
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }


//    //重置预览回调的状态
    private void resetPreviewCallbackStatus(){

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


    private void adjustViewPostion( final int offset_width, final int offset_height, final int dw, final int dh ){

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //A4
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)outlineBorderView.getLayoutParams();
                layoutParams.setMargins( offset_width, offset_height, offset_width, offset_height );
                layoutParams.width = dw;
                layoutParams.height= dh;
                outlineBorderView.setLayoutParams( layoutParams );
                outlineBorderView.setMargins( offset_width, offset_height );

                //offset_margin = offset_width;
            }
        });
    }

    //调整UI
    private void adjuestUI(float bestRate){

        //
        int sw = mainLayout.getWidth(), sh = mainLayout.getHeight();
        if( sh < sw ){
            int tmp = sh;
            sh = sw;
            sw = tmp;
        }

        if( sh*bestRate > sw ){        //width 不够，则width占满，高度方向 添加黑
            int dst = ((int)(sh - sw/bestRate))/2;
            setEdgeView( 0, dst, 0, dst );

            if( bLandCamera ){
                mScreenWidth = sh - 2*dst;
                mScreenHeight= sw;
            }else{
                mScreenWidth = sw;
                mScreenHeight= sh - 2*dst;
            }
        }else{                  //height 不够，height，宽度方向 添加黑
            int dst = (sw - (int)(sh*bestRate))/2;
            setEdgeView( dst, 0, dst, 0 );

            if( bLandCamera ){
                mScreenWidth = sh;
                mScreenHeight= sw - 2*dst;
            }else{
                mScreenWidth = sw - 2*dst;
                mScreenHeight= sh;
            }
            //AppLog.d(" setCameraParam adjuestUI xxx " +dst);
        }
    }

    //设置四边的宽度或者高度
    private void setEdgeView( final int left, final int top, final int right, final int bottom ){

        getActivity().runOnUiThread(new Runnable() {
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

    //-------------------------------------------------------------------------------------------------
    //相机相关功能

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    //private static final String FRAGMENT_DIALOG = "dialog";

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    /**
     * 相机状态 Camera state
     */
    private static final int STATE_PREVIEW = 0;                 //预览显示中    : Showing camera preview.
    private static final int STATE_WAITING_LOCK = 1;            //等待对焦中 : Waiting for the focus to be locked.
    private static final int STATE_WAITING_PRECAPTURE = 2;      //等待曝光前的状态 : Waiting for the exposure to be precapture state.
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;  //等待曝光状态不是预先捕获的东西。: Waiting for the exposure state to be something other than precapture.
    private static final int STATE_PICTURE_TAKEN = 4;           //已拍照 ： Picture was taken.

    //相机ID 后置相机
    private String mCameraId;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * 获取静态图片的句柄(成像图)
     */
    private ImageReader mImageReader;
    private ImageReader mPreviewImageReader;        //预览图

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    //相机拍照时当前的状态
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    //是否支持闪光灯
    private boolean mFlashSupported;

    //相机方向传感器
    private int mSensorOrientation;

    //记录屏幕与预览图的比例  成像比例
    private float rate_width_pre,rate_width_picture;
    private float rate_height_pre,rate_height_picture;

    /**
     * TextureView 回调事件，类似以前的 SurfaceView 回调
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener  = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            AppLog.d("-- dsffsdfsdfs openCamera");
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };


    /**
     * 相机状态回调接口
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            //相机打开成功，开始预览
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;

            ToastUtils.show( mContext, "打开相机失败");
            //关闭界面
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }
    };


    /**
     * 照片回调
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage()));
        }

    };
    /*
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            AppLog.d("CaptureCallback mState onCaptureProgressed " );
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            //AppLog.d("CaptureCallback mState onCaptureCompleted " );
            process(result);
        }

        private void process(CaptureResult result) {
            AppLog.d("CaptureCallback mState = " + mState );
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    AppLog.d("CaptureCallback afState = " + afState );
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState/*TODO*/ ) {

                        if( bAutoFocus ){
                            mainLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    showTipInfo( View.GONE, View.VISIBLE );
                                    mFocusImageView.clearAnimation();
                                    bAutoFocus = false;
                                }
                            });
                            unlockFocus();
                            break;
                        }

                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        AppLog.d("CaptureCallback aeState = " + aeState );
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }


    };

    /**
     * 设置相机参数
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private boolean setUpCameraOutputs() {

        //AppLog.d( "------- CameraOutputs width = " + width + " height = " + height );

        Activity activity = getActivity();
        cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        CameraManager manager = cameraManager;//(CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if( manager==null )
                return false;

            for (String cameraId : manager.getCameraIdList()) {
                AppLog.d("------- CameraOutputs cameraId = " + cameraId );
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                //前置摄像头  不使用
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                //相机属性
                StreamConfigurationMap map = characteristics.get( CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // 输出的JPEG静态图像，最大可用的大小  也即成像图像大小
                Size[] pictureSizes = map.getOutputSizes(ImageFormat.JPEG);
                for( Size size : pictureSizes ){
                    AppLog.d( "------- CameraOutputs size [ " + size.getWidth() + "," + size.getHeight() +"]");
                }

                //以成像参数为基础，300万左右， 成像，预览，屏幕 同比例去设置成像参数
                float screenRate = CameraUtil.getScreenRate( getActivity() );
                float bestRate;
                AppLog.i("------- CameraOutputs screenRate = " + screenRate );

                //最佳成像参数
                picturePoint = CameraUtil.getBestCameraPictureSize2( Arrays.asList(pictureSizes), screenRate );
                if( picturePoint == null ){
                    ToastUtils.show( getActivity(), "相机像素太低，不支持此相机拍照。", Toast.LENGTH_SHORT );
                    getActivity().finish();
                    return false;
                }
                bestRate = picturePoint.y *1f / picturePoint.x;

                AppLog.i("------- CameraOutputs  Picture w = " + picturePoint.x + ",,,, h = " + picturePoint.y+",,,bestRate="+bestRate );

                //Size largest = Collections.max( Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                //成像回调
                mImageReader = ImageReader.newInstance( picturePoint.x, picturePoint.y, ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener( mOnImageAvailableListener, mBackgroundHandler);

                //---------------------------------------------------------------------------
                //根据成像比例寻找 合适的预览比例

                //找出是否需要交换维度以获得相对于传感器的预览大小
                // Find out if we need to swap dimension to get the preview size relative to sensor
//                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
//                boolean swappedDimensions = false;
//                switch (displayRotation) {
//                    case Surface.ROTATION_0:
//                    case Surface.ROTATION_180:
//                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
//                            swappedDimensions = true;
//                        }
//                        break;
//                    case Surface.ROTATION_90:
//                    case Surface.ROTATION_270:
//                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
//                            swappedDimensions = true;
//                        }
//                        break;
//                    default:
//                        //Log.e(TAG, "Display rotation is invalid: " + displayRotation);
//                        break;
//                }

//                Point displaySize = new Point();
//                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
//                int rotatedPreviewWidth = width;
//                int rotatedPreviewHeight = height;
//                int maxPreviewWidth = displaySize.x;
//                int maxPreviewHeight = displaySize.y;
//
//                if (swappedDimensions) {
//                    rotatedPreviewWidth = height;
//                    rotatedPreviewHeight = width;
//                    maxPreviewWidth = displaySize.y;
//                    maxPreviewHeight = displaySize.x;
//                }

                Size previewSizes[] = map.getOutputSizes(SurfaceTexture.class);
                for( Size size : previewSizes ){
                    AppLog.d( "------- CameraOutputs preview size [ " + size.getWidth() + "," + size.getHeight() +"]");
                }
                previewPoint = CameraUtil.getBestCameraSize2( Arrays.asList(previewSizes), bestRate );
                AppLog.i(" ------- CameraOutputs PreviewSize w = " + previewPoint.x + ",,,, h = " + previewPoint.y );

                mPreviewImageReader = ImageReader.newInstance( previewPoint.x, previewPoint.y, ImageFormat.YUV_420_888,  /*maxImages*/2);
                mPreviewImageReader.setOnImageAvailableListener( mOnImageAvailableListener, mBackgroundHandler);

                //根据成像比例, 调整屏幕UI比例效果
                adjuestUI( bestRate );

                mPreviewSize = new Size( previewPoint.x, previewPoint.y );
                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio( mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio( mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // 是否支持闪光灯
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AppLog.d(" ------- CameraOutputs NullPointerException e = " + e.toString() );
        }
        return false;
    }

    /**
     * Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.
     */
    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        if( !setUpCameraOutputs(/*width, height*/) ){
            return;     //设置相机参数失败
        }
        configureTransform(width, height);
        //Activity activity = getActivity();
        CameraManager manager = cameraManager; //(CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        if( manager == null ){
            ToastUtils.show( mContext,"打开相机失败");
            getActivity().finish();
            return;
        }
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
            if( null != mPreviewImageReader ){
                mPreviewImageReader.close();
                mPreviewImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建新的相机预览的句柄
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);
            //mPreviewRequestBuilder.addTarget(mPreviewImageReader.getSurface());

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()/*, mPreviewImageReader.getSurface()*/),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            //自动聚焦（AF），自动曝光（AE）和自动白平衡（AEB）模式
                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // 设置对焦模式:  CONTROL_AF_MODE
                                //AF_MODE_OFF：AF关闭；framework/app直接控制镜头的位置。
                                //AF_MODE_AUTO：Single-sweep自动聚焦。只有AF被触发，镜头才会移动。
                                //AF_MODE_MACRO：Single-sweep微距自动聚焦。只有AF被触发，镜头才会移动。
                                //AF_MODE_CONTINUOUS_VIDEO：平滑的持续聚焦，用于视频录制。触发则立即在当前位置锁住焦点。取消而继续持续聚焦。
                                //AF_MODE_CONTINUOUS_PICTURE：快速持续聚焦，用于静态图片的ZSL捕获。一旦达到扫描目标，触发则立即锁住焦点。取消而继续持续聚焦。
                                //AF_MODE_EDOF：高级的景深聚焦。没有自动聚焦的浏览，触发和取消没有意义。通过HAL层控制图像的聚集。
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                setAutoFlash(mPreviewRequestBuilder);
                                switchFlashMode( getCameraFlashMode() );

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            ToastUtils.show( mContext, "打开相机失败" );
                        }
                    }, null
            );

            //第一次进入
            if( bAutoFocus ){
                mainLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        lockFocus();
                    }
                });
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture() {
        lockFocus();
    }

    /**
     * 第一步， 捕获静止图像，先锁定焦点
     */
    private void lockFocus() {
        AppLog.d("lockFocus 000 mState = " + mState );
        //if( mState != 0 ) return;

        if( bAutoFocus ){
            showTipInfo( View.VISIBLE, View.GONE );
            mFocusImageView.startZoomAndFadeOut();
        }

        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture( mPreviewRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            stopCameraScan();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用 lockFocus 捕获一张静态图
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    unlockFocus();
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), mCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     * 保存图片线程
     */
    private /*static*/ class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;

        ImageSaver(Image image) {
            mImage = image;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            mEdgeDetecter.onPictureTaken( bytes );
            buffer.get(bytes);
            mImage.close();
        }

    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
//    static class CompareSizesByArea implements Comparator<Size> {
//
//        @Override
//        public int compare(Size lhs, Size rhs) {
//            // We cast here to ensure the multiplications won't overflow
//            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
//                    (long) rhs.getWidth() * rhs.getHeight());
//        }
//
//    }

    //根据显示情况，设置取景框高宽，及 定位框
    private void startSetParamThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                while( true ){

                    SystemClock.sleep(50);
                    if(bPause) break;

                    if( tipsLayout.getHeight() == 0 || /*camera == null ||*/ toolsLayout.getHeight() == 0 || mScreenWidth == 0 )
                        continue;

                    //设置相机参数
//                    if( previewPoint ==null || picturePoint == null )
//                        setCameraParams();

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

                    //变换相对预览图的位置
                    cropRect.left = (int)(cropRect.left / rate_w);
                    cropRect.top = (int)(cropRect.top / rate_h);
                    cropRect.right = (int)(cropRect.right / rate_w);
                    cropRect.bottom = (int)(cropRect.bottom / rate_h);

                    //
                    mEdgeDetecter.setData(cropRect, camera_width, camera_height);
                    break;
                }
            }
        }).start();
    }

    //保证对焦，手动对焦提示，稳定提示同时 只显示一个
    private void showTipInfo( int focusImage, /*int handTips, */int handStable ){

        mFocusImageView.setVisibility( focusImage );
        focusTipLayout.setVisibility( View.GONE );
        dealCameraTextView.setVisibility( handStable );         //对焦完成才显示
    }

    private int getCameraFlashMode() {
        SharedPreferences sharep = mContext.getSharedPreferences("ask_camera", 0);
        return sharep.getInt("flashmode", 0);
    }

    private int getImageFlashMode() {
        SharedPreferences sharep = mContext.getSharedPreferences("ask_camera", 0);
        int mode = sharep.getInt("flashmode", 0);
        int img_id;
        if (mode == 1) {
            img_id = R.drawable.ic_flashlight_orange;
        } else {
            img_id = R.drawable.ic_flashlight_white;
        }
        return img_id;
    }

    private void switchFlashMode(int mode) {
        SharedPreferences sharep = mContext.getSharedPreferences("ask_camera", 0);
        SharedPreferences.Editor editor = sharep.edit();
        editor.putInt("flashmode", mode);
        editor.apply();
        //UI变化
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                mFlashLedView.setImageResource(getImageFlashMode());
                if( !mFlashSupported ) mFlashLedLayout.setVisibility( View.GONE );
            }
        });

        if( !mFlashSupported )
            return;

        switch (mode) {
            case 1:
                //设置flash
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                break;
            case 0:
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
                mPreviewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                break;
        }

        //更新
        try {
            mCaptureSession.setRepeatingRequest( mPreviewRequestBuilder.build(), null/*mCaptureCallback*/, null/*mBackgroundHandler*/);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

//    private void setFlashMode(CaptureRequest.Builder requestBuilder) {
//        if( !mFlashSupported ) return;
//        switch (mFlashMode) {
//            case 0:
//                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
//                requestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
//                break;
//            case 1:
//                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
//                break;
//            case 2:
//                requestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
//                break;
//        }
//    }
    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {

//        if (mFlashSupported) {
//            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
//        }
    }
    //----------------------------------------------------------------------------------------------------------------------------
    private long time;
    private class EdgeDetecter{

        private boolean pictureRunning = false;
        private Rect mCropRect;     //图片裁剪框
        private int mCameraPreviewWidth, mCamreaPriviewHeight;
        private int trytime = 0;        //多次尝试，大于3次，强制拍摄

        public void setData(Rect croprect, int viewWidth, int viewHaight ){
            mCropRect = croprect;

            mCameraPreviewWidth = viewWidth;
            mCamreaPriviewHeight = viewHaight;
        }

        /*public*/ void onPictureTaken( final byte[] data ) {

            if (data == null){
                return;
            }
            if( pictureRunning ){
                return;
            }
            pictureRunning = true;

            new Thread(new Runnable() {
                @Override
                public void run() {

                    //time = System.currentTimeMillis();
                    trytime++;

                    AppLog.d("onPictureTaken start 0000  ");
                    //旋转
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options );

                    if( mCropRect!=null && bitmap!=null){

                        // 默认取camera0 的角度
                        //AppLog.d("onPictureTaken start mSensorOrientation = " + mSensorOrientation );

//                        if( mSensorOrientation != 0 ){
//                            Matrix matrix = new Matrix();
//                            matrix.postRotate( mSensorOrientation );
//                            Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//
//                            bitmap.recycle();
//                            bitmap = bmp;
//                        }

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

                        AppLog.d(" dsfdsfsdgfgf create time = " + ( System.currentTimeMillis()-time ) );
                        final int ret[] = OpenCVHelper.detectFourEdge( bitmap, trytime>=3/*bForceCamera*/ );
                        AppLog.d(" dsfdsfsdgfgf picture time = " + ( System.currentTimeMillis()-time ) );
                        AppLog.d(" dsfdsfsdgfgf detectFourEdge = " + ret[0] );

                        //更新到界面显示 同时判断 是否满足条件
                        boolean success = detectResult( ret, false );

                        //强拍时赋值默认识别框
//                        if( (!success || ret[0]!=0 )&& bForceCamera ){
//                            ret = getDefaultquArea( bitmap );
//                        }

//                    if( success ){
//                        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/1/test-"+ (index++) +".jpg";
//                        drawLineInBitmap( bitmap, ret );
//                        path = BitmapUtils.saveImage( path, bitmap );
//                        AppLog.d("dsfdsfsdgfgfsklk DetechAynscTask picture path = " + path );
//                    }
                        bitmap.recycle();

                        AppLog.d("onPictureTaken start 3333  success="+success );
                        //double clarity = OpenCVHelper.clarityMat(MODE_CLARITY_1);
                        //boolean clarity = true/*OpenCVHelper.fuzzyDetection()>0*/;
                        if( success /*|| bForceCamera*/ ){

                            //第二阶段，图片生成阶段
                            final String imagepath = DDWorkManager.getImagePath() + System.currentTimeMillis()+ AppConst.IMAGE_SUFFIX_NAME;
                            success = OpenCVHelper.saveImage( imagepath );
                            //保存图片成功
                            if( success ){
                                mainLayout.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        detectFile( imagepath, ret );
                                    }
                                },200);
                                pictureRunning = false;
                                stopCameraScan();
                                trytime = 0;
                                return;
                            }
                        }
                    }else if(bitmap!=null){
                        bitmap.recycle();
                    }

                    //AppLog.d("onPictureTaken start 44444");
                    //修改状态，重新启动预览重新对焦
                    resetTakePicture();
                    //nextAutoFocus();
                    stopCameraScan();
                    //AppLog.d("onPictureTaken start 99999999");
                    pictureRunning = false;
                }
            }).start();
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
}
