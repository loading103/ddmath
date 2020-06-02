package com.tsinghuabigdata.edu.ddmath.parent.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleListBean;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.CommunityService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.BaseService;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2018/7/10.
 */

public class CommunityImpl extends BaseService implements CommunityService {

    @Override
    public ArticleListBean queryArticleList(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.ARTICLE_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putRequestParam(key, value);
            }
        }
        request.request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, ArticleListBean.class);
    }

    @Override
    public ArticleBean queryArticleDetail(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.ARTICLE_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putRestfulParam(key, value);
            }
        }
        request.requestJson();
        String res = request.getDataBody();
        return new Gson().fromJson(res, ArticleBean.class);
    }

    @Override
    public String operateArticle(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.OPERATE_ARTICLE);
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
        return res;
    }
}
