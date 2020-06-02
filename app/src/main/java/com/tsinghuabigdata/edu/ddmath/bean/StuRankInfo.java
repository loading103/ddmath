package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by 28205 on 2016/11/21.
 */
public class StuRankInfo implements Serializable {
    private static final long serialVersionUID = -2447323737864762693L;
    /**
     * rank	排名
     * headImg	头像
     * studentName	学生姓名
     * askCounts	提问次数
     * studentId	学生id
     * 准星排名属性
     * -----rank	排名
     * -----headImg	头像
     * -----studentName	学生姓名
     * -----askCounts	提问次数
     * -----studentId	学生id
     * -----schoolName	学校名
     */
    private String rank;
    private String headImg;
    private String studentName;
    private String askCounts;
    private String studentId;
    private String schoolName;
    private boolean isDots;   //是否是省略号节点

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getAskCounts() {
        return askCounts;
    }

    public void setAskCounts(String askCounts) {
        this.askCounts = askCounts;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public boolean isDots() {
        return isDots;
    }

    public void setDots(boolean dots) {
        isDots = dots;
    }
    public static StuRankInfo getDotsNode(){
        StuRankInfo stuRankInfo = new StuRankInfo();
        stuRankInfo.setDots(true);
        return stuRankInfo;
    }
}
