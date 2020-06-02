package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * 是否具有参考答案查看权限
 * Created by Administrator on 2017/9/26.
 */

public class AnswerPermission implements Serializable {

    /**
     * showAnswer : 0
     * allowLookAnswer : 1
     */

    private int showAnswer; 	//是否显示问小豆中相似题的答案， 0 显示， 1 不显示
    private int allowLookAnswer;  //允许学生在提交作业后查看答案，0：允许 1：不允许

    public int getShowAnswer() {
        return showAnswer;
    }

    public void setShowAnswer(int showAnswer) {
        this.showAnswer = showAnswer;
    }

    public int getAllowLookAnswer() {
        return allowLookAnswer;
    }

    public void setAllowLookAnswer(int allowLookAnswer) {
        this.allowLookAnswer = allowLookAnswer;
    }
}
