package com.tsinghuabigdata.edu.ddmath.module.studycheat.bean;

import java.io.Serializable;

/**
 * 错题回顾查询结果
 */
public class ErrorQuestionQueryBean implements Serializable {
    private static final long serialVersionUID = -6868399700977900739L;

//    requestId	string	查询id
//    questionId	string	题目id
//    wrongReasons	string  错误原因，多个逗号分割
//    degree	int	掌握程度

    String requestId;   //查询id
    String questionId;  //题目id
    String wrongReasons;    //错误原因
    int degree;         //掌握程度

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getWrongReasons() {
        return wrongReasons;
    }

    public void setWrongReasons(String wrongReasons) {
        this.wrongReasons = wrongReasons;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }
}
