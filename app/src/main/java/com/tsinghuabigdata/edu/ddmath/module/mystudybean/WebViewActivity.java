package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;

public class WebViewActivity extends RoboActivity {

    public static final String MSG_TITLE = "msgtitle";
    public static final String MSG_URL = "msgurl";


    private WorkToolbar workToolbar;
    private ProgressWebView progressWebView;

    private Context mContext;

    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web_view);
        mContext = this;
        parseIntent();
        initView();
    }

    private void initView() {
        workToolbar = (WorkToolbar) findViewById(R.id.toolbar);
        progressWebView = (ProgressWebView) findViewById(R.id.progress_webview);
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        }, null);
        workToolbar.setTitle(title);
        progressWebView.setListener(new ProgressWebView.FaultListener() {
            @Override
            public void retry() {
                loadUrl();
            }
        });
        loadUrl();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        title = intent.getStringExtra(MSG_TITLE);
        url = intent.getStringExtra(MSG_URL);
    }

    private void loadUrl() {
        if (title.equals("充值赠送协议")) {
            progressWebView.loadUrl(url);
        } else {
            progressWebView.loadUrl(DataUtils.getUrl(mContext,url));
        }
    }

}
