package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowlegeDiagnoseBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.QuestionCountBean;

import org.json.JSONException;

import java.util.HashMap;

/**
 * 知识诊断服务
 * Created by Administrator on 2018/8/13.
 */

public interface KnowledgeDiagnoseService {

    /**
     * 知识诊断
     */
    KnowlegeDiagnoseBean getKnowlegeDiagnose(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 创建提升练习返回题目数
     */
    QuestionCountBean getQuestionCount(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 创建提升练习-生成套题
     */
    String produceQuestionSet(HashMap<String, String> params) throws HttpRequestException, JSONException;

}
