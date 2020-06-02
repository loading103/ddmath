package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by 28205 on 2016/10/20.
 */
public class Topic implements Serializable {
    private static final long serialVersionUID = 4734676928062008434L;

    private String questionId;
    private String content;
    private String coefficient;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }
}
