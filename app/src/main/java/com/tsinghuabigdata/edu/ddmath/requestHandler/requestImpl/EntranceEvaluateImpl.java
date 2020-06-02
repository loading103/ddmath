package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EnterBean;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EntranceDetail;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.KnowledgeRecordBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.EntranceEvaluateService;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class EntranceEvaluateImpl extends BaseService implements EntranceEvaluateService {

    @Override
    public KnowledgeRecordBean queryKnowledgeEvaluate(String studentId) throws HttpRequestException, UnsupportedEncodingException, JSONException {

        String url = getUrl(AppRequestConst.ENTRANCE_QUERY_KNOWLEDGE);
        //url = url.replace("http://www.doudoushuxue.com","http://172.16.3.53");
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.requestJson().getDataBody();
        AppLog.d("Entrance  record res = " + res);
        return new Gson().fromJson(res, new TypeToken<KnowledgeRecordBean>() {
        }.getType());
    }

    @Override
    public String saveKnowledgeEvaluate(String studentId, String classId, ArrayList<String> images) throws HttpRequestException, UnsupportedEncodingException, JSONException {

        String url = getUrl(AppRequestConst.ENTRANCE_SAVE_KNOWLEDGE);
        //url = url.replace("http://www.doudoushuxue.com","http://172.16.3.53");

        HttpRequest request = AppRequestUtils.post(url);
        //request.setTimeout(40);

        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for( String path : images ){
                jsonArray.put( path );
            }
            jsonObject.put("studentId", studentId);
            jsonObject.put("classId", classId);
            jsonObject.put("images", jsonArray);
        } catch (Exception e) {
            AppLog.i( "", e );
        }
        request.setJsonStringParams(jsonObject.toString());
        return request.requestJson().getDataBody();
    }

    @Override
    public EntranceDetail queryEnterDetail(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.ENTRANCE_QUERY_INFO);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.requestJson().getDataBody();
        AppLog.d("Entrance  record res = " + res);
        return new Gson().fromJson(res, new TypeToken<EntranceDetail>() {
        }.getType());
    }

    @Override
    public EnterBean applyForReport(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.ENTRANCE_APPLY_INFO);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("studentId", studentId);
        String res = request.request().getDataBody();
        AppLog.d("Entrance  record res = " + res);
        return new Gson().fromJson(res, new TypeToken<EnterBean>() {
        }.getType());
    }

}
