package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteSuccessEvent;
import com.tsinghuabigdata.edu.ddmath.event.EditHeaderEvent;
import com.tsinghuabigdata.edu.ddmath.event.EditInfoEvent;
import com.tsinghuabigdata.edu.ddmath.event.JoinClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.JumpSuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateClassEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateUserPendantEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.UserInfoActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.view.UserPrivilegeView;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.BaseHeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import javax.annotation.Nonnull;


/**
 * 我的资料
 * Created by Administrator on 2017/9/4.
 */

public class MyInfoFragment extends MyBaseFragment implements View.OnClickListener {

    private LinearLayout userInfoLayout;
    private BaseHeadView mCivHead;

    private TextView        mItemMemberGrade;
    private TextView        mItemUserName;
    private TextView        mItemExamNumber;

    private TextView        mMemberGrade;
    private TextView        mExpireDate;
    private RelativeLayout leaveDayLayout;
    private TextView        leaveDayView;

    private Button          mBuyBtn;
    //private TextView        mTvName;
    //private TextView        mTvGender;
    private TextView        mTvReallyName;
    //private TextView        mTvEnterYear;
    private TextView        mTvExamNumber;
    //private TextView        mTvEdit;
    //private TextView        mTvModifyPassword;
    //private TextView        mTvQuit;
    private TextView        mMoreInfoView;
    private ImageView       gradeImageView;
    private UserPrivilegeView userPrivilegeView;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_info, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_info_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    private void initView(View root) {
        userInfoLayout = root.findViewById( R.id.layout_userinfo );
        mCivHead =  root.findViewById(R.id.bhv_headview);

        TextView mTvName =  root.findViewById(R.id.tv_name);
        mTvName.setVisibility(View.GONE);

        mItemMemberGrade = root.findViewById(R.id.tv_item_membergrade);
        mItemUserName = root.findViewById(R.id.tv_item_username);
        mItemExamNumber = root.findViewById(R.id.tv_item_examnumber);

        mMemberGrade =  root.findViewById(R.id.tv_member_grade);
        mExpireDate =  root.findViewById(R.id.tv_expire_date);
        leaveDayLayout = root.findViewById(R.id.layout_expire_leaveday);
        leaveDayView = root.findViewById(R.id.tv_expire_leaveday);
        mTvReallyName =  root.findViewById(R.id.tv_really_name);
        mTvExamNumber =  root.findViewById(R.id.tv_exam_number);

        mMoreInfoView =  root.findViewById(R.id.tv_more_info);
        userPrivilegeView = root.findViewById( R.id.upv_user_priviledge_view );
        gradeImageView = root.findViewById(R.id.iv_membergrade);
//        mMoreInfoView.setOnClickListener(this);
//        mCivHead.setOnClickListener(this);
//        mTvName.setOnClickListener(this);
        userInfoLayout.setOnClickListener(this);

        mBuyBtn = root.findViewById( R.id.btn_goto_buy );
        mBuyBtn.setOnClickListener(this);
    }

    private void initData() {
        showDetailInfo();
        loadUserInfo();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.layout_userinfo /*|| v.getId() == R.id.bhv_headview || v.getId() == R.id.tv_name*/){
            startActivity( new Intent( getContext(), UserInfoActivity.class));
        }else if( v.getId() == R.id.btn_goto_buy ){
            EventBus.getDefault().post(new JumpSuiteEvent(null));
        }
    }

    private void showDetailInfo() {
        UserDetailinfo userdetailInfo = AccountUtils.getUserdetailInfo();
        if (userdetailInfo == null) {
            return;
        }
        if( !isAdded() ) return;

        mCivHead.showHeadImage();
//        String name = userdetailInfo.getNickName();
//        if( TextUtils.isEmpty(name) ) name = userdetailInfo.getPhone();
//        mTvName.setText( name );
        //mTvEnterYear.setText(userdetailInfo.getSerial());
        mTvExamNumber.setText(userdetailInfo.getExamNumber());
        mTvReallyName.setText(userdetailInfo.getNickName());

        mBuyBtn.setVisibility( View.GONE );
        //2018/10/18 根据会员等级显示处理
        if( AppConst.MEMBER_SVIP == userdetailInfo.getVipLevel() ){
            //SVIP会员
            mMemberGrade.setText(getText(R.string.svip_member));
            //勋章
            gradeImageView.setVisibility( View.VISIBLE );
            gradeImageView.setImageResource( R.drawable.svip_big );
            //背景
            userInfoLayout.setBackgroundResource( R.drawable.bg_svip );
            //到期日期
            showExpireInfo( userdetailInfo.getExpire(), userdetailInfo.getVipRemainTime() );

            //
            int color = getResources().getColor(R.color.color_E2B95D);
            mItemMemberGrade.setTextColor(color);
            mItemUserName.setTextColor(color);
            mItemExamNumber.setTextColor(color);
            mMoreInfoView.setTextColor(color);

            color = getResources().getColor(R.color.color_CA9100);
            mMemberGrade.setTextColor(color);
            mTvExamNumber.setTextColor(color);
            mTvReallyName.setTextColor(color);
            mExpireDate.setTextColor(color);

        }else if( AppConst.MEMBER_VIP == userdetailInfo.getVipLevel() ){
            //VIP会员
            mMemberGrade.setText(getText(R.string.vip_member));
            //
            gradeImageView.setVisibility( View.VISIBLE );
            gradeImageView.setImageResource( R.drawable.vip_big );

            userInfoLayout.setBackgroundResource( R.drawable.bg_vip );
            mMoreInfoView.setTextColor( getResources().getColor(R.color.color_59AC75) );
            //到期日期
            showExpireInfo( userdetailInfo.getExpire(), userdetailInfo.getVipRemainTime() );
            //
            int color = getResources().getColor(R.color.color_6CA0FC);
            mItemMemberGrade.setTextColor(color);
            mItemUserName.setTextColor(color);
            mItemExamNumber.setTextColor(color);
            mMoreInfoView.setTextColor(color);

            color = getResources().getColor(R.color.color_5676E8);
            mMemberGrade.setTextColor(color);
            mTvExamNumber.setTextColor(color);
            mTvReallyName.setTextColor(color);
            mExpireDate.setTextColor(color);
        }else {
            //普通会员
            mMemberGrade.setText(getText(R.string.normal_member));
            mExpireDate.setVisibility( View.GONE );
            gradeImageView.setVisibility( View.GONE );
            leaveDayLayout.setVisibility( View.GONE );
            userInfoLayout.setBackgroundResource( R.drawable.bg_rect_white_opactity86_r4 );

            mBuyBtn.setVisibility( View.VISIBLE );

            mMoreInfoView.setTextColor( getResources().getColor(R.color.color_48B8FF) );

            //
            int color = getResources().getColor(R.color.color_999999);
            mItemMemberGrade.setTextColor(color);
            mItemUserName.setTextColor(color);
            mItemExamNumber.setTextColor(color);

            color = getResources().getColor(R.color.color_151515);
            mMemberGrade.setTextColor(color);
            mTvExamNumber.setTextColor(color);
            mTvReallyName.setTextColor(color);
            mExpireDate.setTextColor(color);
        }
    }

    private void showExpireInfo(long time, int remainday ){

        mExpireDate.setVisibility( View.VISIBLE );
        mExpireDate.setText(String.format( Locale.getDefault(),"%s到期", DateUtils.format(time,DateUtils.FORMAT_DATA)));

        //int leaveday = DateUtils.calculateNumberOfDays( time );
        leaveDayLayout.setVisibility( View.GONE );
        if( remainday>1 && remainday <= 7 ){
            leaveDayLayout.setVisibility( View.VISIBLE );
            leaveDayView.setText( String.format(Locale.getDefault(),"还有%d天到期", remainday));
        }else if( remainday == 1 ){
            leaveDayLayout.setVisibility( View.VISIBLE );
            leaveDayView.setText( "明天到期" );
        }else if( remainday == 0 ){
            leaveDayLayout.setVisibility( View.VISIBLE );
            leaveDayView.setText( "今天到期" );
        }
    }

    private void loadUserInfo(){
        //绑定成功后，更新本地的个人信息
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null) {
            return;
        }
        new LoginModel().queryUserdetailInfo(loginInfo.getAccessToken(), loginInfo.getAccountId(), new RequestListener<UserDetailinfo>() {
            @Override
            public void onSuccess(UserDetailinfo res) {
                if (res != null) {
                    AccountUtils.setUserdetailInfo(res);
                    EventBus.getDefault().post(new UpdateUserPendantEvent());
                    showDetailInfo();
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        isFinishing = true;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(BuySuiteSuccessEvent event) {
        AppLog.d(" receive event = " + event );
        loadUserInfo();
        //更新
        userPrivilegeView.loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(EditHeaderEvent event) {
        AppLog.d(" receive event = " + event );
        showDetailInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(EditInfoEvent event) {
        AppLog.d(" receive event = " + event );
        showDetailInfo();
    }

    @Subscribe
    public void receive(UpdateUserPendantEvent event){
        AppLog.d("receive event = " + event );
        mCivHead.showHeadImage();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(UpdateClassEvent event){
        AppLog.d(" receive event = " + event );
        showDetailInfo();
        userPrivilegeView.loadData();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(JoinClassEvent event){
        AppLog.d(" receive event = " + event );
        showDetailInfo();
        userPrivilegeView.loadData();
    }

    public String getUmEventName() {
        return "mycenter_userinfo";
    }



}
