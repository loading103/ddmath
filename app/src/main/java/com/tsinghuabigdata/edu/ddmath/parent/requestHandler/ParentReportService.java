package com.tsinghuabigdata.edu.ddmath.parent.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportListBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportListBean;

import org.json.JSONException;

import java.util.HashMap;

/**
 * 家长报告模块服务
 * Created by Administrator on 2018/7/9.
 */

public interface ParentReportService {

    /**
     * 家长端每日报告列表
     */
    DayReportListBean queryDayReport(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 家长端每日报告列表
     */
    WeekReportListBean queryWeekReport(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 家长阅读报告更新报告状态
     */
    String upadatReadStatus(HashMap<String, String> params) throws HttpRequestException, JSONException;

}
