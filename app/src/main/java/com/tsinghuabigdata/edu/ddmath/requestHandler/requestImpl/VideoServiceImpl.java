package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.requestHandler.VideoService;

import org.json.JSONException;

/**
 * Created by Administrator on 2018/1/25.
 */

public class VideoServiceImpl extends BaseService implements VideoService{
    @Override
    public String recordVideoCount(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.VIDEO_RECORD);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .request();
        return request.getDataBody();
    }
}
