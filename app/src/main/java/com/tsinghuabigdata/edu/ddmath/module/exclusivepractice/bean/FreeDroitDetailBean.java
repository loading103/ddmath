package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean;

import java.io.Serializable;

/**
 * 使用权发放详情
 */

public class FreeDroitDetailBean implements Serializable {
    private static final long serialVersionUID = 7384395601301265706L;

    private int subTimes;           // 	int 	次数
    private int grantType;          // 	int 	发放类型（1：系统发放2：用户自己兑换）
    private long startDate;         // 	long 	起始日期
    private long endDate;           // long 	截止日期

    public int getSubTimes() {
        return subTimes;
    }

    public void setSubTimes(int subTimes) {
        this.subTimes = subTimes;
    }

    public int getGrantType() {
        return grantType;
    }

    public void setGrantType(int grantType) {
        this.grantType = grantType;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
