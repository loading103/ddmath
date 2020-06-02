package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/18.
 */

public class SchoolBean implements Serializable{

    private static final long serialVersionUID = 3132456190482038360L;
    private String schoolId;

    private String schoolName;

    private int learnPeriod;     // 1小学 2初中

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }


    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getLearnPeriod() {
        return learnPeriod;
    }

    public void setLearnPeriod(int learnPeriod) {
        this.learnPeriod = learnPeriod;
    }
}
