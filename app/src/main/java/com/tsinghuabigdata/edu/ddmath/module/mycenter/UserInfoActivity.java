package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialog;
import com.tsinghuabigdata.edu.ddmath.event.EditHeaderEvent;
import com.tsinghuabigdata.edu.ddmath.event.EditInfoEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateUserPendantEvent;
import com.tsinghuabigdata.edu.ddmath.module.badge.BadgeManager;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.module.login.ModifyPassActivity;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.UploadManager;
import com.tsinghuabigdata.edu.ddmath.module.myscore.adapter.UserPendantAdapter;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.BaseHeadView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 用户信息界面
 */
public class UserInfoActivity extends RoboActivity implements View.OnClickListener{

    @ViewInject(R.id.toolbar)
    private WorkToolbar mWorktoolbar;

//    @ViewInject(R.id.main_layout)
//    private LinearLayout mainLayout;
    @ViewInject(R.id.civ_head)
    private BaseHeadView mCivHead;
    @ViewInject(R.id.gv_pandent)
    private GridView pendantGridView;

    @ViewInject(R.id.tv_nickname)
    private TextView        mNickNameView;
    @ViewInject(R.id.tv_usergrade)
    private TextView        mMemberGrade;
    @ViewInject(R.id.tv_sex)
    private TextView        mTvGender;
    @ViewInject(R.id.tv_really_name)
    private TextView        mTvReallyName;
    @ViewInject(R.id.tv_enter_year)
    private TextView        mTvEnterYear;
    @ViewInject(R.id.tv_exam_number)
    private TextView        mTvExamNumber;
    @ViewInject(R.id.tv_mail_adress)
    private TextView        mMailAddr;

    @ViewInject(R.id.tv_edit)
    private TextView        mTvEdit;
    @ViewInject(R.id.tv_modify_password)
    private TextView        mTvModifyPassword;
    @ViewInject(R.id.tv_quit)
    private TextView        mTvQuit;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;
        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_my_info);
        } else {
            setContentView(R.layout.activity_my_info_phone);
        }
        x.view().inject(this);
        initViews();
        showData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_quit:
                quitApp();
                break;
            case R.id.civ_head:
                Intent intent = new Intent(mContext, EditUserHeaderActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_edit:
                edit();
                break;
            case R.id.tv_modify_password:
                Intent passIntent = new Intent(mContext, ModifyPassActivity.class);
                startActivity(passIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void receive(UpdateUserPendantEvent event){
        AppLog.d("receive event = " + event );
        mCivHead.showHeadImage();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(EditHeaderEvent event) {
        AppLog.d(" receive event = " + event );
        mCivHead.showHeadImage();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(EditInfoEvent event) {
        AppLog.d(" receive event = " + event );
        showData();
    }
    //-----------------------------------------------------------------------------------------------------
    private void initViews() {

        mWorktoolbar.setTitle("个人资料");
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

        mCivHead.setOnClickListener(this);
        mTvEdit.setOnClickListener( this );
        mTvModifyPassword.setOnClickListener( this );
        mTvQuit.setOnClickListener( this );

        EventBus.getDefault().register( this );
    }

    private void showData() {
        UserDetailinfo userdetailInfo = AccountUtils.getUserdetailInfo();
        if (userdetailInfo == null) {
            return;
        }
        mCivHead.showHeadImage();

        mNickNameView.setText( TextUtils.isEmpty(userdetailInfo.getNickName())?"":userdetailInfo.getNickName() );

        //根据会员等级显示处理
        if( AppConst.MEMBER_SVIP == userdetailInfo.getVipLevel() ){
            mMemberGrade.setText(getText(R.string.svip_member));
        }else if( AppConst.MEMBER_VIP == userdetailInfo.getVipLevel() ){
            mMemberGrade.setText(getText(R.string.vip_member));
        }else {
            mMemberGrade.setText(getText(R.string.normal_member));
        }

        String sex = userdetailInfo.getSex();
        if("male".equals( sex ) ){
            sex = "男";
        }else if("female".equals( sex ) ){
            sex = "女";
        }
        mTvGender.setText( sex );

        mTvReallyName.setText(userdetailInfo.getReallyName());
        mTvEnterYear.setText(userdetailInfo.getSerial());
        mTvExamNumber.setText(userdetailInfo.getExamNumber());
        mMailAddr.setText(userdetailInfo.getMailAddr());

        //显示挂件列表
        ArrayList<ScoreProductBean> list = userdetailInfo.getHeadPendants();
        //增加默认挂件，还要确认当前使用的挂件
        ScoreProductBean defaultBean = new ScoreProductBean("",0);
        defaultBean.setProductName( "无挂件" );
        boolean hasUse = false;
        if( list!=null ){
            for( ScoreProductBean bean: list ){
                hasUse = hasUse || bean.getUseStatus()!=0;
            }
        }
        defaultBean.setUseStatus( hasUse?0:1 );

        // 2018/11/9  没有兑换过挂件 提示:    你还没有新的挂件，可用积分兑换哦~
        UserPendantAdapter adapter = new UserPendantAdapter( mContext, userdetailInfo );
        pendantGridView.setAdapter( adapter );
        if( list!=null )adapter.addAll( list );
        adapter.add( defaultBean );
        adapter.notifyDataSetChanged();
    }

    private void quitApp() {
        CustomDialog customDialog = AlertManager.showCustomDialog(mContext, "退出登录？", "退出", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                LoginInfo loginInfo = AccountUtils.getLoginUser();
                if (loginInfo == null) {
                    return;
                }

                new BadgeManager(mContext).resetBadge( 0 );
                UploadManager uploadManager = UploadManager.getUploadManager();
                if (uploadManager != null)
                    uploadManager.stopAllUploadTask();
                //progressDialog.dismiss();
                AccountUtils.clear();
                //GlobalData.setClassId( null );      //清空当前班级
                LoginController.getInstance().Login(false);
                mContext.startActivity( new Intent( mContext, LoginActivity.class));
                finish();
            }
        }, null);
        customDialog.setRightBtnAttr(R.drawable.bg_rect_blue_r24, R.color.white);
    }

    private void edit() {
        startActivity(new Intent(mContext, EditPersonalinfoActivity.class));
    }

}
