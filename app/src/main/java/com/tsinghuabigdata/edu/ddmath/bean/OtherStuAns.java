package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 28205 on 2016/11/21.
 */
public class OtherStuAns implements Serializable{
    private static final long serialVersionUID = -5645929652462581582L;
    /**
     * data
     * --otherAnswers	推荐的其他正确答案列表(5个)
     * ----studentId	被推荐学生Id
     * ----answerUrl	答案图片
     * ----stuName	学生姓名
     * ----schoolName	学校名称
     */
    private List<QuestionAns> otherAnswers;

    public List<QuestionAns> getOtherAnswers() {
        return otherAnswers;
    }

    public void setOtherAnswers(List<QuestionAns> otherAnswers) {
        this.otherAnswers = otherAnswers;
    }
}
