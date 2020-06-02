package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.util.Comparator;

/**
 * 题目排序
 */
public class QuestionTypeComparator1 implements Comparator<QuestionType> {
    @Override
    public int compare(QuestionType lhs, QuestionType rhs) {
        int value = 0;
        if( rhs.getSortIndex() < lhs.getSortIndex() ) value = 1;
        else if( rhs.getSortIndex() > lhs.getSortIndex() ) value = -1;
        return value;
    }
}
