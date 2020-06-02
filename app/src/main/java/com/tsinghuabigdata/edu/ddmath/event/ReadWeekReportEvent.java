package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 阅读周报告详情
 * Created by Administrator on 2018/7/18.
 */

public class ReadWeekReportEvent {


    private String reportId;

    public ReadWeekReportEvent(String reportId) {
        this.reportId = reportId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

}
