package com.tsinghuabigdata.edu.ddmath.module.mycenter;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;



/**
 * VIP卡兑换说明
 * Created by Administrator on 2018/5/23.
 */

public class ExchangeIntroActivity extends RoboActivity {

    //private WorkToolbar           mWorktoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_exchange_intro);
        } else {
            setContentView(R.layout.activity_exchange_intro_phone);
        }
        initViews();
        initData();
    }

    private void initViews() {
        WorkToolbar mWorktoolbar =  findViewById(R.id.worktoolbar);
        String title = "VIP卡兑换码说明";
        mWorktoolbar.setTitle(title);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);

    }

    private void initData() {

    }

}
