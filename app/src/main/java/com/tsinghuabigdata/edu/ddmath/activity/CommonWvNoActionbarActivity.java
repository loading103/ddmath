package com.tsinghuabigdata.edu.ddmath.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * 通用报告查看结果界面
 */
public class CommonWvNoActionbarActivity extends RoboActivity {

    public static final String MSG_TITLE             = "msgtitle";
    public static final String MSG_SHARE_TEXT        = "msg_share_text";
    public static final String MSG_URL               = "msgurl";
    public static final String MSG_FROM_KnowledgeMap = "msg_from_knowledgemap";
    public static final String MSG_SCORE_EVENT       = "scoreevent";
    public static final String MSG_CONTENT_ID        = "contentId";

    @ViewInject(R.id.progress_webview)
    private ProgressWebView progressWebView;
    @ViewInject(R.id.iv_delete)
    private ImageView       ivDelete;
    @ViewInject(R.id.iv_share)
    private ImageView       ivShare;

    private Context mContext;

    private String  url;
    private boolean fromKnowledgemap;
    private String mScoreEventId;
    private String mContentId;

    private String mShareTitle;
    private String mShareText;
    private String mShareUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad() ? R.layout.activity_common_wv_nobar : R.layout.activity_common_wv_nobar_phone);
        x.view().inject(this);
        mContext = this;

        parseIntent();

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void onLeftClick() {
//        finish();
//    }

    //----------------------------------------------------------------------
    private void parseIntent() {
        Intent intent = getIntent();
        url = intent.getStringExtra(MSG_URL);
        mShareText = intent.getStringExtra(MSG_SHARE_TEXT);
        String title = intent.getStringExtra(MSG_TITLE);
        mShareTitle = getShareTitle(title);
        mShareUrl = DataUtils.getUrl(mContext, url);
        fromKnowledgemap = intent.getBooleanExtra(MSG_FROM_KnowledgeMap, false);
        mScoreEventId = intent.getStringExtra(MSG_SCORE_EVENT);
        mContentId = intent.getStringExtra(MSG_CONTENT_ID);
    }

    private void initView() {
        //setStatusbarBg(R.color.bar_tint);
        progressWebView.setListener(new ProgressWebView.FaultListener() {
            @Override
            public void retry() {
                loadUrl();
            }
        });
        if (fromKnowledgemap) {
            progressWebView.setKnowledgeMapListener(new ProgressWebView.KnowledgeMapListener() {
                @Override
                public void goToErrorBook(int index) {
                    finish();
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, index));
                }
            });
        }
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                shareLinkDialog.setShareInfo(mShareUrl, mShareTitle, mShareText, true);
                shareLinkDialog.setScoreEventId( mScoreEventId ).setContentId( mContentId );
                shareLinkDialog.show();
            }
        });
        loadUrl();
    }

    private void loadUrl() {
        progressWebView.loadUrl(DataUtils.getUrl(mContext, url));
        //        progressWebView.loadUrl("https://www.baidu.com/");
    }

    private String getShareTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return "报告";
        }
        if (!title.endsWith("报告")) {
            title += "报告";
        }
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            return detailinfo.getReallyName() + "的" + title;
        }
        return title;
    }


}
