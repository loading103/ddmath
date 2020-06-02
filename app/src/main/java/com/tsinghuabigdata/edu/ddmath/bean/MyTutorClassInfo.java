package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class MyTutorClassInfo implements Serializable {

    //    schoolId=SCH2EC067E94C6949239C56D6F575D56B05
    //    schoolName=黄线上测试辅导学校
    //    schoolType=training

    private static final long   serialVersionUID = 5830736026508924027L;
    /**
     * classId	String	班级ID
     * className	String	班级名称
     */
    public static final  String TYPE_JOINED      = "1";  //正式班
    public static final  String TYPE_UNJOIN      = "2";  //待入学


    private String            classId;
    private String            className;
    private String            classCode;
    private String            type;
    private String            website;
    private String            schoolId;
    private String            schoolName;
    private String            schoolType;
    private List<TeacherBean> teachers;

    private boolean allowAnswer;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolType() {
        return schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
    }

    public List<TeacherBean> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherBean> teachers) {
        this.teachers = teachers;
    }

    public boolean isAllowAnswer() {
        return allowAnswer;
    }

    public void setAllowAnswer(boolean allowAnswer) {
        this.allowAnswer = allowAnswer;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }
}
