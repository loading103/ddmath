package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;

/**
 * 四级知识点
 * Created by Administrator on 2018/7/31.
 */

public class SubKnowledgesBean implements Serializable{

    private static final long serialVersionUID = 443170578883561761L;
    private int    wrongCount;
    private int    totalCount;
    private float  accuracy;
    private String knowledgePointName;
    private String knowledgePointId;

    private boolean select;


    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public String getKnowledgePointName() {
        return knowledgePointName;
    }

    public void setKnowledgePointName(String knowledgePointName) {
        this.knowledgePointName = knowledgePointName;
    }

    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(String knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void changeSelect() {
        this.select = !this.select;
    }
}
