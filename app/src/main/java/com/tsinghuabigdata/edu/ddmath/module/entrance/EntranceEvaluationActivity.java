package com.tsinghuabigdata.edu.ddmath.module.entrance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.MVPModel.EntranceModel;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.dialog.EntranceDialog;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EnterBean;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EntranceDetail;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import static com.tsinghuabigdata.edu.ddmath.module.entrance.EntranceListActivity.PARAM_SUBMIT_MODIFY;
import static com.tsinghuabigdata.edu.ddmath.module.entrance.EntranceListActivity.PARAM_SUBMIT_STATUS;
import static com.tsinghuabigdata.edu.ddmath.module.entrance.QualityAnalysisWebviewActivity.PARAM_QUALITY_MODIFY;
import static com.tsinghuabigdata.edu.ddmath.module.entrance.QualityAnalysisWebviewActivity.PARAM_QUALITY_STATUS;


/**
 * 入学评测界面
 * Created by Administrator on 2017/2/17.
 */
@Deprecated
public class EntranceEvaluationActivity extends RoboActivity implements View.OnClickListener {

    private static final String TAG     = "Entrance";
    public static final  String CLASSID = "classid";

    private LinearLayout   mLlEntranceContent;
    private LinearLayout   mLlEntranceApply;
    private LinearLayout   mLlEntranceReturn;
    private TextView       mTvReturnReason;
    private RelativeLayout mRlQualityAnalyze;
    private TextView       mTvQuality;
    private TextView       mTvQualityModify;
    private TextView       mTvQualityTip;
    private ImageView      mIvQuality;
    private RelativeLayout mRlKnowledgeAnalyze;
    private TextView       mTvKnowledge;
    private TextView       mTvKnowledgeModify;
    private TextView       mTvKnowledgeTip;
    private ImageView      mIvKnowledge;
    private RelativeLayout mRlAccurateEvaluation;
    private TextView       mTvAccurateTip;
    private Button         mBtnSeeReport;
    private LoadingPager   mLoadingPager;

    //1:待提交 2：已提交 3：拒绝 4：同意 5：报告已生成  6：报告已发送
    private static final int TO_SUBMIT             = 1;
    private static final int SUBMITTED             = 2;
    private static final int REJECT                = 3;
    private static final int AGREE                 = 4;
    private static final int REPORT_GENERATED      = 5;
    private static final int REPORT_SENDED         = 6;
    private static final int CODE_KNOWLEDGEANALYSE = 100;
    private static final int CODE_QUALITY_ANALYZE  = 101;

    public static final int NEW             = 0;//新建
    public static final int RESULT_EDIT     = 1;//查看并可编辑
    public static final int RESULT_NOT_EDIT = 2;//查看并不可编辑

    private Context mContext;
    private int     state;
    private boolean qualityFinish;
    private boolean knowledgeFinish;
    private boolean qualityModify;
    private boolean knowledgeModify;
    private boolean commentReport;
    private String  reportId;
    private String  studentId;
    private String  mClassId;
    private EntranceModel mModel = new EntranceModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_picture_preview);
        setContentView(GlobalData.isPad() ? R.layout.activity_entrance_evaluation : R.layout.activity_entrance_evaluation_phone);

        initView();
        initData();
    }

    private void initView() {
        WorkToolbar workToolbar = findViewById(R.id.work_toolbar);
        workToolbar.setTitle("入学评测");
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        },null);

        mLlEntranceContent =  findViewById(R.id.ll_entrance_content);
        mLlEntranceApply =  findViewById(R.id.ll_entrance_apply);
        mLlEntranceReturn =  findViewById(R.id.ll_entrance_return);
        mTvReturnReason =  findViewById(R.id.tv_return_reason);
        mRlQualityAnalyze =  findViewById(R.id.rl_quality_analyze);
        mTvQuality =  findViewById(R.id.tv_quality);
        mTvQualityModify =  findViewById(R.id.tv_quality_modify);
        mTvQualityTip =  findViewById(R.id.tv_quality_tip);
        mIvQuality =  findViewById(R.id.iv_quality);
        mRlKnowledgeAnalyze =  findViewById(R.id.rl_knowledge_analyze);
        mTvKnowledge =  findViewById(R.id.tv_knowledge);
        mTvKnowledgeModify =  findViewById(R.id.tv_knowledge_modify);
        mTvKnowledgeTip =  findViewById(R.id.tv_knowledge_tip);
        mIvKnowledge =  findViewById(R.id.iv_knowledge);
        mRlAccurateEvaluation =  findViewById(R.id.rl_accurate_evaluation);
        mTvAccurateTip =  findViewById(R.id.tv_accurate_tip);
        mBtnSeeReport =  findViewById(R.id.btn_see_report);
        mLoadingPager =  findViewById(R.id.loadingPager);

        mLoadingPager.setTargetView(mLlEntranceContent);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryEnterDetail();
                mLoadingPager.showLoading();
            }
        });
        mRlQualityAnalyze.setOnClickListener(this);
        mRlKnowledgeAnalyze.setOnClickListener(this);
        mRlAccurateEvaluation.setOnClickListener(this);
        mBtnSeeReport.setOnClickListener(this);
    }

    private void initData() {
        mContext = this;
        mClassId = getIntent().getStringExtra(CLASSID);
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null) {
            ToastUtils.showShort(this, "请先登录");
            finish();
            return;
        }
        studentId = detailinfo.getStudentId();
        queryEnterDetail();
    }

    private void queryEnterDetail() {
        mModel.queryEnterDetail(studentId, new RequestListener<EntranceDetail>() {

            @Override
            public void onSuccess(EntranceDetail vo) {
                Log.i(TAG, "queryEnterDetail onSuccess");
                if (vo == null) {
                    vo = new EntranceDetail();
                }
                state = vo.getAudit();
                vo.isKnowledge();
                qualityFinish = vo.isQuality();
                knowledgeFinish = vo.isKnowledge();
                qualityModify = vo.isQualityModify();
                knowledgeModify = vo.isKnowledgeModify();
                commentReport = vo.isCommentReport();
                reportId = vo.getReportId();
                showData(vo.getRejectReason());
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<EntranceDetail> response, Exception ex) {
                Log.i(TAG, "queryEnterDetail onFail");
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void showData(String rejectReason) {
        showQuality();
        showKnowledge();
        showAccurate();
        switch (state) {
            case TO_SUBMIT:
                mBtnSeeReport.setText("申请领取报告（专业版）");
                break;
            case SUBMITTED:
                mBtnSeeReport.setText("已申请领取");
                mBtnSeeReport.setEnabled(false);
                break;
            case REJECT:
                mBtnSeeReport.setText("重新申请领取报告（专业版）");
                mLlEntranceApply.setVisibility(View.GONE);
                mTvReturnReason.setText(rejectReason);
                mLlEntranceReturn.setVisibility(View.VISIBLE);
                break;
            case AGREE:
                mBtnSeeReport.setText("已申请领取");
                mBtnSeeReport.setEnabled(false);
                break;
            case REPORT_GENERATED:
                mBtnSeeReport.setText("已申请领取");
                mBtnSeeReport.setEnabled(false);
                break;
            case REPORT_SENDED:
                mBtnSeeReport.setText("查看报告");
                break;
            default:
                state = 1;
                mBtnSeeReport.setText("申请领取报告（专业版）");
                break;
        }
    }

    private void showQuality() {
        if (qualityFinish) {
            mTvQuality.setText("A\n我的素质分析");
            mTvQualityTip.setVisibility(View.INVISIBLE);
            mIvQuality.setVisibility(View.VISIBLE);
            showQualityModify();
        } else {
            mTvQuality.setText("A\n素质分析");
            mTvQualityTip.setVisibility(View.VISIBLE);
            mIvQuality.setVisibility(View.INVISIBLE);
        }
    }

    private void showQualityModify() {
        if (qualityModify && state == REJECT) {
            mTvQualityModify.setVisibility(View.VISIBLE);
        } else {
            mTvQualityModify.setVisibility(View.INVISIBLE);
        }
    }

    private void showKnowledge() {
        if (knowledgeFinish) {
            mTvKnowledge.setText("B\n我的知识分析");
            mTvKnowledgeTip.setVisibility(View.INVISIBLE);
            mIvKnowledge.setVisibility(View.VISIBLE);
            showKnowledgeModify();
        } else {
            mTvKnowledge.setText("B\n知识分析");
            mTvKnowledgeTip.setVisibility(View.VISIBLE);
            mIvKnowledge.setVisibility(View.INVISIBLE);
        }
    }

    private void showKnowledgeModify() {
        if (knowledgeModify && state == REJECT) {
            mTvKnowledgeModify.setVisibility(View.VISIBLE);
        } else {
            mTvKnowledgeModify.setVisibility(View.INVISIBLE);
        }
    }

    private void showAccurate() {
        if (qualityFinish && knowledgeFinish) {
            mTvAccurateTip.setVisibility(View.INVISIBLE);
        } else {
            mTvAccurateTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_quality_analyze:
                gotoQualityAnalysis();
                break;
            case R.id.rl_knowledge_analyze:
                gotoKnowledgeAnalyze();
                break;
            case R.id.rl_accurate_evaluation:
                showAccuateDialog();
                //ApplyForReport();
                break;
            case R.id.btn_see_report:
                String str = mBtnSeeReport.getText().toString();
                if (str.startsWith("申请")) {
                    if (!qualityFinish || !knowledgeFinish) {
                        showApplyDialog();
                    } else {
                        ApplyForReport();
                    }
                } else if (str.startsWith("重新")) {
                    ApplyForReport();
                } else if (str.startsWith("查看")) {
                    if (!TextUtils.isEmpty(reportId)) {
                        MessageUtils.gotoAllroundReport(mContext, reportId, "综合能力", null);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showAccuateDialog() {
        EntranceDialog entranceDialog = new EntranceDialog(this, R.style.dialog);
        entranceDialog.show();
    }

    private void showApplyDialog() {
        EntranceDialog entranceDialog = new EntranceDialog(this, R.style.dialog);
        entranceDialog.setData("需完成AB两项才能申请专业版的入学评测报告哦～");
        entranceDialog.show();
    }

    private void ApplyForReport() {
        mModel.applyForReport(studentId, new RequestListener<EnterBean>() {

            @Override
            public void onSuccess(EnterBean vo) {
                ToastUtils.showShort(mContext, "申请已发送成功！");
                state = SUBMITTED;
                mBtnSeeReport.setText("已申请领取");
                mBtnSeeReport.setEnabled(false);
                showKnowledgeModify();
                showQualityModify();
            }

            @Override
            public void onFail(HttpResponse<EnterBean> response, Exception ex) {
                ToastUtils.showShort(mContext, "申请发送失败！");
            }
        });
    }

    private void gotoQualityAnalysis() {
        Intent intent = new Intent(this, QualityAnalysisWebviewActivity.class);
        int qualityState = getState(qualityFinish);
        intent.putExtra(QualityAnalysisWebviewActivity.MSG_TITLE, "我的素质分析");
        intent.putExtra(CLASSID, mClassId);
        intent.putExtra(QualityAnalysisWebviewActivity.EDIT_STATUS, qualityState);
        intent.putExtra(QualityAnalysisWebviewActivity.SOURCE, QualityAnalysisWebviewActivity.SRC_BEFORE_JOINCLASS);
        startActivityForResult(intent, CODE_QUALITY_ANALYZE);
    }

    private void gotoKnowledgeAnalyze() {
        Intent intent = new Intent(this, EntranceListActivity.class);
        int knowledgeState = getState(knowledgeFinish);
        intent.putExtra(EntranceListActivity.PARAM_EVAL_STATUS, knowledgeState);
        intent.putExtra(EntranceListActivity.PARAM_WAIT_CLASSID, mClassId);
        intent.putExtra(EntranceListActivity.PARAM_UPLOAD_STATUS, knowledgeFinish);
        intent.putExtra(EntranceListActivity.PARAM_MODIFY_FLAG, knowledgeModify);
        startActivityForResult(intent, CODE_KNOWLEDGEANALYSE);
    }

    private int getState(boolean finish) {
        if (!finish) {
            return NEW;
        } else if (state == TO_SUBMIT || state == REJECT) {
            return RESULT_EDIT;
        }
        return RESULT_NOT_EDIT;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "requestCode=" + requestCode);
        if (requestCode == CODE_QUALITY_ANALYZE) {
            boolean complete = data.getBooleanExtra(PARAM_QUALITY_STATUS, false) || qualityFinish;
            boolean modify = data.getBooleanExtra(PARAM_QUALITY_MODIFY, false) || qualityModify;
            if (complete != qualityFinish || modify != qualityModify) {
                qualityFinish = complete;
                qualityModify = modify;
                showQuality();
                showAccurate();
            }
        } else if (requestCode == CODE_KNOWLEDGEANALYSE && data != null) {
            boolean submit = data.getBooleanExtra(PARAM_SUBMIT_STATUS, false);
            boolean modify = data.getBooleanExtra(PARAM_SUBMIT_MODIFY, false);
            if (submit != knowledgeFinish || modify != knowledgeModify) {
                knowledgeFinish = submit;
                knowledgeModify = modify;
                showKnowledge();
                showAccurate();
            }
        }
    }
}
