package com.tsinghuabigdata.edu.ddmath.parent.MVPModel;

import com.tsinghuabigdata.edu.commons.codec.MD5Utils;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.ParentCenterService;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.requestImpl.ParentCenterImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

import java.io.File;
import java.io.FileInputStream;


/**
 * 家长个人中心
 */

public class ParentCenterModel {

    private ParentCenterService mService = new ParentCenterImpl();

    //---------------------------------------------------
    //家长登录
    public void login(String loginName, String password, String deviceId, RequestListener<LoginInfo> requestListener) {
        LoginTask task = new LoginTask(requestListener);
        task.executeMulti( loginName, MD5Utils.stringToMD5(password), deviceId );
    }

    public void queryParentInfo(String parentId, RequestListener<ParentInfo> requestListener ){
        QueryParentInfoTask task = new QueryParentInfoTask(requestListener);
        task.executeMulti( parentId );
    }

    public void uploadHeadImage( String filename, RequestListener requestListener ){
        UploadHeadImageTask task = new UploadHeadImageTask(requestListener);
        task.executeMulti( filename );
    }

//    public void queryProductSuiteList(String studentId,  RequestListener requestListener ){
//        QueryProductSuiteTask task = new QueryProductSuiteTask(requestListener);
//        task.executeMulti( studentId );
//    }

    //-------------------------------------------------------------------------------------------------
    /**
     * 查询套题列表
     */
    private class LoginTask extends AppAsyncTask<String, Void, LoginInfo> {

        private RequestListener<LoginInfo> reqListener;

        LoginTask(RequestListener<LoginInfo> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected LoginInfo doExecute(String... params) throws Exception {
            String loginName  = params[0];
            String password   = params[1];
            String deviceId   = params[2];
            return mService.login( loginName, password, deviceId );
        }

        @Override
        protected void onResult(LoginInfo info) {
            if( reqListener!=null ) reqListener.onSuccess(info);
        }

        @Override
        protected void onFailure(HttpResponse<LoginInfo> response, Exception ex) {
            if( reqListener!=null ) reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询家长信息
     */
    private class QueryParentInfoTask extends AppAsyncTask<String, Void, ParentInfo> {

        private RequestListener<ParentInfo> reqListener;

        QueryParentInfoTask(RequestListener<ParentInfo> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ParentInfo doExecute(String... params) throws Exception {
            String parentId  = params[0];
            return mService.queryParentInfo( parentId );
        }

        @Override
        protected void onResult(ParentInfo info) {
            //更新缓存
            LoginInfo loginInfo = AccountUtils.getLoginParent();
            if( loginInfo!=null && loginInfo.getUserInfos()!=null&& loginInfo.getUserInfos().size()>0&& loginInfo.getUserInfos().get(0)!=null ){
                loginInfo.getUserInfos().get(0).setParentInfoVo( info );
                AccountUtils.setLoginParent( loginInfo );
            }
            if( reqListener!=null ) reqListener.onSuccess(info);
        }

        @Override
        protected void onFailure(HttpResponse<ParentInfo> response, Exception ex) {
            if( reqListener!=null ) reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询家长信息
     */
    private class UploadHeadImageTask extends AppAsyncTask<String, Void, Boolean> {

        private RequestListener<Boolean> reqListener;

        UploadHeadImageTask(RequestListener<Boolean> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Boolean doExecute(String... params) throws Exception {
            String fname  = params[0];
            return mService.uploadHeadImage( new FileInputStream( new File(fname)) );
        }

        @Override
        protected void onResult(Boolean info) {
            if( reqListener!=null ) reqListener.onSuccess(info);
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            if( reqListener!=null ) reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询套餐列表
     */
//    private class QueryProductSuiteTask extends AppAsyncTask<String, Void, ArrayList<ProductBean>> {
//
//        private RequestListener<ArrayList<ProductBean>> reqListener;
//
//        QueryProductSuiteTask(RequestListener<ArrayList<ProductBean>> requestListener) {
//            reqListener = requestListener;
//        }
//
//        @Override
//        protected ArrayList<ProductBean> doExecute(String... params) throws Exception {
//            String studentId  = params[0];
//            return mService.queryProductSuiteList( studentId );
//        }
//
//        @Override
//        protected void onResult(ArrayList<ProductBean> list) {
//            if( reqListener!=null ) reqListener.onSuccess(list);
//        }
//
//        @Override
//        protected void onFailure(HttpResponse<ArrayList<ProductBean>> response, Exception ex) {
//            if( reqListener!=null ) reqListener.onFail(response, ex);
//        }
//    }
}
