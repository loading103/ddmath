package com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 名师精讲视频体类
 * Created by Administrator on 2018/2/6.
 */

public class FamousVideoBean implements Serializable {

    private int                   pageNum;
    private int                   pageSize;
    private int                   price;
    private int                   totalCount;
    private List<SingleVideoBean> items;


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<SingleVideoBean> getItems() {
        return items;
    }

    public void setItems(List<SingleVideoBean> items) {
        this.items = items;
    }
}
