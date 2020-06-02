package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 日日清任务
 */

public class DayCleanResult implements Serializable {

    private static final long serialVersionUID = 5881145621580945368L;

    private int reviseTotalCount;                      //日日清使用人次
    private ArrayList<DayCleanBean> wrongQuestionDCInfoList;   //日日清任务列表

    public int getTotalCount() {
        return reviseTotalCount;
    }

    public void setTotalCount(int totalCount) {
        this.reviseTotalCount = totalCount;
    }

    public ArrayList<DayCleanBean> getWrongQuestionDCInfoList() {
        return wrongQuestionDCInfoList;
    }

    public void setWrongQuestionDCInfoList(ArrayList<DayCleanBean> wrongQuestionDCInfo) {
        this.wrongQuestionDCInfoList = wrongQuestionDCInfo;
    }
}
