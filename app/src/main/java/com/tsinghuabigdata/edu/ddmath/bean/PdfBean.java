package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

public class PdfBean implements Serializable {
    private String pdfUrl;
    private long createTime;
    private String title;

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
