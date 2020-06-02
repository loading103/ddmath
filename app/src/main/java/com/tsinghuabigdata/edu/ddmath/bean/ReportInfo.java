package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;


/**
 * Created by 28205 on 2017/6/13.
 */
public class ReportInfo implements Serializable {
    private static final long serialVersionUID = -4967914645857989447L;
    public static final  int  S_UNREAD         = 0;
    public static final  int  S_READ           = 1;

    private String reportId;

    private String reportName;

    private String remark;

    private Integer readStatus;

    private Long createTime;

    private String examId;

    private String classId;

    private String studentId;

    private String lastExamId;

    private String reportType;

    private float totalScore;
    private float studentScore;
    private int   wrongQuestionCount;
    private int   rightQuestionCount;

    private int paperType;      //1:周 2:自定义, 3:月， 0： 没有
    private String sourceType;

    //    data.items.totalScore	float	总分
    //    data.items.studentScore	float	得分
    //    data.items.wrongQuestionCount	int	错误题数
    //    data.items.rightQuestionCount	int	正确题数

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getLastExamId() {
        return lastExamId;
    }

    public void setLastExamId(String lastExamId) {
        this.lastExamId = lastExamId;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public float getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(float totalScore) {
        this.totalScore = totalScore;
    }

    public float getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(float studentScore) {
        this.studentScore = studentScore;
    }

    public int getWrongQuestionCount() {
        return wrongQuestionCount;
    }

    public void setWrongQuestionCount(int wrongQuestionCount) {
        this.wrongQuestionCount = wrongQuestionCount;
    }

    public int getRightQuestionCount() {
        return rightQuestionCount;
    }

    public void setRightQuestionCount(int rightQuestionCount) {
        this.rightQuestionCount = rightQuestionCount;
    }

    public boolean isUnread() {
        //0是未读 1是已读
        return readStatus == S_UNREAD;
    }

    public int getPaperType() {
        return paperType;
    }

    public String getSourceType(){
        return sourceType;
    }
}
