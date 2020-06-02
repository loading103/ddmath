package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;

import org.json.JSONException;

/**
 * Created by Administrator on 2018/1/25.
 */

public interface VideoService {

    /**
     * 记录学生视频模块浏览记录
     */
    String recordVideoCount(String studentId) throws HttpRequestException, JSONException;

}
