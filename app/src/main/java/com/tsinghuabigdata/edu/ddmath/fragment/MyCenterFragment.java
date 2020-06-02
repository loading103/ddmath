package com.tsinghuabigdata.edu.ddmath.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment.UserCenterFragment;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


/**
 * 个人中心
 * Created by Administrator on 2016/12/14.
 */

public class MyCenterFragment extends MainBaseFragment implements View.OnClickListener {

    //private static final String TAG = "MyCenterFragment";

    private UserCenterFragment mUserCenterFragment;

    private LinearLayout mLlUnLogin;
    //private Button       mBtnRegister;
    //private Button       mBtnLogin;
//    private TextView     mTvCount;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_my_center, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_my_center_mobile, container, false);
        }
        initView(root);
        initData();
        return root;
    }

    private void initView(View root) {
        mLlUnLogin =  root.findViewById(R.id.ll_unLogin);
        Button mBtnRegister =  root.findViewById(R.id.btn_register);
        Button mBtnLogin =  root.findViewById(R.id.btn_login);
//        mTvCount =  root.findViewById(R.id.tv_count);
        mBtnRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    private void initData() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        showView(loginInfo != null);
    }

    public void goTo(int index) {
        // 跳转到指定功能
        if (mUserCenterFragment != null) {
            mUserCenterFragment.gotoFragment(index);
        }
    }

    @Override
    protected void login(boolean isLogin) {
        showView(isLogin);
    }

    private void showView(boolean isLogin) {
        if (isLogin) {
            mLlUnLogin.setVisibility(View.INVISIBLE);
            initFragment();
        } else {
            mLlUnLogin.setVisibility(View.VISIBLE);
            if (mUserCenterFragment != null) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(mUserCenterFragment).commitAllowingStateLoss();
            }
        }
    }

    private void initFragment() {
        mUserCenterFragment = new UserCenterFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_my_center, mUserCenterFragment).commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                //goActivity(RegisterActivity.class);
                ToastUtils.showLong( getContext(), getResources().getString(R.string.regisiter_tips));
                break;
            case R.id.btn_login:
                goActivity(LoginActivity.class);
                break;
            default:
                break;
        }
    }

    private void goActivity(Class clazz) {
        Intent intent = new Intent(getContext(), clazz);
        startActivity(intent);
    }


    @Override
    public String getUmEventName(){
        if( mUserCenterFragment!=null )
            return mUserCenterFragment.getUmEventName();
        return "mycenter_userinfo";     //默认我的资料
    }
}
