package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * 小问结构
 */

public class SubCheckResult implements Serializable {
    private static final long serialVersionUID = 2821246725409875374L;

    private String subQuestionId;
    private String parentId;
    private int correctResult;      // 判卷对错
    private int index;              // 小问和显示的勾叉对应的索引(从0开始)

    public String getSubQuestionId() {
        return subQuestionId;
    }

    public void setSubQuestionId(String subQuestionId) {
        this.subQuestionId = subQuestionId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getCorrectResult() {
        return correctResult;
    }

    public void setCorrectResult(int correctResult) {
        this.correctResult = correctResult;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
