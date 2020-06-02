package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.DoudouWork;
import com.tsinghuabigdata.edu.ddmath.bean.MyCourse;
import com.tsinghuabigdata.edu.ddmath.bean.PdfBean;
import com.tsinghuabigdata.edu.ddmath.bean.ScorePlanBean;
import com.tsinghuabigdata.edu.ddmath.bean.TaskBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekDataBean;
import com.tsinghuabigdata.edu.ddmath.bean.WeekDatailBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.SubmitReviseBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.WorkSubmitBean;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.bean.WorkBean;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ScorePlanService {
      /**
     * 获取周末列表
     */
    ArrayList<WeekBean>   queryWeekList(String studentId, String classId) throws HttpRequestException, JSONException;

    /**
     * 获取定制学首页内容
     */
    ScorePlanBean   queryScorePlan(String studentId) throws HttpRequestException, JSONException;

    /**
     * 获取错题订正和微课列表
     */
    ArrayList<WeekDatailBean>   queryCorretDataList(String studentId, String classId, long date) throws HttpRequestException, JSONException;

    /**
     * 确认加入
     */
    boolean queryConfirmJoinCustomPlan( String studentId) throws HttpRequestException, JSONException;
    /**
     * 获取周错题再练周变式训练
     */
    TaskBean   queryWeekTrainList(String studentId, String classId, long date) throws HttpRequestException, JSONException;
    /**
     * 获取自定义错题本变式训练错题再练任务列表
     */
    ArrayList<WeekDatailBean>   queryDefineDataList(String studentId, String classId, long date) throws HttpRequestException, JSONException;
    /**
     * 下载pdf
     */
    PdfBean queryOneKeyPdf(String studentId, long date) throws HttpRequestException, JSONException;


}
