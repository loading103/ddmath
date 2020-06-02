package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 28205 on 2016/11/21.
 */
public class ClickQaRankInClassInfo implements Serializable {
    private static final long serialVersionUID = -5306802279677714608L;
    /**
     * data
     * --allCount	总提问次数
     * --rankByClass	班级排名
     * --rankList	班级排名列表
     * -----rank	排名
     * -----headImg	头像
     studentName	学生姓名
     askCounts	提问次数
     studentId	学生id
     */
    private int allCount;
    private int rankByClass;
    private List<StuRankInfo> rankList;

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

    public List<StuRankInfo> getRankList() {
        return rankList;
    }

    public void setRankList(List<StuRankInfo> rankList) {
        this.rankList = rankList;
    }
}
