package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/17.
 */

public class JoinedClassInfo implements Serializable {

    private static final long serialVersionUID = -8495910625104759886L;
    /**
     * classId	String	班级ID
     * className	String	班级名称
     * classNum	String	班级编号
     */

    private String classId;
    private String className;
    private String classNum;
    private String website;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassNum() {
        return classNum;
    }

    public void setClassNum(String classNum) {
        this.classNum = classNum;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
