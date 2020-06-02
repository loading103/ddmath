package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.DayClearBean;
import com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean.WeekAnalysisBean;

import org.json.JSONException;

import java.util.List;

/**
 * Created by 28205 on 2016/12/14.
 */
public interface StudyfeedbackReqService {
    /**
     * 报告列表查询
     *
     * @param
     * @param studentId
     * @param reportType
     * @return
     * @throws Exception
     */
    Object queryReports(String studentId, String reportType, String pageNum, String pageSize) throws HttpRequestException, JSONException;

    /**
     * 更新报告状态
     *
     * @param
     */
    void updateReportReadstatus(String accessToken, String reportId, String reportType) throws HttpRequestException, JSONException;

    /**
     * 是否有新报告
     */
    Object queryIshaveNewReport(String access_token, String studentId) throws HttpRequestException, JSONException;

    /**
     * 查询单题批阅详情
     *
     * @param
     */
    QuestionInfo querySingleDetail(String studentId, String examId, String questionId) throws HttpRequestException, JSONException;

    /**
     * 查询错题订正报告详情（老师端接口）
     *
     * @param
     */
    DayClearBean queryReviseDetail(String studentId) throws HttpRequestException, JSONException;

    /**
     * 查询周分析报告列表
     */
    List<WeekAnalysisBean> queryWeekAnalysisReports(String studentId ) throws HttpRequestException, JSONException;
}
