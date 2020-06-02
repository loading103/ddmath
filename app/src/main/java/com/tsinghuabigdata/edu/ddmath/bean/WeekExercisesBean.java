package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/5.
 */

public class WeekExercisesBean implements Serializable {

    private static final long serialVersionUID = -4619295244492453116L;
    /**
     * recordId :
     * title :
     * suitNum : 0
     * downloadStatus : 0
     */

    private String recordId;
    private String title;
    private int    suitNum;
    private int    downloadStatus;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSuitNum() {
        return suitNum;
    }

    public void setSuitNum(int suitNum) {
        this.suitNum = suitNum;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

}
