package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;


/**
 * 报告详情页面
 * Created by Administrator on 2018/7/4.
 */

public class ReportDetailActivity extends RoboActivity {

    private static final String MSG_TITLE       = "msg_title";
    private static final String MSG_URL         = "msg_url";
    private static final String MSG_SHARE_TITLE = "msg_share_title";
    private static final String MSG_SHARE_TEXT  = "msg_share_text";
    private static final String MSG_SHARE_URL   = "msg_share_url";
    private static final String MSG_TYPE        = "type";

    public static void openReportDetailActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, ReportDetailActivity.class);
        intent.putExtra(ReportDetailActivity.MSG_TITLE, title);
        intent.putExtra(ReportDetailActivity.MSG_URL, url);
        context.startActivity(intent);
    }
    public static void openReportDetailActivity(Context context, String title, String url, String shareTitle, String shareText, String shareUrl, String type) {
        Intent intent = new Intent(context, ReportDetailActivity.class);
        intent.putExtra(ReportDetailActivity.MSG_TITLE, title);
        intent.putExtra(ReportDetailActivity.MSG_URL, url);
        intent.putExtra(ReportDetailActivity.MSG_SHARE_TITLE, shareTitle);
        intent.putExtra(ReportDetailActivity.MSG_SHARE_TEXT, shareText);
        intent.putExtra(ReportDetailActivity.MSG_SHARE_URL, shareUrl);
        intent.putExtra(ReportDetailActivity.MSG_TYPE, type);
        context.startActivity(intent);
    }
    //    private ImageView       mIvBack;
    //    private ImageView       mIvShare;
    //    private TextView        mTvTitle;
    private ParentToolbar   parentToolbar;
    private ProgressWebView mProgressWebview;

    private Context mContext;

    private String  mUrl;
    private String  mTitle;
    private String  mShareTitle;
    private String  mShareText;
    private String  mShareUrl;
    private String  type;           //null,day,week

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);
        if( !parseIntent() ){
            ToastUtils.show( this, "参数错误");
            finish();
            return;
        }
        initViews();
        initData();
    }

    @Override
    public void onBackPressed() {
        quit();
    }

    @Override
    public String getUmEventName() {
        return "parent_main_reportdetail";
    }

    private boolean parseIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(MSG_URL);
        mTitle = intent.getStringExtra(MSG_TITLE);

        mShareText = intent.getStringExtra(MSG_SHARE_TEXT);
        mShareTitle = intent.getStringExtra(MSG_SHARE_TITLE);
        mShareUrl = intent.getStringExtra(MSG_SHARE_URL);
        type = intent.getStringExtra(MSG_TYPE);

        return !TextUtils.isEmpty(mUrl) && !TextUtils.isEmpty(mTitle);
    }

    private void initViews() {
        mContext = this;
        parentToolbar = findViewById(R.id.parent_toolbar);
        parentToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quit();
            }
        }, null, TextUtils.isEmpty(mShareUrl)?null:new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                shareLinkDialog.setShareInfo(mShareUrl, mShareTitle, mShareText, true);
                shareLinkDialog.show();
            }
        });

        mProgressWebview = findViewById(R.id.progress_webview);
    }

    private void quit(){
//        if( !TextUtils.isEmpty(type) ){
//            if( "day".equals(type) ){
//                EventBus.getDefault().post(new CreateDayReportEvent() );
//            }else{
//                EventBus.getDefault().post(new CreateWeekReportEvent() );
//            }
//        }
        finish();
    }
    private void initData() {

        parentToolbar.setTitle( mTitle );

        mProgressWebview.setListener(new ProgressWebView.FaultListener() {
            @Override
            public void retry() {
                loadUrl();
            }
        });
        loadUrl();
    }

    private void loadUrl() {
        mProgressWebview.loadUrl(mUrl);
    }
}
