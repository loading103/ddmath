package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.PracticeProductBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 变式训练返回结构
 */

public class VarTrainResult implements Serializable {

    private static final long serialVersionUID = 6538544051162956162L;
    private int userCount;          //使用人次
    private int totalPage;          //总页数
    private int pageNum;            //当前页码
    private ArrayList<PracticeProductBean> products;   //商品列表

    public int getUserCount() {
        return userCount;
    }

    public ArrayList<PracticeProductBean> getProducts() {
        return products;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getPageNum() {
        return pageNum;
    }
}
