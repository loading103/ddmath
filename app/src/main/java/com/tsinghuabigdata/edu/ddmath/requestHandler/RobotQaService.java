package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.ClickQaRankInClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ClickQaRankInZXInfo;
import com.tsinghuabigdata.edu.ddmath.bean.OtherStuAns;
import com.tsinghuabigdata.edu.ddmath.bean.QAAnsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ResultInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StuClickQaInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StuQaClickHistory;
import com.tsinghuabigdata.edu.ddmath.bean.Topic;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.AnswerPermission;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;

import org.json.JSONException;

import java.util.List;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2016/2/1.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.
 * @createTime: 2016/2/1 14:45
 */
public interface RobotQaService {

    /**
     * 获取做题前的答疑详情
     *
     * @GET
     * @Path("/robotHelpBefore/{studentId}/{questionId}")
     * @Consumes(MediaType.APPLICATION_JSON)
     * @Produces(MediaType.APPLICATION_JSON)
     */
    List<QAAnsInfo> getQAAnsDoBefore(String token, String studentId, String questionId) throws HttpRequestException, JSONException;

    /**
     * 查找相似题
     *
     * @param diff 难度系数，1 高 2 低
     * @GET
     * @Path("/alikeQuestion/{questionId}/{diff}")
     */
    List<Topic> getAlikeQuestion(String token, String questionId, String diff) throws HttpRequestException, JSONException;

    /**
     * 查找相似题(13.7开始使用)
     */
    List<QuestionInfo> getNewAlikeQuestion(String token, String questionId, String studentId) throws HttpRequestException, JSONException;

    /**
     * 获取错误原因
     *
     * @param token
     * @param sutdentId
     * @param examId
     * @param questionId
     * @throws HttpRequestException
     * @GET
     * @Path("/robotHelpAfter/{studentId}/{examId}/{questionId}")
     */
    List<QAAnsInfo> getErrorReasonDoAfter(String token, String sutdentId, String examId, String questionId) throws HttpRequestException, JSONException;

    /**
     * 获取学生答题次数
     *
     * @GET
     * @Path("/questionCountInfo/{studentId}/{questionId}")
     */
    int getQuestionDoTimes(String token, String sutdentId, String questionId) throws HttpRequestException, JSONException;

    /**
     * 获取总提问次数
     *
     * @GET
     * @Path("/questionHelpCount/{questionId}")
     */
    int getQuestionHelpCount(String token, String questionId) throws HttpRequestException, JSONException;

    /**
     * 机器人评价
     *
     * @param score
     * @param scoreType
     * @param questionId
     * @param studentId
     * @return success 为200 成功
     * @POST
     * @Path("/scoreRobot")
     */
    ResultInfo commentRobotService(String token, String score, String scoreType, String questionId, String studentId) throws HttpRequestException;

    /**
     * 点赞
     *
     * @param likeType   点赞类型：s相似题点赞，r认知点赞
     * @param questionId
     * @param studentId
     * @return success 为200 成功
     * @POST
     * @Path("/giveLike")
     */
    int giveLike(String token, String likeType, String questionId, String studentId) throws HttpRequestException, JSONException;

    /**
     * 提问机器人请求
     *
     * @param questionId
     * @param studentId
     * @return success 为200 成功
     * @POST
     * @Path("/ask")
     */
    void askRobot(String token, String questionId, String studentId, String classId) throws HttpRequestException;

    QuestionInfo getQuestionInfo(String questionId) throws HttpRequestException;

    AnswerPermission getAnswerPermission(String studentId, String classId) throws HttpRequestException;

    /**
     * 获取学生的微问访问次数
     *
     * @GET
     * @Path("/getClickRobotCount/{studentId}")
     */
    StuClickQaInfo getClickRobotCount(String token, String classId, String studentId) throws HttpRequestException;

    /**
     * 获取学生历史提问记录
     *
     * @param
     */
    StuQaClickHistory getClickRobotHistory(String token, String studentId) throws HttpRequestException;

    /**
     * 获取学生提问班级排行版
     *
     * @GET
     * @Path("/getClassRank/{classId}/{studentId}”)
     */
    ClickQaRankInClassInfo getClickQaClassRankInfo(String token, String classId, String studentId) throws HttpRequestException;

    /**
     * 获取学生提问准星排行版
     *
     * @GET
     * @Path("/getAllRank/{studentId}”)
     */
    ClickQaRankInZXInfo getClickQaZXRankInfo(String token, String studentId) throws HttpRequestException;

    /**
     * 获取同学的答案
     *
     * @GET
     * @Path("/getOtherStudentAnswers/{studentId}/{examId}/{questionId}”)
     */
    OtherStuAns getOtherStuAns(String token, String studentId, String examId, String questionId) throws HttpRequestException;

    /**
     * 对同学正确答案点赞
     *
     * @param studentId
     * @param questionId
     * @param likedStudentId
     * @param examId
     * @GET
     * @Path("/updateAnswersLike”)
     */
    void giveLikeToStuAns(String token, String studentId, String questionId, String likedStudentId, String examId) throws HttpRequestException;

}
