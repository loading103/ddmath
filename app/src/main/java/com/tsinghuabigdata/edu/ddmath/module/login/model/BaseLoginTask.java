package com.tsinghuabigdata.edu.ddmath.module.login.model;

import android.content.Context;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.requestHandler.LoginReqService;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.json.JSONException;

public abstract class BaseLoginTask extends AppAsyncTask<String, Void, LoginInfo> {

    private LoginReqService loginReqService;
    private Context mContext;

    /*public*/ BaseLoginTask(LoginReqService mLoginReqService, Context mContext) {
        this.loginReqService = mLoginReqService;
        this.mContext = mContext;
    }

    @Override
    protected LoginInfo doExecute(String... params) throws Exception {
        try {
            String username = params[0];
            String md5pass = params[1];
            return getLoginInfo(username, md5pass);
        } catch (final HttpRequestException e) {
            e.printStackTrace();
            AppLog.w(ErrTag.TAG_HTTP, e.getRequest().getBody(), e);
            setNetworkErr(0, e);
        }
        return null;
    }

    @Override
    protected abstract void onResult(LoginInfo loginInfo);

    @Override
    protected abstract void onFailure(HttpResponse<LoginInfo> response, Exception ex);

    public LoginInfo getLoginInfo(String username, String md5pass) throws HttpRequestException, JSONException {
        LoginInfo loginInfo = loginReqService.stulogin(username, md5pass, AppUtils.getDeviceId(mContext));
        if (loginInfo == null || TextUtils.isEmpty(loginInfo.getAccessToken())) {
            return null;
        }
        AccountUtils.setLoginUser(loginInfo);
        //登录成功后，更新本地的个人信息
        UserDetailinfo userDetailinfo = loginReqService.queryUserDetailinfo(loginInfo.getAccessToken(), loginInfo.getAccountId());

        //获取加入班级列表
//        ArrayList<JoinedClassInfo> joinedClassInfoList = loginReqService.getJoinedClassList(loginInfo.getAccessToken(), userDetailinfo.getStudentId());
        QueryTutorClassInfo tutorClassInfo = loginReqService.queryTutorClassinfo(loginInfo.getAccessToken(), userDetailinfo.getStudentId());

//        if (userDetailinfo != null) {
//            userDetailinfo.setJoinedClassInfoList(joinedClassInfoList);
            userDetailinfo.setQueryTutorClassInfo(tutorClassInfo);
            userDetailinfo.setFileAddress(loginInfo.getFileServer());
            AccountUtils.setUserdetailInfo(userDetailinfo);
//        }
        //GlobalData.setClassIndex(0);
        MyTutorClassInfo classInfo = AccountUtils.getCurrentClassInfo();
        if( classInfo == null && tutorClassInfo.getClassInfos()!=null && tutorClassInfo.getClassInfos().size()>0 ){
            GlobalData.setClassId( tutorClassInfo.getClassInfos().get(0).getClassId() );
        }
        return loginInfo;
    }
}