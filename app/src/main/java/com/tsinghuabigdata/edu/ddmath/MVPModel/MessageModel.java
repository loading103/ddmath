package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.ExistNewMessage;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MessageService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.MessageServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MessageModel {

    private MessageService mMessageService = new MessageServiceImpl();

    //    private List<AsyncTask> runningTasks = new ArrayList<>();
    //    private String[] rowkeys;

    public void queryImportantMsg(String accountId, RequestListener requestListener) {
        QueryImportantTask task = new QueryImportantTask(requestListener);
        task.executeMulti(accountId);
    }

    public void queryHomeMsg(String accessToken, String accountId, String userRole, RequestListener requestListener) {
        QueryHomePageMsgTask queryHomePageMsgTask = new QueryHomePageMsgTask(requestListener);
        queryHomePageMsgTask.executeMulti(accessToken, accountId, userRole);
    }

    public void queryUserMsg(String studentId, RequestListener requestListener) {
        QueryUserMsgTask task = new QueryUserMsgTask(requestListener);
        task.executeMulti(studentId);
    }

    public void queryMsgDetail(String studentId, String rowKey, RequestListener requestListener) {
        QueryMsgDetailTask task = new QueryMsgDetailTask(requestListener);
        task.executeMulti(studentId, rowKey);
    }

    public void queryNewMsg(String studentId, RequestListener requestListener) {
        QueryNewMsgTask task = new QueryNewMsgTask(requestListener);
        task.executeMulti(studentId);
    }
    //----------------------------------------------------------------------------------------

    /**
     * 获取首页消息
     */
    class QueryHomePageMsgTask extends AppAsyncTask<String, Void, List<MessageInfo>> {

        private RequestListener reqListener;

        public QueryHomePageMsgTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<MessageInfo> doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String accountId = params[1];
            String userRole = params[2];
            return mMessageService.queryHomePageMsg(accessToken, accountId, userRole);
        }

        @Override
        protected void onResult(List<MessageInfo> resultInfo) {
            reqListener.onSuccess((List<MessageInfo>) resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<List<MessageInfo>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取首页重要轮播消息
     */
    class QueryImportantTask extends AppAsyncTask<String, Void, List<MessageInfo>> {

        private RequestListener reqListener;

        public QueryImportantTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected List<MessageInfo> doExecute(String... params) throws Exception {
            String accountId = params[0];
            return mMessageService.queryImportantMsg(accountId);
        }

        @Override
        protected void onResult(List<MessageInfo> resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<List<MessageInfo>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取用户消息列表
     */
    class QueryUserMsgTask extends AppAsyncTask<String, Void, ArrayList<MessageInfo>> {

        private RequestListener reqListener;

        public QueryUserMsgTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ArrayList<MessageInfo> doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mMessageService.queryMessageList(studentId);
        }

        @Override
        protected void onResult(ArrayList<MessageInfo> list) {
            reqListener.onSuccess(list);
        }

        @Override
        protected void onFailure(HttpResponse<ArrayList<MessageInfo>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取用户消息列表
     */
    class QueryMsgDetailTask extends AppAsyncTask<String, Void, MessageInfo> {

        private RequestListener reqListener;

        public QueryMsgDetailTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected MessageInfo doExecute(String... params) throws Exception {
            String studentId = params[0];
            String rowkey = params[1];
            return mMessageService.queryUserMsgInfo(studentId, rowkey);
        }

        @Override
        protected void onResult(MessageInfo msg) {
            reqListener.onSuccess(msg);
        }

        @Override
        protected void onFailure(HttpResponse<MessageInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询是否有信息
     */
    class QueryNewMsgTask extends AppAsyncTask<String, Void, ExistNewMessage> {

        private RequestListener reqListener;

        public QueryNewMsgTask(RequestListener requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ExistNewMessage doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mMessageService.queryNewMsg(studentId);
        }

        @Override
        protected void onResult(ExistNewMessage b) {
            reqListener.onSuccess(b);
        }

        @Override
        protected void onFailure(HttpResponse<ExistNewMessage> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    //    /**
    //     * 获取用户消息详情
    //     */
    //    class QueryUserMsgInfoTask extends AppAsyncTask<String, Void, MessageInfo> {
    //
    //        private RequestListener reqListener;
    //
    //        public QueryUserMsgInfoTask(RequestListener requestListener) {
    //            reqListener = requestListener;
    //        }
    //
    //        @Override
    //        protected MessageInfo doExecute(String... params) throws Exception {
    //            String accessToken = params[0];
    //            String accountId = params[1];
    //            String rowkey = params[2];
    //            return mMessageService.queryUserMsgInfo(accessToken, accountId, rowkey);
    //        }
    //
    //        @Override
    //        protected void onResult(MessageInfo resultInfo) {
    //            reqListener.onSuccess(resultInfo);
    //        }
    //
    //        @Override
    //        protected void onFailure(HttpResponse<MessageInfo> response, Exception ex) {
    //            reqListener.onFail(response, ex);
    //        }
    //    }
    //
    //    /**
    //     * 清除用户消息
    //     */
    //    class CleanUserMsgTask extends AppAsyncTask<String, Void, ResultInfo> {
    //
    //        private RequestListener reqListener;
    //
    //        public CleanUserMsgTask(RequestListener requestListener) {
    //            reqListener = requestListener;
    //        }
    //
    //        @Override
    //        protected ResultInfo doExecute(String... params) throws Exception {
    //            String accessToken = params[0];
    //            String accountId = params[1];
    //            return mMessageService.cleanUserMsg(accessToken, accountId, rowkeys);
    //        }
    //
    //        @Override
    //        protected void onResult(ResultInfo resultInfo) {
    //            reqListener.onSuccess(resultInfo);
    //        }
    //
    //        @Override
    //        protected void onFailure(HttpResponse<ResultInfo> response, Exception ex) {
    //            reqListener.onFail(response, ex);
    //        }
    //    }
    //    /**
    //     * 清除用户消息
    //     */
    //    class CleanUserMsgAllTask extends AppAsyncTask<String, Void, ResultInfo> {
    //
    //        private RequestListener reqListener;
    //
    //        public CleanUserMsgAllTask(RequestListener requestListener) {
    //            reqListener = requestListener;
    //        }
    //
    //        @Override
    //        protected ResultInfo doExecute(String... params) throws Exception {
    //            String accessToken = params[0];
    //            String teacherId = params[1];
    //            String column = params[2];
    //            return mMessageService.cleanUserMsgAll(accessToken, teacherId, column);
    //        }
    //
    //        @Override
    //        protected void onResult(ResultInfo resultInfo) {
    //            reqListener.onSuccess(resultInfo);
    //        }
    //
    //        @Override
    //        protected void onFailure(HttpResponse<ResultInfo> response, Exception ex) {
    //            reqListener.onFail(response, ex);
    //        }
    //    }
    //
    //    /**
    //     * 获取首页消息
    //     */
    //    class QueryHomePageMsgTask extends AppAsyncTask<String, Void, List<MessageInfo>> {
    //
    //        private RequestListener reqListener;
    //
    //        public QueryHomePageMsgTask(RequestListener requestListener) {
    //            reqListener = requestListener;
    //        }
    //
    //        @Override
    //        protected List<MessageInfo> doExecute(String... params) throws Exception {
    //            String accessToken = params[0];
    //            String accountId = params[1];
    //            return mMessageService.queryHomePageMsg(accessToken, accountId);
    //        }
    //
    //        @Override
    //        protected void onResult(List<MessageInfo> resultInfo) {
    //            reqListener.onSuccess((List<MessageInfo>) resultInfo);
    //        }
    //
    //        @Override
    //        protected void onFailure(HttpResponse<List<MessageInfo>> response, Exception ex) {
    //            reqListener.onFail(response, ex);
    //        }
    //    }
    //
    //    /**
    //     * 查看用户是否有新消息
    //     */
    //    class QueryIsexistNewUserMsgTask extends AppAsyncTask<String, Void, IsExistNewMsgResult> {
    //
    //        private RequestListener reqListener;
    //
    //        public QueryIsexistNewUserMsgTask(RequestListener requestListener) {
    //            reqListener = requestListener;
    //        }
    //
    //        @Override
    //        protected IsExistNewMsgResult doExecute(String... params) throws Exception {
    //            String accessToken = params[0];
    //            String accountId = params[1];
    //            String msgColumn = params[2];
    //            return mMessageService.queryIsexistNewUserMsg(accessToken, accountId, msgColumn);
    //        }
    //
    //        @Override
    //        protected void onResult(IsExistNewMsgResult resultInfo) {
    //            reqListener.onSuccess(resultInfo);
    //        }
    //
    //        @Override
    //        protected void onFailure(HttpResponse<IsExistNewMsgResult> response, Exception ex) {
    //            reqListener.onFail(response, ex);
    //        }
    //    }
    //
    //    public void queryHomeMsg(String accessToken, String accountId, RequestListener requestListener) {
    //        QueryHomePageMsgTask queryHomePageMsgTask = new QueryHomePageMsgTask(requestListener);
    //        queryHomePageMsgTask.execute(accessToken, accountId);
    //        runningTasks.add(queryHomePageMsgTask);
    //    }
    //
    //
    //
    //    public void queryUserMsgInfo(String accessToken, String accountId, String rowkey, RequestListener requestListener) {
    //        QueryUserMsgInfoTask queryUserMsgInfoTask = new QueryUserMsgInfoTask(requestListener);
    //        queryUserMsgInfoTask.execute(accessToken, accountId, rowkey);
    //
    //    }
    //    public void cleanUserMsgAll(String accessToken, String teacherId, String column, RequestListener requestListener) {
    //        CleanUserMsgAllTask cleanUserMsgTask = new CleanUserMsgAllTask(requestListener);
    //        cleanUserMsgTask.execute(accessToken, teacherId, column);
    //
    //    }
    //    public void cleanUserMsgInfo(String accessToken, String accountId, String[] rowkey, RequestListener requestListener) {
    //        CleanUserMsgTask cleanUserMsgTask = new CleanUserMsgTask(requestListener);
    //        rowkeys = rowkey;
    //        cleanUserMsgTask.execute(accessToken, accountId);
    //    }
    //    public void queryIsexistNewUserMsgInfo(String accessToken, String accountId, String msgColumn, RequestListener requestListener) {
    //        QueryIsexistNewUserMsgTask queryIsexistNewUserMsgTask = new QueryIsexistNewUserMsgTask(requestListener);
    //        queryIsexistNewUserMsgTask.execute(accessToken, accountId, msgColumn);
    //    }
    //
    //    public void stopRunningTasks() {
    //        if (runningTasks.size() > 0) {
    //            AsyncTaskCancel.cancel(runningTasks);
    //        }
    //    }

}