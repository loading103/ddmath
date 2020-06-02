package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/2.
 */

public class SubmitBean implements Serializable {

    /**
     * endTime : 0
     * submitRank : 0
     * submitTime : 0
     */

    private long endTime;
    private int submitRank;
    private long submitTime;


    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getSubmitRank() {
        return submitRank;
    }

    public void setSubmitRank(int submitRank) {
        this.submitRank = submitRank;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }
}
