package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;

/**
 * 每日报告
 */

public class DayReportItemBean implements Serializable {

    private static final long serialVersionUID = -845474811042908366L;

    //套题类型 1: 每周，2：自定义,3:每月，4,考前
    public static final int PAPER_TYPE_WEEK     = 1;
    public static final int PAPER_TYPE_CUSTOM   = 2;
    public static final int PAPER_TYPE_MONTH    = 3;
    public static final int PAPER_TYPE_EXAM     = 4;

    private int    exerStatus;
    private int    rightCount;
    private long   createTime;
    private int    wrongCount;
    private int    totalScore;
    private String classId;
    private long   submitTime;
    private long   endTime;
    private int    type;                //考试与非考试的区别
    private int    studentScore;
    private int    questionCount;
    private String examId;
    private String examName;
    private int paperType;
    private int uploadType;
    private String sourceType;


    public int getExerStatus() {
        return exerStatus;
    }

    public void setExerStatus(int exerStatus) {
        this.exerStatus = exerStatus;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(int studentScore) {
        this.studentScore = studentScore;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getPaperType() {
        return paperType;
    }

    public int getUploadType() {
        return uploadType;
    }

    public String getSourceType() {
        return sourceType;
    }

}
