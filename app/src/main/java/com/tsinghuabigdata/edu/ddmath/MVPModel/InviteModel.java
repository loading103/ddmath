package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.InviteCountBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.InviteService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.InviteServiceImpl;

import java.util.List;

/**
 * Created by Administrator on 2018/4/26.
 */

public class InviteModel {

    private InviteService mService = new InviteServiceImpl();

    class InviteCountTask extends AppAsyncTask<String, Void, InviteCountBean> {
        private RequestListener reqListener;

        public InviteCountTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected InviteCountBean doExecute(String... params) throws Exception {
            return mService.getInviteCount();
        }

        @Override
        protected void onResult(InviteCountBean countBean) {
            reqListener.onSuccess(countBean);
        }

        @Override
        protected void onFailure(HttpResponse<InviteCountBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class ShareImagesTask extends AppAsyncTask<String, Void, List<String>> {
        private RequestListener reqListener;

        public ShareImagesTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected List<String> doExecute(String... params) throws Exception {
            return mService.getShareImages();
        }

        @Override
        protected void onResult(List<String> list) {
            reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<String>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取学豆数与推荐人数
     */
    public void getInviteCount(RequestListener requestListener) {
        InviteCountTask task = new InviteCountTask(requestListener);
        task.execute();
    }

    /**
     * 获取背景图
     */
    public void getShareImages(RequestListener requestListener) {
        ShareImagesTask task = new ShareImagesTask(requestListener);
        task.execute();
    }

}
