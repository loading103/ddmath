package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.InviteCountBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.InviteService;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Administrator on 2018/4/26.
 */

public class InviteServiceImpl extends BaseService implements InviteService {

    @Override
    public InviteCountBean getInviteCount() throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_INVITE_COUNT);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("type", "app").request();
        String body = request.getDataBody();
        return new Gson().fromJson(body, InviteCountBean.class);
    }

    @Override
    public List<String> getShareImages() throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_SHARE_IMAGES);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("type", "3").request().getDataBody();
        return request.getResult(new TypeToken<List<String>>() {
        });
    }
}
