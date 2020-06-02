package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/8/20.
 */

public class LevelKnowledgesBean implements Serializable{

    private static final long serialVersionUID = 2018659266397665256L;
    private float                      avgAccuracy;
    private int                      totalKnowledgeCount;
    private int                      totalQuestionCount;
    private int                      totalWrongCount;
    private List<MainKnowledgesBean> knowledges;


    public float getAvgAccuracy() {
        return avgAccuracy;
    }

    public void setAvgAccuracy(float avgAccuracy) {
        this.avgAccuracy = avgAccuracy;
    }

    public int getTotalKnowledgeCount() {
        return totalKnowledgeCount;
    }

    public void setTotalKnowledgeCount(int totalKnowledgeCount) {
        this.totalKnowledgeCount = totalKnowledgeCount;
    }

    public int getTotalQuestionCount() {
        return totalQuestionCount;
    }

    public void setTotalQuestionCount(int totalQuestionCount) {
        this.totalQuestionCount = totalQuestionCount;
    }

    public int getTotalWrongCount() {
        return totalWrongCount;
    }

    public void setTotalWrongCount(int totalWrongCount) {
        this.totalWrongCount = totalWrongCount;
    }

    public List<MainKnowledgesBean> getKnowledges() {
        return knowledges;
    }

    public void setKnowledges(List<MainKnowledgesBean> knowledges) {
        this.knowledges = knowledges;
    }
}
