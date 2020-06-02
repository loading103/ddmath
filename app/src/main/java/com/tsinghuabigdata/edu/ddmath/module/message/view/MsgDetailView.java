package com.tsinghuabigdata.edu.ddmath.module.message.view;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyStudyFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageRemarkType;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.ShareUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;


public class MsgDetailView extends RelativeLayout implements View.OnClickListener {

    //对象
    private WebView  mWebView;               //本地网页内容

    //老师评语
    private TeacherCommentView teacherCommentView;

    //普通带参数消息
    private ScrollView mScrollView;
    private TextView contentView;               //内容
    private LinearLayout mainLayout;
    private TextView beforeView;               //之前文本
    private TextView btnView;                  //点击内容
    private TextView afterView;                //之后文本

    //同班同学购买次数
    private LinearLayout buyedLayout;
    private TextView buyedView;

    //跳转到错题订正
    private TextView goReviseView;
    //增加积分提示
    private RelativeLayout scoreLayout;
    private TextView scoreTextView;
    //定制学
    private TextView customPlanTipsView;

    private MessageInfo messageInfo;
    private WorkToolbar workToolbar;

    public MsgDetailView(Context context) {
        super(context);
        initData(context);
    }

    public MsgDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public MsgDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData( context );
    }

    public void setWorkToolbar(WorkToolbar workToolbar) {
        this.workToolbar = workToolbar;
    }

    public void setMessageInfo(MessageInfo messageInfo){
        this.messageInfo = messageInfo;

        //默认
        teacherCommentView.setVisibility(GONE);
        mWebView.setVisibility(GONE);
        mScrollView.setVisibility(GONE);
        customPlanTipsView.setVisibility(GONE);

        //老师寄语--周报告
        if( !TextUtils.isEmpty(messageInfo.getRemark()) && messageInfo.getRemark().contains(MessageRemarkType.TEACHER_COMMENT_WEEK) ){
            teacherCommentView.setVisibility(VISIBLE);
            teacherCommentView.setData( messageInfo );
            if( workToolbar!=null ) workToolbar.setTitle("老师寄语（一周学习总结）");
        }
        //用WebVeiw显示
        else if ( TextUtils.isEmpty(messageInfo.getRemark()) && !"用户反馈信息回复".equals( messageInfo.getMsgTitle()) ){
            mWebView.setVisibility(VISIBLE);
            loadData(messageInfo);
        } else {
            mScrollView.setVisibility(VISIBLE);
            contentView.setText( messageInfo.getContent() );

            boolean b = MessageUtils.setClickableText( messageInfo.getRemark(), beforeView, btnView, afterView, customPlanTipsView );
            mainLayout.setVisibility( b?VISIBLE:GONE);

            //家长端购买
            buyedLayout.setVisibility( MessageUtils.setBuySuiteText(messageInfo.getRemark(),buyedView)?VISIBLE:GONE);
            //错题订正
            goReviseView.setVisibility( MessageUtils.setGoReviseText( getContext(), messageInfo,goReviseView)?VISIBLE:GONE );
            //增加用户积分
            scoreLayout.setVisibility( View.GONE );
            if( !TextUtils.isEmpty(messageInfo.getRemark()) ){
                try {
                    JSONObject jsonObject = new JSONObject( messageInfo.getRemark() );
                    if( jsonObject.has("score") && jsonObject.getInt("score")>0 ){
                        scoreLayout.setVisibility( VISIBLE );
                        scoreTextView.setText( String.valueOf(jsonObject.getInt("score"))/*String.format(Locale.getDefault(),"恭喜，获得%d积分！", jsonObject.getInt("score"))*/);
                    }
                }catch (Exception e){
                    AppLog.d("",e);
                }
            }

            //定制学
            //customPlanTipsView.setVisibility(  MessageUtils.setGoReviseText( getContext(), messageInfo,goReviseView)?VISIBLE:GONE  );
        }
    }

    private void loadData(MessageInfo messageInfo) {
        String data = messageInfo.getContent();
        if( TextUtils.isEmpty(data) ) return;

        data = data.replaceAll("\n","<br/>");
        mWebView.loadDataWithBaseURL(null, data, "text/html" , "utf-8", null);
    }

    @Override
    public void onClick(View v) {
        MessageUtils.startActivity( getContext(), messageInfo.getRemark() );
    }

    //-------------------------------------------------------------------------
    private void initData( Context context ){
        //
        inflate( context, R.layout.view_msgdetail, this );

        mWebView =  findViewById( R.id.webView_msgdetail );
        initWebView( mWebView);

        teacherCommentView = findViewById(R.id.view_teacher_comment);

        mScrollView =  findViewById( R.id.sv_msgdetail );
        contentView    =  findViewById( R.id.view_msgdetail_content );
        mainLayout     = findViewById( R.id.view_msgdetail_layout );
        beforeView     =  findViewById( R.id.view_msgdetail_before );
        btnView        =  findViewById( R.id.view_msgdetail_click );
        afterView      =  findViewById( R.id.view_msgdetail_after );

        buyedLayout     = findViewById( R.id.layout_parentbuyed );
        buyedView       = findViewById( R.id.tv_buyed );

        goReviseView    = findViewById( R.id.tv_gorevise );
        goReviseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //2018/8/20  是否跳转到详情界面，参数不全
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_TASK, MyStudyFragment.MODEL_ERRORREVISE));
                getContext().sendBroadcast(new Intent(RoboActivity.ACTION));
            }
        });
        btnView.setOnClickListener( this );

        scoreLayout = findViewById(R.id.layout_gain_score);
        scoreTextView = findViewById( R.id.tv_add_score );

        customPlanTipsView = findViewById(R.id.tv_customplan_tips);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("JavascriptInterface")
    private void initWebView(WebView webview) {

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

        // Enable plugin
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //webSettings.setBlockNetworkImage(true);


        webview.removeJavascriptInterface("searchBoxJavaBridge_");

        webview.removeJavascriptInterface("accessibility");
        webview.removeJavascriptInterface("accessibilityTraversal");

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);

            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.stopLoading();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                AppLog.d("sdfsfdsg onPageFinished url= " + url);

                //AppLog.i("loaded url " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                AppLog.d("sdfsfdsg shouldOverrideUrlLoading url = " + url);
                if( !TextUtils.isEmpty(url) && url.startsWith("share://") ){
                    String nurl = url.replace("share://","");
                    if( !nurl.startsWith("http") ){
                        nurl = "http://"+nurl;
                    }
                    ShareUtils.shareUrl( getContext(), nurl);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

}

