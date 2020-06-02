package com.tsinghuabigdata.edu.ddmath.module.entrance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.sys.InstallUtil;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
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
 * 素质分析报告查看结果界面
 */
public class QualityAnalysisWebviewActivity extends RoboActivity {
    private static final String TAG = "QualityAnalysisWebviewActivity";
    public static final String MSG_TITLE = "msgtitle";
    public static final String MSG_URL = "msgurl";
    public static final String EDIT_STATUS = "editstauts";  //0:新建 1：查看并可编辑 2:查看并不可编辑
    public static final String SOURCE = "source"; //0:入学前评测， 1：正式班素质报告

    public static final int SRC_BEFORE_JOINCLASS = 0;
    public static final int SRC_JOINEDCLASS = 1;

    //输出参数
    public final static String PARAM_QUALITY_STATUS = "qualityComplete";
    public final static String PARAM_QUALITY_MODIFY = "qualiyModify";

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;
    @ViewInject(R.id.progress_webview)
    private ProgressWebView progressWebView;

    private Context mContext;

    private String title;
    private String classId;
    private int editstatus;
    private int mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalData.isPad() ? R.layout.activity_qualityana_webview : R.layout.activity_qualityana_webview_mobile);

        x.view().inject(this);
        mContext = this;

        parseIntent();

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onLeftClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            progressWebView.getWebview().evaluateJavascript("window.hasUserEditData()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    boolean hasChanged = Boolean.parseBoolean(value);
                    if (hasChanged) {
                        back();
                    } else {
                        endSelf();
                    }
                }
            });
        } else {
            back();
        }
    }

    @Override
    public void onBackPressed() {
        onLeftClick();
    }

    private void back() {
        if (editstatus == EntranceEvaluationActivity.RESULT_NOT_EDIT) {
            //不可编辑
            endSelf();
            return;
        }
        switch (progressWebView.getLoadStatus()) {
            case LOAD_ERROR:
            case LOADING:
                endSelf();
                return;

            case LOAD_SUCCESS:
                break;
            default:
                break;
        }
        AlertManager.showCustomDialog(mContext, "你填写的信息将不会被保存哦～\n" +
                "确定要退出吗？", "放弃保存并退出", "留在此页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endSelf();
            }
        }, null);
    }

    private void endSelf() {
        Intent intent = new Intent();
        intent.putExtra(PARAM_QUALITY_STATUS, progressWebView.isQualityComplete());
        intent.putExtra(PARAM_QUALITY_MODIFY, progressWebView.isQualityModify());
        setResult(0, intent);
        finish();
    }

    //----------------------------------------------------------------------
    private void parseIntent() {
        Intent intent = getIntent();
        title = intent.getStringExtra(MSG_TITLE);
        classId = intent.getStringExtra(EntranceEvaluationActivity.CLASSID);
        editstatus = intent.getIntExtra(EDIT_STATUS, 0);
        mSource = intent.getIntExtra(SOURCE, SRC_BEFORE_JOINCLASS);
    }

    private void initView() {
        workToolbar.setTitle( title );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClick();
            }
        },null);
        progressWebView.setListener(new ProgressWebView.FaultListener() {
            @Override
            public void retry() {
                loadUrl();
            }
        });

        progressWebView.setWinCloseListener(new ProgressWebView.WindowCloseListener() {
            @Override
            public void closeWindow() {
                endSelf();
            }
        });

        loadUrl();
    }

    private void loadUrl() {
        progressWebView.loadUrl(getUrl());
//        progressWebView.loadUrl("https://www.baidu.com/");
    }

    private String getUrl() {
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.QUALITY_ANALYSIS_REPORT;

        String studentId = AccountUtils.getUserdetailInfo().getStudentId();
        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);
        restfulParams.put("classId", classId);
        restfulParams.put("status", editstatus + "");

        // 编译为Restful Url
        if (restfulParams.size() > 0) {
            Set<String> keys = restfulParams.keySet();
            for (String key : keys) {
                url = url.replace(":" + key, restfulParams.get(key));
            }
        }

        url += "?source=" + mSource;
        //url += "&v=" + InstallUtil.getVersionName( mContext, getPackageName() );
        url = AppUtils.getUrlTimestamp( url );

        // 加入token
        String token = AccountUtils.getLoginUser().getAccessToken();
        try {
            url += "&access_token=" + URLEncoder.encode(token, "UTF-8");
            url += "&v=" + InstallUtil.getVersionName( mContext, getPackageName() );
        } catch (UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            url = null;
        }

        AppLog.d("enter  uuurl = " + url);
        return url;
    }
}
