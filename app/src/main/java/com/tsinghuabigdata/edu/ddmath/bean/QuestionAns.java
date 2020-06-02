package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by 28205 on 2016/11/21.
 */
public class QuestionAns implements Serializable{
    private static final long serialVersionUID = -2017156115677828117L;
    /**
     * ----studentId	被推荐学生Id
     * ----answerUrl	答案图片
     * ----stuName	学生姓名
     * ----schoolName	学校名称
     */
    private String studentId;
    private String answerUrl;
    private String stuName;
    private String schoolName;
    private boolean isHadGivelike;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getAnswerUrl() {
        return answerUrl;
    }

    public void setAnswerUrl(String answerUrl) {
        this.answerUrl = answerUrl;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public boolean isHadGivelike() {
        return isHadGivelike;
    }

    public void setHadGivelike(boolean hadGivelike) {
        isHadGivelike = hadGivelike;
    }
}
