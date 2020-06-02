package com.tsinghuabigdata.edu.ddmath.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StdAnswerBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.ReviseResultInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SolveThinkBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.CorrectImage;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionAnswerImageView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionItemView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.robotqa.RobotQaActivity;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.AnswerTextView;
import com.tsinghuabigdata.edu.ddmath.view.AskXiaoDouView;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.ArrayList;
import java.util.List;


/**
 * 作业报告——查看题目详情
 * Created by Administrator on 2018/1/23.
 */

public class ReportQuestionDetailActivity extends RoboActivity {


    private static final String PARAM_EXAMID      = "examId";
    private static final String PARAM_QUESTIONID  = "questionId";
    //private static final String PARAM_ALLOWANSWER = "allowAnswer";

    //private WorkToolbar             mWorktoolbar;
    private LoadingPager            mLoadingPager;
    //private ScrollView              mSvContent;
    private QuestionTextView        mTvStem;
    private CorrectImage            mCorrectImage;
    private AskXiaoDouView          mAskXiaoDouView;
    //private CheckBox                mCbUnfold;
    //private LinearLayout            mLlExtend;
    private QuestionAnswerImageView mReviseanswerimage;
    private LinearLayout            mLlModelAnswer;
    //private TextView                mItemDdworkTitleview;
    private AnswerTextView          mTvModelAnswer;
    private QuestionItemView        mThinkView;
    private QuestionItemView        mKnowledgeView;
    private QuestionItemView        mErrReasonsView;


    private String mStudentId = "";
    private String  mExamId;
    private String  mQuestionId;
    private boolean mCorrect;
    private Context mContext;

    public static void startReportQuestionDetailActivity(Context context, String examId, String questionId) {
        Intent intent = new Intent(context, ReportQuestionDetailActivity.class);
        intent.putExtra(PARAM_EXAMID, examId);
        intent.putExtra(PARAM_QUESTIONID, questionId);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_report_question_detail);
        } else {
            setContentView(R.layout.activity_report_question_detail_phone);
        }
        initViews();
        initData();
    }

    private void initViews() {
        WorkToolbar mWorktoolbar =  findViewById(R.id.worktoolbar);
        mLoadingPager =  findViewById(R.id.loadingPager);
        ScrollView mSvContent =  findViewById(R.id.sv_content);
        mTvStem =  findViewById(R.id.tv_stem);
        mCorrectImage =  findViewById(R.id.correctImage);
        mAskXiaoDouView =  findViewById(R.id.askXiaoDouView);
        //CheckBox mCbUnfold =  findViewById(R.id.cb_unfold);
        //LinearLayout mLlExtend =  findViewById(R.id.ll_extend);
        mReviseanswerimage =  findViewById(R.id.reviseanswerimage);
        mLlModelAnswer =  findViewById(R.id.ll_model_answer);
        //TextView mItemDdworkTitleview =  findViewById(R.id.item_ddwork_titleview);
        mTvModelAnswer =  findViewById(R.id.tv_model_answer);
        mThinkView =  findViewById(R.id.think_view);
        mKnowledgeView =  findViewById(R.id.knowledge_view);
        mErrReasonsView =  findViewById(R.id.errReasons_view);

        String title = "查看题目详情";
        mWorktoolbar.setTitle(title);
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        mLoadingPager.setTargetView(mSvContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryQuestionInfo();
            }
        });
    }

    private void initData() {
        mContext = this;
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
        mExamId = getIntent().getStringExtra(PARAM_EXAMID);
        mQuestionId = getIntent().getStringExtra(PARAM_QUESTIONID);
        //mAllowAnswer = getIntent().getBooleanExtra(PARAM_ALLOWANSWER, true);
        if (TextUtils.isEmpty(mExamId) || TextUtils.isEmpty(mQuestionId)) {
            ToastUtils.showShort(mContext, "参数异常");
            finish();
            return;
        }
        queryQuestionInfo();
    }

    private void queryQuestionInfo() {
        new StudyfeedbackModel().querySingleDetail(mStudentId, mExamId, mQuestionId, new RequestListener<QuestionInfo>() {
            @Override
            public void onSuccess(QuestionInfo vo) {
                mLoadingPager.showTarget();
                showQuestion(vo);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void showQuestion(QuestionInfo item) {
        mCorrect = item.isCorrect();
        mTvStem.setSingleQuestionBook(item, true);

        if (!TextUtils.isEmpty(item.getAnswerUrl())) {    //网络图片
            mCorrectImage.setVisibility(View.VISIBLE);
            mCorrectImage.loadImage(item.getAnswerUrl(), item.getAnswerArea(), "");
        } else {
            mCorrectImage.setVisibility(View.GONE);
        }

        ReviseResultInfo reviseResultInfo = item.getReviseResultResponse();
        String reviseAnswerUrl = "";
        String reviseReasons = "";
        if (reviseResultInfo != null) {
            reviseAnswerUrl = reviseResultInfo.getAnswerUrl();
            reviseReasons = reviseResultInfo.getErrorAnalysis();
        }

        //没有班级意味着，所在的班已经完成上课了，所以可以看
        boolean allowAnswer = true;
        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
        if (currentClassInfo != null) {
            allowAnswer = currentClassInfo.isAllowAnswer();
        }


        List<StdAnswerBean> stdAnswers = item.getStdAnswers();
        if(!allowAnswer || stdAnswers == null || stdAnswers.size() == 0) {
            mLlModelAnswer.setVisibility(View.GONE);
            mTvModelAnswer.setVisibility(View.GONE);
        } else {
            mLlModelAnswer.setVisibility(View.VISIBLE);
            setStdAnswer(item.getStdAnswers(), mTvModelAnswer);
        }

        //在7天范围之内并且没有订正过则显示参考答案且 未掌握
        /*if (TextUtils.isEmpty(reviseAnswerUrl *//*|| !allowAnswer*//*)) {
            mLlModelAnswer.setVisibility(View.GONE);
            mTvModelAnswer.setVisibility(View.GONE);
        } else {
            mLlModelAnswer.setVisibility(View.VISIBLE);
            setStdAnswer(item.getStdAnswers(), mTvModelAnswer);
        }
*/
        //解题思路
        ArrayList<SolveThinkBean> solutions = item.getSolutions();
        if (solutions != null && solutions.size() > 0) {
            mThinkView.setVisibility(View.VISIBLE);
            DDWorkUtil.setSolveThink(solutions, mThinkView);
        } else {
            mThinkView.setVisibility(View.GONE);
        }

        //错误知识点
        String data = DDWorkUtil.getErrorKnowledgeStr(item);
        if (mCorrect || TextUtils.isEmpty(data)) {
            mKnowledgeView.setVisibility(View.GONE);
        } else {
            mKnowledgeView.setVisibility(View.VISIBLE);
            mKnowledgeView.setData("错误知识点", data, null, null);
        }

        //错题原因
        if (!TextUtils.isEmpty(reviseReasons)) {
            mErrReasonsView.setVisibility(View.VISIBLE);
            mErrReasonsView.setReasonData("错题原因", reviseReasons);
        } else {
            mErrReasonsView.setVisibility(View.GONE);
        }

        //问小豆
        mAskXiaoDouView.setCount(item.getAsked());
        mAskXiaoDouView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotQaActivity.startRobotQaActivity(mContext, 1, mExamId, mQuestionId, mCorrect, "{}", false);
            }
        });
        //问小豆设置   主观题才显示
        String qtpye = item.getQuestionType();
        if (!TextUtils.isEmpty(qtpye) && (qtpye.contains(QuestionInfo.TYPE_SOLUTION) || qtpye.contains(QuestionInfo.TYPE_CALCULATION))) {
            mAskXiaoDouView.setVisibility(View.VISIBLE);
        } else {
            mAskXiaoDouView.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(reviseAnswerUrl)){
            mReviseanswerimage.setVisibility(View.GONE);
        }else {
            //订正答案显示
            mReviseanswerimage.setVisibility(View.VISIBLE);
            int revise_status = DDWorkUtil.getQuestionCorrectStatus(item, true, DDWorkDetail.WORK_CORRECTED);
            mReviseanswerimage.setReviseData(item, revise_status, "");
        }

    }

    private void setStdAnswer(List<StdAnswerBean> stdAnswers, AnswerTextView answerTextView) {
        if (/*allowAnswer &&*/ stdAnswers == null || stdAnswers.size() == 0) {
            answerTextView.setText(null);
            return;
        }
        StdAnswerBean fBean = stdAnswers.get(0);
        String contentLatextGraph = fBean.getContentLatextGraph();
        String contentg = fBean.getContentg();
        String graph = fBean.getGraph();
        if (stdAnswers.size() > 1) {
            for (int i = 1; i < stdAnswers.size(); i++) {
                StdAnswerBean bean = stdAnswers.get(i);
                if (TextUtils.isEmpty(bean.getContentg())) {
                    contentg += "\n";
                } else {
                    contentg += "\n" + bean.getContentg();
                }
                if (!TextUtils.isEmpty(bean.getContentLatextGraph())) {
                    if (TextUtils.isEmpty(contentLatextGraph)) {
                        contentLatextGraph += bean.getContentLatextGraph();
                    } else {
                        contentLatextGraph += "#%#" + bean.getContentLatextGraph();
                    }
                }
                if (!TextUtils.isEmpty(bean.getGraph())) {
                    if (TextUtils.isEmpty(graph)) {
                        graph += bean.getGraph();
                    } else {
                        graph += "#%#" + bean.getGraph();
                    }
                }
            }
        }
        StdAnswerBean stdAnswerBean = new StdAnswerBean();
        stdAnswerBean.setContentg(contentg);
        stdAnswerBean.setContentLatextGraph(contentLatextGraph);
        stdAnswerBean.setGraph(graph);
        answerTextView.setQuestionBook(stdAnswerBean, false);
    }


}
