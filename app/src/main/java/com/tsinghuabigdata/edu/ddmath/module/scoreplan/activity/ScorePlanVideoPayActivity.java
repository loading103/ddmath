package com.tsinghuabigdata.edu.ddmath.module.scoreplan.activity;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.FamousTeacherModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.bean.WeekDatailBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.UploadScorePlanEvent;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.EventBusUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;


/**
 * 名师精讲播放界面
 * Created by Administrator on 2018/3/9.
 */

public class ScorePlanVideoPayActivity extends RoboActivity implements View.OnClickListener {


    private static final String PARAM_SINGLEVIDEOBEAN = "SingleVideoBean";
    private static final String PARAM_PRODUCTID = "productid";

    public static void  openScorePlanVideoPayActivity( Context context, WeekDatailBean bean){
        if( context==null || bean==null ) return;
        Intent intent = new Intent(context, ScorePlanVideoPayActivity.class);
        intent.putExtra(PARAM_SINGLEVIDEOBEAN, bean);
        context.startActivity(intent);
    }


    private WorkToolbar  mWorktoolbar;
    private WebView      mWeb;
    private LoadingPager mLoadingPager;


    private Context mContext;
    private String  mUrl;

    //传递过来的参数
    private WeekDatailBean mweekdata;
    private String productId;

    //定制学背景
    private RelativeLayout mRebglayout;
    private ImageView mIvbg;
    private ImageView mIvclose;
    private ImageView    mIvhead;
    private TextView mTvcontent;
    public static final int[] a={ R.drawable.pic01, R.drawable.pic02, R.drawable.pic03, R.drawable.pic04, R.drawable.pic05, R.drawable.pic06};
    public boolean isfirstpress=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad()?R.layout.activity_scoreplan_video:R.layout.activity_scoreplan_video_phone);
        initViews();
        initData();
        recordVideoPlay();
    }

    private void recordVideoPlay() {
        if( mweekdata == null ) return;
        HashMap<String, String> params = new LinkedHashMap<>();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            params.put("studentId", detailinfo.getStudentId());
        }
        params.put("productId",  mweekdata.getProductId());
        params.put("factory", mweekdata.getFactory());
        params.put("videoId", mweekdata.getContentId());
        params.put("deviceId", AppUtils.getDeviceId(mContext));
        new FamousTeacherModel().recordVideoPlay(params, new RequestListener<String>() {
            @Override
            public void onSuccess(String res) {
                EventBusUtils.postDelay(new UploadScorePlanEvent( "" ), new Handler());//定制学订正成功  就通知刷新
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {}
        });
    }

    private void initViews() {
        mContext = this;
        mWorktoolbar =  findViewById(R.id.worktoolbar);
        mWeb =  findViewById(R.id.webView);
        mLoadingPager =  findViewById(R.id.loading_pager);
        mRebglayout = findViewById( R.id.ll_score_bg );
        mIvbg=findViewById( R.id.iv_score_bg );
        mIvclose = findViewById( R.id.iv_close_bg );
        mIvhead=findViewById( R.id.iv_score_head );
        mTvcontent = findViewById( R.id.tv_score_content );
        mIvbg.setOnClickListener(this);
        mIvclose.setOnClickListener(this);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mweekdata.getHasWatch()==0){
                    mWeb.onPause();
                    mRebglayout.setVisibility(View.VISIBLE);
                    isfirstpress=true;
                    mIvbg.setImageResource(a[new Random().nextInt(6)]);
                }else {
                    finish();
                }

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 禁止用户按返回键退出
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && mweekdata.getHasWatch()==0 &&!isfirstpress) {
            isfirstpress=true;
            mWeb.onPause();
            mRebglayout.setVisibility(View.VISIBLE);
            mIvbg.setImageResource(a[new Random().nextInt(6)]);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initData() {
        mweekdata = (WeekDatailBean) getIntent().getSerializableExtra(PARAM_SINGLEVIDEOBEAN);
        productId = mweekdata.getProductId();
        mWorktoolbar.setTitle(mweekdata.getTitle());
        mUrl = "http://teach.doudoushuxue.com/static/player.html?vid=" + mweekdata.getContentId();
        loadUrl();
    }


    private void loadUrl() {
        mWeb.loadUrl(mUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeb.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_score_bg:
                finish();
                break;
            case R.id.iv_close_bg:
                finish();
                break;
        }
    }
}
