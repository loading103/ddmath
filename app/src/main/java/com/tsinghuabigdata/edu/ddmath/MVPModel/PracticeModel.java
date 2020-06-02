package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.VarTrainResult;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.ShareBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.PracticeService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.PracticeServiceImpl;

import java.util.List;


/**
 * 专属套题
 */

public class PracticeModel {

    private PracticeService mPracticeService = new PracticeServiceImpl();

    //---------------------------------------------------
    //变式训练
    public void getVarTrainPracticeList(String studentId, int pageNum, int pageSize, long startTime, long endTime, int paperType, RequestListener requestListener) {
        PracticeListTask task = new PracticeListTask(requestListener);
        task.executeMulti(studentId, String.valueOf(pageNum), String.valueOf(pageSize), String.valueOf(startTime), String.valueOf(endTime), String.valueOf(paperType));
    }

    /**
     * 分享下载
     * @param studentId  学生ID
     * @param productId  商品ID
     * @param recordId   套题ID
     * @param requestListener 回调
     */
    public void sharePractice(String classId, String studentId, String productId, String recordId, RequestListener requestListener) {
        SharePracticeTask task = new SharePracticeTask(requestListener);
        task.executeMulti(studentId,productId,recordId,classId);
    }

    //---------------------------------------------------
    //精品套题
    public void getClassicPracticeList(String studentId, String searchdata, String schoolid, RequestListener requestListener) {
        ClassicPracticeListTask task = new ClassicPracticeListTask(requestListener);
        task.executeMulti(studentId,searchdata,schoolid);
    }

    /**
     * 分享下载
     * @param studentId  学生ID
     * @param productId  商品ID
     * @param recordId   套题ID
     * @param requestListener 回调
     */
    public void shareClassicPractice(String classId, String studentId, String productId, String recordId, RequestListener requestListener) {
        ShareClassicPracticeTask task = new ShareClassicPracticeTask(requestListener);
        task.executeMulti(studentId,productId,recordId,classId);
    }

    //------------------------------------------------------------------------------------------------------
    /**
     * 查询套题列表
     */
    private class PracticeListTask extends AppAsyncTask<String, Void, VarTrainResult> {

        private RequestListener reqListener;

        PracticeListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected VarTrainResult doExecute(String... params) throws Exception {
            String studentId = params[0];
            int pageNum = Integer.valueOf(params[1]);
            int pageSize = Integer.valueOf(params[2]);
            long startTime = Long.valueOf(params[3]);
            long endTime = Long.valueOf(params[4]);
            int paperType = Integer.valueOf(params[5]);
            return mPracticeService.getVarTrainPracticeList(studentId,pageNum,pageSize, startTime, endTime, paperType);
        }

        @Override
        protected void onResult(VarTrainResult list) {
            reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<VarTrainResult> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 分享下载
     */
   private class SharePracticeTask extends AppAsyncTask<String, Void, ShareBean> {

        private RequestListener reqListener;

        SharePracticeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ShareBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String productId = params[1];
            String recordId  = params[2];
            String classId = params[3];
            return mPracticeService.getPracticeShare(classId,studentId,productId,recordId);
        }

        @Override
        protected void onResult(ShareBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<ShareBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 查询套题列表
     */
    private class ClassicPracticeListTask extends AppAsyncTask<String, Void, List<PracticeProductBean>> {

        private RequestListener reqListener;

        ClassicPracticeListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<PracticeProductBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String searchData = params[1];
            String schoolid   = params[2];
            return mPracticeService.getClassicPracticeList(studentId,searchData,schoolid);
        }

        @Override
        protected void onResult(List<PracticeProductBean> list) {
            reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<PracticeProductBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 分享下载
     */
    private class ShareClassicPracticeTask extends AppAsyncTask<String, Void, ShareBean> {

        private RequestListener reqListener;

        ShareClassicPracticeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ShareBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String productId = params[1];
            String recordId  = params[2];
            String classId = params[3];
            return mPracticeService.getClassicPracticeShare(classId,studentId,productId,recordId);
        }

        @Override
        protected void onResult(ShareBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<ShareBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }
}
