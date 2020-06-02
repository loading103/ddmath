package com.tsinghuabigdata.edu.ddmath.module.studycheat.bean;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;

import java.io.Serializable;

/**
 * 强化训练题目
 */
public class FocreTrainQuestionInfo extends QuestionInfo implements Serializable {

    private static final long serialVersionUID = -7608851717583445863L;

//    requestId	String	查询id
//    count	int	点击再来一题做题则+1，其他为0


    private String          requestId;
    private int             count;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

}

