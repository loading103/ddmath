package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * 我的今日学力
 * Created by Administrator on 2017/9/11.
 */

public class TodayStudyAbility implements Serializable {

    /**
     * todayStudyAbilityValue : 123123123
     * totalStudyAbilityValue : 12154545
     */

    private int todayStudyAbilityValue;
    private int totalStudyAbilityValue;

    public int getTodayStudyAbilityValue() {
        return todayStudyAbilityValue;
    }

    public void setTodayStudyAbilityValue(int todayStudyAbilityValue) {
        this.todayStudyAbilityValue = todayStudyAbilityValue;
    }

    public int getTotalStudyAbilityValue() {
        return totalStudyAbilityValue;
    }

    public void setTotalStudyAbilityValue(int totalStudyAbilityValue) {
        this.totalStudyAbilityValue = totalStudyAbilityValue;
    }

}
