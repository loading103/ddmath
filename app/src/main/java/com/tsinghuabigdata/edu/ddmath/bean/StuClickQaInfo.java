package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by 28205 on 2016/11/21.
 */
public class StuClickQaInfo implements Serializable{
    private static final long serialVersionUID = -7197919507695757874L;

    /**
     * data
     todayCount	今日提问次数
     allCount	总提问次数
     rankByClass	班级排名
     rankByAll	准星排名
     */
    private int todayCount;
    private int allCount;
    private int rankByClass;
    private int rankByAll;

    public int getTodayCount() {
        return todayCount;
    }

    public void setTodayCount(int todayCount) {
        this.todayCount = todayCount;
    }

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getRankByClass() {
        return rankByClass;
    }

    public void setRankByClass(int rankByClass) {
        this.rankByClass = rankByClass;
    }

    public int getRankByAll() {
        return rankByAll;
    }

    public void setRankByAll(int rankByAll) {
        this.rankByAll = rankByAll;
    }
}
