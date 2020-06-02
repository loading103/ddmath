package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 阅读日报告详情
 * Created by Administrator on 2018/7/18.
 */

public class ReadDayReportEvent {

    private String examId;

    public ReadDayReportEvent(String examId) {
        this.examId = examId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}
