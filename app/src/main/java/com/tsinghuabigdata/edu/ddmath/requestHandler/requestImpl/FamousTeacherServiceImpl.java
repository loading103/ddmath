package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousProductBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.FamousTeacherService;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 名师精讲 模块
 * Created by Administrator on 2018/3/7.
 */

public class FamousTeacherServiceImpl extends BaseService implements FamousTeacherService {

    @Override
    public List<FamousProductBean> getFamousProductList(String studentId, String schoolId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.get_Famous_Product_List);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("schoolId", schoolId)
                .request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<FamousProductBean>>() {
        }.getType());
    }

    @Override
    public FamousVideoBean getVideoList(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.get_Video_List);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if ("productId".equals(key)) {
                    request.putRestfulParam(key, value);
                } else {
                    request.putJsonParam(key, value);
                }

            }
        }
        request.requestJson();
        String res = request.getDataBody();
        return new Gson().fromJson(res, FamousVideoBean.class);
    }

    @Override
    public List<SingleVideoBean> getExchangedVideoList(String studentId, String productId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.get_Exchanged_Video_List);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("productId", productId)
                .putRequestParam("studentId", studentId)
                .request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<SingleVideoBean>>() {
        }.getType());
    }

    @Override
    public List<SingleVideoBean> getVideoListByKnowledge(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.get_Video_List_ByKnowledge);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        request.requestJson();
        String res = request.getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<SingleVideoBean>>() {
        }.getType());
    }

    @Override
    public String recordVideoPlay(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.record_Video_Play);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        request.requestJson();
        return request.getDataBody();
    }
}
