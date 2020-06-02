package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanDetailBean;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.DayCleanResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.StageReviewResult;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.WeekTrainResult;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.ErrorBookService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.ErrorBookServiceImpl;

/**
 * 错题本
 * Created by cubo on 2017/11/15.
 */

public class ErrorBookModel {

    private ErrorBookService mService = new ErrorBookServiceImpl();

    public void queryDayCleanList(String studentId,/*long startDate, long endDate,*/ RequestListener requestListener) {
        DayCleanTask task = new DayCleanTask(requestListener);
        task.executeMulti(studentId/*,String.valueOf(startDate), String.valueOf(endDate)*/ );
    }
    public void queryDayCleanDetail(String studentId,String currentDate, boolean master, int pageNum, int pageSize, int listNumber, RequestListener requestListener) {
        DayCleanDetailTask task = new DayCleanDetailTask(requestListener);
        task.executeMulti(studentId,currentDate, String.valueOf(master), String.valueOf(pageNum), String.valueOf(pageSize), String.valueOf(listNumber) );
    }
    public void  queryWeekTrainList(String studentId,int pageNum, int pageSize, long startTime, long endTime, int paperType, RequestListener requestListener) {
        WeekTrainTask task = new WeekTrainTask(requestListener);
        task.executeMulti( studentId, String.valueOf(pageNum), String.valueOf(pageSize), String.valueOf(startTime), String.valueOf(endTime), String.valueOf(paperType) );
    }
    public void  queryWeekTrainShare(String studentId, String recordId, RequestListener<ShareBean> requestListener) {
        WeekTrainShareTask task = new WeekTrainShareTask(requestListener);
        task.executeMulti(studentId,recordId);
    }

    public void  queryStageReviewList(String studentId,int pageNum, int pageSize, long startTime, long endTime, int paperType, RequestListener requestListener) {
        StageReviewTask task = new StageReviewTask(requestListener);
        task.executeMulti(studentId, String.valueOf(pageNum), String.valueOf(pageSize), String.valueOf(startTime), String.valueOf(endTime), String.valueOf(paperType) );
    }
    public void  queryStageReviewDetail(String studentId, String recordId, RequestListener requestListener) {
        StageReviewDetailTask task = new StageReviewDetailTask(requestListener);
        task.executeMulti(studentId,recordId);
    }
    public void  queryStageReviewShare(String studentId, String recordId, RequestListener requestListener) {
        StageReviewShareTask task = new StageReviewShareTask(requestListener);
        task.executeMulti(studentId,recordId);
    }

    /**
     * 查询日日清列表
     */
    private class DayCleanTask extends AppAsyncTask<String, Void, DayCleanResult> {

        private RequestListener reqListener;

        DayCleanTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected DayCleanResult doExecute(String... params) throws Exception {
            String studentId = params[0];
            /*long startDate   = Long.parseLong(params[1]);
            long endDate     = Long.parseLong(params[2]);*/
            return mService.queryDayCleanTaskList(studentId/*,startDate,endDate*/);
        }

        @Override
        protected void onResult(DayCleanResult result) {
            reqListener.onSuccess(result);
        }

        @Override
        protected void onFailure(HttpResponse<DayCleanResult> response, Exception ex) {
            reqListener.onFail(response,ex);
        }

    }

    /**
     * 查询日日清详情
     */
    private class DayCleanDetailTask extends AppAsyncTask<String, Void, DayCleanDetailBean> {

        private RequestListener reqListener;

        DayCleanDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected DayCleanDetailBean doExecute(String... params) throws Exception {
            String studentId        = params[0];
            String currentDate      = params[1];
            boolean master          = Boolean.parseBoolean(params[2]);
            int pageNum             = Integer.parseInt( params[3] );
            int pageSize            = Integer.parseInt( params[4] );
            int listNumber          = Integer.parseInt( params[5] );

            return mService.queryDayCleanDetail(studentId, currentDate, listNumber, master, pageNum, pageSize );
        }

        @Override
        protected void onResult(DayCleanDetailBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<DayCleanDetailBean> response, Exception ex) {
            reqListener.onFail(response,ex);
        }

    }

    /**
     * 查询错题周题练列表
     */
    private class WeekTrainTask extends AppAsyncTask<String, Void, WeekTrainResult> {

        private RequestListener<WeekTrainResult> reqListener;

        WeekTrainTask(RequestListener<WeekTrainResult> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected WeekTrainResult doExecute(String... params) throws Exception {
            String studentId = params[0];
            int pageNum      = Integer.parseInt( params[1] );
            int pageSize     = Integer.parseInt( params[2] );
            long startTime   = Long.valueOf( params[3] );
            long endTime     = Long.valueOf( params[4] );
            int paperType    = Integer.parseInt( params[5] );
            return mService.queryWeekTrainList(studentId,pageNum,pageSize, startTime, endTime, paperType);
        }

        @Override
        protected void onResult(WeekTrainResult result) {
            if( reqListener!=null)reqListener.onSuccess(result);
        }

        @Override
        protected void onFailure(HttpResponse<WeekTrainResult> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response,ex);
        }

    }

    /**
     * 查询错题周题练 分享下载
     */
    private class WeekTrainShareTask extends AppAsyncTask<String, Void, ShareBean> {

        private RequestListener<ShareBean> reqListener;

        WeekTrainShareTask(RequestListener<ShareBean> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ShareBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String recordId  = params[1];
            return mService.queryWeekTrainShare(studentId,recordId);
        }

        @Override
        protected void onResult(ShareBean url) {
            if(reqListener!=null)reqListener.onSuccess(url);
        }

        @Override
        protected void onFailure(HttpResponse<ShareBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response,ex);
        }

    }

    /**
     * 查询错题本下载 列表
     */
    private class StageReviewTask extends AppAsyncTask<String, Void, StageReviewResult > {

        private RequestListener reqListener;

        StageReviewTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected StageReviewResult doExecute(String... params) throws Exception {
            String studentId = params[0];
            int pageNum      = Integer.parseInt( params[1] );
            int pageSize     = Integer.parseInt( params[2] );
            long startTime   = Long.parseLong( params[3] );
            long endTime     = Long.parseLong( params[4] );
            int paperType    = Integer.parseInt( params[5] );
            return mService.queryStageReviewList(studentId, pageNum,pageSize, startTime, endTime,paperType);
        }

        @Override
        protected void onResult(StageReviewResult result) {
            reqListener.onSuccess(result);
        }

        @Override
        protected void onFailure(HttpResponse<StageReviewResult> response, Exception ex) {
            reqListener.onFail(response,ex);
        }
    }

    /**
     * 查询错题本下载 --分享
     */
    private class StageReviewShareTask extends AppAsyncTask<String, Void, String> {

        private RequestListener reqListener;

        StageReviewShareTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected String doExecute(String... params) throws Exception {
            String studentId = params[0];
            String recordId  = params[1];
            return mService.queryStageReviewShare(studentId,recordId);
        }

        @Override
        protected void onResult(String path) {
            reqListener.onSuccess(path);
        }

        @Override
        protected void onFailure(HttpResponse<String> response, Exception ex) {
            reqListener.onFail(response,ex);
        }
    }

    /**
     * 查询错题本下载 --详情
     */
    private class StageReviewDetailTask extends AppAsyncTask<String, Void, String> {

        private RequestListener reqListener;

        StageReviewDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected String doExecute(String... params) throws Exception {
            String studentId = params[0];
            String recordId  = params[1];
            return mService.queryStageReviewDetail(studentId,recordId);
        }

        @Override
        protected void onResult(String url) {
            reqListener.onSuccess(url);
        }

        @Override
        protected void onFailure(HttpResponse<String> response, Exception ex) {
            reqListener.onFail(response,ex);
        }
    }
}
