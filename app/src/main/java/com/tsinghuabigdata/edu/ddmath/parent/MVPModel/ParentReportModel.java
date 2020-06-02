package com.tsinghuabigdata.edu.ddmath.parent.MVPModel;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportListBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportListBean;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.ParentReportService;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.requestImpl.ParentReportImpl;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/7/9.
 */

public class ParentReportModel {

    private ParentReportService mService = new ParentReportImpl();

    public void queryDayReport(HashMap<String, String> params, RequestListener requestListener) {
        DayReportTask task = new DayReportTask(requestListener);
        task.execute(params);
    }

    public void queryWeekReport(HashMap<String, String> params, RequestListener requestListener) {
        WeekReportTask task = new WeekReportTask(requestListener);
        task.execute(params);
    }

    public void upadatReadStatus(HashMap<String, String> params, RequestListener requestListener) {
        UpadatStatusTask task = new UpadatStatusTask(requestListener);
        task.execute(params);
    }

    /**
     * 家长端每日报告列表
     */
    class DayReportTask extends AppAsyncTask<HashMap<String, String>, Void, DayReportListBean> {

        private RequestListener reqListener;

        public DayReportTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected DayReportListBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.queryDayReport(map);
        }

        @Override
        protected void onResult(DayReportListBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<DayReportListBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 周报告列表查询
     */
    class WeekReportTask extends AppAsyncTask<HashMap<String, String>, Void, WeekReportListBean> {

        private RequestListener reqListener;

        public WeekReportTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected WeekReportListBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.queryWeekReport(map);
        }

        @Override
        protected void onResult(WeekReportListBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<WeekReportListBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 更新点赞转发阅读记录
     */
    class UpadatStatusTask extends AppAsyncTask<HashMap<String, String>, Void, String> {

        private RequestListener reqListener;

        public UpadatStatusTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected String doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.upadatReadStatus(map);
        }

        @Override
        protected void onResult(String bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<String> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }
}
