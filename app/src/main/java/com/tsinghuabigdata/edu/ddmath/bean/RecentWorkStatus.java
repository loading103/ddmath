package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/19.
 */
@Deprecated
public class RecentWorkStatus implements Serializable{

    public static final int NOT_SUBMIT = 0;
    public static final int CORRECTED = 5;  //统计完成

    public static final int TYPE_WORK = 0;      //作业诊断
    public static final int TYPE_ERROR = 2;     //错题收集


    public static final int NOT_READ = 0;
    public static final int HAS_READ = 1;
    private static final long serialVersionUID = -6095449027476186588L;

    private String examId;
    private String examName;
    private int exerStatus;
    private int readStatus;
    private int uploadType; //0：作业诊断，2：错题收集

    private boolean hasNormalHomework;      //历史上布置过诊断作业
    private boolean hasCollectionHomework;  //历史上布置过错题收集的作业


    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getExerStatus() {
        return exerStatus;
    }

    public void setExerStatus(int exerStatus) {
        this.exerStatus = exerStatus;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public int getUploadType() {
        return uploadType;
    }

    public boolean isHasNormalHomework() {
        return hasNormalHomework;
    }

    public boolean isHasCollectionHomework() {
        return hasCollectionHomework;
    }
}
