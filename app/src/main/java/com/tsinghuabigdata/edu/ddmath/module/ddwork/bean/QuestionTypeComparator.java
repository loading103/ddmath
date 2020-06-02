package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionType;

import java.util.Comparator;

/**
 * 题目排序
 */
public class QuestionTypeComparator implements Comparator<QuestionType> {
    @Override
    public int compare(QuestionType lhs, QuestionType rhs) {
        int value = 0;
        if( rhs.getShowIndex() < lhs.getShowIndex() ) value = 1;
        else if( rhs.getShowIndex() > lhs.getShowIndex() ) value = -1;
        return value;
    }
}
