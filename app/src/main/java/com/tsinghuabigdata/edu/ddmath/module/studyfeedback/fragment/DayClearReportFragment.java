package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.fragment.ReportBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog.ShareLinkDialog;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.DayClearBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ReportUtils;

import java.text.SimpleDateFormat;


/**
 * 错题订正报告
 * Created by Administrator on 2018/3/23.
 */
@Deprecated
public class DayClearReportFragment extends ReportBaseFragment {


    private Context mContext;

    private String mStudentId = "";
    private WebView   mWeb;
    private ImageView mIvShare;
    private String    mUrl;
    private LinearLayout mEmptyLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_day_clear_report, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_day_clear_report_mobile, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    private void initView(View root) {
        mContext = getActivity();
        mWeb = (WebView) root.findViewById(R.id.webView_report);
        mIvShare = (ImageView) root.findViewById(R.id.iv_share);
        mEmptyLayout = (LinearLayout) root.findViewById(R.id.empty_layout);
        mIvShare.setVisibility(View.INVISIBLE);
        initWebView();
    }

    private void initWebView() {
        //webview.clearCache( false );
        if (AppUtils.isDebug()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        //
        WebSettings webSettings = mWeb.getSettings();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWeb.removeJavascriptInterface("searchBoxJavaBridge_");
        }
        mWeb.removeJavascriptInterface("accessibility");
        mWeb.removeJavascriptInterface("accessibilityTraversal");

        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogUtils.i("sdfsfdsg onReceivedError errorCode= " + errorCode);
                view.stopLoading();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LogUtils.d("sdfsfdsg onPageFinished url= " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtils.d("sdfsfdsg shouldOverrideUrlLoading url = " + url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }


    private void initData() {
        createLoginInfo();
        loadReport();
        queryReviseDetail();
    }

    private void queryReviseDetail() {
        new StudyfeedbackModel().queryReviseDetail(mStudentId, new RequestListener<DayClearBean>() {

            @Override
            public void onSuccess(DayClearBean res) {
                if(!res.getHasReviseReport()){//如果是false，代表网页无数据
                    mEmptyLayout.setVisibility(View.VISIBLE);
                    mWeb.setVisibility(View.GONE);
                    return;
                }
                mEmptyLayout.setVisibility(View.GONE);
                mWeb.setVisibility(View.VISIBLE);
                LogUtils.i("queryReviseDetail() onSuccess");
                if (res != null && res.getStartTime()>0) {
                    //获取到报告时间后展示分享按钮
                    showShare(res.getStartTime());
                }
            }

            @Override
            public void onFail(HttpResponse<DayClearBean> response, Exception ex) {
                LogUtils.i("queryReviseDetail() onFail");
            }
        });
    }

    private void showShare(final long startTime) {
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkDialog shareLinkDialog = new ShareLinkDialog(mContext);
                String shareName = ReportUtils.getShareName("最近一周错题订正");
                shareLinkDialog.setShareInfo(DataUtils.getUrl(mContext, mUrl), shareName, getShareText(startTime), true);
                shareLinkDialog.show();
            }
        });
        mIvShare.setVisibility(View.VISIBLE);
    }

    //    http://teach.doudoushuxue.com/rest/teaching/dd/v1/report/revise/class/student?studentId=STUB92FC9EC3527487BBA8794BE77FB3F11

    //查询登录信息www.doudoushuxue.com/instructions?studentId=STUB92FC9EC3527487BBA8794BE77FB3F11
    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
    }

    private void loadReport() {
        String baseUrl = MessageUtils.getCorrectReportUrl(mStudentId);
        mUrl = DataUtils.getUrl(mContext, baseUrl);
        LogUtils.i("errorCorrectReport url=" + mUrl);
        mWeb.loadUrl(mUrl);
    }

    @Override
    protected void refreshReport() {
        if (isPrepared) {
            loadReport();
        }
    }

    private String getShareText(long createtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm生成");
        return dateFormat.format(createtime);
    }

    public String getUmEventName() {
        return "report_dayclean";
    }
}
