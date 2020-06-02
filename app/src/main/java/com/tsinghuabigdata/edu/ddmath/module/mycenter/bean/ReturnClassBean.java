package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/19.
 */

public class ReturnClassBean implements Serializable{

    private static final long serialVersionUID = 330801838989943303L;
    private String schoolId;

    private List<ClassBean> classVoList;

    public String getSchoolId() {
        return schoolId;
    }

    public List<ClassBean> getClassVoList() {
        return classVoList;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setClassVoList(List<ClassBean> classVoList) {
        this.classVoList = classVoList;
    }

}
