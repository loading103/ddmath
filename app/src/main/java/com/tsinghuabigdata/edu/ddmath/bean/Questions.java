package com.tsinghuabigdata.edu.ddmath.bean;

import com.tsinghuabigdata.edu.ddmath.module.errorbook.bean.QuestionVo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */

public class Questions implements Serializable {
    private int              totalCount;
    private List<QuestionVo> questions;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<QuestionVo> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionVo> questions) {
        this.questions = questions;
    }
}