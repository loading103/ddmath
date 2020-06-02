package com.tsinghuabigdata.edu.ddmath.module.myscore.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 积分商品
 */

public class ExchangeRecordList implements Serializable {

    private static final long serialVersionUID = 4890193671730866850L;
    private int totalCount;     //总商品数量
    private int totalPage;      //总页数

    private ArrayList<ScoreProductBean> items;

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public ArrayList<ScoreProductBean> getItems() {
        return items;
    }
}
