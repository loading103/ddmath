package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.AbilityQueryBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.ErrorQuestionQueryBean;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.FocreTrainQuestionInfo;
import com.tsinghuabigdata.edu.ddmath.module.studycheat.bean.StudyAbilityInfo;
import com.tsinghuabigdata.edu.ddmath.requestHandler.StudyCheatService;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONException;

/**
 * 提分秘籍
 */

public class StudyCheatServiceImpl  extends BaseService implements StudyCheatService {

    @Override
    public StudyAbilityInfo queryStudyAbility(String token, String studentId) throws HttpRequestException {

        String url = getUrl(AppRequestConst.STUDY_ABILITY_URL);
        HttpRequest request = AppRequestUtils.get(url)
                .putRestfulParam("studentId", studentId)
                .putHeader("access_token", token)
                .request();
        String body = request.getBody();

//        String body = "{\"completeCount\":200,";        //今日已完成任务人数\n" +
//        body+= "\"surplusReview\":true,";               //今日是否有推荐题
//        body+= "\"surplusStength\":true,";               //今日是否有推荐题
//        body+= "\"remainedReviewChance\":3,";   //今日错题回顾剩余个数
//        body+= "\"remainedTrainChance\":0,";    //今日强化训练剩余个数
//        body+= "\"cheats\":[";
//        body+= "{\"score\":24,\"surplus\":1,\"time\":60,\"type\":12}";
//        body+= ",{\"score\":16,\"surplus\":3,\"time\":123,\"type\":11}";
//        body+= "]}";
        return new Gson().fromJson(body, StudyAbilityInfo.class);
    }

//    @Override
//    public LearnTask queryHasLearnTask(String token, String studentId, String time) throws HttpRequestException {
//        String url = getUrl(AppRequestConst.HAS_NEW_URL);
//        HttpRequest request = AppRequestUtils.get(url)
//                .putRequestParam("studentId", studentId)
//                .putRequestParam("time", time)
//                .putHeader("access_token", token)
//                .request();
//        String body = request.getBody();
//
//        LearnTask vo = new Gson().fromJson(body, LearnTask.class);
//
//        return vo;
//    }

    @Override
    public FocreTrainQuestionInfo getForceTrainQuestion(String studentId, String accessToken, int count )  throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.TRAIN_GET_QUESTION);
        AppLog.d("xxxbook getForceTrainQuestion url = " + url );
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", accessToken)
                .putRestfulParam("studentId",studentId)
                .putRestfulParam("continueNum", String.valueOf(count) );
        String res = request.requestJson().getBody();
        AppLog.d("xxxbook  getForceTrainQuestion res = " + res );
        if( TextUtils.isEmpty(res) || res.equals("null") ) return null;
        return new Gson().fromJson( res, new TypeToken<FocreTrainQuestionInfo>() {}.getType());
    }

    @Override
    public AbilityQueryBean submitForceTrainAnswer(String accessToken, String studentId, String questionId, int count, String answer, long time, String requestId )  throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.TRAIN_POST_COMMITANSWER);
        //String url = "http://172.16.1.158:8080" + AppConfig.TRAIN_POST_COMMITANSWER;
        AppLog.d("xxxbook submitForceTrainAnswer url = " + url );
        HttpRequest request = AppRequestUtils.post(url);
        request.putHeader("access_token", accessToken)
                .putJsonParam( "studentId", studentId )
                .putJsonParam( "questionId", questionId )
                .putJsonParam( "count", String.valueOf(count) )
                .putJsonParam( "answer", answer )
                .putJsonParam( "time", String.valueOf(time) )
                .putJsonParam( "requestId", requestId );
        String res = request.requestJson().getBody();
        AppLog.d("xxxbook  submitForceTrainAnswer res = " + res );
        return new Gson().fromJson( res, new TypeToken<AbilityQueryBean>() {}.getType());
    }

    @Override
    public ErrorQuestionQueryBean getErrorReivewQuestion(String accessToken, String studentId, String knId )  throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.ERRORQUESTION_GET_QUESTION);
        //String url = "http://172.16.1.158:8080" + AppConfig.ERRORQUESTION_GET_QUESTION;
        AppLog.d("xxxbook getErrorReivewQuestion url = " + url );
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", accessToken)
                .putRestfulParam("studentId",studentId);
        if( !TextUtils.isEmpty(knId) ){
            request.putRequestParam("knId", knId );
        }
        String res = request.request().getBody();
        AppLog.d("xxxbook  getErrorReivewQuestion res = " + res );
        if( TextUtils.isEmpty(res) || res.equals("null") ) return null;
        return new Gson().fromJson( res, new TypeToken<ErrorQuestionQueryBean>() {}.getType());
    }

    @Override
    public AbilityQueryBean submitErrorReivewResult(String accessToken, String studentId, String questionId, String wrongReason, String requestId, int degree, long time )  throws HttpRequestException, JSONException{

        String url = getUrl(AppRequestConst.ERRORQUESTION_POST_RESULT);
        //String url = "http://172.16.1.158:8080" + AppConfig.ERRORQUESTION_POST_RESULT;
        AppLog.d("xxxbook  submitErrorReivewResult url = " + url );
        HttpRequest request = AppRequestUtils.post(url);
        request.putHeader("access_token", accessToken)
                .putJsonParam( "studentId", studentId )
                .putJsonParam( "questionId", questionId )
                .putJsonParam( "wrongReason", wrongReason )
                .putJsonParam( "requestId", requestId )
                .putJsonParam( "time", String.valueOf(time) )
                .putJsonParam( "degree", String.valueOf(degree) );
//        if( !TextUtils.isEmpty(knId) ){
//            request.putJsonParam( "knId", knId );
//        }
        String res = request.requestJson().getBody();
        AppLog.d("xxxbook  submitErrorReivewResult res = " + res );
        return new Gson().fromJson( res, new TypeToken<AbilityQueryBean>() {}.getType());
    }
}
