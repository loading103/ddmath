package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean;

import java.io.Serializable;

/**
 * 专属套题分享下载
 */

public class PracticeShareBean implements Serializable{
    private static final long serialVersionUID = 640151655269180242L;

    private String path;        //PDF文件路径
    private String examId;      //布置后的ID

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}
