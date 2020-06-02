package com.tsinghuabigdata.edu.ddmath;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.mob.MobSDK;
import com.mobsandgeeks.saripaar.Validator;
import com.taobao.sophix.SophixManager;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tsinghuabigdata.edu.commons.cache.AppCache;
import com.tsinghuabigdata.edu.commons.cache.CacheManager;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.AppSessionCache;
import com.tsinghuabigdata.edu.ddmath.commons.CrashHandler;
import com.tsinghuabigdata.edu.ddmath.commons.http.InformMapping;
import com.tsinghuabigdata.edu.ddmath.commons.validate.MobileValidateCode;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.BaseConfig;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.event.UpdateScoreEvent;
import com.tsinghuabigdata.edu.ddmath.inter.CartoomViewNameListener;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.FloatActionController;
import com.tsinghuabigdata.edu.ddmath.module.xbook.XBookUtils;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.MyLearnServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.DirUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.Locale;


public class ZxApplication extends Application {
    private static final String TAG = "ZxApplication";
    public static final String LOG_TAG = "ddmath";
    //统计Activity start stop的次数  == 0 进入后台，>0 在前台
//    private static int stateCount = 0;
    private static MyLearnService myLearnService;

    private static ZxApplication application;


    public static ZxApplication getApplication() {
        return application;
    }

    public synchronized static MyLearnService getLearnService() {
        return myLearnService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //热更新查询
        SophixManager.getInstance().queryAndLoadNewPatch();

        //
        ZxApplication.settApplication(this);

        //日志系统
        //initAppLog(this);

        //初始化热更新
        //initHotfix();

        //学校作业上传
        setLearnService(new MyLearnServiceImpl());

        NetworkUtils.init(this);
        XBookUtils.init(this);

        // 初始化Crash日志采集
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext(), this);

        // 不使用注解
        //RoboGuice.setUseAnnotationDatabases(false);

        // 设置注入模块
        //RoboGuice.overrideApplicationInjector(this, new RoboGuiceModule());

        // 注册自定义验证
        Validator.registerAnnotation(MobileValidateCode.class);

        // 接口类初始化
        //AppRequestUtils.initialization(this);

        // 初始化映射
        InformMapping.initialization(this);

        //初始化题型数据
        DDWorkUtil.init(this);

        GlobalData.judgePad(this);

        //初始化xutils注解
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        //初始化缓存类
        initCacheManager();

        //图片缓存
        PicassoUtil.init();

        IWXAPI api = WXAPIFactory.createWXAPI(this, AppConst.APP_ID);
        api.registerApp(AppConst.APP_ID);

        //初始化使用次数信息
        //ProductUtil.updateRechargeCashback();
        //AppLog.d("dfdsafsdfsdfsdfdsfdsfdsffd aaaaaaaaa");
//        Stetho.initialize(
//                Stetho.newInitializerBuilder(this)
//                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
//                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
//                        .build());


        //监听App生命周期，达到控制进入后台和前台的监控
        registerActivityLifecycleCallbacks( new AppBehavior() );

        //关机广播监听
        //com.tsinghuabigdata.edu.ddmath.receive.ShutdownReceiver.register(this);

        //分享
        MobSDK.init(this);
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate: ");
        clear();
        //unRegisterReceiver();
        //unregisterActivityLifecycleCallbacks(userBehavior);
        //ShutdownReceiver.unRegister(this);

        super.onTerminate();

//        android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
//        System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
            // Clear the caches.  Note all pending requests will be removed too.
            clear();
        }
    }
    //用户积分显示
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if( msg.what == 0 && msg.obj instanceof String ){
                String data = (String)msg.obj;
                ToastUtils.showPoint( getBaseContext(), data );
            }
        }
    };

    public void showUserScore( int score ){
        String data = String.format(Locale.getDefault(),"+%d",score);
        mHandler.removeMessages( 0 );
        Message msg = new Message();
        msg.what = 0;
        msg.obj = data;
        mHandler.sendMessage( msg );
        //通知我的积分刷新
        EventBus.getDefault().post( new UpdateScoreEvent(true));

        //延时更新数据
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                EventBus.getDefault().post( new UpdateScoreEvent(true));
//            }
//        }, 10000);
    }

    private synchronized static void settApplication(ZxApplication application) {
        ZxApplication.application = application;
    }

    private synchronized static void setLearnService(MyLearnService service) {
        ZxApplication.myLearnService = service;
    }

//    public synchronized static void addStatCount() {
//        stateCount++;
//    }
//
//    public synchronized static void decStatCount() {
//        stateCount--;
//    }

    //监听App进入后台和恢复
    private class AppBehavior implements ActivityLifecycleCallbacks {
        private int mFinalCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            mFinalCount++;
            //如果mFinalCount ==1，说明是从后台到前台
            if (mFinalCount == 1){
                //说明从后台回到了前台
                FloatActionController.getInstance().show();
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if( activity instanceof CartoomViewNameListener){
                String name = ((CartoomViewNameListener)activity).getViewName();
                FloatActionController.getInstance().setCurrUiName( name );
            }else{
                FloatActionController.getInstance().setCurrUiName( "all" );
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {
            mFinalCount--;
            //如果mFinalCount ==0，说明是前台到后台
            if (mFinalCount == 0){
                //说明从前台回到了后台
                FloatActionController.getInstance().hide();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}
    }


    private void clear() {
        AppCache.getInstance().evictAll();
    }

//    private void registerReceiver() {
//        registerReceiver(receiver, new IntentFilter("NSPenBroadcastRecevie.NsRequest"));
//    }
//
//    private void unRegisterReceiver() {
//        unregisterReceiver(receiver);
//    }

    public static void initAppLog(Context context){
        // 初始化日志
        String  sessionid = AppUtils.getDeviceId(context);
        if (TextUtils.isEmpty(sessionid)) {
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            if (loginInfo != null) {
                sessionid = loginInfo.getAccountId();
            }
        }
        String appver = "";
        PackageInfo info = AppUtils.getPackageInfo(context);
        if (info != null) {
            appver = info.versionName;
        }
        AppLog.configLog4j(context, sessionid, "DDMATH_STUDENT_ANDROID", BaseConfig.LOG_SERVER, appver);
    }

    private void initCacheManager(){
        int appVerCode = 1;
        try {
            // 获取当前的版本号
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVerCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            AppLog.i("PackageInfo", "getPackageManager err", e);
        }
        // 初始缓存类
        CacheManager.init(new File(DirUtils.getExternalDir(AppConst.IMAGE_CACHE_DIR)), appVerCode);
        // 初始化本地缓存
        try {
            // 相对与sdcard/Android/data/{package}/cache/obj/
            AppSessionCache.init(new File(DirUtils.getExternalPrivateFileDir(this, AppConst.OBJECT_CACHE_DIR)), appVerCode);
        } catch (IOException e) {
            AppLog.i(ErrTag.TAG_IO, "init err", e);
            SophixManager.getInstance().killProcessSafely();
        }
    }

}
