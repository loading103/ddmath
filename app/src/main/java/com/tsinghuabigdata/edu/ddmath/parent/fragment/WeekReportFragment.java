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
import com.tsinghuabigdata.edu.ddmath.event.CreateWeekReportEvent;
import com.tsinghuabigdata.edu.ddmath.event.ReadWeekReportEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.parent.MVPModel.ParentReportModel;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ReportDetailActivity;
import com.tsinghuabigdata.edu.ddmath.parent.adapter.WeekReportAdatper;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportItemBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportListBean;
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
 * 每周报告
 * Created by Administrator on 2018/6/27.
 */

public class WeekReportFragment extends MyBaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private PullToRefreshListView mLvWeekReport;
    private LoadingPager          mLoadingPager;

    private Context           mContext;
    private WeekReportAdatper mAdapter;
    private List<WeekReportItemBean> mList = new ArrayList<>();

    private int    pageNum   = 1;
    private int    pageSize  = 10;
    private String mParentId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("ParentReportFragment onCreateView");
        View root = inflater.inflate(R.layout.fragment_week_report_phone, container, false);
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
        return "parent_main_weekreport";
    }


    private void initView(View root) {
        mContext = getActivity();
        mLvWeekReport = (PullToRefreshListView) root.findViewById(R.id.lv_week_report);
        mLoadingPager = (LoadingPager) root.findViewById(R.id.loadingPager);
        MyViewUtils.setPTRText(mContext, mLvWeekReport);

        mLoadingPager.setTargetView(mLvWeekReport);
        mLoadingPager.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingPager.showLoading();
                queryWeekReport();
            }
        });
        mLvWeekReport.setOnRefreshListener(this);
        mLvWeekReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
                if( detailinfo == null ) return;

                WeekReportItemBean bean = mList.get(position - 1);
                ReportDetailActivity.openReportDetailActivity( mContext,
                        ReportDetailUtils.getWeekReportName(),
                        ReportDetailUtils.getWeekReportUrl( detailinfo.getStudentId(),bean.getReportId(), false),
                        ReportDetailUtils.getWeekReportShareName(bean),
                        ReportDetailUtils.getWeekReportShareText(bean),
                        ReportDetailUtils.getWeekReportUrl( detailinfo.getStudentId(),bean.getReportId(), true),
                        "week");

//                if (bean.getCheckStatus() == WeekReportItemBean.UNREAD) {
//                    bean.setCheckStatus(WeekReportItemBean.READED);
//                    mAdapter.notifyDataSetChanged();
//                }
            }
        });
    }

    private void initData() {
        createLoginInfo();
        mAdapter = new WeekReportAdatper(mContext, mList);
        mLvWeekReport.setAdapter(mAdapter);
        queryWeekReport();
    }

    //查询登录信息
    private void createLoginInfo() {
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        if (parentInfo != null) {
            mParentId = parentInfo.getParentId();
        }
    }

    private void queryWeekReport() {
        HashMap<String, String> params = new LinkedHashMap<>();
        params.put("parentId", mParentId);
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        new ParentReportModel().queryWeekReport(params, new RequestListener<WeekReportListBean>() {

            @Override
            public void onSuccess(WeekReportListBean res) {
                LogUtils.i("queryDayReport onSuccess");
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    mLoadingPager.showEmpty();
                    return;
                }
                mList.clear();
                mList.addAll(res.getItems());
                mAdapter.notifyDataSetChanged();
                if (res.getItems().size() < pageSize) {
                    mLvWeekReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvWeekReport.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<WeekReportListBean> response, Exception ex) {
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
        new ParentReportModel().queryWeekReport(params, new RequestListener<WeekReportListBean>() {

            @Override
            public void onSuccess(WeekReportListBean res) {
                LogUtils.i("queryDayReport onSuccess");
                if (res == null || res.getItems() == null || res.getItems().size() == 0) {
                    if (refresh) {
                        ToastUtils.showShort(mContext, R.string.server_error);
                    } else {
                        mLvWeekReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
                    mLvWeekReport.onRefreshComplete();
                    return;
                }
                if (refresh) {
                    mList.clear();
                }
                mList.addAll(res.getItems());
                mAdapter.notifyDataSetChanged();
                mLvWeekReport.onRefreshComplete();
                if (res.getItems().size() < pageSize) {
                    mLvWeekReport.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                } else {
                    mLvWeekReport.setMode(PullToRefreshBase.Mode.BOTH);
                    pageNum++;
                }
                mLoadingPager.showTarget();
            }

            @Override
            public void onFail(HttpResponse<WeekReportListBean> response, Exception ex) {
                LogUtils.i("queryDayReport onFail");
                mLvWeekReport.onRefreshComplete();
                AlertManager.showErrorInfo(mContext, ex);
            }
        });
    }

    @Subscribe()
    public void receive(CreateWeekReportEvent event) {
        // 收到本地消息后，请求网络刷新列表
        pageNum = 1;
        queryWeekReport();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receive(ReadWeekReportEvent event) {
        // 收到本地消息后，更新列表阅读状态
        for (int i = 0; i < mList.size(); i++) {
            WeekReportItemBean bean = mList.get(i);
            if (!TextUtils.isEmpty(bean.getReportId()) && bean.getReportId().equals(event.getReportId())) {
                bean.setCheckStatus(WeekReportItemBean.READED);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }
}
