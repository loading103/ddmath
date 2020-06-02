package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * 我的学豆
 * Created by Administrator on 2017/12/20.
 */

public class StudyBean implements Serializable {

    private int rechargeDdAmt;
    private int returnDdAmt;
    private int rewardDdAmt;

    private int totalDdAmt;

    public int getRechargeDdAmt() {
        return rechargeDdAmt;
    }

    public int getReturnDdAmt() {
        return returnDdAmt;
    }

    public int getRewardDdAmt() {
        return rewardDdAmt;
    }

    public void setRechargeDdAmt(int rechargeDdAmt) {
        this.rechargeDdAmt = rechargeDdAmt;
    }

    public void setReturnDdAmt(int returnDdAmt) {
        this.returnDdAmt = returnDdAmt;
    }

    public void setRewardDdAmt(int rewardDdAmt) {
        this.rewardDdAmt = rewardDdAmt;
    }

    public int getTotalDdAmt() {
        return totalDdAmt;
    }
}
