package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.parent.MVPModel.CommunityModel;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.fragment.CommunityFragment;
import com.tsinghuabigdata.edu.ddmath.parent.util.ReportDetailUtils;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * 文章详情页面
 * Created by Administrator on 2018/7/4.
 */

public class ArticleDetailActivity extends RoboActivity {

    public static final String MSG_URL  = "msgurl";
    public static final String MSG_BEAN = "msgbean";


    private ImageView    mIvBack;
    private ImageView    mIvShare;
    private CheckBox     mCbLike;
    private TextView     mTvTitle;
    private TextView     mTvTime;
    private TextView     mTvReadCount;
    private TextView     mTvLikeCount;
    private WebView      mWebView;
    private LinearLayout mLlContent;
    private LoadingPager mLoadingPager;

    private Context mContext;
    //private String  url;
    private String mParentId = "";
    //private ArticleItemBean mArticleItemBean;
    private String articleName;
    private String articleId;
    private long   createTime;

    //    private int             mOriginalLike;
    //    private int             mLastLike;
    private int     mlikeCount;
    private int     mReadCount;
    private boolean mSuccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        parseIntent();
        initViews();
        initData();

        MobclickAgent.onEvent(this, "parent_main_detailarticle");
    }

    private void parseIntent() {
        Intent intent = getIntent();
        articleName = intent.getStringExtra("articleName");
        articleId = intent.getStringExtra("articleId");
        createTime = intent.getLongExtra("createTime", System.currentTimeMillis());
    }

    private void initViews() {
        mContext = this;
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mCbLike = (CheckBox) findViewById(R.id.cb_like);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvReadCount = (TextView) findViewById(R.id.tv_read_Count);
        mTvLikeCount = (TextView) findViewById(R.id.tv_like_Count);
        mWebView = (WebView) findViewById(R.id.webView);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
        mLoadingPager = (LoadingPager) findViewById(R.id.loadingPager);
        mLoadingPager.setTargetView(mLlContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryArticleDetail();
            }
        });
        mCbLike.setVisibility(View.INVISIBLE);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToCommunity();
            }
        });
    }


    private void initData() {
        createLoginInfo();
        mTvTitle.setText(articleName);
        String date = DateUtils.getCurrDateStr(createTime);
        mTvTime.setText(date);
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                String articleShareUrl = ReportDetailUtils.getArticleShareUrl(mParentId, articleId);
                LogUtils.i("articleShareUrl=" + articleShareUrl);
                shareLinkDialog.setShareInfo(articleShareUrl, articleName, getShareText(createTime), false);
                shareLinkDialog.show();
            }
        });
        mCbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                givelike(mCbLike.isChecked());
            }
        });
        queryArticleDetail();
        readArticle();
    }


    //查询登录信息
    private void createLoginInfo() {
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo != null) {
            mParentId = parentInfo.getParentId();
        }
    }

    private void queryArticleDetail() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("parentId", mParentId);
        params.put("articleId", articleId);
        new CommunityModel().queryArticleDetail(params, new RequestListener<ArticleBean>() {

            @Override
            public void onSuccess(ArticleBean res) {
                LogUtils.i("queryArticleDetail onSuccess");
                if (res == null) {
                    mLoadingPager.showServerFault();
                    return;
                }
                mSuccess = true;
                mReadCount = res.getReadCount() + 1;
                mTvReadCount.setText(mReadCount + "浏览");
                mlikeCount = res.getLikeCount();
                mTvLikeCount.setText(mlikeCount + "赞");
                mWebView.loadDataWithBaseURL(null, res.getContent(), "text/html", "utf-8", null);
                mCbLike.setChecked(res.getLike() == ArticleBean.LIKE);
                mCbLike.setVisibility(View.VISIBLE);
                mLoadingPager.showTarget();

            }

            @Override
            public void onFail(HttpResponse<ArticleBean> response, Exception ex) {
                LogUtils.i("queryArticleDetail onFail");
                mLoadingPager.showFault(ex);
            }
        });
    }


    private void readArticle() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("parentId", mParentId);
        params.put("articleId", articleId);
        params.put("type", "1");
        params.put("status", "2");
        new CommunityModel().operateArticle(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                LogUtils.i("readArticle onSuccess");
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                LogUtils.i("readArticle onFail");
            }
        });
    }

    private void givelike(final boolean like) {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("parentId", mParentId);
        params.put("articleId", articleId);
        params.put("type", "2");
        params.put("status", like ? "2" : "1");
        new CommunityModel().operateArticle(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                LogUtils.i("readArticle onSuccess");
                if (like) {
                    mlikeCount++;
                } else {
                    mlikeCount--;
                }
                mTvLikeCount.setText(mlikeCount + "赞");
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                LogUtils.i("readArticle onFail");
            }
        });
    }

    @Override
    public void onBackPressed() {
        backToCommunity();
    }

    private void backToCommunity() {
        if (mSuccess) {
            //数据是使用Intent返回
            LogUtils.i("backToCommunity mSuccess");
            Intent intent = new Intent();
            //把返回数据存入Intent
            intent.putExtra(CommunityFragment.MSG_READ_COUNT, mReadCount);
            intent.putExtra(CommunityFragment.MSG_LIKE_COUNT, mlikeCount);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private String getShareText(long createtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(createtime);
    }


}
