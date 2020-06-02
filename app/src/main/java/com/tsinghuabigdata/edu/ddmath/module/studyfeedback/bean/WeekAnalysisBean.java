package com.tsinghuabigdata.edu.ddmath.module.studyfeedback.bean;

import java.io.Serializable;

/**
 * 周学习报告分析
 */

public class WeekAnalysisBean implements Serializable {

    private static final long serialVersionUID = 8014140537687876297L;
    private String reportId;
    private String studentId;
    private String title;
    private String pdfUrl;
    private long startTime;
    private long stopTime;
    private long createTime;

    public String getReportId() {
        return reportId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getTitle() {
        return title;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public long getCreateTime() {
        return createTime;
    }
}
