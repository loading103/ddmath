package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.TodayStudyAbility;
import com.tsinghuabigdata.edu.ddmath.bean.TotalStudyAbility;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.backstage.bean.FirstPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeRecordList;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.MyScoreBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductList;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreRecordResult;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.UserScoreBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.UserCenterService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 */

public class UserCenterServiceImpl extends BaseService implements UserCenterService {

    @Override
    public List<RewardBean> queryRewardBean() throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.REWARD_STUDY_BEAN);
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<List<RewardBean>>(){}.getType());
    }

    @Override
    public StudyBean queryMyStudyBean(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.MY_STUDY_BEAN);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<StudyBean>(){}.getType());
    }

    @Override
    public TotalStudyAbility queryMyStudyAbility(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.MY_STUDY_ABILITY);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<TotalStudyAbility>() {
        }.getType());
    }

    @Override
    public TodayStudyAbility queryMyTodayStudyAbility(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.MY_TODAY_STUDY_ABILITY);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<TodayStudyAbility>() {
        }.getType());
    }


    @Override
    public String submitRedeemCode(HashMap<String, String> map) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.SUBMIT_REDEEM_CODE);
        HttpRequest request = AppRequestUtils.post(url);
        if (map != null) {
            Iterator<String> it = map.keySet().iterator();
            JSONObject jsonObject = new JSONObject();
            while (it.hasNext()) {
                String key = it.next();
                String value = map.get(key);
                jsonObject.put(key, value);
            }
            request.setJsonStringParams(jsonObject.toString());
        }
        return request.requestJson().getBody();
    }


    @Override
    public String addAdvice(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.ADD_ADVICE);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            JSONObject jsonObject = new JSONObject();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                jsonObject.put(key, value);
            }
            request.setJsonStringParams(jsonObject.toString());
        }
        return request.requestJson().getBody();

        /*JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("classId", classId);
            jsonObject.put("questionCount", questionCount);
            jsonObject.put("files", jsonArray);
        } catch (Exception e) {
            AppLog.i( "", e );
        }
        request.setJsonStringParams(jsonObject.toString());*/
    }

    @Override
    public FirstPrivilegeBean queryUserFirstPrivilege(String accountId ) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.USER_FIRST_PRIVILEGE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("accountId", accountId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<FirstPrivilegeBean>() {
        }.getType());
    }

    @Override
    public UserScoreBean queryUserScore(String studentId) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.USER_SCORE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<UserScoreBean>() {
        }.getType());
    }

    @Override
    public ScoreProductList queryProductList(String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.SCORE_PRODUCT_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRequestParam("pageNum",String.valueOf(pageNum))
                .putRequestParam("pageSize",String.valueOf(pageSize));
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ScoreProductList>() {
        }.getType());
    }

    @Override
    public ExchangeRecordList queryExchangeRecordList(String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.EXCHANGE_RECORD_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRequestParam("pageNum",String.valueOf(pageNum))
                .putRequestParam("pageSize",String.valueOf(pageSize));
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ExchangeRecordList>() {
        }.getType());
    }

    @Override
    public ScoreProductBean queryScoreProductDetail(String studentId, String productId) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.EXCHANGE_PRODUCT_DETAIL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRestfulParam("productId",productId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ScoreProductBean>() {
        }.getType());
    }

    @Override
    public ExchangeProductBean execExchangeProductAction(String studentId, String productId) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.EXEC_EXCHANGE_PRODUCT);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRestfulParam("studentId", studentId)
                .putRestfulParam("productId",productId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ExchangeProductBean>() {
        }.getType());
    }

    @Override
    public int useHeaderPendant(String studentId, String recordId) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.POST_USE_HEADER_PENDANT);
        HttpRequest request = AppRequestUtils.post(url);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("studentId", studentId);
        jsonObject.put("recordId",recordId);
        request.setJsonStringParams( jsonObject.toString() );
        String res = request.requestJson().getDataBody();
        if( !TextUtils.isEmpty(res) && res.startsWith("{") && res.endsWith("}") && res.contains("status")){
            com.alibaba.fastjson.JSONObject json = JSON.parseObject(res);
            return json.getIntValue("status");
        }
        return 0;
    }

    @Override
    public ArrayList<MyScoreBean> getCommandScoreList(String studentId) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.GET_COMMAND_SROCE_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ArrayList<MyScoreBean>>() {
        }.getType());
    }

    @Override
    public ScoreRecordResult getScoreRecordList(String studentId, int pageNum, int pageSize, long starttime, long endtime ) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.GET_SROCE_RECORD_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("studentId", studentId)
                .putRequestParam("pageNum",String.valueOf(pageNum))
                .putRequestParam("pageSize",String.valueOf(pageSize))
                .putRequestParam("startTime", String.valueOf(starttime))
                .putRequestParam("endTime", String.valueOf(endtime));
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ScoreRecordResult>() {
        }.getType());
    }

    @Override
    public void addUserScore(String studentId, String eventId, String contentId ) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.POST_ADD_USERSCORE);
        HttpRequest request = AppRequestUtils.post(url);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("studentId",studentId);
        jsonObject.put("eventId",eventId);
        if( !TextUtils.isEmpty(contentId) ) jsonObject.put("contentId", contentId);
        jsonObject.put("clientType", "2");
        request.setJsonStringParams(jsonObject.toString());
        request.requestJson().getDataBody();
    }


}
