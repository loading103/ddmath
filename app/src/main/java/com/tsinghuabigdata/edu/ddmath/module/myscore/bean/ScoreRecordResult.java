package com.tsinghuabigdata.edu.ddmath.module.myscore.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 积分记录
 */

public class ScoreRecordResult implements Serializable{

    private static final long serialVersionUID = -9092339185069624506L;

    private int totalCount;
    private int totalPage;
    private ArrayList<MyScoreBean> items;

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public ArrayList<MyScoreBean> getItems() {
        return items;
    }
}
