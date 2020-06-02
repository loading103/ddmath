package com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

/**
 * 提分方案向导页
 */

public class ScoreGuideActivity extends RoboActivity  {

    public final static String PRE_KEY_FIRST = "first_enter";

    @ViewInject(R.id.gif)
    private GifImageView mgif;
    @ViewInject(R.id.tv_progress)
    private TextView mTtvProgress;
    @ViewInject(R.id.iv_head)
    private TextView mIvhead;
    @ViewInject(R.id.tv_name)
    private TextView mTvame;
    @ViewInject(R.id.tv_content)
    private TextView mTvcontent;
    private String name;

    private  int blinkTime = 100;
    private  int totalTime = 3000;  //总时长
    //加载进度数值
    private long adTime = 0;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad()?R.layout.activity_score_guide:R.layout.activity_score_guide_phone);

        x.view().inject(this);
        initView();
        initData();
    }

    private void initView() {
        getStudentInfo();
        mTvame.setText("hi,"+ name+"同学");
    }

    private void initData() {
        countDownNext();
    }

    private void countDownNext() {
        mTtvProgress.setText(adTime+"%");
        if (timer == null) {
            timer = new Timer();
        }else {
            timer.cancel();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                adTime += (int)(Math.random()*3+2);
                blinkTime+=100;
                if (blinkTime > totalTime) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                            }
                            StartScorePlanActivity();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(blinkTime>=2900){
                            mTtvProgress.setText(100+"%");
                        }else {
                            mTtvProgress.setText(adTime+"%");
                        }
                    }
                });

            }
        }, 0, 100);
    }


    private void StartScorePlanActivity() {
        PreferencesUtils.putBoolean( this, PRE_KEY_FIRST, true );

        startActivity(new Intent(this,ScorePlanActivity.class));
        finish();
    }
    /**
     * 获取孩子信息
     */
    public void getStudentInfo() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        name = loginInfo.getLoginName();
    }

}
