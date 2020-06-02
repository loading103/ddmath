package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 28205 on 2016/11/21.
 */
public class ClickQaRankInZXInfo implements Serializable {
    private static final long serialVersionUID = -5306802279677714608L;
    /**
     * data
     * --allCount	总提问次数
     * --rankByAll	准星排名
     * --rankList	准星排名列表
     * -----rank	排名
     * -----headImg	头像
     * -----studentName	学生姓名
     * -----askCounts	提问次数
     * -----studentId	学生id
     * -----schoolName	学校名
     * --otherList	在前十排名中，则为null；否则，查询学生排名的前2后1的列表
     */

    private int allCount;
    private int rankByAll;
    private List<StuRankInfo> rankList;
    private List<StuRankInfo> otherList;

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getRankByAll() {
        return rankByAll;
    }

    public void setRankByAll(int rankByAll) {
        this.rankByAll = rankByAll;
    }

    public List<StuRankInfo> getRankList() {
        return rankList;
    }

    public void setRankList(List<StuRankInfo> rankList) {
        this.rankList = rankList;
    }

    public List<StuRankInfo> getOtherList() {
        return otherList;
    }

    public void setOtherList(List<StuRankInfo> otherList) {
        this.otherList = otherList;
    }
}
