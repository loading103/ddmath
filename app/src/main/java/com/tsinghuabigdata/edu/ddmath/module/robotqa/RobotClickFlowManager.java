package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.content.Context;


import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.BehaviorUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 28205 on 2016/11/24.
 */
public class RobotClickFlowManager {
    private static class SingletonHolder {
        private static final RobotClickFlowManager instance = new RobotClickFlowManager();
    }
    private RobotClickFlowManager(){
    }

    public static final RobotClickFlowManager getInstance(){
        return SingletonHolder.instance;
    }

    private JSONObject commonParas;
    /**
     * 构建json字串
     * extend格式内容
     * --examId
     * --questionId
     * --sutdentId
     * --hasDone    1表示已完成，0表示未完成
     * --isCorrect  boolean类型  YES表示做正确的题  NO表示做错的题
     * --瞄一眼答案同学答案点赞多一个参数 likedStudentId
     * --对相似题点赞的时候要添加一个参数likedQuestionId
     */
    public JSONObject getCommonJson(Context context, String studentId, String examId, String questionId,
                                    String hasDone, boolean isCorrect) {
        commonParas = new JSONObject();
        try {
            commonParas.put("deviceId", AppUtils.getDeviceId(context)).
                    put("studentId", studentId).
                    put("examId", examId).
                    put("questionId", questionId).
                    put("hasDone", hasDone).
                    put("isCorrect", isCorrect);
        } catch (JSONException e) {
            AppLog.i("",e);
        }
        return commonParas;
    }

    public JSONObject getCommonJson() {
        return commonParas;
    }

    //机器人功能点击事件
    public void robotClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.AR_CLICK, extendJson.toString());
    }

    //机器人功能评分事件
    public void robotStarAssess(String evaluateScore){
        JSONObject extendJson = getCommonJson();
        try {
            extendJson.put("evaluateScore", evaluateScore);
        } catch (Exception e) {
            AppLog.i("",e);
        }
        BehaviorUtil.updateEventLog(ClickFlowEvent.AR_ASSESS, extendJson.toString());
    }

    //查看小豆找到的原因点击事件
    public void robotFoundReasonClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.LRR_CLICK, extendJson.toString());
    }
    //查看小豆找到的原因点赞事件
    public void robotFoundReasonLikeClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.LRR_LIKE, extendJson.toString());
    }
    //查看相似题点击事件
    public void robotLookSimilarTopicClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.LSQ_CLICK, extendJson.toString());
    }

    //查看相似题点赞事件
    public void robotLookSimilarTopicLikeClick(String likedQuestionId){
        JSONObject extendJson = getCommonJson();
        try {
            extendJson.put("likedQuestionId", likedQuestionId);
        } catch (Exception e) {
            AppLog.i("",e);
        }
        BehaviorUtil.updateEventLog(ClickFlowEvent.LSQ_LIKE, extendJson.toString());
    }

    //瞄一眼同学的答案的点击事件
    public void robotLookOtherAnsClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.LCA_CLICK, extendJson.toString());
    }

    //瞄一眼同学的答案的点赞事件
    public void robotLookOtherAnsLikeClick(String likedStudentId){
        JSONObject extendJson = getCommonJson();
        try {
            extendJson.put("likedStudentId", likedStudentId);
        } catch (Exception e) {
            AppLog.i("",e);
        }
        BehaviorUtil.updateEventLog(ClickFlowEvent.LCA_LIKE, extendJson.toString());
    }

    //不理解题意点击事件
    public void robotDontUQClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.DNUQ_CLICK, extendJson.toString());
    }

    //知识点未掌握点击事件
    public void robotKnowledgeUnknowClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.NMK_CLICK, extendJson.toString());
    }

    //不会分析点击事件
    public void robotUnableAnalysisClick(){
        JSONObject extendJson = getCommonJson();
        BehaviorUtil.updateEventLog(ClickFlowEvent.NA_CLICK, extendJson.toString());
    }
}
