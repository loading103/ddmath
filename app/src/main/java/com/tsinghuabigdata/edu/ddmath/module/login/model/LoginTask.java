package com.tsinghuabigdata.edu.ddmath.module.login.model;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.requestHandler.LoginReqService;

/**
 * 登录
 */

public class LoginTask extends BaseLoginTask {
    private RequestListener reqListener;

    public LoginTask(LoginReqService mUserService, Context mContext, RequestListener requestListener) {
        super(mUserService, mContext);
        reqListener = requestListener;
    }

    @Override
    protected void onResult(LoginInfo loginInfo) {
        if(null!=reqListener)reqListener.onSuccess(loginInfo);
    }

    @Override
    protected void onFailure(HttpResponse<LoginInfo> response, Exception ex) {
        if(null!=reqListener)reqListener.onFail(response, ex);
    }

}
