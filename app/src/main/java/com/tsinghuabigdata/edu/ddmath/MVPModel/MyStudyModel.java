package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.alibaba.fastjson.JSON;
import com.tsinghuabigdata.edu.ddmath.bean.DoudouWork;
import com.tsinghuabigdata.edu.ddmath.bean.MyCourse;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SubmitReviseBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyLearnService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.MyLearnServiceImpl;

import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * 我的学习
 */

public class MyStudyModel {

    private MyLearnService learnService = new MyLearnServiceImpl();


    public void queryMyCourse(String accessToken, String studentId, String classId,
                              int pageNum, int pageSize, RequestListener requestListener) {
        QueryMyCourseTask queryMyCourseTask = new QueryMyCourseTask(requestListener);
        queryMyCourseTask.executeMulti(accessToken, studentId, classId, pageNum + "", pageSize + "");
    }

    /**
     * 查询豆豆作业详情
     */
    public void queryDDWorkDetail(String accessToken, String studentId, String workId, String recordId, RequestListener requestListener) {
        QueryWorkDetailTask queryClassTask = new QueryWorkDetailTask(requestListener);
        queryClassTask.executeMulti(accessToken, studentId, workId, recordId);
    }

    /**
     * 查询豆豆作业列表(老版)
     */
    public void queryDoudouWork(String studentId, String classId, int pageNum, int pageSize, int status, RequestListener requestListener) {
        queryDoudouCheckWork(studentId, classId, pageNum, pageSize, status, 0, requestListener);
    }

    /**
     * 查询豆豆作业列表（V12.0，新增字段type）
     */
    public void queryDoudouCheckWork(String studentId, String classId, int pageNum, int pageSize, int status, int type, RequestListener requestListener) {
        QueryDoudouWorkTask queryMyCourseTask = new QueryDoudouWorkTask(requestListener);
        queryMyCourseTask.executeMulti(studentId, classId, pageNum + "", pageSize + "", status + "", type + "");
    }

    /**
     * 查询豆豆撤回状态
     */
    public void queryDDWorkRevokeStatus(String studentId, String examId, RequestListener requestListener) {
        QueryDDWorkStatusTask task = new QueryDDWorkStatusTask(requestListener);
        task.executeMulti(studentId, examId);
    }

    /**
     * 分享豆豆作业
     */
    public void shareDDWorkRecord(String examId, RequestListener requestListener) {
        ShareDDWorkRecordTask task = new ShareDDWorkRecordTask(requestListener);
        task.executeMulti(examId);
    }

    /**
     * 错题纠错申请
     */
    public void applyCorrectErrorQuestion(String studentId, boolean isRevise, String reason, RequestListener requestListener) {
        CorrectErrorQuestionTask task = new CorrectErrorQuestionTask(requestListener);
        task.executeMulti(studentId, String.valueOf(isRevise), reason);
    }

    /**
     * 错题订正
     */
    public void applyReviseErrorQuestion(String studentId, String examId, String questionId, String params, RequestListener requestListener) {
        ReviseErrorQuestionTask task = new ReviseErrorQuestionTask(requestListener);
        task.executeMulti(studentId, examId, questionId, params);
    }


    /**
     * 今天自我检查作业布置的次数
     */
    public void getTodayCheckWorkCount(RequestListener<Integer> requestListener) {
        TodayCheckWorkCountTask task = new TodayCheckWorkCountTask(requestListener);
        task.executeMulti();
    }

    /**
     * 今天自我检查作业布置的次数
     */
    public void checkBookWorkSubmitStatus(String bookId, RequestListener<Integer> requestListener) {
        BookTeacherSubmitStatusTask task = new BookTeacherSubmitStatusTask(requestListener);
        task.executeMulti( bookId );
    }

    //-----------------------------------------------------------------------------------------------------
    /**
     * 查询我的课程
     */
    private class QueryMyCourseTask extends AppAsyncTask<String, Void, List<MyCourse>> {

        private RequestListener reqListener;

        QueryMyCourseTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<MyCourse> doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            String classId = params[2];
            int pageNum = Integer.parseInt(params[3]);
            int pageSize = Integer.parseInt(params[4]);
            return learnService.myCourse(accessToken, studentId, classId, pageNum, pageSize);
        }

        @Override
        protected void onResult(List<MyCourse> myCourses) {
            reqListener.onSuccess(myCourses);
        }

        @Override
        protected void onFailure(HttpResponse<List<MyCourse>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 学生作业列表查询
     */
    private class QueryDoudouWorkTask extends AppAsyncTask<String, Void, DoudouWork> {

        private RequestListener reqListener;

        QueryDoudouWorkTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected DoudouWork doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId = params[1];
            int pageNum = Integer.parseInt(params[2]);
            int pageSize = Integer.parseInt(params[3]);
            int status = Integer.parseInt(params[4]);
            int type = Integer.parseInt(params[5]);
            return learnService.queryDoudouWork(studentId, classId, pageNum, pageSize, status, type);
        }

        @Override
        protected void onResult(DoudouWork doudouWork) {
            reqListener.onSuccess(doudouWork);
        }

        @Override
        protected void onFailure(HttpResponse<DoudouWork> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询豆豆作业详情
     */
    private class QueryWorkDetailTask extends AppAsyncTask<String, Void, DDWorkDetail> {

        private RequestListener reqListener;

        QueryWorkDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected DDWorkDetail doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            String workId = params[2];
            String recordId = params[3];
            return learnService.getDDWorkDetail(accessToken, studentId, workId, recordId);
        }

        @Override
        protected void onResult(DDWorkDetail list) {
            reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<DDWorkDetail> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询豆豆作业是否撤回状态
     */
    private class QueryDDWorkStatusTask extends AppAsyncTask<String, Void, Integer> {

        private RequestListener reqListener;

        QueryDDWorkStatusTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Integer doExecute(String... params) throws Exception {
            String studentId = params[0];
            String examId = params[1];
            return learnService.getDDWorkRevokeStatus(studentId, examId);
        }

        @Override
        protected void onResult(Integer status) {
            if (reqListener != null)
                reqListener.onSuccess(status);
        }

        @Override
        protected void onFailure(HttpResponse<Integer> response, Exception ex) {
            if (reqListener != null)
                reqListener.onFail(response, ex);
        }
    }

    /**
     * 分享豆豆作业记录
     */
    private class ShareDDWorkRecordTask extends AppAsyncTask<String, Void, Boolean> {

        private RequestListener reqListener;

        ShareDDWorkRecordTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Boolean doExecute(String... params) throws Exception {
            String examId = params[0];
            return learnService.shareDDWorkRecord(examId);
        }

        @Override
        protected void onResult(Boolean success) {
            if (reqListener != null)
                reqListener.onSuccess(success);
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            if (reqListener != null)
                reqListener.onFail(response, ex);
        }
    }

    /**
     * 错题纠错
     */
    private class CorrectErrorQuestionTask extends AppAsyncTask<String, Void, Boolean> {

        private RequestListener reqListener;

        CorrectErrorQuestionTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Boolean doExecute(String... params) throws Exception {
            String studentId = params[0];
            boolean isRevise = "true".equals(params[1]);
            String reason = params[2];
            return learnService.applyCorrectError(studentId, isRevise, JSON.parseObject(reason));
        }

        @Override
        protected void onResult(Boolean success) {
            if (reqListener != null)
                reqListener.onSuccess(success);
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            if (reqListener != null)
                reqListener.onFail(response, ex);
        }
    }

    /**
     * 错题订正
     */
    private class ReviseErrorQuestionTask extends AppAsyncTask<String, Void, SubmitReviseBean> {

        private RequestListener reqListener;

        ReviseErrorQuestionTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected SubmitReviseBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String examId = params[1];
            String questionId = params[2];
            String param = params[3];
            return learnService.applyReviseError(studentId, examId, questionId, parseObject(param));
        }

        @Override
        protected void onResult(SubmitReviseBean submitReviseBean) {
            if (reqListener != null)
                reqListener.onSuccess(submitReviseBean);
        }

        @Override
        protected void onFailure(HttpResponse<SubmitReviseBean> response, Exception ex) {
            if (reqListener != null)
                reqListener.onFail(response, ex);
        }
    }

    private class TodayCheckWorkCountTask extends AppAsyncTask<Void, Void, Integer> {

        private RequestListener<Integer> reqListener;

        TodayCheckWorkCountTask(RequestListener<Integer> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Integer doExecute(Void... voids) throws Exception {
            return learnService.getTodayCheckWorkCount();
        }

        @Override
        protected void onResult(Integer count) {
            if (reqListener != null)reqListener.onSuccess(count);
        }

        @Override
        protected void onFailure(HttpResponse<Integer> response, Exception ex) {
            if (reqListener != null) reqListener.onFail(response, ex);
        }
    }

    private class BookTeacherSubmitStatusTask extends AppAsyncTask<String, Void, Integer> {

        private RequestListener<Integer> reqListener;

        BookTeacherSubmitStatusTask(RequestListener<Integer> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Integer doExecute(String... params) throws Exception {
            return learnService.getBookTeacherSubmitStatus( params[0] );
        }

        @Override
        protected void onResult(Integer count) {
            if (reqListener != null)reqListener.onSuccess(count);
        }

        @Override
        protected void onFailure(HttpResponse<Integer> response, Exception ex) {
            if (reqListener != null) reqListener.onFail(response, ex);
        }
    }



}
