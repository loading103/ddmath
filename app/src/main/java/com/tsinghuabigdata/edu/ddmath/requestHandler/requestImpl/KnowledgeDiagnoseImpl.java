package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowlegeDiagnoseBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.QuestionCountBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.KnowledgeDiagnoseService;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2018/8/13.
 */

public class KnowledgeDiagnoseImpl extends BaseService implements KnowledgeDiagnoseService {


    @Override
    public KnowlegeDiagnoseBean getKnowlegeDiagnose(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.KNOWLEDGE_DIAGNOSE);
        HttpRequest request = AppRequestUtils.get(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if ("studentId".equals(key)) {
                    request.putRestfulParam(key, value);
                } else {
                    request.putRequestParam(key, value);
                }

            }
        }
        request.request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, KnowlegeDiagnoseBean.class);
    }

    @Override
    public QuestionCountBean getQuestionCount(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.PROMOTE_QUESTION_COUNT);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if ("studentId".equals(key)) {
                    request.putRestfulParam(key, value);
                } else {
                    request.putJsonParam(key, value);
                }

            }
        }
        request.requestJson();
        String res = request.getDataBody();
        return new Gson().fromJson(res, QuestionCountBean.class);
    }

    @Override
    public String produceQuestionSet(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.PRODUCE_QUESTION_SET);
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
