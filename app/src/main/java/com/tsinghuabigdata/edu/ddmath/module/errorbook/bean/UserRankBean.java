package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/18.
 */

public class UserRankBean implements Serializable{

    private static final long serialVersionUID = -780086264151572940L;

    private String studentName;         //学生名
    private String studentId;       	 //学生id
    private String headImage;           //学生头像
    private double score;                //掌握度

    private transient boolean me;     //是否是自己本人

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getHeadImage() {
        if( headImage == null )
            return "";
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }
}
