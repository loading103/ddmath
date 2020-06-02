package com.tsinghuabigdata.edu.ddmath.module.studycheat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.sys.InstallUtil;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.AbilityQueryBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.ErrorQuestionQueryBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.dailog.QuestionReviewDialog;
import com.tsinghuabigdata.edu.ddmath.requestHandler.StudyCheatService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.StudyCheatServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.StringUtil;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;
import com.tsinghuabigdata.edu.ddmath.view.ProgressWebView;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * 错题回顾
 */
public class ErrQuestionReviewActivity extends RoboActivity implements ProgressWebView.ErrorQuestionListener, ProgressWebView.LoadFinishListener, ProgressWebView.FaultListener {

    public static final String PARAM_KNOWID = "knowId";
    public static final String RESULT_PARAM = "reward";

    @ViewInject(R.id.work_toolbar)
    private WorkToolbar workToolbar;

    @ViewInject(R.id.loadingPager)
    private LoadingPager mLoadingPager;

    @ViewInject(R.id.progress_webview)
    private ProgressWebView mWebView;

    //从知识点界面 进入需要传 知识点ID
    private String mKnowId;

    private StudyCheatService studyCheatService;

    private ErrorQuestionQueryBean mQueryBean;

    private Context mContext;

    private long startTime, endtime;
    private ProgressDialog mProgressDialog;

    private GetQuestionDetailTask mGetQuestionDetailTask;
    private SubmitAnswerTask      mSubmitAnswerTask;

    private boolean urlLoadFinished = false;

    public void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( GlobalData.isPad() ? R.layout.activity_errorquestionreview : R.layout.activity_errorquestionreview_phone );
        mContext = this;
        x.view().inject(this);

        workToolbar.setTitle( "错题回顾" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLeftClick();
            }
        }, null);

        studyCheatService = new StudyCheatServiceImpl();
        parseIntent();
        initView();

        loadQuestion();
        MobclickAgent.onEvent( this, "cheat_errreview" );
    }

    @Override
    public void submitReasons(final String reasons, final int level) {

        if (endtime == 0)
            endtime = System.currentTimeMillis();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //提交用户操作数据   成功，完成任务
                if (mSubmitAnswerTask == null || mSubmitAnswerTask.isComplete() || mSubmitAnswerTask.isCancelled()) {

                    mProgressDialog.setMessage(getResources().getString(R.string.wait_submit));
                    mProgressDialog.show();

                    mSubmitAnswerTask = new SubmitAnswerTask(level, reasons, (endtime - startTime) / 1000);
                    mSubmitAnswerTask.execute();
                }
            }
        });

    }

    public void onLeftClick() {
        quit(false, 0);
    }

    @Override
    public void onBackPressed() {
        quit(false, 0);
    }

    @Override
    public void loadfinished() {
        urlLoadFinished = true;
    }

    //------------------------------------------------------------------------------------
    private void parseIntent() {
        if (getIntent().hasExtra(PARAM_KNOWID)) {
            mKnowId = getIntent().getStringExtra(PARAM_KNOWID);
        }
    }

    private void initView() {
        //
        mWebView.setErrorQuestionListener(this);
        mWebView.setLoadFinishListener(this);
        mWebView.setListener(this);

        //异常重试
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadQuestion();
            }
        });
        //meterView.setUserData( 0, 65 );

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
    }

    private void quit(boolean hasReward, float vaule) {

        if (hasReward) {
            Intent intent = new Intent();
            //intent.putExtra( RESULT_PARAM, vaule );
            setResult(RESULT_OK, intent);
            finish();
        } else {
            if (mQueryBean == null || !urlLoadFinished) {
                finish();
                return;
            }

            QuestionReviewDialog dialog = new QuestionReviewDialog(mContext, R.style.FullTransparentDialog);
            dialog.setData(getResources().getString(R.string.errreview_quit), "放弃", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {        //确认返回
                    Intent intent = new Intent();
                    //intent.putExtra( RESULT_PARAM, vaule );
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }, null);
            dialog.show();
        }
    }


    private void loadUrl() {

        urlLoadFinished = false;

        UserDetailinfo studentInfo = AccountUtils.getUserdetailInfo();
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo == null || studentInfo == null) {
            AlertManager.toast(this, "未能获取到学生信息。");
            return;
        }

        //http://teach.iclassedu.com/mobile/errorReview.html? access_token=xxx studentId=xxx questionId=xxx
        String baseUrl = /*AppRequestConst.WEB_ADDRESS*/"http://teach.iclassedu.com" + AppRequestConst.WEB_ERROR_QUESTION_REVIEW;       //这里用VIP的链接
        /*try {
            url = url.concat("access_token=").concat(URLEncoder.encode( loginInfo.getAccessToken(), "UTF-8"));
        }catch(UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE,"err", e);
            url = url.concat("access_token=").concat( loginInfo.getAccessToken() );
        }
        url = url.concat("&studentId=").concat( studentInfo.getStudentId() );
        url = url.concat("&questionId=") +  mQueryBean.getQuestionId();
        url = url.concat("&requestId=")  + mQueryBean.getRequestId();
        url = url.concat("&v=") + InstallUtil.getVersionName( mContext, getPackageName() );
        url = url.concat("&type=doudou");*/

        HashMap<String, String> map = new HashMap<String, String>();
        try {
            map.put("access_token", URLEncoder.encode(loginInfo.getAccessToken(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            map.put("access_token", loginInfo.getAccessToken());
        }
        map.put("studentId", studentInfo.getStudentId());
        map.put("questionId", mQueryBean.getQuestionId());
        map.put("requestId", mQueryBean.getRequestId());
        map.put("v", InstallUtil.getVersionName(mContext, getPackageName()));
        map.put("type", "doudou");
        String url = baseUrl + StringUtil.getUrl(map);
        AppLog.d(" xxxxxurl = " + url);
        mWebView.loadUrl(url);
    }

    /**
     * 加载题目
     */
    private void loadQuestion() {
        if (mGetQuestionDetailTask == null || mGetQuestionDetailTask.isComplete() || mGetQuestionDetailTask.isCancelled()) {
            mQueryBean = null;
            mLoadingPager.showLoading();
            mGetQuestionDetailTask = new GetQuestionDetailTask();
            mGetQuestionDetailTask.execute();
        }
    }

    @Override
    public void retry() {
        loadQuestion();
    }

    //    private Handler mHandler = new Handler(){
    //        public void handleMessage( Message msg ){
    //            if( msg.what == 0 ){
    //                mProgressDialog.dismiss();
    //            }
    //        }
    //    };
    //    private void hideProgressDialog(){
    //        mHandler.sendEmptyMessage( 0 );
    //    }
    //---------------------------------------------------------------------------------------------------

    //查询错题回顾的信息
    private class GetQuestionDetailTask extends AppAsyncTask<String, Void, ErrorQuestionQueryBean> {

        @Override
        protected ErrorQuestionQueryBean doExecute(String... params) throws Exception {
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if (loginInfo == null || detailinfo == null)
                throw new Exception("请登录");

            return studyCheatService.getErrorReivewQuestion(loginInfo.getAccessToken(), detailinfo.getStudentId(), mKnowId);
        }

        @Override
        protected void onResult(ErrorQuestionQueryBean queryBean) {
            mLoadingPager.hideall();

            if (queryBean == null) {
                if (TextUtils.isEmpty(mKnowId))
                    AlertManager.toast(mContext, getResources().getString(R.string.nodata_question_review));
                else
                    AlertManager.toast(mContext, getResources().getString(R.string.nodata_question_review_know));
                finish();
            } else {
                startTime = System.currentTimeMillis();
                endtime = 0;
                mQueryBean = queryBean;
                loadUrl();
            }
        }

        @Override
        protected void onFailure(HttpResponse<ErrorQuestionQueryBean> response, Exception ex) {
            mLoadingPager.showFault(ex);
            AlertManager.showErrorInfo(mContext, ex);
        }
    }

    //提交结果
    private class SubmitAnswerTask extends AppAsyncTask<String, Void, AbilityQueryBean> {

        private String reasons;
        private long   time;
        private int    degree;

        SubmitAnswerTask(int degree, String reasons, long time) {
            this.degree = degree;
            this.reasons = reasons;
            this.time = time;
        }

        @Override
        protected AbilityQueryBean doExecute(String... params) throws Exception {
            LoginInfo loginInfo = AccountUtils.getLoginUser();
            UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
            if (loginInfo == null || detailinfo == null)
                throw new Exception("请登录");
            return studyCheatService.submitErrorReivewResult(loginInfo.getAccessToken(), detailinfo.getStudentId(), mQueryBean.getQuestionId(), reasons, mQueryBean.getRequestId(), degree, time);
        }

        @Override
        protected void onResult(final AbilityQueryBean abilityBean) {
            mProgressDialog.dismiss();
            //
            if (abilityBean == null) {
                AlertManager.toast(mContext, "提交答案失败");
                return;
            }

            //显示结果
            QuestionReviewDialog dialog = new QuestionReviewDialog(mContext, R.style.FullTransparentDialog);
            final boolean hasQuestion = abilityBean.getSurplus() != 0;
            dialog.setFinishReviewData(abilityBean, hasQuestion ? "再来一道" : "真棒，全部完成了！", "休息一下",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (hasQuestion) {
                                loadQuestion();
                            } else {
                                quit(true, abilityBean.getIncrease());
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            quit(true, abilityBean.getIncrease());
                        }
                    });
            dialog.show();
        }

        @Override
        protected void onFailure(HttpResponse<AbilityQueryBean> response, Exception ex) {
            mProgressDialog.dismiss();
            //AlertManager.showErrorInfo( mContext, ex);
            AlertManager.toast(mContext, "提交答案失败");
        }
    }

}
