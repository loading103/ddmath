package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 日日清任务 详情
 */

public class DayCleanDetailBean implements Serializable {

    private static final long serialVersionUID = -350168868964499150L;

    private int totalCount;      //题目总量
    private int totalPage;       //总页数
//    private int pageNum;       //当前页

    private ArrayList<QuestionVo> wrongQuestionVoList;     //题目列表


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

//    public int getPageNum() {
//        return pageNum;
//    }
//
//    public void setPageNum(int pageNum) {
//        this.pageNum = pageNum;
//    }

    public ArrayList<QuestionVo> getQuestions() {
        return wrongQuestionVoList;
    }

    public void setQuestions(ArrayList<QuestionVo> questions) {
        this.wrongQuestionVoList = questions;
    }
}
