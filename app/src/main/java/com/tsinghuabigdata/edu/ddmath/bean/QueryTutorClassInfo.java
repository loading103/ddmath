package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class QueryTutorClassInfo implements Serializable {
    private static final long serialVersionUID = -9144008583711497270L;
    /**
     * classId	String	班级ID
     * className	String	班级名称
     */

    private String studentId;
    private String studentName;
    private List<MyTutorClassInfo> classInfos;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<MyTutorClassInfo> getClassInfos() {
        return classInfos;
    }

    public void setClassInfos(List<MyTutorClassInfo> classInfos) {
        this.classInfos = classInfos;
    }
}
