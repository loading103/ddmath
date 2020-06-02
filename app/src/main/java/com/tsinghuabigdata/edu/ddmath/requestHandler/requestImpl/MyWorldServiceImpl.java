package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.ClassGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.ErrorReviseStatus;
import com.tsinghuabigdata.edu.ddmath.bean.FirstGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.GradeGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.RecentWorkStatus;
import com.tsinghuabigdata.edu.ddmath.bean.WeekErrorStatus;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.first.bean.FirstWorkStatusBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.CityRankResult;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgeMasterDetail;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MyWorldService;

import org.json.JSONException;

import java.util.List;

public class MyWorldServiceImpl extends BaseService implements MyWorldService {

    @Override
    public List<FirstGloryRank> queryFirstGloryRank(String classId, String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.FIRST_GLORY_RANK);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("classId", classId)
                .putRestfulParam("studentId", studentId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<FirstGloryRank>>() {
        }.getType());
    }

    @Override
    public ClassGloryRank queryClassGloryRank(String classId, String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.CLASS_GLORY_RANK);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("classId", classId)
                .putRestfulParam("studentId", studentId)
                .putRequestParam("pageNum", String.valueOf(pageNum))
                .putRequestParam("pageSize", String.valueOf(pageSize));
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ClassGloryRank>() {
        }.getType());
    }

    @Override
    public GradeGloryRank queryGradeGloryRank(String classId, String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GRADE_GLORY_RANK);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("classId", classId)
                .putRestfulParam("studentId", studentId)
                .putRequestParam("pageNum", String.valueOf(pageNum))
                .putRequestParam("pageSize", String.valueOf(pageSize));
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<GradeGloryRank>() {
        }.getType());
    }

    @Override
    public KnowledgeMasterDetail queryKnowDiagnose(String studentId, String examId, String lastExamId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_USER_KNOWDIAGNOSE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRestfulParam("examId", examId)
                .putRequestParam("lastExamId", lastExamId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<KnowledgeMasterDetail>() {
        }.getType());
    }

    @Override
    public ErrorReviseStatus queryErrorReviseStatus(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.DAY_ERROR_REVISE_STATUS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ErrorReviseStatus>() {
        }.getType());
    }

    @Override
    public RecentWorkStatus queryRecentWorkStatus(String classId, String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.RECENT_WORK_STATUS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRequestParam("classId", classId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<RecentWorkStatus>() {
        }.getType());
    }

    @Override
    public WeekErrorStatus queryWeekErrorStatus(String studentId, long createTime) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.RECENT_WEEK_ERROR_STATUS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        if( createTime!=0 ) request.putRequestParam("createTime",String.valueOf(createTime));
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<WeekErrorStatus>() {
        }.getType());
    }

    @Override
    public CityRankResult queryCityGloryRank( String studentId, String classId ) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_KNOW_RANK_CITY);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("classId", classId)
                .putRestfulParam("studentId", studentId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<CityRankResult>() {
        }.getType());
    }
    @Override
    public FirstWorkStatusBean queryFirstWorkStatus( String studentId, String classId ) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_APP_FIRST_WORKSTATUS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("classId", classId)
                .putRestfulParam("studentId", studentId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<FirstWorkStatusBean>() {
        }.getType());
    }

}
