package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;


/**
 * 文章详情页面（内嵌网页 暂时不用）
 * Created by Administrator on 2018/7/4.
 */

public class ArticleDetailWebActivity extends RoboActivity {

    public static final String MSG_TITLE = "msgtitle";
    public static final String MSG_URL   = "msgurl";

    private ImageView       mIvBack;
    private ImageView       mIvShare;
    private CheckBox        mCbLike;
    private ProgressWebView mProgressWebview;


    private Context mContext;
    private String  url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail_web);
        parseIntent();
        initViews();
        initData();
    }

    private void parseIntent() {

    }

    private void initViews() {
        mContext = this;
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mCbLike = (CheckBox) findViewById(R.id.cb_like);
        mProgressWebview = (ProgressWebView) findViewById(R.id.progress_webview);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        url = intent.getStringExtra(MSG_URL);
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ShareUtils.shareUrl(mContext, DataUtils.getUrl(mContext, url));
                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                shareLinkDialog.setShareUrl(DataUtils.getUrl(mContext, url));
                shareLinkDialog.show();
            }
        });
        mProgressWebview.setListener(new ProgressWebView.FaultListener() {
            @Override
            public void retry() {
                loadUrl();
            }
        });
        loadUrl();
    }

    private void loadUrl() {
        //        mProgressWebview.loadUrl(DataUtils.getUrl(mContext, url));
        mProgressWebview.loadUrl("http://www.baidu.com/");
    }

}
