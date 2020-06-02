package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.QueryReportsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ReportInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.fragment.ReportBaseFragment;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.StudyfeedbackModel;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.adapter.AllroundReportAdapter;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ReportUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import java.util.ArrayList;
import java.util.List;


/**
 * 综合能力报告
 * Created by Administrator on 2018/3/23.
 */

public class AllroundReportFragment extends ReportBaseFragment implements PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {


    private Context mContext;

    private int    pageNum    = 1;
    private int    pageSize   = AppConst.REPORT_PAGE_NUM;
    private String mStudentId = "";
    private String type = "0";
    private List<ReportInfo> mList = new ArrayList<>();

    private AllroundReportAdapter mAdapter;
    private PullToRefreshListView mLvReport;
    private LoadingPager          mLoadingPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_week_extract_report, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_week_extract_report_mobile, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }


    private void initView(View root) {
        mContext = getActivity();
        mLvReport = (PullToRefreshListView) root.findViewById(R.id.lv_report);
        mLoadingPager = (LoadingPager) root.findViewById(R.id.loadingPager);

        mLoadingPager.setTargetView(mLvReport);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                getReportList();
            }
        });
        MyViewUtils.setPTRText(mContext, mLvReport);
        mLvReport.setOnRefreshListener(this);
        mLvReport.setOnItemClickListener(this);
    }


    private void initData() {
        createLoginInfo();
        getReportList();
        mAdapter = new AllroundReportAdapter(mContext, mList);
        mLvReport.setAdapter(mAdapter);
    }

    //查询登录信息
    private void createLoginInfo() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
    }

    private void getReportList() {
        new StudyfeedbackModel().queryReports(mStudentId, type, pageNum + "", pageSize + "", new RequestListener<QueryReportsInfo>() {
            @Override
            public void onSuccess(QueryReportsInfo vo) {
                if( isDetached() ) return;
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }
                LogUtils.i("getReportList infoList.size=" + vo.getItems().size());
                mList.clear();
                mList.addAll(vo.getItems());
                mAdapter.notifyDataSetChanged();
                if (vo.getPageNum() >= vo.getTotalPage()) {
                    mLvReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvReport.setMode(PullToRefreshBase.Mode.BOTH);
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                if( isDetached() ) return;
                mLoadingPager.showFault(ex);
            }
        });
    }

    private void getData(final boolean refresh) {
        if (refresh) {
            pageNum = 1;
        }
        new StudyfeedbackModel().queryReports(mStudentId, type, pageNum + "", pageSize + "", new RequestListener<QueryReportsInfo>() {
            @Override
            public void onSuccess(QueryReportsInfo vo) {
                LogUtils.i("refreshData onSuccess");
                if (vo == null || vo.getItems() == null || vo.getItems().size() == 0) {
                    if (refresh) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else {
                        mLvReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    mLvReport.onRefreshComplete();
                    return;
                }
                if (refresh) {
                    mList.clear();
                }
                mList.addAll(vo.getItems());
                mAdapter.notifyDataSetChanged();
                mLvReport.onRefreshComplete();
                if (vo.getPageNum() >= vo.getTotalPage()) {
                    mLvReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvReport.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                LogUtils.i("refreshData onFail");
                mLvReport.onRefreshComplete();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ReportInfo reportInfo = mList.get(position - 1);
        if (reportInfo == null) {
            return;
        }
        reportInfo.setReadStatus(ReportInfo.S_READ);
        mAdapter.notifyDataSetChanged();

        //跳转报告详情
        String createTimeText = ReportUtils.getUpdateTimeText(reportInfo.getCreateTime());
        MessageUtils.gotoAllroundReport(mContext, reportInfo.getReportId(),reportInfo.getReportName(),createTimeText);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        refreshData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        getMoreData();
    }

    public void refreshData() {
        getData(true);
    }

    private void getMoreData() {
        getData(false);
    }

    @Override
    protected void refreshReport() {
        if (isPrepared) {
            mLoadingPager.showLoading();
            getReportList();
        }
    }

    public String getUmEventName() {
        return "report_whole";
    }
}
