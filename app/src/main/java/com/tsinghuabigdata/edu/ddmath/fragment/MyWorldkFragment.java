package com.tsinghuabigdata.edu.ddmath.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.MVPModel.MyWorldModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.AdverPageAdapter;
import com.tsinghuabigdata.edu.ddmath.bean.FirstGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.AssignNewWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.ChangeClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.CorrectWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.CreateWorkReportEvent;
import com.tsinghuabigdata.edu.ddmath.event.DownloadWeekEvent;
import com.tsinghuabigdata.edu.ddmath.event.ErrorReviseEvent;
import com.tsinghuabigdata.edu.ddmath.event.LookMsgDetailEvent;
import com.tsinghuabigdata.edu.ddmath.event.LookWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.RecallWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.RefreshMyworldEvent;
import com.tsinghuabigdata.edu.ddmath.event.TopMessageListEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.UploadMyWorkEvent;
import com.tsinghuabigdata.edu.ddmath.module.first.view.UserModelNavView;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageDetailActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.activity.KnowledgeActivity;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.view.KnowDiagnoseButtonView;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.StudyCheatActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.RollTextView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;

import static com.tsinghuabigdata.edu.ddmath.util.ActivityUtil.goActivity;


/**
 * 我的世界
 * Created by Administrator on 2017/11/22.
 */

public class MyWorldkFragment extends MyBaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener/*, NewbieGuide.OnGuideChangedListener*/, UserModelNavView.UserWorkStatusListener {

    private Context                mContext;
//    private RelativeLayout         mRlMyWorldContent;
//    private View                   mViewMiddle;
    private RelativeLayout         mRlBanner;
    private ImageView              mIvAdver;
    private ViewPager              mViewPager;
    private LinearLayout           mIndicatorContainer;
    private ImageView              mIvPlane2;
    private ImageView              mIvPlane1;
    private KnowDiagnoseButtonView diagnoseButtonView;

    private UserModelNavView userModelNavView;

    //private RelativeLayout mRlMessageBanner;
    //private TextView       mTvMessage;
    private RollTextView           rollTextView;
//    private ImageView              mIvStudycheat;
    private LoadingPager           mLoadingPager;

    private AutoScrollTask    mAutoScrollTask;
//    private AdverPageAdapter  mAdverPageAdapter;
    private List<MessageInfo> mAdverBeanList;
    private HashMap<String, FirstGloryRank> mGloryMap = new HashMap<>();

    private Handler mHandler = new Handler();
    private String studentId;
//    private String mRecentExamId;

    private float   rawXAdver;
    private float   rawYAdver;
    private long    downTimeAdver;
    private boolean isPlayAnimation;
    //private boolean hasMyWork = true;
    private float mGlory;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        mContext = getActivity();
        if (!GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_world_phone, container, false);
        } else if (small()) {
            root = inflater.inflate(R.layout.fragment_my_world_small, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_world, container, false);
        }
        initView(root);
        initData();
        return root;
    }

    @Override
    public void dealWorkStatusCallback(boolean succ, boolean init, Exception ex) {
        if( !succ ){
//            if (init) {
//                //    initCommonGudie();
//                //queryErrorReviseStatus(true);
//            }
//        }else{
            if (init) {
                mLoadingPager.showFault(ex);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(userModelNavView!=null){
            userModelNavView.queryWorkStatus(false);
            userModelNavView.queryCustomPlanStatus(false);
        }
    }

    //----------------------------------------------------------------------------------------------
    private void initView(View root) {
        RelativeLayout mRlMyWorldContent =  root.findViewById(R.id.rl_my_world_content);
//        mViewMiddle = root.findViewById(R.id.view_middle);
        mRlBanner =  root.findViewById(R.id.rl_banner);
        mIvAdver =  root.findViewById(R.id.iv_adver);
        mViewPager =  root.findViewById(R.id.viewPager);
        mIndicatorContainer =  root.findViewById(R.id.indicator_container);
        mIvPlane2 =  root.findViewById(R.id.iv_plane2);
        mIvPlane1 =  root.findViewById(R.id.iv_plane1);
        diagnoseButtonView =  root.findViewById(R.id.kdbv_diagnoseview);

        userModelNavView = root.findViewById(R.id.umn_modelnav_view);
        userModelNavView.setUserWorkStatusListener( this );

        rollTextView =  root.findViewById(R.id.rtv_message);
        rollTextView.setEnabled( false );       //不可点击
        ImageView mIvStudycheat =  root.findViewById(R.id.iv_studycheat);
        mLoadingPager =  root.findViewById(R.id.loading_pager);

        mLoadingPager.setTargetView(mRlMyWorldContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryGlory();
            }
        });
        mIvPlane1.setOnClickListener(this);
        mIvPlane2.setOnClickListener(this);
        diagnoseButtonView.setOnClickListener(this);
        mIvStudycheat.setOnClickListener(this);

    }

    private boolean small() {
        int screenWidthDp = WindowUtils.getScreenWidthDp(mContext);
        return screenWidthDp < AppConst.NAVI_WIDTH_PAD + 1100;
    }

    private void initData() {
        createLoginInfo();
        mLoadingPager.showTarget();
        queryGlory();
        EventBus.getDefault().register(this);
    }


    //查询登录信息
    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            studentId = detailinfo.getStudentId();
        }
    }

    /**
     * 查询荣耀值
     */
    public void queryGlory() {
        String classIds = AccountUtils.getFormalClassIds();
        new MyWorldModel().queryFirstGloryRank(classIds, studentId, new RequestListener<List<FirstGloryRank>>() {
            @Override
            public void onSuccess(List<FirstGloryRank> list) {
                userModelNavView.queryWorkStatus( true );
                mGloryMap.clear();
                if (list == null || list.size() == 0) {
                    diagnoseButtonView.setKnowRate(0);
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    FirstGloryRank vo = list.get(i);
                    if (vo != null) {
                        mGloryMap.put(vo.getClassId(), vo);
                    }
                }
                MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
                if (currentClassInfo != null && !TextUtils.isEmpty(currentClassInfo.getClassId())) {
                    FirstGloryRank firstGloryRank = mGloryMap.get(currentClassInfo.getClassId());
                    if (firstGloryRank != null) {
                        //String glory = String.valueOf(Math.round(firstGloryRank.getGlory() * 1000));
                        mGlory = firstGloryRank.getGlory();
                        AccountUtils.setUserGlory( mGlory );
                        diagnoseButtonView.setKnowRate(mGlory);
                    }
                }
            }

            @Override
            public void onFail(HttpResponse<List<FirstGloryRank>> response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }

    /**
     * 切换班级后更新当前班级荣耀值
     */
    private void showGlory() {
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if ( classInfo != null) {
            String classId = classInfo.getClassId();
            FirstGloryRank firstGloryRank = mGloryMap.get(classId);
            if (firstGloryRank != null) {
                //String glory = String.valueOf(Math.round(firstGloryRank.getGlory() * 1000));
                diagnoseButtonView.setKnowRate(firstGloryRank.getGlory());
            }
        }
    }

    /**
     * 初始化首页消息轮播图
     */
    private void initAdaver(List<MessageInfo> bannerList) {
        if (bannerList == null || bannerList.size() == 0) {
            return;
        }
        if (bannerList.size() > 4) {
            mAdverBeanList = bannerList.subList(0, 4);
        } else {
            mAdverBeanList = bannerList;
        }
        mRlBanner.setVisibility(View.VISIBLE);
        mIvPlane1.setVisibility(View.VISIBLE);
        if (mAdverBeanList.size() == 1) {
            mViewPager.setVisibility(View.INVISIBLE);
            initImageView();
        } else {
            mIvAdver.setVisibility(View.INVISIBLE);
            initViewPager();
        }
    }

    private void initImageView() {
        final MessageInfo messageInfo = mAdverBeanList.get(0);
        if( messageInfo == null ) return;

        String url = messageInfo.getPicUrl();
        PicassoUtil.displayImageIndetUrl(url, mIvAdver);
        mIvAdver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageDetailActivity.class);
                intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
                startActivity(intent);
                MobclickAgent.onEvent(getContext(), "myworld_ad");
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViewPager() {
        // 设置ViewPager的Adapter信息
        AdverPageAdapter mAdverPageAdapter = new AdverPageAdapter(mContext, mAdverBeanList);
        mViewPager.setAdapter(mAdverPageAdapter);
        // 移除mIndicatorContainer里面的所有的视图
        int index = 20 - 20 % mAdverBeanList.size();
        //int index = Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % mAdverBeanList.size();
        mViewPager.setCurrentItem(index);
        for (int i = 0; i < mAdverBeanList.size(); i++) {
            ImageView indicator = new ImageView(mContext);
            int diameter;
            int margin;
            if (GlobalData.isPad()) {
                diameter = DensityUtils.dp2px(mContext, 10);
                margin = DensityUtils.dp2px(mContext, 6);
            } else {
                diameter = DensityUtils.dp2px(mContext, 5);
                margin = DensityUtils.dp2px(mContext, 3);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(diameter, diameter);
            params.leftMargin = margin;
            if (GlobalData.isPad()) {
                indicator.setImageResource(R.drawable.selector_banner_dot);
            } else {
                indicator.setImageResource(R.drawable.selector_banner_dot);
            }
            mIndicatorContainer.addView(indicator, params);
            // 默认第一个是选中的效果
            if (i == 0) {
                indicator.setSelected(true);
            } else {
                indicator.setSelected(false);
            }
        }

        // 自动轮播
        if (mAutoScrollTask == null) {
            mAutoScrollTask = new AutoScrollTask();
        }
        // 设置viewPager滚动的监听
        mViewPager.setOnPageChangeListener(this);
        // 按下去的时候停止轮播
        mViewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 停止轮播
                        if (mAutoScrollTask != null) {
                            mAutoScrollTask.stop();
                        }
                        rawXAdver = event.getRawX();
                        rawYAdver = event.getRawY();
                        downTimeAdver = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        // 开始轮播
                        if (mAutoScrollTask != null) {
                            mAutoScrollTask.start();
                        }
                        float rawX1 = event.getRawX();
                        float rawY1 = event.getRawY();
                        if (Math.abs(rawX1 - rawXAdver) < 10 && Math.abs(rawY1 - rawYAdver) < 10 && (System.currentTimeMillis() - downTimeAdver) < 500) {
                            Intent intent = new Intent(mContext, MessageDetailActivity.class);
                            MessageInfo messageInfo = mAdverBeanList.get(mViewPager.getCurrentItem() % mAdverBeanList.size());
                            intent.putExtra(AppConst.MSG_ROWKEY, messageInfo.getRowKey());
                            startActivity(intent);

                            MobclickAgent.onEvent(getContext(), "myworld_ad");
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }

        });
        mAutoScrollTask.start();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < mAdverBeanList.size(); i++) {
            ImageView imageView = (ImageView) mIndicatorContainer.getChildAt(i);
            if (i == position % mAdverBeanList.size()) {
                imageView.setSelected(true);
            } else {
                imageView.setSelected(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAutoScrollTask != null) {
            mAutoScrollTask.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAutoScrollTask != null) {
            mAutoScrollTask.stop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_plane1:
                vanishAnimation();
                break;
            case R.id.iv_plane2:
                appearAnimation();
                break;
            case R.id.kdbv_diagnoseview:
                Intent parentintent = new Intent(mContext, KnowledgeActivity.class);
                parentintent.putExtra(KnowledgeActivity.PARAM_ALLACCURACY, mGlory);
                startActivity(parentintent);
                break;
            case R.id.iv_studycheat:
                goActivity(getActivity(), StudyCheatActivity.class);
                break;
            default:
                break;
        }
    }

//    private void jumpWorkList() {
//        userWorkStatusView.jumpWorkList( true );
//    }

    /**
     * 轮播图消失动画
     */
    private void vanishAnimation() {
        if (isPlayAnimation) {
            return;
        }
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        int leftDis = mIvPlane1.getLeft() + mIvPlane1.getWidth();
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, -leftDis, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, 0f);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(2000);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPlayAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvPlane1.setVisibility(View.INVISIBLE);
                mIvPlane2.setVisibility(View.VISIBLE);
                mRlBanner.setVisibility(View.INVISIBLE);
                isPlayAnimation = false;
                if (mAutoScrollTask != null) {
                    mAutoScrollTask.stop();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AlphaAnimation bannerAlphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        bannerAlphaAnimation.setDuration(2000);
        mIvPlane1.startAnimation(animationSet);
        mRlBanner.startAnimation(bannerAlphaAnimation);
    }


    /**
     * 轮播图出现动画
     */
    private void appearAnimation() {
        if (isPlayAnimation) {
            return;
        }
        int leftDis = mIvPlane2.getLeft() - mIvPlane1.getLeft();
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, -leftDis, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, 0f);
        translateAnimation.setDuration(2000);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isPlayAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvPlane1.setVisibility(View.VISIBLE);
                mIvPlane2.setVisibility(View.INVISIBLE);
                mRlBanner.setVisibility(View.VISIBLE);
                isPlayAnimation = false;
                if (mAutoScrollTask != null) {
                    mAutoScrollTask.start();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AlphaAnimation bannerAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        bannerAlphaAnimation.setDuration(2000);

        mIvPlane2.startAnimation(translateAnimation);
        mRlBanner.startAnimation(bannerAlphaAnimation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public class AutoScrollTask implements Runnable {
        /**
         * 开始滚动
         */
        public void start() {
            // 得到一个主线程的handler
            mHandler.removeCallbacks(this);
            mHandler.postDelayed(this, 5000);
        }

        /**
         * 停止滚动
         */
        public void stop() {
            // 移除任务
            mHandler.removeCallbacks(this);
        }

        @Override
        public void run() {
            int currentItem = mViewPager.getCurrentItem();
            currentItem++;
            mViewPager.setCurrentItem(currentItem);
            // 递归
            start();
        }
    }



    /*@Subscribe
    public void receive(LastWorkTypeEvent event) {
        if (mLookGuideDialog == null) {
            mLookGuideDialog = new LookGuideDialog(mContext);
        }
        mLookGuideDialog.setLastCheckWork(event.isCheckWork());
    }*/

    //private MyWorkEvent myWorkEvent;
//    @Subscribe
//    public void receive(MyWorkEvent event) {
//        if( myWorkEvent == null )
//            myWorkEvent = event;
//        else
//            myWorkEvent.combine( event );
//        userWorkStatusView.setHasMyWork( myWorkEvent );
//    }

    //以下是事件处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(UpdateClassEvent event) {
        AppLog.d("event = " + event );
        mLoadingPager.showLoading();
        queryGlory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ChangeClassEvent event) {
        AppLog.d("event = " + event );
        //myWorkEvent = null;     //重置
        showGlory();
        //queryMyWork();
        //userWorkStatusView.queryRecentWorkStatus(false, mViewLookWork);
        userModelNavView.queryWorkStatus(false);
    }

    @Subscribe
    public void receive(RefreshMyworldEvent event) {
        AppLog.d("event = " + event );
        queryGlory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(TopMessageListEvent event) {
        initAdaver(event.getBannerList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(LookMsgDetailEvent event) {
        if (!TextUtils.isEmpty(event.getRowKey())) {
            rollTextView.queryMessageBannerByKey(event.getRowKey());
        }
    }

    //
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(LookWorkEvent event) {
        AppLog.d("event = " + event );
        userModelNavView.queryWorkStatus(false);
    }

    //布置作业
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(AssignNewWorkEvent event) {
        AppLog.d("event = " + event );
        //rollTextView.updateMessage(event.getMessageInfo(), MessageInfo.ASSGIN_WORK);
        //userWorkStatusView.queryRecentWorkStatus(false, mViewLookWork);
        userModelNavView.queryWorkStatus(false);
    }

    //批阅作业
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(CorrectWorkEvent event) {
        AppLog.d("event = " + event );
        //rollTextView.updateMessage(event.getMessageInfo(), MessageInfo.COREECT_WORK);
        userModelNavView.queryCustomPlanStatus(false);
        userModelNavView.queryWorkStatus(false);
    }

    //作业报告生成
    @Subscribe
    public void receive(CreateWorkReportEvent event){
        AppLog.d("event = " + event );
        userModelNavView.queryWorkStatus(false);
    }

    //撤回作业
    @Subscribe
    public void receive(RecallWorkEvent event){
        AppLog.d("event = " + event );
        userModelNavView.queryWorkStatus(false);
    }

    //提交作业
    @Subscribe
    public void receive(UploadMyWorkEvent event) {
        AppLog.d("event = " + event );
        userModelNavView.queryWorkStatus(false);
    }

    @Subscribe
    public void receive(ErrorReviseEvent event) {
        AppLog.d("event = " + event );
        userModelNavView.queryCustomPlanStatus(false);
    }

    @Subscribe
    public void receive(DownloadWeekEvent event) {
        AppLog.d("event = " + event );
        userModelNavView.queryCustomPlanStatus(false);
    }

    @Subscribe
    public void receive(BuySuiteEvent event) {
        AppLog.d("event = " + event );
        userModelNavView.queryCustomPlanStatus(false);
    }

    //--------------------------------------------------------------------------------------
    //增加浮层提示
//    private NewbieGuideManager mGuideManager;
//    private int guideViewIndex = 0;
//
//    private void initCommonGudie() {
//        String cname = MainActivity.class.getName();
//        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
//        if (detailinfo == null)
//            return;
//
//        //没有显示过 且 手机号是空的 提示
//        if ( NewbieGuideManager.isNeverShowed(getActivity(), cname, "_v2")) {
//            mGuideManager = new NewbieGuideManager(getActivity(), cname, "_v2");
//            mGuideManager.showWithListener(this);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    addGuideView();
//                }
//            }, 2500);
//        }
//    }
//
//    private void addGuideView() {
//        if (!mRlMyWorldContent.isShown())
//            return;
//
//        final NewbieGuide mNewbieGuide = mGuideManager.getNewbieGuide();
//        if( guideViewIndex == 0 ){
//            Rect rect = new Rect();
//            //作业诊断
//            userWorkStatusView.diagnoseLayout.getGlobalVisibleRect(rect);
//            mNewbieGuide.addHighLightView(userWorkStatusView.diagnoseLayout, HoleBean.TYPE_CIRCLE);
//
//            if (GlobalData.isPad()) {
//                //指示图
//                int offx = rect.right;
//                int offy = rect.top + rect.height()/2 - get(130+24);
//                if( offy <= 0 ) offy = 1;
//                mNewbieGuide.addIndicateImg(R.drawable.img_step1zhenduan, offx, offy, get(440), get(130));
//            } else {
//                //指示图
//                int offx = rect.right;
//                int offy = rect.top + rect.height()/2 - get(86+16);
//                if( offy <= 0 ) offy = 1;
//                mNewbieGuide.addIndicateImg(R.drawable.img_step1zhenduan, offx, offy, get(292), get(86));
//            }
//
//            //收集错题
//            userWorkStatusView.collegeLayout.getGlobalVisibleRect(rect);
//            mNewbieGuide.addHighLightView(userWorkStatusView.collegeLayout, HoleBean.TYPE_CIRCLE);
//
//            if (GlobalData.isPad()) {
//                //指示图
//                int offx = rect.right - get(18);
//                int offy = rect.top + get(18) - get(90+24);
//                if( offy <= 0 ) offy = 1;
//                mNewbieGuide.addIndicateImg(R.drawable.img_step1shouji, offx, offy, get(390), get(90));
//
//                //下一步
//                offx = offx + get(390-146) / 2;
//                offy = offy + get(90) + get(40);
//                mNewbieGuide.addBtnImage(R.drawable.btn_next_step, offx, offy, get(180), get(90));
//            } else {
//                //指示图
//                int offx = rect.right - get(8);
//                int offy = rect.top + get(8) - get(60+16);
//                if( offy <= 0 ) offy = 1;
//                mNewbieGuide.addIndicateImg(R.drawable.img_step1shouji, offx, offy, get(260), get(60));
//
//                //下一步
//                offx = offx + get(260-93) / 2;
//                offy = offy + get(60) + get(16);
//                mNewbieGuide.addBtnImage(R.drawable.btn_next_step, offx, offy, get(93), get(46));
//            }
//
//        }else if( guideViewIndex == 1 ){
//            Rect rect = new Rect();
//            mViewLookWork.getGlobalVisibleRect(rect);
//
//            //高亮区域
//            mNewbieGuide.addHighLightView(mViewLookWork.getMainLayout(), HoleBean.TYPE_ROUNDRECT, mViewLookWork.getMainLayout().getHeight()/2 );
//            mNewbieGuide.addHighLightView(mViewErrorCorrect.getMainLayout(), HoleBean.TYPE_ROUNDRECT,mViewLookWork.getMainLayout().getHeight()/2 );
//
//            if (GlobalData.isPad()) {
//                //指示图
//                int offx = rect.left - get(520) + get(2) ;
//                int offy = rect.bottom - get(280)/2 ;
//                mNewbieGuide.addIndicateImg(R.drawable.img_step2, offx, offy, get(520), get(280));
//
//                //下一步
//                offx = offx + get(520-60) / 2;
//                offy = offy + get(280) + get(50);
//                mNewbieGuide.addBtnImage(R.drawable.btn_next_step, offx, offy, get(180), get(90));
//            } else {
//                //指示图
//                int offx = rect.left - get(266) + get(2) ;
//                int offy = rect.bottom - get(144)/2 - get(10);
//                mNewbieGuide.addIndicateImg(R.drawable.img_step2, offx, offy, get(266), get(144));
//
//                //下一步
//                offx = offx + get(266-60) / 2;
//                offy = offy + get(144) + get(20);
//                mNewbieGuide.addBtnImage(R.drawable.btn_next_step, offx, offy, get(93), get(46));
//            }
//        }else if( guideViewIndex == 2 ){
//            Rect rect = new Rect();
//            mViewErrorPractice.getGlobalVisibleRect(rect);
//
//            //高亮区域
//            mNewbieGuide.addHighLightView(mViewErrorPractice.getMainLayout(), HoleBean.TYPE_ROUNDRECT, mViewErrorPractice.getMainLayout().getHeight()/2 );
//            mNewbieGuide.addHighLightView(mViewTrainExcellent.getMainLayout(), HoleBean.TYPE_ROUNDRECT, mViewTrainExcellent.getMainLayout().getHeight()/2 );
//
//            if (GlobalData.isPad()) {
//                //指示图
//                int offx = rect.left - get(460) ;
//                int offy = rect.bottom - get(160)-get(16);
//                mNewbieGuide.addIndicateImg(R.drawable.img_step3, offx, offy, get(460), get(160));
//
//                //知道了
//                offx = offx + get(460-80) / 2;
//                offy = offy + get(160) + get(20);
//                mNewbieGuide.addBtnImage(R.drawable.ic_i_see, offx, offy, get(180), get(90));
//            } else {
//                //指示图
//                int offx = rect.left - get(232) ;
//                int offy = rect.bottom - get(84)-get(16);
//                mNewbieGuide.addIndicateImg(R.drawable.img_step3, offx, offy, get(232), get(84));
//
//                //知道了
//                offx = offx + get(232-80) / 2;
//                offy = offy + get(84);
//                mNewbieGuide.addBtnImage(R.drawable.ic_i_see, offx, offy, get(92), get(45));
//            }
//        }else {
//            return;
//        }
//        mGuideManager.show();
//    }
//
//    private int get(int dis) {
//        return DensityUtils.dp2px(mContext, dis);
//    }
//
//    @Override
//    public void onShowed() {
//        guideViewIndex++;
//    }
//
//    @Override
//    public void onRemoved() {
//        addGuideView();
//    }


}
