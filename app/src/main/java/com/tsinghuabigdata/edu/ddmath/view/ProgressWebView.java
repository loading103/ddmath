/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.commons.network.DNSHelper;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.activity.ReportQuestionDetailActivity;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.event.ReadDayReportEvent;
import com.tsinghuabigdata.edu.ddmath.event.ReadWeekReportEvent;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.FamousTeacherActivity;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RobotQaActivity;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.utils.ProxyUtils;

import org.greenrobot.eventbus.EventBus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import au.id.villar.dns.DnsException;
import au.id.villar.dns.engine.Utils;


/**
 * <p>
 * Created by yanshen@tsinghuabigdatFaulta.com on 2015/12/18.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 */
public class ProgressWebView extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = "ProgressWebView";
    private NoScrollWebView       webview;
    private ProgressBar   progress_bar;
    private LinearLayout  load_failed_layout;
    private TextView      refresh_btn;
    private FaultListener listener;
    private boolean       playVideo;

    private PostInvokeListener mPostInvokeListener;
    private String             url;
    private boolean            loadedCanBack;
    private boolean            mQualityComplete;
    private boolean            mQualityModify;
    private Status loadStatus = Status.LOADING;

    public enum Status {
        LOADING, LOAD_FINISH, LOAD_SUCCESS, LOAD_ERROR;
        private int type;
    }

    private ErrorQuestionListener mErrorQuestionListener;

    public ProgressWebView(Context context) {
        super(context);
        init();
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setWebViewExpand( boolean expand ){
        if(webview!=null) webview.setExpand(expand);
    }

    /**
     * 设置如果有乐视，就跳转到全屏播放
     *
     * @param playVideo
     * @deprecated 暂时不用跳转到全屏播放
     */
    @Deprecated
    public void setPlayVideo(boolean playVideo) {
        // 暂时不用跳转到全屏播放
        //        this.playVideo = playVideo;
    }

    public void setListener(FaultListener listener) {
        this.listener = listener;
    }

    public void setPostInvokeListener(PostInvokeListener postInvokeListener) {
        this.mPostInvokeListener = postInvokeListener;
    }

    private void init() {
        inflate(getContext(), R.layout.view_progress_webview, this);
        webview =  findViewById(R.id.webview);
        progress_bar =  findViewById(R.id.progress_bar);
        load_failed_layout =  findViewById(R.id.load_failed_layout);
        refresh_btn =  findViewById(R.id.refresh_btn);

        // 设置点击事件
        refresh_btn.setOnClickListener(this);
        initWebView();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("JavascriptInterface")
    private void initWebView() {

        //webview.clearCache( false );
        if (AppUtils.isDebug()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        //
        WebSettings webSettings = webview.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);

        webSettings.setSupportZoom(false);
        //webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // Enable plugin
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //webSettings.setBlockNetworkImage(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webview.removeJavascriptInterface("searchBoxJavaBridge_");
        }
        webview.removeJavascriptInterface("accessibility");
        webview.removeJavascriptInterface("accessibilityTraversal");

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    loadStatus = Status.LOAD_FINISH;
                    if (mLoadFinishListener != null)
                        mLoadFinishListener.loadfinished();
                    progress_bar.setVisibility(View.GONE);
                    webview.loadUrl("javascript:androidSDK.setWindowTitle(window.document.title)");
                } else {
                    if (progress_bar.getVisibility() == View.GONE)
                        progress_bar.setVisibility(View.VISIBLE);
                    progress_bar.setProgress(newProgress);
                    loadStatus = Status.LOADING;
                }
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
                Log.d(TAG, "onCloseWindow: ");
                if (winCloseListener != null) {
                    winCloseListener.closeWindow();
                }
            }
/*@Override
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                super.onShowCustomView(view, requestedOrientation, callback);
                AppLog.i("全屏");
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                AppLog.i("全屏111");
                AlertManager.toast(getContext(), "全屏");
            }

            @Override
            public void onHideCustomView() {
                AppLog.i("退出111");
            }*/
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.stopLoading();
                loadStatus = Status.LOAD_ERROR;
                AppLog.i(TAG, "sdfsfdsg onReceivedError: " + errorCode + " des : " + description);
                /*if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    view.clearView();
                } else {
                    view.showWeb("about:blank");
                }*/
                // api 18 使用此代码代替clearView()

                // 如果host加载失败，则再次通过DNS服务器解析一次
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
                    new UseProxyTask().execute(errorCode, description, failingUrl);
                } else {
                    setLoadFailed(errorCode, description, failingUrl);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                AppLog.d("sdfsfdsg onPageFinished url= " + url);
                loadStatus = Status.LOAD_SUCCESS;
                if (progress_bar.getVisibility() == View.VISIBLE) {
                    progress_bar.setVisibility(View.GONE);
                }
                if (url.contains("/user/login?") || url.contains("timeout")) {
                    loadedCanBack = false;
                    return;
                }
                if (!ProgressWebView.this.url.equals(url)) {
                    loadedCanBack = true;
                } else {
                    loadedCanBack = false;
                }
                if( !TextUtils.isEmpty(url) && url.contains("/report/studyingAnalysis/") ){
                    progress_bar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webview.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, webview.getLeft()+5, webview.getTop()+5, 0));
                            webview.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, webview.getLeft()+5, webview.getTop()+5, 0));
                        }
                    }, 500);
                }
                AppLog.d("loaded url " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                AppLog.d("sdfsfdsg shouldOverrideUrlLoading url = " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        installInterface();
    }

    public void loadUrl(String url) {
        //        webview.loadUrl("http://yuntv.letv.com/bcloud.html?uu=758d9faf84&vu=8bfcc81818&pu=5f4d4199ac&auto_play=0&gpcflag=1&width=640&height=360");
        //        webview.loadUrl("http://m.letv.com/vplay_24296590.html");
        //        webview.loadUrl("http://v.youku.com/v_show/id_XNTM5NTIyODUy.html?from=s1.8-1-1.2");
        if (!url.toLowerCase().contains("javascript:")) {
            this.url = url;
        }
        webview.loadUrl(url);
    }

    private void closePopPlay() {
        String js = "var closeBtn = document.getElementById(\"directive-video-playclose\");" +
                "var evObj = document.createEvent('MouseEvents');" +
                "evObj.initEvent('click', true, false);" +
                "closeBtn.dispatchEvent(evObj);";
        loadUrl("javascript:" + js);
    }

    @SuppressLint("JavascriptInterface")
    private void installInterface() {
        webview.addJavascriptInterface(new JavascriptBridge(), "androidSDK");
    }

    public void goBack() {
        if (loadedCanBack && webview.canGoBack()) {
            webview.goBack();
            AppLog.i("go back");
        } else {
            AppLog.i("can not go back");
        }
    }

    public boolean canGoBack() {
        return loadedCanBack && webview.canGoBack();
    }

    public WebView getWebview() {
        return webview;
    }

    @Override
    public void onClick(View v) {
        load_failed_layout.setVisibility(View.GONE);
        if (listener != null) {
            listener.retry();
        }
    }

    public interface FaultListener {
        void retry();
    }

    public void setErrorQuestionListener(ErrorQuestionListener listener) {
        mErrorQuestionListener = listener;
    }

    public interface WindowCloseListener {
        void closeWindow();
    }

    private WindowCloseListener winCloseListener;

    public void setWinCloseListener(WindowCloseListener winCloseListener) {
        this.winCloseListener = winCloseListener;
    }

    public interface LoadFinishListener {
        void loadfinished();
    }

    private LoadFinishListener mLoadFinishListener;

    public void setLoadFinishListener(LoadFinishListener listener) {
        mLoadFinishListener = listener;
    }

    public interface KnowledgeMapListener {
        void goToErrorBook(int index);
    }

    private KnowledgeMapListener mKnowledgeMapListener;

    public void setKnowledgeMapListener(KnowledgeMapListener knowledgeMapListener) {
        mKnowledgeMapListener = knowledgeMapListener;
    }

    /**
     * WebView回调
     */
    public interface PostInvokeListener {
        /**
         * 触发回调
         *
         * @param command
         * @param params
         */
        void onPostInvoke(String command, String params);
    }

    /**
     * js and android bridge
     */
    private class JavascriptBridge {
        @JavascriptInterface
        public void setWindowTitle(String title) {
            AppLog.i("load web title is " + title);
        }

        @JavascriptInterface
        public void postInvoke(String command, String params) {
            if (mPostInvokeListener != null) {
                mPostInvokeListener.onPostInvoke(command, params);
            }
        }

        //        @JavascriptInterface
        //        public void fullScreent(String url) {
        //            Intent intent = new Intent(getContext(), WebViewVedioFullActivity.class);
        //            intent.putExtra("url", url);
        //            getContext().startActivity(intent);
        //        }

        //        /**
        //         * 笔迹预览放大
        //         *
        //         * @param json
        //         */
        //        @JavascriptInterface
        //        public void trackReview(String json) {
        //            Intent intent = new Intent(getContext(), TrackingGalleryActivity.class);
        //            intent.putExtra(TrackingGalleryActivity.MARKINGINFO, json);
        //            getContext().startActivity(intent);
        //        }

        /**
         * 进入微问界面
         *
         * @param type       0:做题之前  1：做题后
         * @param right      true:答对   false：答错
         * @param questionId 问题ID
         * @param extend     扩展数据你，JSON结构字符串
         */
        @JavascriptInterface
        public void openQA(int type, boolean right, String questionId, String examId, String extend) {
            /*Intent intent = new Intent("android.intent.action.WWMACHINE");
            intent.putExtra("type", type);
            intent.putExtra("right", right);
            intent.putExtra("questionId", questionId);
            intent.putExtra("examId", examId);
            intent.putExtra("extend", extend);*/
            if (!AppUtils.isNetworkConnected(getContext())) {
                AlertManager.toast(getContext(), getResources().getString(R.string.no_connection));
            } else {
                RobotQaActivity.startRobotQaActivity(getContext(), type, examId, questionId, right, extend, false);
            }
        }

        /**
         * 错题回顾 用户提交回调
         *
         * @param reasons     错误原因
         * @param masterlevel 掌握程度
         */
        @JavascriptInterface
        public void errorQuestionReview(String reasons, int masterlevel) {
            //
            if (mErrorQuestionListener != null) {
                mErrorQuestionListener.submitReasons(reasons, masterlevel);
            }
        }

        /**
         * 显示用户获得的积分效果
         *
         * @param point 积分
         */
        @JavascriptInterface
        public void showUserScore(int point) {
            if( point > 0 ) ZxApplication.getApplication().showUserScore( point );
        }

        /**
         * 弹出QQ对话界面
         *
         * @param qq 呼出的QQ
         */
        @JavascriptInterface
        public void pushToQQ(String qq) {
            jumptoQQ(qq);
        }

        /**
         * 获取素质分析报告结果
         */
        @JavascriptInterface
        public void getQualityAnalysisResult(boolean isUpdateSuccess, boolean qualityModify) {
            mQualityComplete = mQualityComplete || isUpdateSuccess;
            mQualityModify = mQualityModify || qualityModify;
        }

        /**
         * 知识图谱跳转错题本
         */
        @JavascriptInterface
        public void knowledgeMapWebGotoWrongBook(int type, String knowledgeId) {
            if (mKnowledgeMapListener != null && (type == 1 || type == 2)) {
                if( type == 1 ) type = 0;
                else type = 3;
                mKnowledgeMapListener.goToErrorBook(type);
            } else if (type == 3) { //微课
                Intent intent = new Intent(getContext(), FamousTeacherActivity.class);
                intent.putExtra(FamousTeacherActivity.KNOWLEDGEID, knowledgeId);
                getContext().startActivity(intent);
            }
        }

        /**
         * 知识图谱跳转错题本
         */
        @JavascriptInterface
        public void getQuestionParaData(String examId, String questionId) {
            if (TextUtils.isEmpty(examId) || TextUtils.isEmpty(questionId)) {
                ToastUtils.showShort(getContext(), R.string.param_error);
            } else {
                ReportQuestionDetailActivity.startReportQuestionDetailActivity(getContext(), examId, questionId );
            }
        }

        /**
         * 家长端 家长已阅读回调
         */
        @JavascriptInterface
        public void parentReadReport(String type, String examId) {
            if (TextUtils.isEmpty(examId) || TextUtils.isEmpty(type)) {
                ToastUtils.showShort(getContext(), R.string.param_error);
            } else {
                if( "day".equals(type) ){
                    EventBus.getDefault().post(new ReadDayReportEvent(examId) );
                }else{
                    EventBus.getDefault().post(new ReadWeekReportEvent(examId) );
                }
            }
        }
    }

    private void jumptoQQ(String dstQQ) {
        if (AppUtils.isQQClientAvailable(getContext())) {
            Log.d("QQ", "jumptoQQ: qq installed");
            //弹出聊天界面
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + dstQQ;
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            Log.d("QQ", "jumptoQQ: qq not installed");
            openQQDownloadUrl();
        }
    }

    private void openQQDownloadUrl() {
        String url = "http://im.qq.com/download/";
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        getContext().startActivity(it);
    }

    /**
     * 通过NDS服务器解析得到IP
     *
     * @return
     */
    private List<String> getIpFromDnsServer() {
        try {
            String domain = new URL(url).getHost();
            if (Utils.isValidDnsName(domain)) {
                AppLog.i("DNS", "解析domain, URL:" + url + ", domain:" + domain);
                return new DNSHelper(AppRequestConst.DNS_SERVERS).resolver(domain);
            } else {
                AppLog.i("DNS", "解析域名发现无效的域名。" + domain);
                return Collections.EMPTY_LIST;
            }
        } catch (DnsException e) {
            AppLog.w(ErrTag.TAG_DNS, "域名解析出错", e);
            return Collections.EMPTY_LIST;
        } catch (MalformedURLException e) {
            AppLog.w(ErrTag.TAG_DNS, "域名解析出错", e);
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 使用代理
     *
     * @return
     */
    private boolean useProxy() {
        // Wifi环境下才能走代理
        // 已经设置了代理，就不再走代理解析了
        if (!AppUtils.isNetworkConnected(getContext())) {
            AppLog.i("网络不通，不能解析DNS");
            return false;
        }

        if (!AppRequestConst.IS_USE_DNSPROXY) {
            return false;
        }

        // 504 time out
        List<String> ips = getIpFromDnsServer();
        if (ips.size() > 0) {
            // 使用代理
            ProxyUtils.setProxy(getWebview(), ips.get(0), 80);
            AppLog.i("已解析出域名." + ips.get(0));
            return true;
        }

        AppLog.i("没有解析出域名.");
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        //webview.clearCache(true);
        ProxyUtils.clearProxy(getWebview());
        super.onDetachedFromWindow();
    }

    private void setLoadFailed(int errorCode, String description, String failingUrl) {
        webview.loadUrl("about:blank");
        AppLog.w(ErrTag.TAG_HTTP, errorCode + " " + description + " " + failingUrl);
        load_failed_layout.setVisibility(View.VISIBLE);
    }


    public interface ErrorQuestionListener {
        void submitReasons(String reasons, int level);
    }


    /**
     * 域名解析异步执行
     */
    class UseProxyTask extends AppAsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doExecute(Object... params) throws Exception {
            AppLog.i("开始解析DNS");
            return useProxy();
        }

        @Override
        protected void onResult(Boolean success) {
            if (success) {
                AppLog.i("reload " + url);
                webview.loadUrl(url);
            } else {
                setLoadFailed(((Integer) getParams()[0]), ((String) getParams()[1]), ((String) getParams()[2]));
            }
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            AlertManager.showErrorInfo(getContext(), ex);
            setLoadFailed(0, "域名解析失败", null);
        }
    }

    public boolean isQualityComplete() {
        return mQualityComplete;
    }

    public boolean isQualityModify() {
        return mQualityModify;
    }

    public Status getLoadStatus() {
        return loadStatus;
    }


}


