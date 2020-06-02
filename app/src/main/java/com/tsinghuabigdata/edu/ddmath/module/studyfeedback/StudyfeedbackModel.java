package com.tsinghuabigdata.edu.ddmath.module.studyfeedback;

import android.os.AsyncTask;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.DayClearBean;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.WeekAnalysisBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.StudyfeedbackReqService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.StudyfeedbackReqImpl;
import com.tsinghuabigdata.edu.ddmath.util.AsyncTaskCancel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 28205 on 2016/12/14.
 */
public class StudyfeedbackModel {
    private StudyfeedbackReqService studyfeedbackReq = new StudyfeedbackReqImpl();
    private List<AsyncTask>         runningTasks     = new ArrayList<AsyncTask>();

    /**
     * 报告列表
     */
    class QueryReportsTask extends AppAsyncTask<String, Void, Object> {
        private RequestListener reqListener;

        public QueryReportsTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Object doExecute(String... params) throws Exception {
            String studentId = params[0];
            String reportType = params[1];
            String pageNum = params[2];
            String pageSize = params[3];
            return studyfeedbackReq.queryReports(studentId, reportType, pageNum, pageSize);
        }

        @Override
        protected void onResult(Object resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Object> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 更新报告状态
     */
    class UpdateReportStatusTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        public UpdateReportStatusTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String reportId = params[1];
            String reportType = params[2];
            String pageNum = params[3];
            String pageSize = params[4];
            studyfeedbackReq.updateReportReadstatus(accessToken, reportId, reportType);
            return null;
        }

        @Override
        protected void onResult(Void resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 是否有新报告
     */
    class IsHaveNewReportsTask extends AppAsyncTask<String, Void, Object> {
        private RequestListener reqListener;

        public IsHaveNewReportsTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Object doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            return studyfeedbackReq.queryIshaveNewReport(accessToken, studentId);
        }

        @Override
        protected void onResult(Object resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Object> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class SingleDetailkTask extends AppAsyncTask<String, Void, QuestionInfo> {

        private RequestListener reqListener;

        SingleDetailkTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected QuestionInfo doExecute(String... params) throws Exception {
            String studentId = params[0];
            String examId = params[1];
            String questionId = params[2];
            return studyfeedbackReq.querySingleDetail(studentId, examId, questionId);
        }

        @Override
        protected void onResult(QuestionInfo bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<QuestionInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class ReviseDetailkTask extends AppAsyncTask<String, Void, DayClearBean> {

        private RequestListener reqListener;

        ReviseDetailkTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected DayClearBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            return studyfeedbackReq.queryReviseDetail(studentId);
        }

        @Override
        protected void onResult(DayClearBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<DayClearBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    private class WeekAnalysisReportsTask extends AppAsyncTask<String, Void, List<WeekAnalysisBean>> {

        private RequestListener<List<WeekAnalysisBean>> reqListener;

        WeekAnalysisReportsTask(RequestListener<List<WeekAnalysisBean>> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<WeekAnalysisBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            return studyfeedbackReq.queryWeekAnalysisReports(studentId);
        }

        @Override
        protected void onResult(List<WeekAnalysisBean> list) {
            if(reqListener!=null)reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<WeekAnalysisBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询报告列表
     */
    public void queryReports(String accessToken, String studentId, String reportType,
                             String pageNum, String pageSize, RequestListener requestListener) {
        QueryReportsTask queryReportsTask = new QueryReportsTask(requestListener);
        queryReportsTask.executeMulti(studentId, reportType, pageNum, pageSize);
        runningTasks.add(queryReportsTask);
    }

    /**
     * 查询报告列表
     */
    public void queryReports(String studentId, String reportType,
                             String pageNum, String pageSize, RequestListener requestListener) {
        QueryReportsTask queryReportsTask = new QueryReportsTask(requestListener);
        queryReportsTask.executeMulti(studentId, reportType, pageNum, pageSize);
        runningTasks.add(queryReportsTask);
    }

    /**
     * 更新报告状态
     */
    public void updateReportstatus(String accessToken, String reportId, String reportType, RequestListener requestListener) {
        UpdateReportStatusTask queryReportsTask = new UpdateReportStatusTask(requestListener);
        queryReportsTask.executeMulti(accessToken, reportId, reportType);
        runningTasks.add(queryReportsTask);
    }

    /**
     * 是否有新报告
     */
    public void isHaveNewReports(String accessToken, String studentId, RequestListener requestListener) {
        IsHaveNewReportsTask queryReportsTask = new IsHaveNewReportsTask(requestListener);
        queryReportsTask.executeMulti(accessToken, studentId);
        runningTasks.add(queryReportsTask);
    }

    public void querySingleDetail(String studentId, String examId, String questionId, RequestListener requestListener) {
        SingleDetailkTask task = new SingleDetailkTask(requestListener);
        task.executeMulti(studentId, examId, questionId);
        runningTasks.add(task);
    }

    public void queryReviseDetail(String studentId, RequestListener requestListener) {
        ReviseDetailkTask task = new ReviseDetailkTask(requestListener);
        task.executeMulti(studentId);
        runningTasks.add(task);
    }

    /**
     * 查询周分析报告列表
     */
    public void queryWeekAnalysisReports(String studentId, RequestListener<List<WeekAnalysisBean>> requestListener) {
        WeekAnalysisReportsTask task = new WeekAnalysisReportsTask(requestListener);
        task.executeMulti(studentId);
        runningTasks.add(task);
    }

    public void stopRunningTasks() {
        if (runningTasks.size() > 0) {
            AsyncTaskCancel.cancel(runningTasks);
        }
    }
}
