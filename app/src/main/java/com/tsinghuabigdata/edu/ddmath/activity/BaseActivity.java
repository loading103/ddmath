package com.tsinghuabigdata.edu.ddmath.activity;

import android.app.Activity;
import android.os.Bundle;

import com.tsinghuabigdata.edu.ddmath.inter.CartoomViewNameListener;
import com.umeng.analytics.MobclickAgent;


public class BaseActivity extends Activity implements CartoomViewNameListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume( this );
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause( this );
    }

    @Override
    public String getViewName() {
        return null;            //返回null,不显示卡通豆豆
    }

}
