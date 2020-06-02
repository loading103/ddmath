package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.InviteCountBean;

import org.json.JSONException;

import java.util.List;

/**
 * 邀请分享类网络请求服务
 * Created by Administrator on 2018/4/26.
 */

public interface InviteService {

    /**
     * 获取学豆数与推荐人数
     */
    InviteCountBean getInviteCount() throws HttpRequestException, JSONException;

    /**
     * 获取背景图
     */
    List<String> getShareImages() throws HttpRequestException, JSONException;

}
