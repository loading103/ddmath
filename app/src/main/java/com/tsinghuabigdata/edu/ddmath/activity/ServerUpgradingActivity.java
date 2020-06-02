package com.tsinghuabigdata.edu.ddmath.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.event.ServerUpgradingEvent;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

/**
 * 服务器维护页（13.0改版）
 * Created by Administrator on 2018/3/13.
 */

public class ServerUpgradingActivity extends RoboActivity {

    private Context   mContext;
    private ImageView mIvKnow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_server_upgrading);
        } else {
            setContentView(R.layout.activity_server_upgrading_phone);
        }
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
        mIvKnow = (ImageView) findViewById(R.id.iv_quit);
        mIvKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                sendBroadcast(new Intent(com.tsinghuabigdata.edu.ddmath.activity.RoboActivity.ACTION));
                EventBus.getDefault().post(new ServerUpgradingEvent());
            }
        });
    }

    private void initData() {


    }

    @Override
    public void onBackPressed() {

    }
}
