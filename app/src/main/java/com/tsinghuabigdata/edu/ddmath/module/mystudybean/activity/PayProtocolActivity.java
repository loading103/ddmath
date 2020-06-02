package com.tsinghuabigdata.edu.ddmath.module.mystudybean.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;

public class PayProtocolActivity extends RoboActivity {

    private WorkToolbar toolbar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        // 设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_send_protocol);
        initView();
    }

    private void initView() {
        toolbar = (WorkToolbar) findViewById(R.id.worktoolbar);
        toolbar.setTitle("支付协议");
        toolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);
        webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl(AppRequestConst.SEND_PROTOCOL + "?t=" + System.currentTimeMillis());
    }
}
