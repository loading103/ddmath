package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 免费使用权
 */

public class FreeDroitBean implements Serializable {
    private static final long serialVersionUID = -8020424871608372976L;

    private int totalTimes;         // 	int 	总次数
    private ArrayList<FreeDroitDetailBean> details; // 	次数详情

    public int getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(int totalTimes) {
        this.totalTimes = totalTimes;
    }

    public ArrayList<FreeDroitDetailBean> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<FreeDroitDetailBean> details) {
        this.details = details;
    }
}
