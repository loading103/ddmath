package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 题型
 */
public class QuestionType implements Serializable {

    private static final long serialVersionUID = -2421194177231322890L;

    int pagenum;                  // 第一次出现在第几页，也即在第几页显示
    int showindex;                //显示数字
    String qustionType;

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    int sortIndex;                //默认显示顺序，排序使用

    ArrayList<LocalQuestionInfo> questionList = new ArrayList<>();

    public int getPageNum() {
        return pagenum;
    }

    public void setPageNum(int pagenum) {
        this.pagenum = pagenum;
    }

    public int getShowIndex() {
        return showindex;
    }
    public void setShowIndex( int index ) {
        showindex = index;
    }

    public String getQustionType() {
        return qustionType;
    }

    public void setQustionType(String qustionType) {
        this.qustionType = qustionType;
    }

    public ArrayList<LocalQuestionInfo> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(ArrayList<LocalQuestionInfo> questionList) {
        this.questionList = questionList;
    }
}
