package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment.MyStudyBeanFragment;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 购买套餐页面
 * Created by Administrator on 2018/4/11.
 */

public class BuySuiteActivity extends RoboActivity {

    private static final String PARAM_TYPE         = "type";
    private static final String PARAM_PRIVILEAGEID = "privilegeId";
    private static final String PARAM_PRI_IDS = "privilegeIds";

    public static final int WORK      = 0;
    public static final int DAY_CLEAR = 1;

    //private WorkToolbar mWorktoolbar;
    private String privilegeId;
    private String mPrivilegeIds;

    public static void openActivityForSuite(Context context, String privilegeIds) {
        //GlobalData.setExpandPrivilege(privilegeId);
        Intent intent = new Intent(context, BuySuiteActivity.class);
        //intent.putExtra(PARAM_TYPE, type);
        intent.putExtra( PARAM_PRI_IDS,privilegeIds );
        context.startActivity(intent);
    }
    public static void startBuySuiteActivity(Context context, int type, String privilegeId) {
        //GlobalData.setExpandPrivilege(privilegeId);
        Intent intent = new Intent(context, BuySuiteActivity.class);
        intent.putExtra(PARAM_TYPE, type);
        intent.putExtra( PARAM_PRIVILEAGEID,privilegeId );
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        privilegeId = getIntent().getStringExtra( PARAM_PRIVILEAGEID );
        mPrivilegeIds = getIntent().getStringExtra( PARAM_PRI_IDS );

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_buy_suite);
        } else {
            setContentView(R.layout.activity_buy_suite_phone);
        }
        initViews();
    }

    private void initViews() {
        WorkToolbar mWorktoolbar = findViewById(R.id.worktoolbar);
        String title = "我要购买";
        mWorktoolbar.setTitle(title);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MyStudyBeanFragment myStudyBeanFragment = new MyStudyBeanFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(MyStudyBeanFragment.PARAM_SHOW_BEAN, false);
        if( !TextUtils.isEmpty(privilegeId) ){
            bundle.putString(MyStudyBeanFragment.PARAM_SHOW_SINGLE_PRIVILEDGEID, privilegeId);
        }
        if(!TextUtils.isEmpty(mPrivilegeIds)){
            bundle.putBoolean(MyStudyBeanFragment.PARAM_SHOW_BEAN, false);
            bundle.putString(MyStudyBeanFragment.PARAM_SHOW_PRIVILEDGEIDS, mPrivilegeIds);
        }

        //fragment保存参数，传入一个Bundle对象
        myStudyBeanFragment.setArguments(bundle);

        fragmentTransaction.add(R.id.container_suite, myStudyBeanFragment).commitAllowingStateLoss();
    }

}
