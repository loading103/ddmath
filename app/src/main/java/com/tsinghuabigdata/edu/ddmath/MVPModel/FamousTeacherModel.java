package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousProductBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.FamousTeacherService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.FamousTeacherServiceImpl;

import java.util.HashMap;
import java.util.List;

/**
 * 名师精讲 模块
 * Created by Administrator on 2018/3/7.
 */

public class FamousTeacherModel {

    private FamousTeacherService mService = new FamousTeacherServiceImpl();

    /**
     * 获取名师精讲商品列表
     */
    public void getFamousProductList(String studentId, String schoolId, RequestListener requestListener) {
        FamousProductListTask task = new FamousProductListTask(requestListener);
        task.execute(studentId, schoolId);
    }

    /**
     * 学习任务精讲视频列表查询
     */
    public void getVideoList(HashMap<String, String> params, RequestListener requestListener) {
        VideoListTask task = new VideoListTask(requestListener);
        task.execute(params);
    }

    /**
     * 查看已兑换视频列表
     */
    public void getExchangedVideoList(String studentId, String productId, RequestListener requestListener) {
        ExchangedVideoListTask task = new ExchangedVideoListTask(requestListener);
        task.execute(studentId, productId);
    }

    /**
     * 根据知识点获取视频列表
     */
    public void getVideoListByKnowledge(HashMap<String, String> params, RequestListener requestListener) {
        VideoListByKnowledgeTask task = new VideoListByKnowledgeTask(requestListener);
        task.execute(params);
    }

    /**
     * 记录视频播放
     */
    public void recordVideoPlay(HashMap<String, String> params, RequestListener requestListener) {
        RecordVideoPlayTask task = new RecordVideoPlayTask(requestListener);
        task.execute(params);
    }

    /**
     * 获取名师精讲商品列表
     */
    class FamousProductListTask extends AppAsyncTask<String, Void, List<FamousProductBean>> {
        private RequestListener reqListener;

        public FamousProductListTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected List<FamousProductBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String schoolId = params[1];
            return mService.getFamousProductList(studentId, schoolId);
        }

        @Override
        protected void onResult(List<FamousProductBean> list) {
            reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<List<FamousProductBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 学习任务精讲视频列表查询
     */
    class VideoListTask extends AppAsyncTask<HashMap<String, String>, Void, FamousVideoBean> {

        private RequestListener reqListener;

        public VideoListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected FamousVideoBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.getVideoList(map);
        }

        @Override
        protected void onResult(FamousVideoBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<FamousVideoBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 查看已兑换视频列表
     */
    private class ExchangedVideoListTask extends AppAsyncTask<String, Void, List<SingleVideoBean>> {

        private RequestListener reqListener;

        ExchangedVideoListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<SingleVideoBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            String productId = params[1];
            return mService.getExchangedVideoList(studentId, productId);
        }

        @Override
        protected void onResult(List<SingleVideoBean> bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<List<SingleVideoBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 根据知识点获取视频列表
     */
    class VideoListByKnowledgeTask extends AppAsyncTask<HashMap<String, String>, Void, List<SingleVideoBean>> {

        private RequestListener reqListener;

        public VideoListByKnowledgeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected List<SingleVideoBean> doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.getVideoListByKnowledge(map);
        }

        @Override
        protected void onResult(List<SingleVideoBean> bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<List<SingleVideoBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 记录视频播放
     */
    class RecordVideoPlayTask extends AppAsyncTask<HashMap<String, String>, Void, String> {

        private RequestListener reqListener;

        public RecordVideoPlayTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected String doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.recordVideoPlay(map);
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
