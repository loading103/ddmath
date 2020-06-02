package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EnterBean;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EntranceDetail;
import com.tsinghuabigdata.edu.ddmath.requestHandler.EntranceEvaluateService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.EntranceEvaluateImpl;

/**
 * 入学评测
 * Created by Administrator on 2017/6/12.
 */

public class EntranceModel {

    private EntranceEvaluateService mEntranceEvaluateService = new EntranceEvaluateImpl();

    public void  queryEnterDetail(String studentId,RequestListener requestListener) {
        EnterDetailTask task = new EnterDetailTask(requestListener);
        task.executeMulti(studentId);
    }

    public void  applyForReport(String studentId,RequestListener requestListener) {
        ApplyReportTask task = new ApplyReportTask(requestListener);
        task.executeMulti(studentId);
    }

    /**
     * 查询入学评测详情
     */
    class EnterDetailTask extends AppAsyncTask<String, Void, EntranceDetail> {

        private RequestListener reqListener;

        public EnterDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected EntranceDetail doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mEntranceEvaluateService.queryEnterDetail(studentId);
        }

        @Override
        protected void onResult(EntranceDetail entranceDetail) {
            reqListener.onSuccess(entranceDetail);
        }

        @Override
        protected void onFailure(HttpResponse<EntranceDetail> response, Exception ex) {
            reqListener.onFail(response,ex);
        }

    }

    /**
     * 学评测申请领取报告
     */
    class ApplyReportTask extends AppAsyncTask<String, Void, EnterBean> {

        private RequestListener reqListener;

        public ApplyReportTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected EnterBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mEntranceEvaluateService.applyForReport(studentId);
        }

        @Override
        protected void onResult(EnterBean enterBean) {
            reqListener.onSuccess(enterBean);
        }

        @Override
        protected void onFailure(HttpResponse<EnterBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }
}
