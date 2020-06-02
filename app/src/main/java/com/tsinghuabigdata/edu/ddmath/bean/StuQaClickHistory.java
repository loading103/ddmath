package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 28205 on 2016/11/21.
 */
public class StuQaClickHistory implements Serializable {
    private static final long serialVersionUID = -3525688769496168342L;

    /**
     * allCount	总提问次数
     * lastMonthCount	上月总提问次数
     * stuName	学生姓名
     * lastMonthAsks	最近一个月的访问列表
     *  -  date    ： yyyy-MM-dd
     *  -  count
     */
    private int allCount;
    private int lastMonthCount;
    private String stuName;
    private List<ClickQaInfo> lastMonthAsks;

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getLastMonthCount() {
        return lastMonthCount;
    }

    public void setLastMonthCount(int lastMonthCount) {
        this.lastMonthCount = lastMonthCount;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public List<ClickQaInfo> getLastMonthAsks() {
        return lastMonthAsks;
    }

    public void setLastMonthAsks(List<ClickQaInfo> lastMonthAsks) {
        this.lastMonthAsks = lastMonthAsks;
    }
}
