package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.taobao.sophix.SophixManager;
import com.tsinghuabigdata.edu.ddmath.MVPModel.MessageModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.NewbieGuide;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.NewbieGuideManager;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.ServerUpgradingEvent;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.FloatActionController;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.message.NewMsgListener;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.ExistNewMessage;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.WaitUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.upgrade.UpgradeManager;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.event.ParentCenterEvent;
import com.tsinghuabigdata.edu.ddmath.parent.fragment.CommunityFragment;
import com.tsinghuabigdata.edu.ddmath.parent.fragment.ParentCenterFragment;
import com.tsinghuabigdata.edu.ddmath.parent.fragment.ParentReportFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.DirUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


public class ParentMainActivity extends RoboActivity implements NewbieGuide.OnGuideChangedListener{


    public static final int FRAGMENT_REPORT    = 0;
//    public static final int FRAGMENT_COMMUNITY = 1;
//    public static final int FRAGMENT_CENTER    = 2;

    private int mCurPosition = FRAGMENT_REPORT;

    // 最底的几个fragment
    private ParentReportFragment mReportFragment;
    private CommunityFragment    mCommunityFragment;
    private ParentCenterFragment mCenterFragment;


    private List<Fragment> mainFragments = new ArrayList<>();


    private LinearLayout mLlTeam;
    //private LinearLayout mLlFirst;

    private LinearLayout mLlReport;
    //private ImageView    mIvReport;
    //private TextView     mTvReport;
    private LinearLayout mLlCommunity;
    //private ImageView    mIvCommunity;
    //private TextView     mTvCommunity;
    private LinearLayout mLlCenter;
    //private ImageView    mIvCenter;
    private TextView     mTvCenterMessage;
    //private TextView     mTvCenter;

    private Context mContext;
    private long    exitTime;
    private int     mMsgCount;

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
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_parent_main_phone);
        initFragment();
        initViews();
        initData();
        new UpgradeManager(this).checkApkUpdate();
        initMessageSys();
        initCommonGudie();
        mContext = this;
        //bFinishroadcast = false;
        startAddImageReceiver();

        MobclickAgent.setDebugMode(true);

        // 初始化推送服务
        MessageUtils.initJPushSdk(this);

//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        if (activityManager != null) {
//            int memorySize = activityManager.getLargeMemoryClass();
//            AppLog.d("dfd max memorySize = " + memorySize);
//            if (memorySize < 256) {
//                ToastUtils.show(mContext, AppUtils.getAppName()+"可使用的内存太小，使用中可能会不稳定，请更换手机再使用.", Toast.LENGTH_LONG);
//            }
//        }

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

        //关闭悬浮窗
        FloatActionController.getInstance().stopMonkServer(this);

}

    @Override
    public String getViewName() {

        if (mLlReport.isActivated()) {
            return mReportFragment.getUmEventName();
        } else if (mLlCommunity.isActivated()) {
            return mCommunityFragment.getUmEventName();
        } else if (mLlCenter.isActivated()) {
            return mCenterFragment.getUmEventName();
        }
        return super.getViewName();
    }

    //---------------------------------------------------------------------------------------
    private void initFragment() {
        mReportFragment = new ParentReportFragment();
        mCommunityFragment = new CommunityFragment();
        mCenterFragment = new ParentCenterFragment();

        mainFragments.add(mReportFragment);
        mainFragments.add(mCommunityFragment);
        mainFragments.add(mCenterFragment);
    }

    private void initViews() {
        mContext = this;
        mLlTeam = (LinearLayout) findViewById(R.id.ll_team);
        mLlReport = (LinearLayout) findViewById(R.id.ll_report);
        //ImageView mIvReport = (ImageView) findViewById(R.id.iv_report);
        //TextView mTvReport = (TextView) findViewById(R.id.tv_report);
        mLlCommunity = (LinearLayout) findViewById(R.id.ll_community);
        //ImageView mIvCommunity = (ImageView) findViewById(R.id.iv_community);
        //TextView mTvCommunity = (TextView) findViewById(R.id.tv_community);
        mLlCenter = (LinearLayout) findViewById(R.id.ll_center);
        //ImageView mIvCenter = (ImageView) findViewById(R.id.iv_center);
        mTvCenterMessage = (TextView) findViewById(R.id.tv_center_message);
        //TextView mTvCenter = (TextView) findViewById(R.id.tv_center);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_fl, mReportFragment).
                add(R.id.container_fl, mCommunityFragment).
                add(R.id.container_fl, mCenterFragment).
                hide(mCommunityFragment).hide(mCenterFragment).
                commit();

        //setHead();
        mLlReport.setActivated(true);

        for (int i = 0; i < mLlTeam.getChildCount(); i++) {
            LinearLayout child = (LinearLayout) mLlTeam.getChildAt(i);
            final int finalI = i;
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchFragment(finalI);
                }
            });
        }
    }

    private void initData() {
        EventBus.getDefault().register(this);
        //        boolean reportStatus = GlobalData.isHasLoadReportStatus();
        //        LogUtils.i("reportStatus= "+reportStatus);

        //建立缓存目录
        FileUtil.createPath(mContext, DirUtils.getExternalDir(AppConst.IMAGE_CACHE_DIR));
        if (AccountUtils.getLoginUser() != null) {
            //AccountUtils.checkJionSchoolClass(mContext);
            umengAddAccount();
        }
        //更新注册赠送数据
        ProductUtil.updateRegisterReward();
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
        for (int i = 0; i < mLlTeam.getChildCount(); i++) {
            LinearLayout child = (LinearLayout) mLlTeam.getChildAt(i);
            if (position == i) {
                child.setActivated(true);
            } else {
                child.setActivated(false);
            }
        }
        Fragment showedFragment = mainFragments.get(position);
        showFragment(showedFragment);
        mCurPosition = position;
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
            if (AppConst.ACTION_START_WAITUPLOAD.equals(intent.getAction())) {
                //mWaitWorkAdapter.notifyDataSetChanged();
                startActivity(new Intent(mContext, WaitUploadActivity.class));
            } else if (AppConst.ACTION_APPLICATION_EXIT.equals(intent.getAction())) {
                String msg = intent.getStringExtra("msg");
                if (!TextUtils.isEmpty(msg))
                    ToastUtils.showToastCenter(mContext, msg);
                mLlTeam.postDelayed(new Runnable() {
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
                //setNewMsgStatus(ishavenew);
                queryIsexistNewMsg();
            }
        });
        MessageUtils.getInstance().registerNewmsgBrd();
        //查询是否有新的消息
        queryIsexistNewMsg();
    }


    private void queryIsexistNewMsg() {
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo == null || TextUtils.isEmpty(parentInfo.getParentId())) {
            return;
        }

        new MessageModel().queryNewMsg(parentInfo.getParentId(), new RequestListener<ExistNewMessage>() {
            @Override
            public void onSuccess(ExistNewMessage msg) {
                LogUtils.i("queryIsexistNewMsg onSuccess");
                if (msg == null) {
                    return;
                }
                //新增未读消息数量
                //new BadgeManager(mContext).resetBadge(msg.getCount());
                //通知个人中心更新未读消息数量
                EventBus.getDefault().post(new ParentCenterEvent(ParentCenterEvent.TYPE_MSG_COUNT, msg.getCount()));
                //setNewMsgStatus(msg.isWdxx());
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ParentCenterEvent event) {
        // 收到本地消息后，更新用户信息
        if (event.getType() == ParentCenterEvent.TYPE_MSG_COUNT) {
            mMsgCount = event.getUnReadCount();
        } else if (event.getType() == ParentCenterEvent.TYPE_MSG_DEC) {
            mMsgCount--;
        } else if (event.getType() == ParentCenterEvent.TYPE_MSG_ADD) {
            mMsgCount++;
        }
        showMsgCount();
    }

    private void showMsgCount() {
        LogUtils.i("mMsgCount =" + mMsgCount);
        if (mMsgCount > 99) {
            mTvCenterMessage.setVisibility(View.VISIBLE);
            mTvCenterMessage.setText( "99+");
        } else if (mMsgCount > 0) {
            mTvCenterMessage.setVisibility(View.VISIBLE);
            mTvCenterMessage.setText(String.valueOf(mMsgCount));
        } else {
            mTvCenterMessage.setVisibility(View.INVISIBLE);
        }
    }

//    @Subscribe
//    public void receive(JoinClassEvent event) {
//        LoginUtils.queryTutorClass();
//    }


//    @Subscribe
//    public void receive(GotoCreateWorkEvent event) {
//        Intent intent = new Intent(mContext, CreateWorkActivity.class);
//        startActivity(intent);
//    }

    @Subscribe
    public void receive(ServerUpgradingEvent event) {
        AppLog.d("event = " + event );
        finish();
    }

    private void umengAddAccount() {
        //友盟账号统计
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo != null &&  parentInfo.getParentId().length() > 10) {
            String uid = parentInfo.getParentId().substring(10);
            MobclickAgent.onProfileSignIn(uid);
        }
    }

    public String getUmEventName() {
        return "parent_main";
    }

    //--------------------------------------------------------------------------------------
    //增加浮层提示
    private NewbieGuideManager mGuideManager;
    //private int guideViewIndex = 0;

    private void initCommonGudie() {
        String cname = MainActivity.class.getName();

        //没有显示过 且 手机号是空的 提示
        if ( NewbieGuideManager.isNeverShowed( this, cname, "1")) {
            mGuideManager = new NewbieGuideManager( this, cname, "1");
            mGuideManager.showWithListener(this);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addGuideView();
                }
            }, 2000);
        }
    }

    private void addGuideView() {

        final NewbieGuide mNewbieGuide = mGuideManager.getNewbieGuide();
        //高亮区域 必须要有高亮区域
        //mNewbieGuide.addHighLightView( findViewById(R.id.view_null), HoleBean.TYPE_CIRCLE);

        //
        mNewbieGuide.addImageView( R.drawable.ribaogao_yindao01, NewbieGuide.CENTER, get(90), get(361), get(90));

        //指示图
        mNewbieGuide.addIndicateImg(R.drawable.ribaogao_yindao02, NewbieGuide.CENTER, get(180), get(277), get(165));

        //我知道了
        mNewbieGuide.addBtnImage(R.drawable.zhidaola_heibai, NewbieGuide.CENTER, get(60 + 180+165), get(93), get(46));

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
