package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RobotQaModel;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.StdAnswerBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SolveThinkBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionItemView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.QuestionTextView;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.AnswerTextView;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 问小豆——查看详情
 * Created by Administrator on 2017/9/13.
 */

public class QuestionDetailActivity extends RoboActivity {

    private LoadingPager     mLoadingPager;
    private ScrollView       mSvContent;
    private LinearLayout     mLlContent;
    private QuestionTextView mTvStem;
    private LinearLayout     mLlModelAnswer;
    private AnswerTextView   mTvModelAnswer;
    private QuestionItemView mKnowledgeView;

    private String  mQuestionId;
    private boolean mAllowAnswer;
    private Context mContext;
    private RobotQaModel mRobotQaModel = new RobotQaModel();

    public static void startQuestionDetailActivity(Context context, String questionId, boolean allowAnswer) {
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        intent.putExtra("questionId", questionId);
        intent.putExtra("allowAnswer", allowAnswer);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_question_detail);

        initViews();
        initData();
    }

    private void initViews() {

        WorkToolbar workToolbar = findViewById(R.id.work_toolbar);
        workToolbar.setTitle("查看详情");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        mLoadingPager =  findViewById(R.id.loading_pager);
        mSvContent =  findViewById(R.id.sv_content);
        mLlContent =  findViewById(R.id.ll_content);
        mTvStem =  findViewById(R.id.tv_stem);
        mLlModelAnswer =  findViewById(R.id.ll_model_answer);
        mTvModelAnswer =  findViewById(R.id.tv_model_answer);
        mKnowledgeView =  findViewById(R.id.knowledge_view);

        mLoadingPager.setTargetView(mSvContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryQuestionInfo();
            }
        });
    }

    private void initData() {
        mContext = this;
        mQuestionId = getIntent().getStringExtra("questionId");
        mAllowAnswer = getIntent().getBooleanExtra("allowAnswer", true);
        if (TextUtils.isEmpty(mQuestionId)) {
            ToastUtils.showShort(mContext, "参数异常");
            finish();
            return;
        }
        queryQuestionInfo();
    }

    private void queryQuestionInfo() {
        mRobotQaModel.getQuestionInfo(mQuestionId, new RequestListener<QuestionInfo>() {
            @Override
            public void onSuccess(QuestionInfo vo) {
                mLoadingPager.showTarget();
                showData(vo);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void showData(QuestionInfo item) {
        mTvStem.setQuestionBook(item, true);
        List<StdAnswerBean> stdAnswers = item.getStdAnswers();
        if (!mAllowAnswer) {
            mLlModelAnswer.setVisibility(View.GONE);
            mTvModelAnswer.setVisibility(View.GONE);
        } else if (stdAnswers != null && stdAnswers.size() > 0) {
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
            mTvModelAnswer.setQuestionBook(stdAnswerBean, false);
        } else {
            mTvModelAnswer.setText(null);
        }

        ArrayList<SolveThinkBean> solutions = item.getSolutions();
        if (solutions != null && solutions.size() > 0) {
            mKnowledgeView.setVisibility(View.VISIBLE);
            DDWorkUtil.setSolveThink(solutions, mKnowledgeView);
        } else {
            mKnowledgeView.setVisibility(View.GONE);
        }
    }
}
