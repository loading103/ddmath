package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/23.
 */

public class DayClearBean implements Serializable{

    private static final long serialVersionUID = -2032336937353505148L;
    private long endTime;
    private long startTime;
    private Boolean hasReviseReport;

    public Boolean getHasReviseReport() {
        return hasReviseReport;
    }
    public void setHasReviseReport(Boolean hasReviseReport) {
        this.hasReviseReport = hasReviseReport;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
