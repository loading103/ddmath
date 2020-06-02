package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.AbilityQueryBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.ErrorQuestionQueryBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.FocreTrainQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.StudyAbilityInfo;

import org.json.JSONException;

/**
 * Created by Administrator on 2017/9/5.
 */

public interface StudyCheatService {

//    /**
//     * 查询是否有新学习任务
//     *
//     * @param token
//     * @param studentId
//     * @return
//     */
//    LearnTask queryHasLearnTask(String token, String studentId,String time) throws HttpRequestException;

    /**
     * 查询学力个人信息
     *
     * @param token
     * @param studentId
     * @return
     */
    StudyAbilityInfo queryStudyAbility(String token, String studentId) throws HttpRequestException;

    /**
     * 强化训练 得到题目详情
     * @param studentId     学生ID
     * @param accessToken   tokenid
     * @param count          完成次数 点击再来一题做题则+1，其他为0
     * @return 记录ID
     */
    FocreTrainQuestionInfo getForceTrainQuestion(String studentId, String accessToken, int count )  throws HttpRequestException, JSONException;

    /**
     * 强化训练 提交答案
     * @param accessToken  tokenid
     * @param studentId
     * @param questionId  参数
     * @param count     完成题数
     * @param answer    答案
     * @param time    答题时间，单位秒
     * @return 记录ID
     */
    AbilityQueryBean submitForceTrainAnswer(String accessToken, String studentId, String questionId, int count, String answer, long time, String requestId )  throws HttpRequestException, JSONException;

    /**
     * 错题回顾 查询详情
     * @param studentId     学生ID
     * @param accessToken   tokenid
     * @return 记录ID
     */
    ErrorQuestionQueryBean getErrorReivewQuestion(String accessToken, String studentId, String knId )  throws HttpRequestException, JSONException;

    /**
     * 错题回顾 提交答案
     * @param accessToken  tokenid
     * @param studentId
     * @param questionId  参数
     * @param wrongReason    错误原因
     * @param time    答题时间，单位秒
     * @param requestId 请求id
     * @param degree  掌握程度
     * @return 记录ID
     */
    AbilityQueryBean submitErrorReivewResult(String accessToken, String studentId, String questionId, String wrongReason, String requestId, int degree, long time )  throws HttpRequestException, JSONException;


}
