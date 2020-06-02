package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.QueryReportsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.fragment.ReportBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ReportUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 知识图谱报告
 * Created by Administrator on 2018/3/23.
 */

public class KnowledgeCharttFragment extends ReportBaseFragment implements View.OnClickListener {


    private Context mContext;

    private String type       = "1";
    private String mStudentId = "";
    private String mExamId;
    private String mLastExamId;

    private LinearLayout mLlKnowledgeChart;
    private TextView     mTvStudentName;
    private TextView     mTvUpdateTime;
    private Button       mIvAtlasMode;
    private TextView     mTvStudentName2;
    private TextView     mTvUpdateTime2;
    private Button       mIvListMode;
    private LoadingPager mLoadingPagerKnowledgeChart;
    private Long         mCreateTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_knowledge_report, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_knowledge_report_mobile, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    private void initView(View root) {
        mContext = getActivity();
        mLlKnowledgeChart = (LinearLayout) root.findViewById(R.id.ll_knowledge_chart);
        mTvStudentName = (TextView) root.findViewById(R.id.tv_student_name);
        mTvUpdateTime = (TextView) root.findViewById(R.id.tv_update_time);
        mIvAtlasMode = (Button) root.findViewById(R.id.btn_atlas_mode);
        mTvStudentName2 = (TextView) root.findViewById(R.id.tv_student_name2);
        mTvUpdateTime2 = (TextView) root.findViewById(R.id.tv_update_time2);
        mIvListMode = (Button) root.findViewById(R.id.btn_list_mode);
        mLoadingPagerKnowledgeChart = (LoadingPager) root.findViewById(R.id.loadingPager_knowledge_chart);

        mIvAtlasMode.setOnClickListener(this);
        mIvListMode.setOnClickListener(this);
        mLoadingPagerKnowledgeChart.setTargetView(mLlKnowledgeChart);
        mLoadingPagerKnowledgeChart.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPagerKnowledgeChart.showLoading();
                getKnowledgeReportList();
            }
        });
    }


    private void initData() {
        createLoginInfo();
        getKnowledgeReportList();
    }

    //查询登录信息
    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
            String reallyName = detailinfo.getReallyName();
            mTvStudentName.setText(reallyName);
            mTvStudentName2.setText(reallyName);
        }
    }

    private void getKnowledgeReportList() {
        new StudyfeedbackModel().queryReports(mStudentId, type, "1", "1", new RequestListener<QueryReportsInfo>() {
            @Override
            public void onSuccess(QueryReportsInfo vo) {
                LogUtils.i("getKnowledgeReportList onSuccess");
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
                    mLoadingPagerKnowledgeChart.showEmpty();
                    return;
                }
                List<ReportInfo> infoList = vo.getItems();
                LogUtils.i("getKnowledgeReportList infoList.size=" + infoList.size());
                ReportInfo reportInfo = vo.getItems().get(0);
                if (reportInfo == null) {
                    mLoadingPagerKnowledgeChart.showEmpty();
                    return;
                }
                mCreateTime = reportInfo.getCreateTime();
                String datestr = getUpdateTime(mCreateTime);
                mTvUpdateTime.setText(datestr);
                mTvUpdateTime2.setText(datestr);
                mExamId = reportInfo.getExamId();
                mLastExamId = reportInfo.getLastExamId();
                mLoadingPagerKnowledgeChart.showTarget();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                LogUtils.i("getKnowledgeReportList onFail");
                mLoadingPagerKnowledgeChart.showFault(ex);
            }
        });
    }


    @Override
    protected void refreshReport() {
        if (isPrepared) {
            mLoadingPagerKnowledgeChart.showLoading();
            getKnowledgeReportList();
        }
    }

    @Override
    public void onClick(View v) {
        String createTimeText = ReportUtils.getUpdateTimeText(mCreateTime);
        switch (v.getId()) {
            case R.id.btn_atlas_mode:
                MessageUtils.gotoKnowledgeAtlasModeReport(mContext, mExamId, mStudentId, mLastExamId, "知识图谱", createTimeText);
                break;
            case R.id.btn_list_mode:
                MessageUtils.gotoKnowledgeReport(mContext, mExamId, mStudentId, mLastExamId, "知识图谱", createTimeText);
                break;
            default:
                break;
        }
    }

    private String getUpdateTime(long time) {
        DateFormat formatter = new SimpleDateFormat("更新时间：yyyy-MM-dd HH:mm");
        return formatter.format(time);
    }

    public String getUmEventName() {
        return "report_knowchart";
    }
}
