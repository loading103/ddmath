package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.bean.PdfBean;
import com.tsinghuabigdata.edu.ddmath.bean.ScorePlanBean;
import com.tsinghuabigdata.edu.ddmath.bean.TaskBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekDatailBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.requestHandler.ScorePlanService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.ScorePlanServiceImpl;

import java.util.List;

/**
 * 我的学习
 */

public class ScorePlanModel {

    private ScorePlanService scorePlanService = new ScorePlanServiceImpl();

    /**
     * 查询定制学日期列表
     */
    public void queryWeekCheckWork(String studentId, String classId, RequestListener requestListener) {
        QueryWeekListTask queryMyCourseTask = new QueryWeekListTask(requestListener);
        queryMyCourseTask.executeMulti(studentId, classId);
    }
    private class QueryWeekListTask extends AppAsyncTask<String, Void,  List<WeekBean>> {

        private RequestListener reqListener;

        QueryWeekListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<WeekBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId = params[1];
            return scorePlanService.queryWeekList(studentId, classId);
        }

        @Override
        protected void onResult(List<WeekBean> doudouWork) {
            reqListener.onSuccess(doudouWork);
        }

        @Override
        protected void onFailure(HttpResponse<List<WeekBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询定制学首页数据
     */
    public void queryScorePlan( String studentId, RequestListener<ScorePlanBean> requestListener) {
        QueryScorePlanTask queryMyCourseTask = new QueryScorePlanTask(requestListener);
        queryMyCourseTask.executeMulti(studentId);
    }

    private class QueryScorePlanTask extends AppAsyncTask<String, Void, ScorePlanBean> {
        private RequestListener<ScorePlanBean> reqListener;

        QueryScorePlanTask(RequestListener<ScorePlanBean> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ScorePlanBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            return scorePlanService.queryScorePlan(studentId);
        }

        @Override
        protected void onResult(ScorePlanBean doudouWork) {
            reqListener.onSuccess(doudouWork);
        }

        @Override
        protected void onFailure(HttpResponse<ScorePlanBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 错题订正和微课
     */
    public void queryCorretDataWork(String studentId, String classId, long data, RequestListener requestListener) {
        QueryCorretDataTask queryMyCourseTask = new QueryCorretDataTask(requestListener);
        queryMyCourseTask.executeMulti(studentId, classId,data+"");
    }
    private class QueryCorretDataTask extends AppAsyncTask<String, Void,  List<WeekDatailBean>> {

        private RequestListener reqListener;

        QueryCorretDataTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<WeekDatailBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId = params[1];
            long date =Long.parseLong(params[2]) ;
            return scorePlanService.queryCorretDataList(studentId, classId,date);
        }

        @Override
        protected void onResult(List<WeekDatailBean> doudouWork) {
            reqListener.onSuccess(doudouWork);
        }

        @Override
        protected void onFailure(HttpResponse<List<WeekDatailBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }
    /**
     * 周训练
     */
    public void queryWeekTrainWork(String studentId, String classId, long data, RequestListener requestListener) {
        QueryWeekTrainTask queryMyCourseTask = new QueryWeekTrainTask(requestListener);
        queryMyCourseTask.executeMulti(studentId, classId,data+"");
    }
    private class QueryWeekTrainTask extends AppAsyncTask<String, Void, TaskBean> {

        private RequestListener reqListener;

        QueryWeekTrainTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected TaskBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId = params[1];
            long date =Long.parseLong(params[2]) ;
            return scorePlanService.queryWeekTrainList(studentId, classId,date);
        }

        @Override
        protected void onResult(TaskBean doudouWork) {
            reqListener.onSuccess(doudouWork);
        }

        @Override
        protected void onFailure(HttpResponse<TaskBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }
    /**
     * 自定义
     */
    public void queryDefineDataWork(String studentId, String classId, long data, RequestListener requestListener) {
        QueryDefineDataTask queryMyCourseTask = new QueryDefineDataTask(requestListener);
        queryMyCourseTask.executeMulti(studentId, classId,data+"");
    }
    private class QueryDefineDataTask extends AppAsyncTask<String, Void,  List<WeekDatailBean>> {

        private RequestListener reqListener;

        QueryDefineDataTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<WeekDatailBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId = params[1];
            long date =Long.parseLong(params[2]) ;
            return scorePlanService.queryDefineDataList(studentId, classId,date);
        }

        @Override
        protected void onResult(List<WeekDatailBean> doudouWork) {
            reqListener.onSuccess(doudouWork);
        }

        @Override
        protected void onFailure(HttpResponse<List<WeekDatailBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }
    /**
     * 确认加入定制学
     */
    public void confirmJoinCustomPlan(String studentId, RequestListener<Boolean> requestListener) {
        ConfirmJoinCustomPlanTask queryMyCourseTask = new ConfirmJoinCustomPlanTask(requestListener);
        queryMyCourseTask.executeMulti(studentId);
    }
    private class ConfirmJoinCustomPlanTask extends AppAsyncTask<String, Void,  Boolean> {

        private RequestListener<Boolean> reqListener;

        ConfirmJoinCustomPlanTask(RequestListener<Boolean> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Boolean doExecute(String... params) throws Exception {
            String studentId = params[0];
            return scorePlanService.queryConfirmJoinCustomPlan(studentId);
        }

        @Override
        protected void onResult(Boolean doudouWork) {
            reqListener.onSuccess(doudouWork);
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }
    /**
     * 一键下载pdf
     */
    public void oneKeyDownPdf(String studentId,long data, RequestListener<PdfBean> requestListener) {
        oneKeyDownPdfTask queryMyCourseTask = new oneKeyDownPdfTask(requestListener);
        queryMyCourseTask.executeMulti(studentId,data+"");
    }
    private class oneKeyDownPdfTask extends AppAsyncTask<String, Void, PdfBean> {
        private RequestListener<PdfBean> reqListener;
        oneKeyDownPdfTask(RequestListener<PdfBean> requestListener) {
            reqListener = requestListener;
        }
        @Override
        protected PdfBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            long date =Long.parseLong(params[1]) ;
            return scorePlanService.queryOneKeyPdf(studentId,date);
        }
        @Override
        protected void onResult(PdfBean doudouWork) {
            reqListener.onSuccess(doudouWork);
        }
        @Override
        protected void onFailure(HttpResponse<PdfBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }
}
