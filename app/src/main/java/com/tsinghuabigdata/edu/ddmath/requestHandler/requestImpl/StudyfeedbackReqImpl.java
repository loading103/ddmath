package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.QueryNewReportInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryReportsInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.DayClearBean;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.WeekAnalysisBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.StudyfeedbackReqService;

import org.json.JSONException;

import java.util.List;

/**
 * Created by 28205 on 2016/12/14.
 */
public class StudyfeedbackReqImpl extends BaseService implements StudyfeedbackReqService {

    @Override
    public Object queryReports(String studentId, String reportType, String pageNum, String pageSize) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUERY_REPORTS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRestfulParam("reportType", reportType)
                .putRequestParam("pageNum", pageNum)
                .putRequestParam("pageSize", pageSize)
                .request().getBody();
        return request.getResult(new TypeToken<QueryReportsInfo>() {
        });
    }

    @Override
    public void updateReportReadstatus(String accessToken, String reportId, String reportType) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_UPDATE_REPORT_READSTATUS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", accessToken)
                .putRestfulParam("reportId", reportId)
                .putRestfulParam("reportType", reportType)
                .request().getBody();
        return;
    }

    @Override
    public Object queryIshaveNewReport(String access_token, String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_ISHAVE_NEWREPORT);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", access_token)
                .putRestfulParam("studentId", studentId)
                .request().getBody();
        return request.getResult(new TypeToken<QueryNewReportInfo>() {
        });
    }

    @Override
    public QuestionInfo querySingleDetail(String studentId, String examId, String questionId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.SINGLE_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRequestParam("examId", examId)
                .putRequestParam("questionId", questionId)
                .request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, QuestionInfo.class);
    }

    @Override
    public DayClearBean queryReviseDetail(String studentId) throws HttpRequestException, JSONException {
        String url = getTeacherUrl(AppRequestConst.REVISE_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("studentId", studentId)
                .request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, DayClearBean.class);
    }

    @Override
    public List<WeekAnalysisBean> queryWeekAnalysisReports(String studentId) throws HttpRequestException, JSONException {
        String url = getTeacherUrl(AppRequestConst.GET_WEEKANALYSIS_REPORT);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .requestJson();
        //String res = request.getDataBody();
        return request.getResult(new TypeToken<List<WeekAnalysisBean>>() {
        });
    }

}
