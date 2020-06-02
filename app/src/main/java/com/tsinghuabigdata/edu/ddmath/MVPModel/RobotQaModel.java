package com.tsinghuabigdata.edu.ddmath.MVPModel;

import android.os.AsyncTask;
import android.util.Log;

import com.tsinghuabigdata.edu.ddmath.bean.ClickQaRankInClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ClickQaRankInZXInfo;
import com.tsinghuabigdata.edu.ddmath.bean.OtherStuAns;
import com.tsinghuabigdata.edu.ddmath.bean.QAAnsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ResultInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StuClickQaInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StuQaClickHistory;
import com.tsinghuabigdata.edu.ddmath.bean.Topic;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.AnswerPermission;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.QuestionVo;
import com.tsinghuabigdata.edu.ddmath.requestHandler.RobotQaService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.RobotQaServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AsyncTaskCancel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 28205 on 2016/9/2.
 */
public class RobotQaModel {

    private RobotQaService  robotQaService = new RobotQaServiceImpl();
    private List<AsyncTask> runningTasks   = new ArrayList<AsyncTask>();

    /**
     * 获取做题前的答疑详情
     */
    class QueryQAAnsDoBeforeTask extends AppAsyncTask<String, Void, List<QAAnsInfo>> {


        private RequestListener reqListener;

        public QueryQAAnsDoBeforeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<QAAnsInfo> doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            String questionId = params[2];
            return robotQaService.getQAAnsDoBefore(accessToken, studentId, questionId);
        }

        @Override
        protected void onResult(List<QAAnsInfo> resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<List<QAAnsInfo>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取做题次数
     */
    class QuestionDoTimesTask extends AppAsyncTask<String, Void, Integer> {

        private RequestListener reqListener;

        public QuestionDoTimesTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Integer doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            String questionId = params[2];
            Log.d("QuestionDoTimesTask", "doExecute: ");
            return robotQaService.getQuestionDoTimes(accessToken, studentId, questionId);
        }

        @Override
        protected void onResult(Integer resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Integer> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取相似题
     */
    class GetAlikeQuestionTask extends AppAsyncTask<String, Void, List<Topic>> {

        private RequestListener reqListener;

        public GetAlikeQuestionTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<Topic> doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String questionId = params[1];
            String diff = params[2];
            return robotQaService.getAlikeQuestion(accessToken, questionId, diff);
        }

        @Override
        protected void onResult(List<Topic> resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<List<Topic>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取相似题(13.7开始使用)
     */
    class GetNewAlikeQuestionTask extends AppAsyncTask<String, Void, List<QuestionInfo>> {

        private RequestListener reqListener;

        public GetNewAlikeQuestionTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<QuestionInfo> doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String questionId = params[1];
            String studentId = params[2];
            return robotQaService.getNewAlikeQuestion(accessToken, questionId, studentId);
        }

        @Override
        protected void onResult(List<QuestionInfo> resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<List<QuestionInfo>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取错误原因
     */
    class GetErrorReasonDoAfterTask extends AppAsyncTask<String, Void, List<QAAnsInfo>> {

        private RequestListener reqListener;

        public GetErrorReasonDoAfterTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<QAAnsInfo> doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            String examId = params[2];
            String questionId = params[3];
            return robotQaService.getErrorReasonDoAfter(accessToken, studentId, examId, questionId);
        }

        @Override
        protected void onResult(List<QAAnsInfo> resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<List<QAAnsInfo>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取题目详情
     */
    class GetQuestionInfoTask extends AppAsyncTask<String, Void, QuestionInfo> {

        private RequestListener reqListener;

        public GetQuestionInfoTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected QuestionInfo doExecute(String... params) throws Exception {
            String questionId = params[0];
            return robotQaService.getQuestionInfo(questionId);
        }

        @Override
        protected void onResult(QuestionInfo question) {
            reqListener.onSuccess(question);
        }

        @Override
        protected void onFailure(HttpResponse<QuestionInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取题目详情
     */
    class GetAnswerPermissionTask extends AppAsyncTask<String, Void, AnswerPermission> {

        private RequestListener reqListener;

        public GetAnswerPermissionTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected AnswerPermission doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId = params[1];
            return robotQaService.getAnswerPermission(studentId, classId);
        }

        @Override
        protected void onResult(AnswerPermission answerPermission) {
            reqListener.onSuccess(answerPermission);
        }

        @Override
        protected void onFailure(HttpResponse<AnswerPermission> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 机器人提问请求，以便服务器统计次数
     */
    class AskRobotTask extends AppAsyncTask<String, Void, Void> {

        private RequestListener reqListener;

        public AskRobotTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String questionId = params[1];
            String studentId = params[2];
            String classId = params[3];
            robotQaService.askRobot(accessToken, questionId, studentId, classId);
            return null;
        }

        @Override
        protected void onResult(Void res) {
            reqListener.onSuccess(res);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 点赞
     * likeType 点赞类型：s相似题点赞，r认知点赞
     */
    class GaveLikeTask extends AppAsyncTask<String, Void, Integer> {

        private RequestListener reqListener;

        public GaveLikeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Integer doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String likeType = params[1];
            String questionId = params[2];
            String studentId = params[3];
            Log.d("QuestionDoTimesTask", "doExecute: ");
            return robotQaService.giveLike(accessToken, likeType, questionId, studentId);
        }

        @Override
        protected void onResult(Integer resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Integer> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 机器人评分
     */
    class RobotCommentTask extends AppAsyncTask<String, Void, ResultInfo> {

        private RequestListener reqListener;

        public RobotCommentTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ResultInfo doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String score = params[1];
            String scoreType = params[2];
            String questionId = params[3];
            String studentId = params[4];
            Log.d("QuestionDoTimesTask", "doExecute: ");
            return robotQaService.commentRobotService(accessToken, score, scoreType, questionId, studentId);
        }

        @Override
        protected void onResult(ResultInfo resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<ResultInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取学生访问微问信息
     */
    class StuClickQaInfoTask extends AppAsyncTask<String, Void, StuClickQaInfo> {

        private RequestListener reqListener;

        public StuClickQaInfoTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected StuClickQaInfo doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String classId = params[1];
            String studentId = params[2];
            return robotQaService.getClickRobotCount(accessToken, classId, studentId);
        }

        @Override
        protected void onResult(StuClickQaInfo resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<StuClickQaInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取学生访问微问历史记录
     */
    class StuClickQaHistoryTask extends AppAsyncTask<String, Void, StuQaClickHistory> {

        private RequestListener reqListener;

        public StuClickQaHistoryTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected StuQaClickHistory doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            return robotQaService.getClickRobotHistory(accessToken, studentId);
        }

        @Override
        protected void onResult(StuQaClickHistory resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<StuQaClickHistory> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 获取学生访问微问班级排名
     */
    class GetClickQaClassRankInfoTask extends AppAsyncTask<String, Void, ClickQaRankInClassInfo> {

        private RequestListener reqListener;

        public GetClickQaClassRankInfoTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ClickQaRankInClassInfo doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String classId = params[1];
            String studentId = params[2];
            return robotQaService.getClickQaClassRankInfo(accessToken, classId, studentId);
        }

        @Override
        protected void onResult(ClickQaRankInClassInfo resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<ClickQaRankInClassInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取学生访问微问准星排名
     */
    class GetClickQaZXRankInfoTask extends AppAsyncTask<String, Void, ClickQaRankInZXInfo> {

        private RequestListener reqListener;

        public GetClickQaZXRankInfoTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ClickQaRankInZXInfo doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            return robotQaService.getClickQaZXRankInfo(accessToken, studentId);
        }

        @Override
        protected void onResult(ClickQaRankInZXInfo resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<ClickQaRankInZXInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取同学答案
     */
    class GetOtherStuAnsTask extends AppAsyncTask<String, Void, OtherStuAns> {

        private RequestListener reqListener;

        public GetOtherStuAnsTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected OtherStuAns doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            String examId = params[2];
            String questionId = params[3];
            return robotQaService.getOtherStuAns(accessToken, studentId, examId, questionId);
        }

        @Override
        protected void onResult(OtherStuAns resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<OtherStuAns> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 给同学答案点赞
     */
    class GiveLikeToStuAnsTask extends AppAsyncTask<String, Void, Void> {

        private RequestListener reqListener;

        public GiveLikeToStuAnsTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            String questionId = params[2];
            String likedStudentId = params[3];
            String examId = params[4];
            robotQaService.giveLikeToStuAns(accessToken, studentId, questionId, likedStudentId, examId);
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

    public void queryQAAnsDoBefore(String accessToken, String studentId, String questionId, RequestListener requestListener) {
        QueryQAAnsDoBeforeTask queryQAAnsDoBeforeTask = new QueryQAAnsDoBeforeTask(requestListener);
        queryQAAnsDoBeforeTask.execute(accessToken, studentId, questionId);
        runningTasks.add(queryQAAnsDoBeforeTask);
    }

    public void getQuestDotimes(String accessToken, String studentId, String questionId, RequestListener requestListener) {
        QuestionDoTimesTask questionDoTimesTask = new QuestionDoTimesTask(requestListener);
        questionDoTimesTask.execute(accessToken, studentId, questionId);
        runningTasks.add(questionDoTimesTask);

    }

    public void getAlikeQuestion(String accessToken, String questionId, String diff, RequestListener requestListener) {
        GetAlikeQuestionTask getAlikeQTask = new GetAlikeQuestionTask(requestListener);
        getAlikeQTask.execute(accessToken, questionId, diff);
        runningTasks.add(getAlikeQTask);
    }

    public void getNewAlikeQuestion(String accessToken, String questionId, String studentId, RequestListener requestListener) {
        GetNewAlikeQuestionTask task = new GetNewAlikeQuestionTask(requestListener);
        task.execute(accessToken, questionId, studentId);
        runningTasks.add(task);
    }

    public void getErrorReason(String accessToken, String studentId, String examId, String questionId, RequestListener requestListener) {
        GetErrorReasonDoAfterTask getErrorReasonTask = new GetErrorReasonDoAfterTask(requestListener);
        getErrorReasonTask.execute(accessToken, studentId, examId, questionId);
        runningTasks.add(getErrorReasonTask);
    }

    public void getOtherClassmateAns(String accessToken, String studentId, String examId, String questionId, RequestListener requestListener) {
        GetErrorReasonDoAfterTask getErrorReasonTask = new GetErrorReasonDoAfterTask(requestListener);
        getErrorReasonTask.execute(accessToken, studentId, examId, questionId);
        runningTasks.add(getErrorReasonTask);
    }

    public void getQuestionInfo(String questionId, RequestListener requestListener) {
        GetQuestionInfoTask getQuestionInfoTask = new GetQuestionInfoTask(requestListener);
        getQuestionInfoTask.execute(questionId);
        runningTasks.add(getQuestionInfoTask);
    }

    public void getAnswerPermission(String studentId, String classId, RequestListener requestListener) {
        GetAnswerPermissionTask task = new GetAnswerPermissionTask(requestListener);
        task.execute(studentId, classId);
        runningTasks.add(task);
    }

    public void askRobot(String accessToken, String questionId, String studentId, String classId, RequestListener requestListener) {
        AskRobotTask askRobotTask = new AskRobotTask(requestListener);
        askRobotTask.execute(accessToken, questionId, studentId, classId);
        runningTasks.add(askRobotTask);
    }

    public void giveLike(String accessToken, String likeType, String questionId, String studentId, RequestListener requestListener) {
        GaveLikeTask gaveLikeTask = new GaveLikeTask(requestListener);
        gaveLikeTask.execute(accessToken, likeType, questionId, studentId);
        runningTasks.add(gaveLikeTask);
    }

    public void commentRobot(String accessToken, String score, String scoreType, String questionId, String studentId, RequestListener requestListener) {
        RobotCommentTask robotCommentTask = new RobotCommentTask(requestListener);
        robotCommentTask.execute(accessToken, score, scoreType, questionId, studentId);
        runningTasks.add(robotCommentTask);
    }

    public void getClickQaInfo(String accessToken, String classId, String studentId, RequestListener requestListener) {
        StuClickQaInfoTask stuClickQaInfoTask = new StuClickQaInfoTask(requestListener);
        stuClickQaInfoTask.execute(accessToken, classId, studentId);
        runningTasks.add(stuClickQaInfoTask);
    }

    public void getStuClickQaHistory(String accessToken, String studentId, RequestListener requestListener) {
        StuClickQaHistoryTask stuQaClickHistory = new StuClickQaHistoryTask(requestListener);
        stuQaClickHistory.execute(accessToken, studentId);
        runningTasks.add(stuQaClickHistory);
    }

    public void getClickQaClassRankInfo(String accessToken, String classId, String studentId, RequestListener requestListener) {
        GetClickQaClassRankInfoTask getClickQaClassRankInfoTask = new GetClickQaClassRankInfoTask(requestListener);
        getClickQaClassRankInfoTask.execute(accessToken, classId, studentId);
        runningTasks.add(getClickQaClassRankInfoTask);
    }

    public void getClickQaZXRankInfo(String accessToken, String studentId, RequestListener requestListener) {
        GetClickQaZXRankInfoTask getClickQaZXRankInfoTask = new GetClickQaZXRankInfoTask(requestListener);
        getClickQaZXRankInfoTask.execute(accessToken, studentId);
        runningTasks.add(getClickQaZXRankInfoTask);
    }

    /**
     * 同学答案
     * String accessToken = params[0];
     * String studentId = params[1];
     * String examId = params[2];
     * String questionId = params[3];
     * return robotQaService.getOtherStuAns(accessToken, studentId, examId, questionId);
     */
    public void getOtherStuAns(String accessToken, String studentId, String examId, String questionId,
                               RequestListener requestListener) {
        GetOtherStuAnsTask getOtherStuAnsTask = new GetOtherStuAnsTask(requestListener);
        getOtherStuAnsTask.execute(accessToken, studentId, examId, questionId);
        runningTasks.add(getOtherStuAnsTask);
    }

    /**
     * 给同学答案点赞
     */
    public void givelikeToOtherStuAns(String accessToken, String studentId, String questionId, String likedStudentId, String examId,
                                      RequestListener requestListener) {
        GiveLikeToStuAnsTask giveLikeToStuAnsTask = new GiveLikeToStuAnsTask(requestListener);
        giveLikeToStuAnsTask.execute(accessToken, studentId, questionId, likedStudentId, examId);
        runningTasks.add(giveLikeToStuAnsTask);
    }

    public void stopRunningTasks() {
        if (runningTasks.size() > 0) {
            AsyncTaskCancel.cancel(runningTasks);
        }
    }

}