package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;
import java.util.List;

/**
 *班级信息
 */

public class ClassBean implements Serializable {

    private static final long serialVersionUID = 8461652793487309513L;

    private String className;
    private String classId;

    private String schoolName;
    private String schoolId;

    private int studentNum;
    private String serial;          //入学年份

    private List<String> teaNameList;

    private int allowAddStudent;

    public int getAllowAddStudent() {
        return allowAddStudent;
    }

    public void setAllowAddStudent(int allowAddStudent) {
        this.allowAddStudent = allowAddStudent;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public List<String> getTeaNameList() {
        return teaNameList;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }

    public void setTeaNameList(List<String> teaNameList) {
        this.teaNameList = teaNameList;
    }

    public String getClassName() {
        return className;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getEnrollmentYear() {
        return serial;
    }

    public void setEnrollmentYear(String enrollmentYear) {
        this.serial = enrollmentYear;
    }
}
