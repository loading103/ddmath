package com.tsinghuabigdata.edu.ddmath.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * 素质分析报告查看结果界面
 */
public class CommonWebviewActivity extends RoboActivity {

    public static final String MSG_TITLE = "msgtitle";
    public static final String MSG_URL = "msgurl";
    public static final String MSG_FROM = "iscored";
    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    @ViewInject(R.id.progress_webview)
    private ProgressWebView progressWebView;

    private Context mContext;

    private String title;
    private String url;
    private boolean iscored=false;

    public static void startActivity( Context context, String title,String url){
        Intent intent = new Intent( context, CommonWebviewActivity.class );
        intent.putExtra(CommonWebviewActivity.MSG_TITLE,title);
        intent.putExtra(CommonWebviewActivity.MSG_URL,url);
        intent.putExtra(CommonWebviewActivity.MSG_FROM,true);       //true 没有token
        context.startActivity( intent );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad() ? R.layout.activity_common_webview : R.layout.activity_common_webview_mobile);

        x.view().inject(this);
        mContext = this;

        parseIntent();


        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //----------------------------------------------------------------------
    private void parseIntent() {
        Intent intent = getIntent();
        title = intent.getStringExtra(MSG_TITLE);
        url = intent.getStringExtra(MSG_URL);
        iscored = intent.getBooleanExtra(MSG_FROM,false);
    }

    private void initView() {
        workToolbar.setTitle( title );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        },null);
        progressWebView.setListener(new ProgressWebView.FaultListener() {
            @Override
            public void retry() {
                loadUrl();
            }
        });
        loadUrl();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
        }
        return true;
    }
    private void loadUrl() {
        if(iscored){
            progressWebView.loadUrl(url);
        }else {
            progressWebView.loadUrl(DataUtils.getUrl(mContext,url));
        }
//        progressWebView.loadUrl("https://www.baidu.com/");
    }


    private void goBack() {
        if( progressWebView.canGoBack()){
            progressWebView.goBack();
        }else {
            finish();
        }
    }

}
