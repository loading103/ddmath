package com.tsinghuabigdata.edu.ddmath.module.famousteacher;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tsinghuabigdata.edu.ddmath.MVPModel.FamousTeacherModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * 名师精讲播放界面
 * Created by Administrator on 2018/3/9.
 */

public class FamousVideoPayActivity extends RoboActivity {


    private static final String PARAM_SINGLEVIDEOBEAN = "SingleVideoBean";
    private static final String PARAM_PRODUCTID = "productid";

    public static void openFamousVideoPayActivity( Context context, SingleVideoBean bean, String productId ){
        if( context==null || bean==null ) return;
        Intent intent = new Intent(context, FamousVideoPayActivity.class);
        intent.putExtra(PARAM_SINGLEVIDEOBEAN, bean);
        intent.putExtra( PARAM_PRODUCTID, productId);
        context.startActivity(intent);
    }


    private WorkToolbar  mWorktoolbar;
    private WebView      mWeb;
    private LoadingPager mLoadingPager;


    private Context mContext;
    private String  mUrl;

    //传递过来的参数
    private SingleVideoBean mSingleVideoBean;
    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_famous_video);
        initViews();
        initData();
        recordVideoPlay();
    }

    private void recordVideoPlay() {

        //要支持定制学的播放功能

        if( mSingleVideoBean == null ) return;

        HashMap<String, String> params = new LinkedHashMap<>();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            params.put("studentId", detailinfo.getStudentId());
        }
        params.put("productId", productId);
        params.put("factory", mSingleVideoBean.getFactory());
        params.put("videoId", mSingleVideoBean.getVideoId());
        params.put("deviceId", AppUtils.getDeviceId(mContext));
        new FamousTeacherModel().recordVideoPlay(params, new RequestListener<String>() {
            @Override
            public void onSuccess(String res) {}

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {}
        });
    }

    private void initViews() {
        mContext = this;
        mWorktoolbar =  findViewById(R.id.worktoolbar);
//        mWorktoolbar.setVisibility(View.GONE);

        mWeb =  findViewById(R.id.webView);
        mLoadingPager =  findViewById(R.id.loading_pager);

        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl();
            }
        });

        mLoadingPager.setTargetView(mWeb);
        WebSettings settings = mWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        mWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWeb.getSettings().setSupportZoom(true);  //支持放大缩小
        mWeb.getSettings().setBuiltInZoomControls(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWeb.getSettings().setSaveFormData(true);// 保存表单数据
        mWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtils.i("onProgressChanged newProgress = " + newProgress);
                if (newProgress == 100) {
                    // 网页加载完成
                    mLoadingPager.showTarget();
                } else {
                    // 加载中

                }

            }
        });
        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.stopLoading();
                mLoadingPager.showCustomFault(description);
                LogUtils.i("onPageFinished errorCode = " + errorCode + " description = " + description);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.i("onPageFinished url = " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.i("shouldOverrideUrlLoading url = " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        //mWeb.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //缓存模式
        mWeb.getSettings().setAppCacheEnabled(false); //开启缓存功能
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWeb.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }



    private void initData() {
        mSingleVideoBean = (SingleVideoBean) getIntent().getSerializableExtra(PARAM_SINGLEVIDEOBEAN);
        productId = getIntent().getStringExtra(PARAM_PRODUCTID);
        mWorktoolbar.setTitle(getName(mSingleVideoBean));
        mUrl = "http://teach.doudoushuxue.com/static/player.html?vid=" + mSingleVideoBean.getVideoId();
        /*String token = "";
        if (AccountUtils.getLoginUser() != null) {
            token = AccountUtils.getLoginUser().getAccessToken();
        }
        try {
            mUrl = "http://teach.doudoushuxue.com/static/player.html?vid=" + item.getVideoId() + "&access_token=" + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            mUrl = null;
        }*/
        loadUrl();
    }

    private String getName(SingleVideoBean item) {
        if (TextUtils.isEmpty(item.getSection())) {
            return item.getChapter();
        }
        return item.getChapter() + " " + item.getSection();
    }

    private void loadUrl() {
        mWeb.loadUrl(mUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeb.setVisibility(View.GONE);
        //mWeb.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWeb.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWeb.onPause();
    }
}
