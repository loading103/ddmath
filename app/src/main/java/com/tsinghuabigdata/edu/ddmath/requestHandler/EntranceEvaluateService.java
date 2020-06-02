package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EnterBean;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.EntranceDetail;
import com.tsinghuabigdata.edu.ddmath.module.entrance.bean.KnowledgeRecordBean;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 入学评测
 */
public interface EntranceEvaluateService {

    /**
     * 知识分析查询
     *
     * @param studentId   学生分析
     * @return ret        查询结果
     * @throws HttpRequestException
     * @throws JSONException
     */
    KnowledgeRecordBean queryKnowledgeEvaluate(String studentId) throws HttpRequestException, UnsupportedEncodingException, JSONException;

    /**
     * 知识分析保存
     *
     * @param studentId   学生分析
     * @param images   图片数组
     * @return 记录ID
     * @throws HttpRequestException
     * @throws JSONException
     */
    String saveKnowledgeEvaluate(String studentId, String classId, ArrayList<String> images ) throws HttpRequestException, UnsupportedEncodingException, JSONException;


    /**
     * 查询入学评测详情
     */
    EntranceDetail queryEnterDetail(String studentId) throws HttpRequestException, JSONException;


    /**
     * 学评测申请领取报告
     */
    EnterBean applyForReport(String studentId) throws HttpRequestException, JSONException;

}
