package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;

import org.json.JSONException;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by 28205 on 2016/12/14.
 */
public interface MycenterReqService {
    /**
     * 上传自定义头像
     *
     * @param accessToken
     * @param stream
     */
    boolean uploadHeadImage(String accessToken, InputStream stream) throws HttpRequestException;

    /**
     * 个人信息编辑
     */
    void editPersoninfo(String accessToken, String accountId, String reallyName, String serial, String sex, String nickName, String phone ) throws HttpRequestException, JSONException;


    /**
     * 个人信息更新(v12.0修改)
     */
    String updatePersoninfo(HashMap<String, String> params) throws HttpRequestException, JSONException;

}
