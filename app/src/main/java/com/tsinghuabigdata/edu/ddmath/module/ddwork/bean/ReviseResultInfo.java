package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * 订正结果结构
 */
public class ReviseResultInfo implements Serializable {

    //错因分析 用户选择的错误原因
    private String errorAnalysis;

    //订正批阅结果 答案批改标示区域信息 [{“x”:0.322323, “y”:0.2222222, “width”:1.0, “height”:0.1412}]
    private String answerArea;

    //订正图片
    private String answerUrl;

    //订正对错 订正答案批阅结果是否正确
    private boolean correct;

    //订正纠错状态 1：正常, 2：已申请纠错, 3：老师已经处理
    private int correctionStatus;

    private String correctTime;        //批阅时间

    private String reviseId;            //订正ID

    private String reviseLocalpath;     //本地图片地址，订正后 显示使用

    public String getErrorAnalysis() {
        return errorAnalysis;
    }

    public void setErrorAnalysis(String errorAnalysis) {
        this.errorAnalysis = errorAnalysis;
    }

    public String getAnswerArea() {
        return answerArea;
    }

    public void setAnswerArea(String answerArea) {
        this.answerArea = answerArea;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getCorrectionStatus() {
        return correctionStatus;
    }

    public void setCorrectionStatus(int correctionStatus) {
        this.correctionStatus = correctionStatus;
    }

    public String getCorrectTime() {
        return correctTime;
    }

    public void setCorrectTime(String correctTime) {
        this.correctTime = correctTime;
    }

    public String getReviseId() {
        return reviseId;
    }

    public void setReviseId(String reviseId) {
        this.reviseId = reviseId;
    }

    public String getReviseLocalpath() {
        return reviseLocalpath;
    }

    public void setReviseLocalpath(String reviseLocalpath) {
        this.reviseLocalpath = reviseLocalpath;
    }
}
