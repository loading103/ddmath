package com.tsinghuabigdata.edu.ddmath.parent.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/7/2.
 */

public class DayReportListBean implements Serializable {

    private int                     pageNum;
    private int                     pageSize;
    private int                     totalCount;
    private int                     totalPage;
    private List<DayReportItemBean> items;

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

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<DayReportItemBean> getItems() {
        return items;
    }

    public void setItems(List<DayReportItemBean> items) {
        this.items = items;
    }
}
