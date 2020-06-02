/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 文本放大
 */
public class TextZoomActivity extends RoboActivity implements View
        .OnClickListener {
    public static final String MSG_CONTENT = "msg_content";
    @ViewInject(R.id.tv_content)
    private QuestionTextView tvContent;
    @ViewInject(R.id.fr_container)
    private FrameLayout      frContainer;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_text_zoom);
        x.view().inject( this );
        context = this;
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        initView();
    }

    private void initView() {
        frContainer.setOnClickListener(this);
        String content = getIntent().getExtras().getString(MSG_CONTENT);
        tvContent.setQuestionContent(content);
    }


    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
