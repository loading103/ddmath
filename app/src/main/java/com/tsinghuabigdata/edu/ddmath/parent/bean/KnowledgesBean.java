package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/28.
 */

public class KnowledgesBean implements Serializable {

    /**
     * correctRate : 0
     * knowledgePointName :
     */

    private int correctRate;
    private String knowledgePointName;

    public int getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(int correctRate) {
        this.correctRate = correctRate;
    }

    public String getKnowledgePointName() {
        return knowledgePointName;
    }

    public void setKnowledgePointName(String knowledgePointName) {
        this.knowledgePointName = knowledgePointName;
    }

}
