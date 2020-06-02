package com.tsinghuabigdata.edu.ddmath.module.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 消息跳转到webview查看结果界面
 */
@Deprecated
public class ShowMsgWebviewActivity extends RoboActivity {

    public static final String MSG_TITLE = "msgtitle";
    public static final String MSG_URL = "msgurl";

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    @ViewInject(R.id.progress_webview)
    private ProgressWebView progressWebView;

    private Context mContext;

    private String rowKey;      //消息ID，
    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(GlobalData.isPad() ? R.layout.activity_showmsg_webview : R.layout.activity_showmsg_webview_mobile);
//        setLeftTitle("返回", R.color.white);
//        setBarTextcolor(R.color.white);

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
    }

    private void initView() {
        workToolbar.setTitle(title);
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void loadUrl() {
        progressWebView.loadUrl(getUrl(url));
//        progressWebView.loadUrl("https://www.baidu.com/");
    }

    private String getUrl(String url) {

        Map<String, String> restfulParams = new HashMap<>();

        // 编译为Restful Url
        url = getRestfulUrl(AppRequestConst.WEB_ADDRESS + url, restfulParams);
        // 加入token
        String token = AccountUtils.getLoginUser().getAccessToken();
        try {
            url += "?access_token=" + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            url = null;
        }

        AppLog.d("shdlkasflkdsf  uuurl = " + url);
        return url;
    }

    private String getRestfulUrl(String url, Map<String, String> restfulParams) {
        String reqUrl = url;
        if (restfulParams.size() > 0) {
            Set<String> keys = restfulParams.keySet();
            for (String key : keys) {
                reqUrl = reqUrl.replace(":" + key, restfulParams.get(key));
            }
        }
        return reqUrl;
    }
}
