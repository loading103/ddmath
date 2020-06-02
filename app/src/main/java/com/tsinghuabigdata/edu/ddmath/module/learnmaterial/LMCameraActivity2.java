package com.tsinghuabigdata.edu.ddmath.module.learnmaterial;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.camera2.Camera2BasicFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


/**
 * 原版教辅作业拍照界面
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LMCameraActivity2 extends RoboActivity/*BaseCameraActivity*/ {

    public static void openActivity(Context context, LocalPageInfo pageInfo, float rate, boolean teacher ){
        if( context == null || pageInfo ==null )
            return;

        Intent intent = new Intent( context, LMCameraActivity2.class );
        AccountUtils.setLocalPageInfo( pageInfo );
        intent.putExtra( PARAM_PAGEINFO, true );
        intent.putExtra( PARAM_RATE, rate );
        intent.putExtra( PARAM_TEACHER, teacher );
        context.startActivity( intent );
    }

    //纸张框的比例宽度
    private static final String PARAM_PAGEINFO  = "pageinfo";       //原版教辅页信息
    private static final String PARAM_RATE      = "rate";          //原版教辅宽度/高比
    private static final String PARAM_TEACHER   = "teacher";       //是否老师布置的作业

    private Camera2BasicFragment camera2BasicFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        transparent = true;
        setTheme( R.style.Theme_Custom_AppCompat );
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        camera2BasicFragment = Camera2BasicFragment.newInstance();
        if( !camera2BasicFragment.parseIntent( getIntent()) ){
            ToastUtils.show( this, "参数错误", Toast.LENGTH_SHORT );
            finish();
            return;
        }

        if( camera2BasicFragment.getBookRate() > 1 ){     //横版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(GlobalData.isPad()? R.layout.activity_lmmaterial_camera_land: R.layout.activity_lmmaterial_camera_land_phone);
        }else{      //竖版拍摄
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(GlobalData.isPad()? R.layout.activity_lmmaterial_camera: R.layout.activity_lmmaterial_camera_phone);
        }

        setContentView(R.layout.activity_lmcamera);
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideNavigationBar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( camera2BasicFragment!=null ) camera2BasicFragment.onActivityResult( requestCode, resultCode, data );
    }

    private void hideNavigationBar()
    {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if( Build.VERSION.SDK_INT >= 19 ){
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }
}
