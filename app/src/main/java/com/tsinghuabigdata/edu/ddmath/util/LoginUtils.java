package com.tsinghuabigdata.edu.ddmath.util;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.event.UpdateClassEvent;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;

import org.greenrobot.eventbus.EventBus;

/**
 * 登录工具类
 * Created by Administrator on 2018/3/20.
 */

public class LoginUtils {


    public static void queryTutorClass() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        final UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
        if (loginInfo == null || userDetailinfo == null) {
            return;
        }
        new LoginModel().getTutorClassList(loginInfo.getAccessToken(), userDetailinfo.getStudentId(), new RequestListener<QueryTutorClassInfo>() {

            @Override
            public void onSuccess(QueryTutorClassInfo res) {
                userDetailinfo.setQueryTutorClassInfo(res);
                AccountUtils.setUserdetailInfo(userDetailinfo);
                //GlobalData.setClassIndex(0);
                EventBus.getDefault().post(new UpdateClassEvent());
            }

            @Override
            public void onFail(HttpResponse<QueryTutorClassInfo> response, Exception ex) {

            }
        });
    }
}
