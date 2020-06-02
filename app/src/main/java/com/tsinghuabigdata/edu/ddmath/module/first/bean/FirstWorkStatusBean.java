package com.tsinghuabigdata.edu.ddmath.module.first.bean;

import java.io.Serializable;

/**
 * 学生app首页作业状态查询
 */

public class FirstWorkStatusBean implements Serializable {
    private static final long serialVersionUID = 2028396187791708769L;

    public static final int WORK_UNSUBMIT = 0;
    public static final int WORK_SUBMITED = 1;
    public static final int WORK_CORRECTED = 4;
    public static final int WORK_STATED = 5;


    private String examId;
    private String examName;
    private int exerStatus;                     //作业状态，0：未提交，1：已提交，4:已批阅，5:已统计可看报告
    private int readStatus;                     //0：未读，1：已读
    private boolean hasHomeworkLast3Days;      //最近三天有老师布置的作业或者自己上传的作业

    public String getExamId() {
        return examId;
    }

    public String getExamName() {
        return examName;
    }

    public int getExerStatus() {
        return exerStatus;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public boolean isHasHomeworkLast3Days() {
        return hasHomeworkLast3Days;
    }
}
