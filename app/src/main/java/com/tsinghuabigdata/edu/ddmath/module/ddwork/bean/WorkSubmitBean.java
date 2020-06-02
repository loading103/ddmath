package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * 作业提交返回
 */

public class WorkSubmitBean implements Serializable {
    private static final long serialVersionUID = 4444239748909040480L;

    private String recordId;        //记录ID
    private int rank;               //>0,班级排名 <=0,不显示排名
    private int value;              //学力值
    private boolean overdue;        //是否超期提交

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }
}
