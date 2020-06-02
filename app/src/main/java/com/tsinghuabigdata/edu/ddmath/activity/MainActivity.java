package com.tsinghuabigdata.edu.ddmath.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.taobao.sophix.SophixManager;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MessageModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.EditHeaderEvent;
import com.tsinghuabigdata.edu.ddmath.event.GotoCreateWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.JoinClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpSuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.ServerUpgradingEvent;
import com.tsinghuabigdata.edu.ddmath.event.SyncShowRedpointEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateUserPendantEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.ErrorBookFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.FirstFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyCenterFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.StudyConditionFragment;
import com.tsinghuabigdata.edu.ddmath.module.backstage.FirstPrivilegeUtil;
import com.tsinghuabigdata.edu.ddmath.module.badge.BadgeManager;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.CreateWorkActivity;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.message.NewMsgListener;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.ExistNewMessage;
import com.tsinghuabigdata.edu.ddmath.module.message.service.TopMessagService;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.VerifyState;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.VerifyPhoneDialog;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment.UserCenterFragment;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.BuySuiteActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity.ScoreGuideActivity;
import com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity.ScorePlanActivity;
import com.tsinghuabigdata.edu.ddmath.module.upgrade.UpgradeManager;
import com.tsinghuabigdata.edu.ddmath.opencv.OpenCVHelper;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.AssetsUtils;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.DeviceUtils;
import com.tsinghuabigdata.edu.ddmath.util.DirUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.LoginUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.HeadView;
import com.tsinghuabigdata.edu.utils.RestfulUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends RoboActivity implements View.OnClickListener, Observer{

    public static final String KEY_CURRENT_POSTION = "key_current_position";
    public static final  int FRAGMENT_FIRST           = 0;
    public static final  int FRAGMENT_STUDY_TASK      = 1;
    public static final  int FRAGMENT_QUESTION_BOOK   = 2;
    public static final  int FRAGMENT_STUDY_CONDITION = 3;
    public static final  int FRAGMENT_MY_CENTER       = 4;

    private static final int LOGIN_IN_OUT             = 2;

    private int mCurPosition = FRAGMENT_FIRST;

    // 最底的几个fragment
    private FirstFragment          mFirstFragment;//首页界面
    private MyStudyFragment        mMyStudyFragment;   //学习界面
    private StudyConditionFragment mStudyConditionFragment;  //报告界面
    private ErrorBookFragment      mErrorBookFragment;  //提升界面
    private MyCenterFragment       mMyCenterFragment; //我的

    private List<Fragment> mainFragments = new ArrayList<>();


    private RelativeLayout mLlRoot;
    private LinearLayout   mLlTeam;
    private ImageView      mIvFirst;
    private ImageView      mIvLearnTask;
    private ImageView      mIvErrBook;
    private ImageView      mIvReport;
    private HeadView       mHeadView;

    private Context mContext;
    private long    exitTime;


    private static RoboActivity instance;
    public static RoboActivity getInstance(){ return instance; }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LOGIN_IN_OUT) {
                setHead();
                boolean isLogin = (boolean) msg.obj;
                if (!isLogin) {
                    //消息状态复原
                    setNewMsgStatus(false);
                }
            }
        }
    };

    /**
     * fragment 使用方法
     * addStack()    将fragment添加进fragment栈管理起来，但不显示
     * showTopFragment()   显示对应的栈最上面的fragment, 如果不是当前tab, 不要调用
     * switchFragment()   切换到对应的tab
     *
     * @param savedInstanceState ff
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_phone);
        }
        // 接口类初始化
        if(RestfulUtils.mRequestQueue == null){
            AppRequestUtils.initialization( this );
        }
        initFragment(savedInstanceState);
        initViews(savedInstanceState);
        initData();
        new UpgradeManager(this).checkApkUpdate();
        initMessageSys();
        AccountUtils.initEnableAutoRecGc();
        ProductUtil.updateLearnDou( null );
        //注册用户首次登陆获得免费的使用权提示
        new FirstPrivilegeUtil().startQuery( this );

        //initCommonGudie();
        instance = this;
        mContext = this;
        bFinishroadcast = false;
        startAddImageReceiver();

        MobclickAgent.setDebugMode(true);

        // 初始化推送服务
        MessageUtils.initJPushSdk(this);

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            int memorySize = activityManager.getLargeMemoryClass();
            AppLog.d("dfd max memorySize = " + memorySize);
            if (memorySize < 256) {
                ToastUtils.show(mContext, AppUtils.getAppName()+"可使用的内存太小，使用中可能会不稳定，请更换手机再使用.", Toast.LENGTH_LONG);
            }
        }

        //原版教辅拍照引擎初始化
//        OpenCVHelper.init( this );

        //        if( AppUtils.isDebug() ){
        //            boolean isPermission = FloatPermissionManager.getInstance().applyFloatWindow(this);
        //            //有对应权限或者系统版本小于7.0
        //            if (isPermission || Build.VERSION.SDK_INT < 24) {
        //                //开启悬浮窗
        //                FloatActionController.getInstance().startMonkServer(this);
        //            }
        //        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //反初始化推送服务
        //MessageUtils.unIinitJPushSdk(this);

        //消息系统取消注册
        MessageUtils.getInstance().unregisterNewmsgBrd();

        stopAddImageReceiver();

        EventBus.getDefault().unregister(this);
        LoginController.getInstance().deleteAllObservers();

        //关闭悬浮窗
        //FloatActionController.getInstance().stopMonkServer(this);

        //SophixManager.getInstance().killProcessSafely();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if( intent.hasExtra("from") && intent.getStringExtra("from").equals("login") )
            AccountUtils.checkJionSchoolClass( mContext );
    }

    @Override
    public String getViewName() {

        if (mHeadView.isActivated()) {
            return mMyCenterFragment.getUmEventName();
        } else if (mIvFirst.isActivated()) {
            return mFirstFragment.getUmEventName();
        } else if (mIvLearnTask.isActivated()) {
            return mMyStudyFragment.getUmEventName();
        } else if (mIvErrBook.isActivated()) {
            return mErrorBookFragment.getUmEventName();
        } else if (mIvReport.isActivated()) {
            return mStudyConditionFragment.getUmEventName();
        }
        return super.getViewName();
    }

    //---------------------------------------------------------------------------------------
    private void initFragment(Bundle saveInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(null != saveInstanceState){
            mFirstFragment = (FirstFragment)fragmentManager.findFragmentByTag(FirstFragment.class.getSimpleName());
            mMyStudyFragment = (MyStudyFragment) fragmentManager.findFragmentByTag(MyStudyFragment.class.getSimpleName());
            mStudyConditionFragment = (StudyConditionFragment) fragmentManager.findFragmentByTag(StudyConditionFragment.class.getSimpleName());
            mErrorBookFragment = (ErrorBookFragment) fragmentManager.findFragmentByTag(ErrorBookFragment.class.getSimpleName());
            mMyCenterFragment = (MyCenterFragment) fragmentManager.findFragmentByTag(MyCenterFragment.class.getSimpleName());
        }else {
            mFirstFragment = new FirstFragment();
            mMyStudyFragment = new MyStudyFragment();
            mStudyConditionFragment = new StudyConditionFragment();
            mErrorBookFragment = new ErrorBookFragment();
            mMyCenterFragment = new MyCenterFragment();
        }

        mainFragments.add(mFirstFragment);
        mainFragments.add(mMyStudyFragment);
        mainFragments.add(mErrorBookFragment);
        mainFragments.add(mStudyConditionFragment);
        mainFragments.add(mMyCenterFragment);
    }

    private void initViews(Bundle saveInstanceState) {
        mContext = this;
        mLlRoot =  findViewById(R.id.ll_root);
        mLlTeam =  findViewById(R.id.ll_team);
        LinearLayout mLlFirst =  findViewById(R.id.ll_first);

        mIvFirst =  findViewById(R.id.iv_first);
        mIvLearnTask =  findViewById(R.id.iv_study_task);
        mIvErrBook =  findViewById(R.id.iv_question_book);
        mIvReport =  findViewById(R.id.iv_study_condition);

        LinearLayout mLlStudyTask =  findViewById(R.id.ll_study_task);
        LinearLayout mLlQuestionBook =  findViewById(R.id.ll_question_book);
        LinearLayout mLlStudyCondition =  findViewById(R.id.ll_study_condition);
        mHeadView =  findViewById(R.id.headView);
        TextView mTvUsercenter =  findViewById(R.id.tv_usercenter);
        if (!GlobalData.isPad()) {
            DeviceUtils.setMainPhoneParams(this, mLlTeam);
        }
        for (int i = 0; i < mLlTeam.getChildCount(); i++) {
            LinearLayout child = (LinearLayout) mLlTeam.getChildAt(i);
            TextView textView = (TextView) child.getChildAt(1);
            textView.setTypeface(AssetsUtils.getMyTypeface(mContext));
        }
        mTvUsercenter.setTypeface(AssetsUtils.getMyTypeface(mContext));
        if(null != mainFragments && mainFragments.size() > 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            for(Fragment fragment : mainFragments){
                if(!fragment.isAdded()){
                    fragmentTransaction.add(R.id.container_fl,fragment,fragment.getClass().getSimpleName());
                    if(fragment instanceof FirstFragment){
                        if(null == saveInstanceState){
                            fragmentTransaction.show(fragment);
                        }
                    }else {
                        fragmentTransaction.hide(fragment);
                    }
                }
            }
            fragmentTransaction.commitAllowingStateLoss();
        }
        setHead();
        if(null == saveInstanceState){
            mHeadView.setActivated(false);
            mIvFirst.setActivated(true);
        }else {
            mCurPosition = saveInstanceState.getInt(KEY_CURRENT_POSTION);
            restoreNavButtonState(mCurPosition);
        }
        mHeadView.setOnClickListener(this);
        mLlFirst.setOnClickListener(this);
        mLlStudyTask.setOnClickListener(this);
        mLlStudyCondition.setOnClickListener(this);
        mLlQuestionBook.setOnClickListener(this);
    }

    private void initData() {
        LoginController.getInstance().addObserver(this);
        EventBus.getDefault().register(this);
        //        boolean reportStatus = GlobalData.isHasLoadReportStatus();
        //        LogUtils.i("reportStatus= "+reportStatus);

        //建立缓存目录
        FileUtil.createPath(mContext, DirUtils.getExternalDir(AppConst.IMAGE_CACHE_DIR));
        if (AccountUtils.getLoginUser() != null) {
            umengAddAccount();
        }
        //更新注册赠送数据
        //ProductUtil.updateRegisterReward();

        //参数处理
        Intent intent = getIntent();
        if( intent.hasExtra("joinclass") && intent.getBooleanExtra("joinclass",false) ){
            mLlRoot.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginUtils.queryTutorClass();
                }
            },200);
        }else if( !intent.hasExtra("register") || !intent.getBooleanExtra("register",false) ){
            AccountUtils.checkJionSchoolClass(mContext);
        }
    }

    public void hideAllMainFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment : mainFragments) {
            fragmentTransaction.hide(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void showFragment(Fragment f) {
        hideAllMainFragments();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.show(f);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void switchFragment(int position) {
        if (mCurPosition == position) {
            return;
        }
        switchBackground(position);
        for (int i = 0; i < mLlTeam.getChildCount(); i++) {
            LinearLayout child = (LinearLayout) mLlTeam.getChildAt(i);
            TextView textView = (TextView) child.getChildAt(1);
            if (position == i) {
                child.getChildAt(0).setActivated(true);
                textView.setActivated(true);
            } else {
                child.getChildAt(0).setActivated(false);
                textView.setActivated(false);
            }
        }
        mHeadView.setActivated(position == FRAGMENT_MY_CENTER);

        Fragment showedFragment = mainFragments.get(position);
        showFragment(showedFragment);
        mCurPosition = position;
    }

    private void switchBackground(int position) {
        //如果在我的世界/服务中心之间切换 不需要更换背景
        if ((mCurPosition == FRAGMENT_FIRST || mCurPosition == FRAGMENT_MY_CENTER) && (position == FRAGMENT_FIRST || position == FRAGMENT_MY_CENTER)) {
            return;
        }
        if (position == FRAGMENT_STUDY_TASK) {
            mLlRoot.setBackgroundResource(R.drawable.bg_task);
        } else if (position == FRAGMENT_QUESTION_BOOK) {
            mLlRoot.setBackgroundResource(R.drawable.bg_conquer);
        } else if (position == FRAGMENT_STUDY_CONDITION) {
            mLlRoot.setBackgroundResource(R.drawable.bg_diagnose);
        } else {
            mLlRoot.setBackgroundResource(R.drawable.bg);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.headView:
                switchFragment(FRAGMENT_MY_CENTER);
                MobclickAgent.onEvent(mContext, "main_mycenter");
                break;
            case R.id.ll_first:
                switchFragment(FRAGMENT_FIRST);
                MobclickAgent.onEvent(mContext, "main_myworld");
                break;
            case R.id.ll_study_task:
                switchFragment(FRAGMENT_STUDY_TASK);
                MobclickAgent.onEvent(mContext, "main_learntask");
                break;
            case R.id.ll_question_book:
                switchFragment(FRAGMENT_QUESTION_BOOK);
                MobclickAgent.onEvent(mContext, "main_errbook");
                break;
            case R.id.ll_study_condition:
//                switchFragment(FRAGMENT_STUDY_CONDITION);
//                mStudyConditionFragment.addView();
//                MobclickAgent.onEvent(mContext, "main_rport");
                startActivity(new Intent(this,ScorePlanActivity.class));
                break;
            default:
                break;
        }
        //FloatActionController.getInstance().setCurrUiName(getViewName());
    }

    //    private int index = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            AlertManager.toast(this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            //            finish();
            super.onBackPressed();
        }
    }

    private void setHead() {
        mHeadView.refreshHeadImage();
    }

    //--------------------------------------------------------------------------------------------------------

    private StartActivityReceiver addImageReceiver;

    private void startAddImageReceiver() {
        addImageReceiver = new StartActivityReceiver();
        registerReceiver(addImageReceiver, new IntentFilter(AppConst.ACTION_START_WAITUPLOAD));
        registerReceiver(addImageReceiver, new IntentFilter(AppConst.ACTION_APPLICATION_EXIT));
        //intentFilter = new IntentFilter( AppConst.ACTION_XBOOK_EDIT );
        //registerReceiver(addImageReceiver, intentFilter);
    }

    private void stopAddImageReceiver() {
        unregisterReceiver(addImageReceiver);
    }


    /**
     * 监听添加图片
     */
    class StartActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听消息
            /*if (AppConst.ACTION_START_WAITUPLOAD.equals(intent.getAction())) {
                //mWaitWorkAdapter.notifyDataSetChanged();
                startActivity(new Intent(mContext, WaitUploadActivity.class));
            } else */if (AppConst.ACTION_APPLICATION_EXIT.equals(intent.getAction())) {
                String msg = intent.getStringExtra("msg");
                if (!TextUtils.isEmpty(msg))
                    ToastUtils.showToastCenter(mContext, msg);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SophixManager.getInstance().killProcessSafely();
                    }
                }, 3000);
            }
        }
    }

    private void initMessageSys() {
        MessageUtils.getInstance().init(this);
        MessageUtils.getInstance().setNewMsgListener(new NewMsgListener() {
            @Override
            public void msgComeCallback(boolean ishavenew) {
                setNewMsgStatus(ishavenew);
            }
        });
        MessageUtils.getInstance().registerNewmsgBrd();
        queryTopMessage();
        queryVerifyState();
        //查询是否有新的消息
        queryIsexistNewMsg();
    }

    private void queryTopMessage() {
        if (DataUtils.isLoginSuccess()) {
            //弹出首页消息图片
            Intent intent = new Intent(this, TopMessagService.class);
            startService(intent);
        }
    }

    private void queryVerifyState() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            boolean verifyState = AccountUtils.getVerifyState(mContext, detailinfo.getStudentId());
            //LogUtils.i("verifyState=" + verifyState);
            if (!verifyState) {
                queryNetVerifyState(detailinfo.getStudentId());
            }
        }
    }

    private void queryNetVerifyState(final String studentId) {
        new LoginModel().queryVerifyState(studentId, new RequestListener<VerifyState>() {

            @Override
            public void onSuccess(VerifyState vo) {
                //LogUtils.i("queryVerifyState onSuccess");
                if (vo == null) {
                    return;
                }
                if (vo.isConfirmed()) {
                    AccountUtils.saveVerifyState(mContext, studentId);
                } else {
                    showVerifyDialog();
                }
            }

            @Override
            public void onFail(HttpResponse<VerifyState> response, Exception ex) {
                //LogUtils.i("queryVerifyState onFail");
                showVerifyDialog();
            }
        });
    }

    private void showVerifyDialog() {
        VerifyPhoneDialog verifyPhoneDialog = new VerifyPhoneDialog(mContext);
        verifyPhoneDialog.setActivity(this);
        verifyPhoneDialog.show();
    }

    private void queryIsexistNewMsg() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null || TextUtils.isEmpty(detailinfo.getStudentId()))
            return;

        new MessageModel().queryNewMsg(detailinfo.getStudentId(), new RequestListener<ExistNewMessage>() {
            @Override
            public void onSuccess(ExistNewMessage msg) {
                LogUtils.i("queryIsexistNewMsg onSuccess");
                if (msg == null)
                    return;

                //新增未读消息数量
                new BadgeManager(mContext).resetBadge(msg.getCount());
                setNewMsgStatus(msg.isWdxx());
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
            }
        });
    }

    //设置是否有新消息
    private void setNewMsgStatus(final boolean ishavenew) {
        EventBus.getDefault().post(new SyncShowRedpointEvent(ishavenew));
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mHeadView.setRedpoint(ishavenew);
            }
        });
    }

    @Subscribe
    public void receive(JoinClassEvent event) {
        AppLog.d("event = " + event);
        LoginUtils.queryTutorClass();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(JumpEvent event) {
        int module = event.getMainModule();
        switchFragment(module);
        if (module == FRAGMENT_MY_CENTER) {
            mMyCenterFragment.goTo(event.getSubModule());
        } else if (module == FRAGMENT_STUDY_TASK) {
            mMyStudyFragment.goTo(event.getSubModule());
        } else if (module == FRAGMENT_QUESTION_BOOK) {
            mErrorBookFragment.goTo(event.getSubModule());
        } else if (module == FRAGMENT_STUDY_CONDITION) {
            mStudyConditionFragment.addView();
            mStudyConditionFragment.goTo(event.getSubModule());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(JumpSuiteEvent event) {
        AppLog.d("event = " + event);
        switchFragment(FRAGMENT_MY_CENTER);
        mMyCenterFragment.goTo(UserCenterFragment.MODEL_MYSTUDYBEAN);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(EditHeaderEvent event) {
        AppLog.d("event = " + event);
        setHead();
    }

    @Subscribe
    public void receive(GotoCreateWorkEvent event) {
        AppLog.d("event = " + event);
        Intent intent = new Intent(mContext, CreateWorkActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void receive(ServerUpgradingEvent event) {
        AppLog.d("event = " + event);
        finish();
    }

    @Subscribe
    public void receive(UpdateUserPendantEvent event){
        AppLog.d("receive event = " + event );
        mHeadView.refreshHeadImage();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Boolean) {
            PicassoUtil.init();
            boolean isLogin = (boolean) data;
            if (isLogin) {  //登录
                MessageUtils.initJPushSdk(mContext);
                queryTopMessage();
                queryVerifyState();
                //查询是否有新的消息
                queryIsexistNewMsg();
                //initCommonGudie();
                //友盟账号统计
                umengAddAccount();
            } else {        //退出登录
                MessageUtils.unIinitJPushSdk(getApplicationContext());
                MobclickAgent.onProfileSignOff();
            }
            mHandler.obtainMessage(LOGIN_IN_OUT, isLogin).sendToTarget();
        }

    }

    private void umengAddAccount() {
        //友盟账号统计
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null && detailinfo.getStudentId() != null && detailinfo.getStudentId().length() > 10) {
            String uid = detailinfo.getStudentId().substring(10);
            MobclickAgent.onProfileSignIn(uid);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_POSTION,mCurPosition);
        super.onSaveInstanceState(outState);
    }
    public void restoreNavButtonState(int position) {
        if(null != mLlTeam){
            for (int i = 0; i < mLlTeam.getChildCount(); i++) {
                LinearLayout child = (LinearLayout) mLlTeam.getChildAt(i);
                TextView textView = (TextView) child.getChildAt(1);
                if (position == i) {
                    child.getChildAt(0).setActivated(true);
                    textView.setActivated(true);
                } else {
                    child.getChildAt(0).setActivated(false);
                    textView.setActivated(false);
                }
            }
        }
        if(null != mHeadView){
            mHeadView.setActivated(position == FRAGMENT_MY_CENTER);
        }
    }
}
