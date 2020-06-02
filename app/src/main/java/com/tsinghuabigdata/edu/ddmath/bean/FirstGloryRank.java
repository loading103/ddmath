package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * 荣耀值排行榜-首页
 * Created by Administrator on 2017/11/16.
 */

public class FirstGloryRank implements Serializable {


    /**
     * classId : 28EF112952A24CA98D68891D057B6563
     * classNumber : 0
     * classRank : 0
     * glory : 0
     * gradeNumber : 0
     * gradeRank : 0
     * schoolId : 69B5EAA7842147859C4A92F5DBFA0A07
     * serial : 2018
     * studentId : STU156AECBFEDD641239233FB3C475198DF
     */

    private String classId;
    private int    classNumber;
    private int    classRank;
    private float  glory;
    private int    gradeNumber;
    private int    gradeRank;
    private String schoolId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public int getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(int classNumber) {
        this.classNumber = classNumber;
    }

    public int getClassRank() {
        return classRank;
    }

    public void setClassRank(int classRank) {
        this.classRank = classRank;
    }

    public float getGlory() {
        return glory;
    }

    public void setGlory(float glory) {
        this.glory = glory;
    }

    public int getGradeNumber() {
        return gradeNumber;
    }

    public void setGradeNumber(int gradeNumber) {
        this.gradeNumber = gradeNumber;
    }

    public int getGradeRank() {
        return gradeRank;
    }

    public void setGradeRank(int gradeRank) {
        this.gradeRank = gradeRank;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
