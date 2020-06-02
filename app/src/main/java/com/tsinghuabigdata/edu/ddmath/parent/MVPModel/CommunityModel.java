package com.tsinghuabigdata.edu.ddmath.parent.MVPModel;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleListBean;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.CommunityService;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.requestImpl.CommunityImpl;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/7/10.
 */

public class CommunityModel {

    private CommunityService mService = new CommunityImpl();

    public void queryArticleList(HashMap<String, String> params, RequestListener requestListener) {
        ArticleListTask task = new ArticleListTask(requestListener);
        task.execute(params);
    }

    public void queryArticleDetail(HashMap<String, String> params, RequestListener requestListener) {
        ArticleDetailTask task = new ArticleDetailTask(requestListener);
        task.executeMulti(params);
    }

    public void operateArticle(HashMap<String, String> params, RequestListener requestListener) {
        OperateArticleTask task = new OperateArticleTask(requestListener);
        task.executeMulti(params);
    }

    /**
     * 文章列表
     */
    class ArticleListTask extends AppAsyncTask<HashMap<String, String>, Void, ArticleListBean> {

        private RequestListener reqListener;

        public ArticleListTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ArticleListBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.queryArticleList(map);
        }

        @Override
        protected void onResult(ArticleListBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<ArticleListBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 社区界面详情
     */
    class ArticleDetailTask extends AppAsyncTask<HashMap<String, String>, Void, ArticleBean> {

        private RequestListener reqListener;

        public ArticleDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ArticleBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.queryArticleDetail(map);
        }

        @Override
        protected void onResult(ArticleBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<ArticleBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 更新点赞转发阅读记录
     */
    class OperateArticleTask extends AppAsyncTask<HashMap<String, String>, Void, String> {

        private RequestListener reqListener;

        public OperateArticleTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected String doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.operateArticle(map);
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
