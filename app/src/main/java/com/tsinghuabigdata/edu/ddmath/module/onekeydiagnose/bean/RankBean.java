package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;

/**
 * 排行通用实体类
 * Created by Administrator on 2017/11/17.
 */

public class RankBean implements Serializable {

    private static final long serialVersionUID = -3022257503045024947L;
    /**
     * studentId : 31afdsafd
     * rank : 1
     * headimg : fdasfasdfsa
     * studentName : 朱林
     * glory : 0.5
     */

    private String studentId;
    private int    rank;
    private String headImage;
    private String imagePath;        //挂件文件
    private int vipLevel;                   //用户等级
    private String studentName;
    private String className;
    private float  glory;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getGlory() {
        return glory;
    }

    public void setGlory(float glory) {
        this.glory = glory;
    }

    public String getPendentImage() {
        return imagePath;
    }

    public int getVipLevel() {
        return vipLevel;
    }
}
