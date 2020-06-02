package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.ClassGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.ErrorReviseStatus;
import com.tsinghuabigdata.edu.ddmath.bean.FirstGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.GradeGloryRank;
import com.tsinghuabigdata.edu.ddmath.bean.RecentWorkStatus;
import com.tsinghuabigdata.edu.ddmath.bean.WeekErrorStatus;
import com.tsinghuabigdata.edu.ddmath.module.first.bean.FirstWorkStatusBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.CityRankResult;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgeMasterDetail;

import org.json.JSONException;

import java.util.List;

/**
 * 我的世界模块
 * Created by Administrator on 2017/11/16.
 */

public interface MyWorldService {

    /**
     * 荣耀值排行榜-首页
     */
    List<FirstGloryRank> queryFirstGloryRank(String classId, String studentId) throws HttpRequestException, JSONException;

    /**
     * 荣耀排行榜-班级排行榜-个人中心
     */
    ClassGloryRank queryClassGloryRank(String classId, String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException;

    /**
     * 荣耀排行榜-年级排名-个人中心
     */
    GradeGloryRank queryGradeGloryRank(String classId, String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException;

    /**
     * 查看用户的知识掌握情况
     */
    KnowledgeMasterDetail queryKnowDiagnose(String studentId,String examId, String lastExamId) throws HttpRequestException, JSONException;

    /**
     * 每日错题订正状态查询
     */
    ErrorReviseStatus queryErrorReviseStatus(String studentId) throws HttpRequestException, JSONException;

    /**
     * 查询每日最近一次布置作业的状态
     */
    RecentWorkStatus queryRecentWorkStatus(String classId, String studentId) throws HttpRequestException, JSONException;

    /**
     * 每日错题订正状态查询
     */
    WeekErrorStatus queryWeekErrorStatus(String studentId, long createTime) throws HttpRequestException, JSONException;


    /**
     * 荣耀值城市排行榜
     */
    CityRankResult queryCityGloryRank(String studentId, String classId ) throws HttpRequestException, JSONException;

//    /**
//     * 查询商品使用手册图片路径
//     */
//    List<ProductGuideBean> getProductGuide(String privilegeId) throws HttpRequestException, JSONException;

    /**
     * 学生app首页作业状态查询
     */
    FirstWorkStatusBean queryFirstWorkStatus(String studentId, String classId) throws HttpRequestException, JSONException;
}
