package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;

/**
 * 家长端每日报告界面（作业详情界面）——实体类
 * Created by Administrator on 2018/6/28.
 */

public class DayReportBean implements Serializable {


    public static final int TOUPLOAD      = 1;  //待上传
    public static final int TOMAKE        = 2;  //待批阅
    public static final int MAKING        = 3;  //批阅中
    public static final int MADE          = 4;  //已批阅
    public static final int STAT_FINISHED = 5;  //统计完成
    public static final int PARENT_READ   = 6;  //家长已阅读

    public static final int EXAM   = 0;  //考试

    /**
     * code : 10000
     * data : {"className":"","reportName":"","schoolName":"","score":{"avgScore":0,"firstFlag":false,"increaseCorrect":0,"questionCount":0,"rank":0,"rightCount":0,"studentScore":0,"totalScore":0},"submit":{"endTime":0,"submitRank":0,"submitTime":0},"totalStudent":0,"updateTime":0}
     * exerStatus : 0
     * knowledge : {"knowledges":[{"correctRate":0,"knowledgePointName":""}],"totalKnowledgeCount":0}
     * type : 0
     */


    private long                updateTime;
    private int                 totalStudent;
    private String              reportName;
    private ScoreBean           score;
    private SubmitBean          submit;
    private String              schoolName;
    private String              className;
    private int                 exerStatus;
    private int                 type;
    private ParentKnowledgeBean knowledge;

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getTotalStudent() {
        return totalStudent;
    }

    public void setTotalStudent(int totalStudent) {
        this.totalStudent = totalStudent;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public ScoreBean getScore() {
        return score;
    }

    public void setScore(ScoreBean score) {
        this.score = score;
    }

    public SubmitBean getSubmit() {
        return submit;
    }

    public void setSubmit(SubmitBean submit) {
        this.submit = submit;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getExerStatus() {
        return exerStatus;
    }

    public void setExerStatus(int exerStatus) {
        this.exerStatus = exerStatus;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ParentKnowledgeBean getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(ParentKnowledgeBean knowledge) {
        this.knowledge = knowledge;
    }
}
