package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.ClickQaRankInClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ClickQaRankInZXInfo;
import com.tsinghuabigdata.edu.ddmath.bean.OtherStuAns;
import com.tsinghuabigdata.edu.ddmath.bean.QAAnsInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ResultInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StuClickQaInfo;
import com.tsinghuabigdata.edu.ddmath.bean.StuQaClickHistory;
import com.tsinghuabigdata.edu.ddmath.bean.Topic;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequest;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.AnswerPermission;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;
import com.tsinghuabigdata.edu.ddmath.requestHandler.RobotQaService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2016/2/1.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.
 * @createTime: 2016/2/1 14:47
 */
public class RobotQaServiceImpl extends BaseService implements RobotQaService {

    protected String getUrl(String restful) {
        return AppRequestConst.QA_ADDRESS + restful;
    }

    protected String  getStudentUrl(String restful) {
        //        if( restful.contains("studying") ){
        //            return "http://192.168.30.24:8080" + restful + "?t=" + System.currentTimeMillis();
        //        }
        return AppRequestConst.RESTFUL_ADDRESS + restful + "?t=" + System.currentTimeMillis();
    }

    protected  String getDouDouUrl(String restful){
        return AppRequestConst.RESTFUL_ADDRESS + restful + "?t=" + System.currentTimeMillis();
    }

    @Override
    public List<QAAnsInfo> getQAAnsDoBefore(String token, String studentId, String questionId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QAANS_DO_BEFORE);
        String body = AppRequestUtils.get(url)
                .putRestfulParam("studentId", studentId)
                .putRestfulParam("questionId", questionId)
                .putHeader("access_token", token)
                .request().getBody();

        // 转换json为对象
        return new Gson().fromJson(body,
                new TypeToken<List<QAAnsInfo>>() {
                }.getType()
        );
    }

    @Override
    public List<Topic> getAlikeQuestion(String token, String questionId, String diff) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_ALIKE_QUESTION);
        String body = AppRequestUtils.get(url)
                .putRestfulParam("questionId", questionId)
                .putRestfulParam("diff", diff)
                .putHeader("access_token", token)
                .request().getBody();

        // 转换json为对象
        return new Gson().fromJson(body,
                new TypeToken<List<Topic>>() {
                }.getType()
        );
    }

    @Override
    public List<QuestionInfo> getNewAlikeQuestion(String token, String questionId, String studentId) throws HttpRequestException, JSONException {
        String url = getStudentUrl(AppRequestConst.GET_NEW_ALIKE_QUESTION);
        String body = AppRequestUtils.get(url)
                .putRequestParam("questionId", questionId)
                .putRequestParam("studentId", studentId)
                .putRequestParam("count", "10")
                .putHeader("access_token", token)
                .request().getBody();

        // 转换json为对象
        return new Gson().fromJson(body,
                new TypeToken<List<QuestionInfo>>() {
                }.getType()
        );
    }

    @Override
    public List<QAAnsInfo> getErrorReasonDoAfter(String token, String studentId, String examId, String questionId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QAANS_DO_AFTER);
        AppRequest appRequest = (AppRequest) AppRequestUtils.get(url)
                .putRestfulParam("studentId", studentId)
                .putRestfulParam("examId", examId)
                .putRestfulParam("questionId", questionId)
                .putHeader("access_token", token)
                .request();
        String body = appRequest.getFullBody();

        // 转换json为对象
//        return new Gson().fromJson(body,
//                new TypeToken<List<QAAnsInfo>>() {
//                }.getType()
//        );
        JSONObject json = new JSONObject(body);
        JSONArray jsonArray = json.getJSONArray("data");

        List<QAAnsInfo> qaAnsInfos = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json1 = jsonArray.getJSONObject(i);
            String type = json1.getString("type");
            if (!type.equals("robotReason")) {
                QAAnsInfo qaAnsInfo = new Gson().fromJson(json1.toString(), new TypeToken<QAAnsInfo>() {
                }.getType());
                qaAnsInfos.add(qaAnsInfo);
            } else {
                JSONArray content = json1.getJSONArray("content");
                String title = json1.getString("title");
                String type1 = json1.getString("type");
                String congContent = "";
                QAAnsInfo qaAnsInfo1 = new QAAnsInfo();
                for (int j = 0; j < content.length(); j++) {
                    JSONArray congition = content.getJSONArray(j);
                    congContent += congition + "####%%####";
                }
                qaAnsInfo1.setTitle(title);
                qaAnsInfo1.setType(type1);
                qaAnsInfo1.setContent(congContent);
                qaAnsInfos.add(qaAnsInfo1);
            }

        }
        return qaAnsInfos;
    }

    @Override
    public int getQuestionDoTimes(String token, String studentId, String questionId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUESTION_DO_TIMES);
        String body = AppRequestUtils.get(url)
                .putRestfulParam("studentId", studentId)
                .putRestfulParam("questionId", questionId)
                .putHeader("access_token", token)
                .request().getBody();

        // 转换json为对象
        return new JSONObject(body).getInt("count");
    }

    @Override
    public int getQuestionHelpCount(String token, String questionId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUESTION_HELP_COUNT);
        String body = AppRequestUtils.get(url)
                .putRestfulParam("questionId", questionId)
                .putHeader("access_token", token)
                .request().getBody();

        // 转换json为对象
        return new JSONObject(body).getJSONObject("message").getInt("questionHelpCount");
    }

    @Override
    public ResultInfo commentRobotService(String token, String score, String scoreType, String questionId, String studentId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.POST_COMMENT_ROBOT);
        HttpRequest request = AppRequestUtils.get(url);
        return request.putHeader("access_token", token).
                putRequestParam("score", score).
                putRequestParam("scoreType", scoreType).
                putRequestParam("questionId", questionId).
                putRequestParam("studentId", studentId).
                request().getResult(ResultInfo.class);
    }

    @Override
    public int giveLike(String token, String likeType, String questionId, String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_GIVE_LIKE);
        HttpRequest request = AppRequestUtils.get(url);
        String body = request.putHeader("access_token", token).
                putRequestParam("likeType", likeType).
                putRequestParam("questionId", questionId).
                putRequestParam("studentId", studentId).
                request().getBody();
        return new JSONObject(body).getInt("count");
    }

    @Override
    public void askRobot(String token, String questionId, String studentId, String classId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.POST_ASK_ROBOT);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", token).
                putRequestParam("questionId", questionId).
                putRequestParam("studentId", studentId).
                putRequestParam("classId", classId).
                request().getBody();
    }

    @Override
    public QuestionInfo getQuestionInfo(String questionId) throws HttpRequestException {
        String url = getDouDouUrl(AppRequestConst.GET_ROBOT_QUESTION_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        return request.putRestfulParam("questionId", questionId).
                request().getResult(QuestionInfo.class);
    }

    @Override
    public AnswerPermission getAnswerPermission(String studentId,String classId) throws HttpRequestException {
        String url = getDouDouUrl(AppRequestConst.GET_ANSWER_PERMISSION);
        HttpRequest request = AppRequestUtils.get(url);
        return request.putRequestParam("classId", classId).
                putRestfulParam("studentId", studentId).
                request().getResult(AnswerPermission.class);
    }

    @Override
    public StuClickQaInfo getClickRobotCount(String token, String classId, String studentId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.GET_CLICK_ROBOT_INFO);
        HttpRequest request = AppRequestUtils.get(url);
        String body = request.putHeader("access_token", token).
                putRestfulParam("classId", classId).
                putRestfulParam("studentId", studentId).
                request().getBody();
        StuClickQaInfo stuClickQaInfo = new Gson().fromJson(body, StuClickQaInfo.class);
        return stuClickQaInfo;
    }

    @Override
    public StuQaClickHistory getClickRobotHistory(String token, String studentId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.GET_STU_HISTORY_CLICK_QA);
        HttpRequest request = AppRequestUtils.get(url);
        return request.putHeader("access_token", token).
                putRestfulParam("studentId", studentId).
                request().getResult(StuQaClickHistory.class);
    }

    @Override
    public ClickQaRankInClassInfo getClickQaClassRankInfo(String token, String classId, String studentId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.GET_STU_CLICKQA_RANK_INCLASS);
        HttpRequest request = AppRequestUtils.get(url);
        return request.putHeader("access_token", token).
                putRestfulParam("classId", classId).
                putRestfulParam("studentId", studentId).
                request().getResult(ClickQaRankInClassInfo.class);
    }

    @Override
    public ClickQaRankInZXInfo getClickQaZXRankInfo(String token, String studentId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.GET_STU_CLICKQA_RANK_INZX);
        HttpRequest request = AppRequestUtils.get(url);
        return request.putHeader("access_token", token).
                putRestfulParam("studentId", studentId).
                request().getResult(ClickQaRankInZXInfo.class);
    }

    @Override
    public OtherStuAns getOtherStuAns(String token, String studentId, String examId, String questionId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.GET_OTHER_STU_ANS);
        HttpRequest request = AppRequestUtils.get(url);
        return request.putHeader("access_token", token).
                putRestfulParam("studentId", studentId).
                putRestfulParam("examId", examId).
                putRestfulParam("questionId", questionId).
                request().getResult(OtherStuAns.class);
    }

    @Override
    public void giveLikeToStuAns(String token, String studentId, String questionId, String likedStudentId, String examId) throws HttpRequestException {
        String url = getUrl(AppRequestConst.GET_GIVELIKE_TO_OTHER_STUANS);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", token).
                putRequestParam("studentId", studentId).
                putRequestParam("likeStudentId", likedStudentId).
                putRequestParam("examId", examId).
                putRequestParam("questionId", questionId).
                request().getBody();
    }
}
