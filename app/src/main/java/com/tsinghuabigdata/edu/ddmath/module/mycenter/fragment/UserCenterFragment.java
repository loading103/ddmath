package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.event.SyncShowRedpointEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageActivity;
import com.tsinghuabigdata.edu.ddmath.module.myscore.fragment.MyScoreFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DeviceUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 个人中心
 * Created by Administrator on 2017/9/4.
 */

public class UserCenterFragment extends MyBaseFragment implements View.OnClickListener {

    public final static int MODEL_MYINFO      = 0;
    public final static int MODEL_MYCLASS     = 1;

    public final static int MODEL_MYSTUDYBEAN = 2;
    public final static int MODEL_CARDEXC     = 3;
    public final static int MODEL_MYSCORE     = 4;
    public final static int MODEL_MYPOWER     = 5;

    public final static int MODEL_INVITE      = 6;
    public final static int MODEL_FEEDBACK    = 7;
    public final static int MODEL_ABOUT       = 8;

    public final static String NEW_TIPS = "new_content_tips";

    private LinearLayout   mLlMyMessage;
    private ImageView      mIvNewMessage;
    private LinearLayout   mLlItemList;
    private RelativeLayout mRlMyInfo;
    private RelativeLayout mRvInvite;
    private RelativeLayout mRlFeedback;

    private ImageView newImageView;
//    private TextView       mTvMyPower;
//    private TextView       mTvMyStudybean;
//    private TextView       mTvMyScore;

    private MainActivity         mainActivity;

    private MyInfoFragment       mMyInfoFragment;
    private CardExchangeFragment mCardExchangeFragment;
    private MyStudyBeanFragment  mMyStudyBeanFragment;
    private MyPowerFragment      mMyPowerFragment;
    private MyClassFragment      mMyClassFragment;
    private InviteFragment       mInviteFragment;
    private FeedbackFragment     mFeedbackFragment;
    private AboutFragment        mAboutFragment;
    private MyScoreFragment mMyScoreFragment;

    private List<MyBaseFragment> mFragments;
    private int                  mCurPosition;

    //    private MyBaseFragment mCurFragment;
    private HashMap<MyBaseFragment, Integer> mFragmentMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_user_center, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_user_center_phone, container, false);
        }
        initFragment();
        initView(root);
        initData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void initFragment() {
        mMyInfoFragment = new MyInfoFragment();
        mFragments = new ArrayList<>();
        mFragmentMap.put(mMyInfoFragment, MODEL_MYINFO);
        addToTransaction(mMyInfoFragment);
    }

    private void initView(View root) {
        mainActivity = (MainActivity) getActivity();
        mLlMyMessage =  root.findViewById(R.id.ll_my_message);
        mIvNewMessage =  root.findViewById(R.id.iv_new_message);
        mLlItemList =  root.findViewById(R.id.ll_item_list);
        mRlMyInfo =  root.findViewById(R.id.rl_my_info);
        mRlFeedback =  root.findViewById(R.id.rl_feedback);
        mRvInvite =  root.findViewById(R.id.rl_invite);
//        mTvMyPower =  root.findViewById(R.id.tv_my_power);
//        mTvMyStudybean =  root.findViewById(R.id.tv_my_studybean);
//        mTvMyScore = root.findViewById(R.id.tv_my_score);
        newImageView = root.findViewById(R.id.iv_has_newcontent);

        RelativeLayout layout = root.findViewById(R.id.rl_my_studybean);
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo != null && !detailinfo.enableBuySuite() ){
            layout.setVisibility(View.GONE);
        }

        for (int i = 0; i < mLlItemList.getChildCount(); i++) {
            final int finalI = i;
            mLlItemList.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchFragment(finalI);
                }
            });
        }
        mLlMyMessage.setOnClickListener(this);
        mRvInvite.setOnClickListener(this);
        mRlFeedback.setOnClickListener(this);
        mRlMyInfo.setActivated(true);

        //新功能提示
        boolean userview = PreferencesUtils.getBoolean(getContext(),NEW_TIPS,false);
        newImageView.setVisibility( userview?View.GONE:View.VISIBLE);
    }

    private void initData() {
        EventBus.getDefault().register(this);
//        queryTodayAbility();
//        queryUserScore();
//        queryStudyBean();
    }

//    //查询今日学力
//    private void queryTodayAbility() {
//        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
//        if (detailinfo != null) {
//            String studentId = detailinfo.getStudentId();
//            new UserCenterModel().queryTodayAbilityTask(studentId, new RequestListener<TodayStudyAbility>() {
//
//                @Override
//                public void onSuccess(TodayStudyAbility vo) {
//                    LogUtils.i("queryTodayAbility onSuccess");
//                    if (vo != null) {
//                        mTvMyPower.setText( String.valueOf(vo.getTotalStudyAbilityValue()) );
//                    }
//                }
//
//                @Override
//                public void onFail(HttpResponse<TodayStudyAbility> response, Exception ex) {
//                    LogUtils.i("queryTodayAbility onFail");
//                }
//            });
//        }
//    }

//    //查询总积分
//    private void queryUserScore() {
//        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
//        if (detailinfo != null) {
//            String studentId = detailinfo.getStudentId();
//
//            new UserCenterModel().queryUserScore(studentId, new RequestListener<UserScoreBean>() {
//
//                @Override
//                public void onSuccess(UserScoreBean bean) {
//                    if (bean != null) {
//                        mTvMyScore.setText(String.valueOf(bean.getTotalCredit()));
//                        AccountUtils.userScoreBean = bean;
//                        EventBus.getDefault().post( new ChangeScoreEvent() );
//                    }
//                }
//
//                @Override
//                public void onFail(HttpResponse<UserScoreBean> response, Exception ex) {
//                }
//            });
//        }
//    }

//    private void queryStudyBean() {
//        ProductUtil.updateLearnDou(new RequestListener<StudyBean>() {
//            @Override
//            public void onSuccess(StudyBean res) {
//                LogUtils.i("queryStudyBean success");
//                if (res != null) {
//                    mTvMyStudybean.setText( String.valueOf(res.getTotalBean()));
//                }
//            }
//
//            @Override
//            public void onFail(HttpResponse<StudyBean> response, Exception ex) {
//                LogUtils.i("queryStudyBean failed" + ex.getMessage());
//            }
//        });
//    }

    public void gotoFragment(int index) {
        switchFragment(index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_my_message:
                mainActivity.goActivity(MessageActivity.class);
                break;
            case R.id.iv_phone_number:
                dial();
                break;
            case R.id.rl_invite:
             switchFragment(6);
                break;
            case R.id.rl_feedback:
                switchFragment(7);
                newImageView.setVisibility( View.GONE );
                PreferencesUtils.putBoolean( getContext(), NEW_TIPS, true);
                break;
            default:
                break;
        }
    }

    private void dial() {
        if (DeviceUtils.hasSimCard(getActivity())) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:4009928918"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            ToastUtils.showShort(getActivity(), R.string.cannot_dial);
        }
    }

    private void switchFragment(int position) {
        if (mCurPosition == position) {
            return;
        }
        for (int i = 0; i < mLlItemList.getChildCount(); i++) {
            RelativeLayout child = (RelativeLayout) mLlItemList.getChildAt(i);
//            if (position == i) {
//                child.setActivated(true);
//            } else {
//                child.setActivated(false);
//            }
            if(position!=7  && position!=6 ){
                mRlFeedback.setActivated(false);
                mRvInvite.setActivated(false);
                if (position == i) {
                    child.setActivated(true);
                }else {
                    child.setActivated(false);
                }
            }else if(position==6){
                child.setActivated(false);
                mRlFeedback.setActivated(false);
                mRvInvite.setActivated(true);
            }else if(position==7){
                child.setActivated(false);
                mRlFeedback.setActivated(true);
                mRvInvite.setActivated(false);
            }

        }
        createFragment(position);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            MyBaseFragment fragment = mFragments.get(i);
            if (mFragmentMap.containsKey(fragment) && mFragmentMap.get(fragment) == position) {
                fragmentTransaction.show(fragment);
                fragment.setUserVisibleHint(true);
            } else {
                fragmentTransaction.hide(fragment);
                fragment.setUserVisibleHint(false);
            }
        }
        fragmentTransaction.commitAllowingStateLoss();
        mCurPosition = position;
    }

    private void createFragment(int position) {
        if (position == MODEL_CARDEXC && mCardExchangeFragment == null) {
            mCardExchangeFragment = new CardExchangeFragment();
            mFragmentMap.put(mCardExchangeFragment, MODEL_CARDEXC);
            addToTransaction(mCardExchangeFragment);
        } else if (position == MODEL_MYPOWER && mMyPowerFragment == null) {
            mMyPowerFragment = new MyPowerFragment();
            mFragmentMap.put(mMyPowerFragment, MODEL_MYPOWER);
            addToTransaction(mMyPowerFragment);
        } else if (position == MODEL_MYSTUDYBEAN && mMyStudyBeanFragment == null) {
            mMyStudyBeanFragment = new MyStudyBeanFragment();
            mFragmentMap.put(mMyStudyBeanFragment, MODEL_MYSTUDYBEAN);
            addToTransaction(mMyStudyBeanFragment);
        } else if (position == MODEL_MYCLASS && mMyClassFragment == null) {
            mMyClassFragment = new MyClassFragment();
            mFragmentMap.put(mMyClassFragment, MODEL_MYCLASS);
            addToTransaction(mMyClassFragment);
        } else if (position == MODEL_INVITE && mInviteFragment == null) {
            mInviteFragment = new InviteFragment();
            mFragmentMap.put(mInviteFragment, MODEL_INVITE);
            addToTransaction(mInviteFragment);
        } else if (position == MODEL_FEEDBACK && mFeedbackFragment == null) {
            mFeedbackFragment = new FeedbackFragment();
            mFragmentMap.put(mFeedbackFragment, MODEL_FEEDBACK);
            addToTransaction(mFeedbackFragment);
        } else if (position == MODEL_ABOUT && mAboutFragment == null) {
            mAboutFragment = new AboutFragment();
            mFragmentMap.put(mAboutFragment, MODEL_ABOUT);
            addToTransaction(mAboutFragment);
        } else if (position == MODEL_MYSCORE && mMyScoreFragment == null) {
            mMyScoreFragment = new MyScoreFragment();
            mFragmentMap.put(mMyScoreFragment, MODEL_MYSCORE);
            addToTransaction(mMyScoreFragment);
        }
    }

    private void addToTransaction(MyBaseFragment fragment) {
        mFragments.add(fragment);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_user_center, fragment).commitAllowingStateLoss();
    }

//    @Subscribe
//    public void receive(ChangeAbilityEvent event) {
//        AppLog.d(" receive event = " + event );
//        queryTodayAbility();
//    }

//    @Subscribe
//    public void receive(UpdateScoreEvent event){
//        AppLog.d(" receive event = " + event );
//        queryUserScore();
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receive(SyncShowStudybeanEvent event) {
//        mTvMyStudybean.setText(String.valueOf(event.getTotalBean()));
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receive(SyncShowStudybeanFromDialogEvent event) {
//        mTvMyStudybean.setText(String.valueOf(event.getTotalBean()));
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(SyncShowRedpointEvent event) {
        if (event.isHasNew()) {
            mIvNewMessage.setVisibility(View.VISIBLE);
        } else {
            mIvNewMessage.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public String getUmEventName() {
        if( mLlItemList!=null )
            for (int i = 0; i < mLlItemList.getChildCount(); i++) {
                RelativeLayout child = (RelativeLayout) mLlItemList.getChildAt(i);
                if ( child.isActivated() ) {

                    for (int j = 0; j < mFragments.size(); j++) {
                        MyBaseFragment fragment = mFragments.get(j);
                        if (mFragmentMap.containsKey(fragment) && mFragmentMap.get(fragment) == i) {
                            return fragment.getUmEventName();
                        }
                    }
                }
            }
        return "mycenter_userinfo";     //默认我的资料
    }
}
