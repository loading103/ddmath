package com.tsinghuabigdata.edu.ddmath.parent.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.CreateDayReportEvent;
import com.tsinghuabigdata.edu.ddmath.event.ReadDayReportEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.parent.MVPModel.ParentReportModel;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ReportDetailActivity;
import com.tsinghuabigdata.edu.ddmath.parent.adapter.DayReportAdapter;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportItemBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportListBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.util.ReportDetailUtils;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.MyViewUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;
import com.tsinghuabigdata.edu.ddmath.view.LoadingPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 每日报告
 * Created by Administrator on 2018/6/27.
 */

public class DayReportFragment extends MyBaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshListView mLvDayReport;
    private LoadingPager          mLoadingPager;

    private Context          mContext;
    private DayReportAdapter mAdapter;
    private List<DayReportItemBean> mList = new ArrayList<>();

    private int    pageNum    = 1;
    private int    pageSize   = 10;
    private String mParentId  = "";
    private String mStudentId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("DayReportFragment onCreateView");
        View root = inflater.inflate(R.layout.fragment_day_report_phone, container, false);
        initView(root);
        setPrepared();
        initData();
        EventBus.getDefault().register(this);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getUmEventName(){
        return "parent_main_dayreport";
    }

    private void initView(View root) {
        mContext = getActivity();
        mLvDayReport = (PullToRefreshListView) root.findViewById(R.id.lv_day_report);
        mLoadingPager = (LoadingPager) root.findViewById(R.id.loadingPager);
        MyViewUtils.setPTRText(mContext, mLvDayReport);

        mLoadingPager.setTargetView(mLvDayReport);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryDayReport();
            }
        });
        mLvDayReport.setOnRefreshListener(this);
        mLvDayReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DayReportItemBean bean = mList.get(position - 1);
                if (bean == null || bean.getExerStatus() < DayReportBean.STAT_FINISHED) {
                    return;
                }
                ReportDetailActivity.openReportDetailActivity( mContext,
                        ReportDetailUtils.getDayReportName(),
                        ReportDetailUtils.getDayReportUrl(bean, false),
                        ReportDetailUtils.getDayReportShareName(bean),
                        ReportDetailUtils.getDayReportShareText(bean),
                        ReportDetailUtils.getDayReportUrl(bean, true),
                        "day");
//                if (bean.getExerStatus() == DayReportBean.STAT_FINISHED) {
//                    updateReadStatus(bean.getExamId());
//                    bean.setExerStatus(DayReportBean.PARENT_READ);
//                    mAdapter.notifyDataSetChanged();
//                }
            }
        });
    }

    private void updateReadStatus(String examId) {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("studentId", mStudentId);
        params.put("examId", examId);
        new ParentReportModel().upadatReadStatus(params, new RequestListener<String>() {

            @Override
            public void onSuccess(String res) {
                LogUtils.i("upadatReadStatus onSuccess");
            }

            @Override
            public void onFail(HttpResponse<String> response, Exception ex) {
                LogUtils.i("upadatReadStatus onFail");
            }
        });
    }


    private void initData() {
        createLoginInfo();
        mAdapter = new DayReportAdapter(mContext, mList);
        mLvDayReport.setAdapter(mAdapter);
        queryDayReport();
    }

    //查询登录信息
    private void createLoginInfo() {
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo != null) {
            mParentId = parentInfo.getParentId();
        }
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if (detailinfo != null) {
            mStudentId = detailinfo.getStudentId();
        }
    }

    private void queryDayReport() {
        //analogList();
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("parentId", mParentId);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        new ParentReportModel().queryDayReport(params, new RequestListener<DayReportListBean>() {

            @Override
            public void onSuccess(DayReportListBean res) {
                LogUtils.i("queryDayReport onSuccess");
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }
                mList.clear();
                mList.addAll(res.getItems());
                mAdapter.notifyDataSetChanged();
                if (res.getItems().size() < pageSize) {
                    mLvDayReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvDayReport.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<DayReportListBean> response, Exception ex) {
                LogUtils.i("queryDayReport onFail");
                mLoadingPager.showFault(ex);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase pullToRefreshBase) {
        getReport(true);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase pullToRefreshBase) {
        getReport(false);
    }

    private void getReport(final boolean refresh) {
        if (refresh) {
            pageNum = 1;
        }
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("parentId", mParentId);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        new ParentReportModel().queryDayReport(params, new RequestListener<DayReportListBean>() {

            @Override
            public void onSuccess(DayReportListBean res) {
                LogUtils.i("queryDayReport onSuccess");
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    if (refresh) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else {
                        mLvDayReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    mLvDayReport.onRefreshComplete();
                    return;
                }
                if (refresh) {
                    mList.clear();
                }
                mList.addAll(res.getItems());
                mAdapter.notifyDataSetChanged();
                mLvDayReport.onRefreshComplete();
                if (res.getItems().size() < pageSize) {
                    mLvDayReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvDayReport.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<DayReportListBean> response, Exception ex) {
                LogUtils.i("queryDayReport onFail");
                mLvDayReport.onRefreshComplete();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    @Subscribe()
    public void receive(CreateDayReportEvent event) {
        // 收到本地消息后，请求网络刷新列表
        pageNum = 1;
        queryDayReport();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ReadDayReportEvent event) {
        // 收到本地消息后，更新列表阅读状态
        //updateReadStatus(event.getExamId());
        for (int i = 0; i < mList.size(); i++) {
            DayReportItemBean bean = mList.get(i);
            if (!TextUtils.isEmpty(bean.getExamId()) && bean.getExamId().equals(event.getExamId())) {
                //updateReadStatus(bean.getExamId());
                bean.setExerStatus(DayReportBean.PARENT_READ);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
