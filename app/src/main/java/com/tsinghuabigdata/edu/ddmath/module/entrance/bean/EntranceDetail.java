package com.tsinghuabigdata.edu.ddmath.module.entrance.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/6/12.
 */

public class EntranceDetail implements Serializable {

    /**
     * enterId : 123123123
     * recordId : 12154545
     * knowledge : false
     * quality : true
     * audit : 1
     * rejectReason : null
     * qualiyModify : false
     * knowledgeModify : false
     */

    private String  enterId;
    private String  reportId;
    private String  recordId;
    private boolean knowledge;
    private boolean quality;
    private int     audit = 1;
    private String  rejectReason;
    private boolean qualityModify;
    private boolean knowledgeModify;
    private boolean commentReport;

    public String getEnterId() {
        return enterId;
    }

    public void setEnterId(String enterId) {
        this.enterId = enterId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public boolean isKnowledge() {
        return knowledge;
    }

    public void setKnowledge(boolean knowledge) {
        this.knowledge = knowledge;
    }

    public boolean isQuality() {
        return quality;
    }

    public void setQuality(boolean quality) {
        this.quality = quality;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public boolean isQualityModify() {
        return qualityModify;
    }

    public void setQualityModify(boolean qualityModify) {
        this.qualityModify = qualityModify;
    }

    public boolean isKnowledgeModify() {
        return knowledgeModify;
    }

    public void setKnowledgeModify(boolean knowledgeModify) {
        this.knowledgeModify = knowledgeModify;
    }

    public boolean isCommentReport() {
        return commentReport;
    }

    public void setCommentReport(boolean commentReport) {
        this.commentReport = commentReport;
    }
}
