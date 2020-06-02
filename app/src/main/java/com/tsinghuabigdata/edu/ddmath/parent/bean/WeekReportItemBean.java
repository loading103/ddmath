package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/3.
 */

public class WeekReportItemBean implements Serializable {


    public static final int UNREAD = 0;  //未阅读
    public static final int READED = 1;  //已阅读

    /**
     * title :
     * checkStatus : 0
     * totalCount : 0
     * unRevisedCount : 0
     * totalAccuracy :
     * reportId :
     */

    private String title;
    private int    checkStatus;
    private int    totalCount;
    private int    unRevisedCount;
    private float totalAccuracy;
    private String reportId;
    private long createTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUnRevisedCount() {
        return unRevisedCount;
    }

    public void setUnRevisedCount(int unRevisedCount) {
        this.unRevisedCount = unRevisedCount;
    }

    public float getTotalAccuracy() {
        return totalAccuracy;
    }

    public void setTotalAccuracy(float totalAccuracy) {
        this.totalAccuracy = totalAccuracy;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
