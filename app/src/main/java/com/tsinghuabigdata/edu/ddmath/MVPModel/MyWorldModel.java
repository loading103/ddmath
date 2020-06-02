package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.bean.ClassGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.ErrorReviseStatus;
import com.tsinghuabigdata.edu.ddmath.bean.FirstGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.GradeGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.RecentWorkStatus;
import com.tsinghuabigdata.edu.ddmath.bean.WeekErrorStatus;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.first.bean.FirstWorkStatusBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.CityRankResult;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgeMasterDetail;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyWorldService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.MyWorldServiceImpl;

import java.util.List;


/**
 * 首页我的世界
 */

public class MyWorldModel {

    private MyWorldService mMyWorldService = new MyWorldServiceImpl();

    public void queryFirstGloryRank(String classId, String studentId, RequestListener requestListener) {
        FirstGloryRankTask task = new FirstGloryRankTask(requestListener);
        task.executeMulti(classId, studentId);
    }

    public void queryClassGloryRank(String classId, String studentId, int pageNum, int pageSize, RequestListener requestListener) {
        ClassGloryRankTask task = new ClassGloryRankTask(requestListener);
        task.executeMulti(classId, studentId, pageNum + "", pageSize + "");
    }

    public void queryGradeGloryRank(String classId, String studentId, int pageNum, int pageSize, RequestListener requestListener) {
        GradeGloryRankTask task = new GradeGloryRankTask(requestListener);
        task.executeMulti(classId, studentId, pageNum + "", pageSize + "");
    }
    public void querCityGloryRank( String studentId, String classId, RequestListener<CityRankResult> requestListener) {
        CityGloryRankTask task = new CityGloryRankTask(requestListener);
        task.executeMulti( studentId, classId );
    }

    public void queryKnowDiagnose(String studentId, String examId, String lastExamId, RequestListener requestListener) {
        KnowDiagnoseTask task = new KnowDiagnoseTask(requestListener);
        task.executeMulti(studentId, examId, lastExamId);
    }


    public void queryErrorReviseStatus(String studentId, RequestListener requestListener) {
        ErrorReviseTask task = new ErrorReviseTask(requestListener);
        task.executeMulti(studentId);
    }

    public void queryRecentWorkStatus(String classId, String studentId, RequestListener<RecentWorkStatus> requestListener) {
        RecentWorkStatusTask task = new RecentWorkStatusTask(requestListener);
        task.executeMulti(classId, studentId );
    }

    public void queryWeekErrorStatus(String studentId, long createTime, RequestListener<WeekErrorStatus> requestListener) {
        WeekErrorStatusTask task = new WeekErrorStatusTask(requestListener);
        task.executeMulti(studentId, String.valueOf(createTime));
    }

    public void queryFirstWorkStatus(String studentId, String classId, RequestListener<FirstWorkStatusBean> requestListener) {
        FirstWorkStatusTask task = new FirstWorkStatusTask(requestListener);
        task.executeMulti(studentId, classId);
    }

    //------------------------------------------------------------------------------------------------------------------
    /**
     * 荣耀值排行榜-首页
     */
    class FirstGloryRankTask extends AppAsyncTask<String, Void, List<FirstGloryRank>> {

        private RequestListener reqListener;

        public FirstGloryRankTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected List<FirstGloryRank> doExecute(String... params) throws Exception {
            String classId = params[0];
            String studentId = params[1];
            return mMyWorldService.queryFirstGloryRank(classId, studentId);
        }

        @Override
        protected void onResult(List<FirstGloryRank> list) {
            reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<FirstGloryRank>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 荣耀排行榜-班级排行榜-个人中心
     */
    class ClassGloryRankTask extends AppAsyncTask<String, Void, ClassGloryRank> {

        private RequestListener reqListener;

        public ClassGloryRankTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ClassGloryRank doExecute(String... params) throws Exception {
            String classId = params[0];
            String studentId = params[1];
            int pageNum = Integer.parseInt(params[2]);
            int pageSize = Integer.parseInt(params[3]);
            return mMyWorldService.queryClassGloryRank(classId, studentId, pageNum, pageSize);
        }

        @Override
        protected void onResult(ClassGloryRank classGloryRank) {
            reqListener.onSuccess(classGloryRank);
        }

        @Override
        protected void onFailure(HttpResponse<ClassGloryRank> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 知识诊断
     */
    private class KnowDiagnoseTask extends AppAsyncTask<String, Void, KnowledgeMasterDetail> {

        private RequestListener reqListener;

        /*public*/ KnowDiagnoseTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected KnowledgeMasterDetail doExecute(String... params) throws Exception {
            String studentId = params[0];
            String examId = params[1];
            String lastExamId = params[2];

            return mMyWorldService.queryKnowDiagnose(studentId, examId, lastExamId);
        }

        @Override
        protected void onResult(KnowledgeMasterDetail classGloryRank) {
            reqListener.onSuccess(classGloryRank);
        }

        @Override
        protected void onFailure(HttpResponse<KnowledgeMasterDetail> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 荣耀排行榜-年级排名-个人中心
     */
    class GradeGloryRankTask extends AppAsyncTask<String, Void, GradeGloryRank> {

        private RequestListener reqListener;

        public GradeGloryRankTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected GradeGloryRank doExecute(String... params) throws Exception {
            String classId = params[0];
            String studentId = params[1];
            int pageNum = Integer.parseInt(params[2]);
            int pageSize = Integer.parseInt(params[3]);
            return mMyWorldService.queryGradeGloryRank(classId, studentId, pageNum, pageSize);
        }

        @Override
        protected void onResult(GradeGloryRank gradeGloryRank) {
            if(reqListener!=null)reqListener.onSuccess(gradeGloryRank);
        }

        @Override
        protected void onFailure(HttpResponse<GradeGloryRank> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * 荣耀排行榜-城市排名
     */
    private class CityGloryRankTask extends AppAsyncTask<String, Void, CityRankResult> {

        private RequestListener<CityRankResult> reqListener;

        /*public*/ CityGloryRankTask(RequestListener<CityRankResult> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected CityRankResult doExecute(String... params) throws Exception {
            String classId = params[1];
            String studentId = params[0];
            return mMyWorldService.queryCityGloryRank(studentId,classId);
        }

        @Override
        protected void onResult(CityRankResult gradeGloryRank) {
            reqListener.onSuccess(gradeGloryRank);
        }

        @Override
        protected void onFailure(HttpResponse<CityRankResult> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 每日错题订正状态查询
     */
    class ErrorReviseTask extends AppAsyncTask<String, Void, ErrorReviseStatus> {

        private RequestListener reqListener;

        /*public*/ ErrorReviseTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ErrorReviseStatus doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mMyWorldService.queryErrorReviseStatus(studentId);
        }

        @Override
        protected void onResult(ErrorReviseStatus gradeGloryRank) {
            if(reqListener!=null)reqListener.onSuccess(gradeGloryRank);
        }

        @Override
        protected void onFailure(HttpResponse<ErrorReviseStatus> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * 查询每日最近一次布置作业的状态
     */
    private class RecentWorkStatusTask extends AppAsyncTask<String, Void, RecentWorkStatus> {

        private RequestListener<RecentWorkStatus> reqListener;

        /*public*/ RecentWorkStatusTask(RequestListener<RecentWorkStatus> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected RecentWorkStatus doExecute(String... params) throws Exception {
            String classId = params[0];
            String studentId = params[1];
            return mMyWorldService.queryRecentWorkStatus(classId, studentId);
        }

        @Override
        protected void onResult(RecentWorkStatus gradeGloryRank) {
            if(reqListener!=null)reqListener.onSuccess(gradeGloryRank);
        }

        @Override
        protected void onFailure(HttpResponse<RecentWorkStatus> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

    /**
     * 诊断首页查询最近一周周题练和每周培优状态
     */
    private class WeekErrorStatusTask extends AppAsyncTask<String, Void, WeekErrorStatus> {

        private RequestListener<WeekErrorStatus> reqListener;

        /*public*/ WeekErrorStatusTask(RequestListener<WeekErrorStatus> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected WeekErrorStatus doExecute(String... params) throws Exception {
            String studentId = params[0];
            long createTime = Long.decode(params[1]);
            return mMyWorldService.queryWeekErrorStatus(studentId,createTime);
        }

        @Override
        protected void onResult(WeekErrorStatus weekErrorStatus) {
            if(reqListener!=null)reqListener.onSuccess(weekErrorStatus);
        }

        @Override
        protected void onFailure(HttpResponse<WeekErrorStatus> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }
    /**
     * 诊断首页查询最近一周周题练和每周培优状态
     */
    private class FirstWorkStatusTask extends AppAsyncTask<String, Void, FirstWorkStatusBean> {

        private RequestListener<FirstWorkStatusBean> reqListener;

        /*public*/ FirstWorkStatusTask(RequestListener<FirstWorkStatusBean> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected FirstWorkStatusBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String classId = params[1];
            return mMyWorldService.queryFirstWorkStatus(studentId,classId);
        }

        @Override
        protected void onResult(FirstWorkStatusBean weekErrorStatus) {
            if(reqListener!=null)reqListener.onSuccess(weekErrorStatus);
        }

        @Override
        protected void onFailure(HttpResponse<FirstWorkStatusBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }



}
