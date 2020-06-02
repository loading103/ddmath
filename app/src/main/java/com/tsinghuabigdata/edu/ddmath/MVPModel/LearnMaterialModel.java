package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.BookListBean;
import com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean.CatalogBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.LearnMaterialService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.LearnMaterialServiceImpl;

import java.util.HashMap;


/**
 * 原版教辅
 */

public class LearnMaterialModel {

    private LearnMaterialService mService = new LearnMaterialServiceImpl();

    /**
     * 原版教辅查询业内题目信息
     *
     * @param bookId          j教辅ID
     * @param pageNums        页码
     * @param requestListener 回调
     */
    public void querySelfWorkDetail(String bookId, String pageNums, RequestListener<DDWorkDetail> requestListener) {
        SelfWorkDetailTask task = new SelfWorkDetailTask(requestListener);
        task.executeMulti(bookId, pageNums);
    }

//    /**
//     * 原版教辅作业创建与上传
//     *
//     * @param studentId       学生ID
//     * @param classId         班级
//     * @param pages           班级信息
//     * @param requestListener 回调
//     */
//    public void createAndUploadSelfWork(String studentId, String classId, int pageCount, JSONArray pages, RequestListener requestListener ){
//        SelfWorkSubmitTask task = new SelfWorkSubmitTask(requestListener);
//        task.executeMulti(studentId,classId, String.valueOf(pageCount), pages.toJSONString());
//    }

    public void queryBookList(HashMap<String, String> params, RequestListener<BookListBean> requestListener) {
        QueryBookListTask task = new QueryBookListTask(requestListener);
        task.executeMulti(params);
    }

//    public void selectBook(String studentId, String bookId, RequestListener requestListener) {
//        SelectBookTask task = new SelectBookTask(requestListener);
//        task.executeMulti(studentId, bookId);
//    }

    public void queryBookCatalog(HashMap<String, String> params, RequestListener<CatalogBean> requestListener) {
        QueryBookCatalogTask task = new QueryBookCatalogTask(requestListener);
        task.executeMulti(params);
    }


    //------------------------------------------------------------------------------------------------
    private class SelfWorkDetailTask extends AppAsyncTask<String, Void, DDWorkDetail> {

        private RequestListener<DDWorkDetail>reqListener;

        SelfWorkDetailTask(RequestListener<DDWorkDetail> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected DDWorkDetail doExecute(String... params) throws Exception {
            String bookId = params[0];
            String pageNums = params[1];
            return mService.querySelfWorkDetail(bookId, pageNums);
        }

        @Override
        protected void onResult(DDWorkDetail bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<DDWorkDetail> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }

//    private class SelfWorkSubmitTask extends AppAsyncTask<String, Void, SelfWorkSubmitBean> {
//
//        private RequestListener reqListener;
//
//        SelfWorkSubmitTask(RequestListener requestListener) {
//            reqListener = requestListener;
//        }
//
//        @Override
//        protected SelfWorkSubmitBean doExecute(String... params) throws Exception {
//            String studentId = params[0];
//            String classId = params[1];
//            int pageCount = Integer.parseInt( params[2] );
//            org.json.JSONArray jsonArray = new org.json.JSONArray( params[3] );
//            return mService.createAndUploadSelfWork(studentId,classId, pageCount, jsonArray);
//        }
//
//        @Override
//        protected void onResult(SelfWorkSubmitBean bean) {
//            reqListener.onSuccess(bean);
//        }
//
//        @Override
//        protected void onFailure(HttpResponse<SelfWorkSubmitBean> response, Exception ex) {
//            reqListener.onFail(response, ex);
//        }
//    }

    private class QueryBookListTask extends AppAsyncTask<HashMap<String, String>, Void, BookListBean> {

        private RequestListener<BookListBean> reqListener;

        QueryBookListTask(RequestListener<BookListBean> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected BookListBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.queryBookList(map);
        }

        @Override
        protected void onResult(BookListBean bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<BookListBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }

//    private class SelectBookTask extends AppAsyncTask<String, Void, BookBean> {
//
//        private RequestListener<BookBean> reqListener;
//
//        SelectBookTask(RequestListener<BookBean> requestListener) {
//            reqListener = requestListener;
//        }
//
//        @Override
//        protected BookBean doExecute(String... params) throws Exception {
//            String studentId = params[0];
//            String bookId = params[1];
//            return mService.selectBook(studentId, bookId);
//        }
//
//        @Override
//        protected void onResult(BookBean bean) {
//            if(reqListener!=null)reqListener.onSuccess(bean);
//        }
//
//        @Override
//        protected void onFailure(HttpResponse<BookBean> response, Exception ex) {
//            if(reqListener!=null)reqListener.onFail(response, ex);
//        }
//    }


    class QueryBookCatalogTask extends AppAsyncTask<HashMap<String, String>, Void, CatalogBean> {

        private RequestListener<CatalogBean> reqListener;

        /*public*/ QueryBookCatalogTask(RequestListener<CatalogBean> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected CatalogBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.queryBookCatalog(map);
        }

        @Override
        protected void onResult(CatalogBean bean) {
            if(reqListener!=null)reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<CatalogBean> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }

    }

}
