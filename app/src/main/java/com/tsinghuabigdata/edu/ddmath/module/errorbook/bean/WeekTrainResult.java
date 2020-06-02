package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.FreeDroitDetailBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 错题周题练
 */

public class WeekTrainResult implements Serializable {

    private static final long serialVersionUID = 4479238014369196141L;

    private int totalCount;                     //周题练使用人次
    private int chargeDdAmt;                    //每道题单价
    //private int freeUseTimes;                   //免费使用次数

    private int totalRecordCount;               //总记录条数

    private ArrayList<FreeDroitDetailBean> freeList;   //使用权 list
    private ArrayList<WeekTrainBean> unit;      //任务列表

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ArrayList<WeekTrainBean> getUnit() {
        return unit;
    }

    public void setUnit(ArrayList<WeekTrainBean> unit) {
        this.unit = unit;
    }

    public int getChargeDdAmt() {
        return chargeDdAmt;
    }

    public void setChargeDdAmt(int chargeDdAmt) {
        this.chargeDdAmt = chargeDdAmt;
    }

    public int getFreeUseTimes() {
            int count = 0;
            if( freeList!=null ){
                for(FreeDroitDetailBean bean : freeList){
                    int time = bean.getSubTimes();
                    if( time == -1 ) return -1;
                    else if( time < -1 ) time = 0;
                    count += time;
                }
            }
        return count;
    }

    public ArrayList<FreeDroitDetailBean> getDriotlist() {
        return freeList;
    }

    public void setDriotlist(ArrayList<FreeDroitDetailBean> driotlist) {
        this.freeList = driotlist;
    }

    public int getTotalRecordCount() {
        return totalRecordCount;
    }

    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }
}
