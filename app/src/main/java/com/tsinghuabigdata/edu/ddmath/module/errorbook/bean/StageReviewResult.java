package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.FreeDroitDetailBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 错题本下载
 */

public class StageReviewResult implements Serializable {

    private static final long serialVersionUID = -2633197352312925002L;

    private int reviewTotalCount;                               //错题本下载 使用人次
    private int chargeDdAmt;                    //每道题单价
    //private int freeUseTimes;                   //免费使用次数
    private int totalCount;                     //总条数

    private ArrayList<FreeDroitDetailBean> freeList;   //使用权 list

    private ArrayList<StageReviewBean> periodReviewInfoList;    //任务列表

    public int getReviewTotalCount() {
        return reviewTotalCount;
    }

    public void setReviewTotalCount(int totalCount) {
        this.reviewTotalCount = totalCount;
    }

    public ArrayList<StageReviewBean> getPeriodReviewInfoList() {
        return periodReviewInfoList;
    }

    public void setPeriodReviewInfoList(ArrayList<StageReviewBean> periodReviewInfoList) {
        this.periodReviewInfoList = periodReviewInfoList;
    }

    public int getChargeDdAmt() {
        return chargeDdAmt;
    }

    public void setChargeDdAmt(int chargeDdAmt) {
        this.chargeDdAmt = chargeDdAmt;
    }

    public ArrayList<FreeDroitDetailBean> getDriotlist() {
        return freeList;
    }

    public void setDriotlist(ArrayList<FreeDroitDetailBean> driotlist) {
        this.freeList = driotlist;
    }

    public int getFreeUseTimes() {
        int count = 0;
        if( freeList!=null ){
            for(FreeDroitDetailBean bean : freeList){
                count += bean.getSubTimes();
            }
        }
        return count;
    }

//    public void setFreeUseTimes(int freeUseTimes) {
//        this.freeUseTimes = freeUseTimes;
//    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
