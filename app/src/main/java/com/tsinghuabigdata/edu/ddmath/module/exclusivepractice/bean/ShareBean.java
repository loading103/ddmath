package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean;

import java.io.Serializable;

/**
 * 专属套题分享下载
 */

public class ShareBean implements Serializable{
    private static final long serialVersionUID = 640151655269180242L;

    private String path;        //PDF文件路径
    private String examId;      //布置后的ID
    private int buy;            //是否购买， 0没购买，1购买
    private int ddAmt;          //豆豆数量是否充足， 0不充足，1充足

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

    public int getBuy() {
        return buy;
    }

    public void setBuy(int buy) {
        this.buy = buy;
    }

    public int getDdAmt() {
        return ddAmt;
    }

    public void setDdAmt(int ddAmt) {
        this.ddAmt = ddAmt;
    }
}
