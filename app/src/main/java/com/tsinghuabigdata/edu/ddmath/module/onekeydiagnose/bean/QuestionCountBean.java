package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;

/**
 * 创建提升练习返回题目数实体类
 * Created by Administrator on 2018/8/14.
 */

public class QuestionCountBean implements Serializable{

    private static final long serialVersionUID = 6786181958996121145L;
    private int questionCount;
    private int useTimes;

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(int useTimes) {
        this.useTimes = useTimes;
    }
}
