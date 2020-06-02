package com.tsinghuabigdata.edu.ddmath.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.daemon.GrayService;
import com.tsinghuabigdata.edu.ddmath.module.apkupgrade.DownProgressObserver;
import com.tsinghuabigdata.edu.ddmath.receive.NetworkStateRecevie;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.ActivityUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PermissionUtil;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.ScreenLock;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.Setting;

import java.util.List;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;


public class InitActivity extends AppCompatActivity {

    private final static String TAG = InitActivity.class.getName();
    private Context mContext;

    //APK upgrade
    DownProgressObserver downloadObserve;
//    private ProgressDialog dialog;

    // 禁止锁屏，保持高亮
    private ScreenLock screenLock = ScreenLock.alloct();

    private ImageView mIvWelcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Custom_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //设置全屏显示
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;

        //UploadLogs.moveLogsAndUpload();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_init);
        } else {
            setContentView(R.layout.activity_init_phone);
        }

        initView();

        NetworkStateRecevie.setNetWorkState(this);

        //------------------------------------------------------------------------------------
        //必须获得权限才能使用
        requestPermission();

        //启动保活
        Intent grayIntent = new Intent(getApplicationContext(), GrayService.class);
        startService(grayIntent);
    }

    private void initView() {
        mIvWelcome = findViewById(R.id.iv_welcome);
        int screenHeight = WindowUtils.getScreenHeight(this);

        ViewGroup.LayoutParams layoutParams = mIvWelcome.getLayoutParams();
        int realHeight = screenHeight * 195 / 360;
        layoutParams.width = realHeight * 317 / 195;
        //Log.i("sky", "realWidth=" + realWidth + " realHeight=" + realHeight);
        mIvWelcome.setLayoutParams(layoutParams);
    }


    private void openWelcomePager() {
        ActivityUtil.goActivity(this, WelcomeActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        screenLock.acquireWakeLock(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        screenLock.releaseWakeLock();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁止用户按返回键退出
        return (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) || super.onKeyDown(keyCode, event);
    }

    private void launchApp() {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showShort(this, "启动失败!");
            Log.d(TAG, "磁盘挂载失败，不能启动应用. ");
            finish();
            return;
        }

        //访问磁盘权限
        if( !PermissionUtil.hasExternalStoragePermission(this) ){
            ToastUtils.showShort(this, "启动失败!");
            finish();
            return;
        }

        //日志系统
        ZxApplication.initAppLog(mContext);
        // 接口类初始化
        AppRequestUtils.initialization(mContext);

        //触发一次,如果token过期，单独触发一次
        AccountUtils.initEnableAutoRecGc();

        mIvWelcome.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isOpenWelcomePager()) {
                    openWelcomePager();
                    finish();
                    return;
                }
                AccountUtils.gotoMainActivity(mContext);
                finish();
            }
        }, 500);
        //        new Handler() {
        //            @Override
        //            public void handleMessage(Message msg) {
        //
        //                if (isOpenWelcomePager()) {
        //                    openWelcomePager();
        //                    finish();
        //                    return;
        //                }
        //
        //                ActivityUtil.goActivity(context, MainActivity.class);
        //
        //                finish();
        //            }
        //        }.sendEmptyMessageDelayed(1, 500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss();
//        }

        //删除注册信息
        if (downloadObserve != null) {
            downloadObserve.unregisterObserver();
        }
    }

    public boolean isOpenWelcomePager() {
        //        if (AppUtils.isDebug()){
        //            return true;
        //        }
        return PreferencesUtils.getInt(this, AppConst.WELCOME_OPENED, 0) == 0;
    }

    //--------------------------------------------------------------------------
    //权限

    private final String[] permissions = { Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION,
            Permission.CAMERA,
            Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE,
            Permission.CALL_PHONE, Permission.READ_PHONE_STATE

            };
    /**
     * Request permissions.
     */
    private void requestPermission() {
        AndPermission.with(this)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.d(TAG, "用户允许使用权限 ");
                        //必须获得权限才能使用
                        launchApp();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        Log.d(TAG, "请允许“读取外部存储信息”权限! ");
                        if (AndPermission.hasAlwaysDeniedPermission( mContext, permissions)) {
                            showSettingDialog( mContext, permissions);
                        }else{
                            ToastUtils.showShort( mContext, String.format(Locale.getDefault(),"权限请求失败，正在退出%s!",mContext.getString(R.string.app_name) ));
                            finish();
                        }
                    }
                })
                .start();
    }
    /**
     * Set permissions.
     */
    private void setPermission() {
        AndPermission.with(this)
                .runtime()
                .setting()
                .onComeback(new Setting.Action() {
                    @Override
                    public void onAction() {
                        //用户从设置界面返回  处理
                        requestPermission();
                    }
                })
                .start();
    }

    void showSettingDialog(Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString( R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames) );

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("提示")
                .setMessage(message)
                .setPositiveButton( R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //用户拒绝授权
                        ToastUtils.showShort( mContext, String.format(Locale.getDefault(),"权限请求失败，正在退出%s!",mContext.getString(R.string.app_name) ));
                        android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                    }
                })
                .show();
    }

    //
    class RuntimeRationale implements Rationale<List<String>> {

        @Override
        public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
            List<String> permissionNames = Permission.transformText(context, permissions);
            String message = context.getString(R.string.message_permission_rationale, TextUtils.join("\n", permissionNames));

            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle(R.string.app_name)
                    .setMessage(message)
                    .setPositiveButton( R.string.enter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.execute();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            executor.cancel();
                        }
                    })
                    .show();
        }
    }
}
