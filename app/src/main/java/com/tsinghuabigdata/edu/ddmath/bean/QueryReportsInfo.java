package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 28205 on 2017/6/13.
 */
public class QueryReportsInfo implements Serializable {
    private static final long serialVersionUID = -2283376294474779601L;
    /**
     * items : []
     * pageNum : 3
     * pageSize : 15
     * reportType : 3,4
     * serverFileAddress : null
     * systemTime : 1506500894157
     * totalCount : 25
     * totalPage : 2
     */
    private int              pageNum;
    private int              pageSize;
    private String           reportType;
    private long             systemTime;
    private int              totalCount;
    private int              totalPage;
    private List<ReportInfo> items;

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

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
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

    public List<ReportInfo> getItems() {
        return items;
    }

    public void setItems(List<ReportInfo> items) {
        this.items = items;
    }
}
