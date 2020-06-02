package com.tsinghuabigdata.edu.ddmath.parent.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousVideoBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportListBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportListBean;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.ParentReportService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.BaseService;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator on 2018/7/9.
 */

public class ParentReportImpl extends BaseService implements ParentReportService {

    @Override
    public DayReportListBean queryDayReport(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.DAY_REPORT_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if ("parentId".equals(key)) {
                    request.putRestfulParam(key, value);
                } else {
                    request.putRequestParam(key, value);
                }

            }
        }
        request.request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, DayReportListBean.class);
    }

    @Override
    public WeekReportListBean queryWeekReport(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.WEEK_REPORT_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if ("parentId".equals(key)) {
                    request.putRestfulParam(key, value);
                } else {
                    request.putRequestParam(key, value);
                }

            }
        }
        request.request();
        String res = request.getDataBody();
        return new Gson().fromJson(res, WeekReportListBean.class);
    }

    @Override
    public String upadatReadStatus(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.UPADAT_READ_STATUS);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putRestfulParam(key, value);
            }
        }
        request.request();
        return request.getDataBody();
    }
}
