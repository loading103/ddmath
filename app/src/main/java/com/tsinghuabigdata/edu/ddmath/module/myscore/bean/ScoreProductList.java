package com.tsinghuabigdata.edu.ddmath.module.myscore.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 积分商品
 */

public class ScoreProductList implements Serializable {

    private static final long serialVersionUID = -5764306434099362381L;

    private int totalCount;       //总商品数量
    private int totalPage;      //总页数

    private ArrayList<ScoreProductBean> items;

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public ArrayList<ScoreProductBean> getProductList() {
        return items;
    }
}
