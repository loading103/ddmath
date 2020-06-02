package com.tsinghuabigdata.edu.ddmath.bean;

import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.QuestionVo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/9.
 */

public class StudentQuestionVo implements Serializable {

    private List<QuestionVo> items;
    private int              pageNum;
    private int              pageSize;
    private String           serverFileAddress;
    private long             systemTime;
    private int              totalCount;
    private int              totalPage;

    //private boolean allowAnswer;        //是否允许参考参考答案

    public List<QuestionVo> getItems() {
        return items;
    }

    public void setItems(List<QuestionVo> items) {
        this.items = items;
    }

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

    public String getServerFileAddress() {
        return serverFileAddress;
    }

    public void setServerFileAddress(String serverFileAddress) {
        this.serverFileAddress = serverFileAddress;
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

//    public boolean isAllowAnswer() {
//        return allowAnswer;
//    }
//
//    public void setAllowAnswer(boolean allowAnswer) {
//        this.allowAnswer = allowAnswer;
//    }
}
