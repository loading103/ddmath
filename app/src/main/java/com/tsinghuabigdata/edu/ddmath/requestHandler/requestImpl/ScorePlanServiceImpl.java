package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.PdfBean;
import com.tsinghuabigdata.edu.ddmath.bean.ScorePlanBean;
import com.tsinghuabigdata.edu.ddmath.bean.TaskBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekDatailBean;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.ScorePlanService;

import org.json.JSONException;

import java.util.ArrayList;

public class ScorePlanServiceImpl extends BaseService implements ScorePlanService {

    @Override
    public ArrayList<WeekBean> queryWeekList(String studentId, String classId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_SCORE_WEEKLIST) + "&studentId=" + studentId + "&classId=" + classId ;
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.requestJson().getBody();
        return new Gson().fromJson(res, new TypeToken<ArrayList<WeekBean>>() {}.getType());
    }

    @Override
    public ScorePlanBean queryScorePlan(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_SCORE_FIRSTPAGE) + "&studentId=" + studentId  ;
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.requestJson().getBody();
        return new Gson().fromJson(res, new TypeToken<ScorePlanBean>() {}.getType());
    }

    @Override
    public ArrayList<WeekDatailBean> queryCorretDataList(String studentId, String classId, long date) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_CORRET_LIST) + "&studentId=" + studentId  +"&date=" +date;
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.requestJson().getBody();
        return new Gson().fromJson(res, new TypeToken<ArrayList<WeekDatailBean>>() {}.getType());
    }
    @Override
    public TaskBean queryWeekTrainList(String studentId, String classId, long date) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_WEEK_TRAIN_LIST) + "&studentId=" + studentId +"&date=" + date;
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.requestJson().getBody();
        return new Gson().fromJson(res, new TypeToken<TaskBean>() {}.getType());
    }
    @Override
    public ArrayList<WeekDatailBean> queryDefineDataList(String studentId, String classId, long date) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_DEFINE_TRAIN_LIST) + "&studentId=" + studentId +"&date=" + date;
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.requestJson().getBody();
        return new Gson().fromJson(res, new TypeToken<ArrayList<WeekDatailBean>>() {}.getType());
    }

    @Override
    public boolean queryConfirmJoinCustomPlan( String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_CONFIRM_JOIN_CUSTOPMPLAN);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId);
        String res = request.request().getDataBody();
        if( !TextUtils.isEmpty(res) ){
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject( res );
            return jsonObject.getBoolean("success");
        }
        return false;
    }

    @Override
    public PdfBean queryOneKeyPdf(String studentId, long date) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.WEB_DOWNLOAD_PDF) + "&studentId=" + studentId  +"&date=" +date;
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.request().getBody();
        return new Gson().fromJson(res, new TypeToken<PdfBean>() {}.getType());
    }
}
