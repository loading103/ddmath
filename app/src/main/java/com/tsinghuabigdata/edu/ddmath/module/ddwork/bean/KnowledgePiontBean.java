package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/8.
 *
 */

public class KnowledgePiontBean implements Serializable {

    private static final long serialVersionUID = 7393887641609143359L;

    private String knowledgePointId;
    private String knowledgePointName;
    private String knowledgeId;
    private String knowledgeName;

    private float accuracy;     //正确率

    private transient boolean mastery = false;        //是否主要知识点
    private transient int errorTimes;                  //知识点错误次数

    public KnowledgePiontBean(){}
    public KnowledgePiontBean(String name, String id, float acc){
        knowledgeName = name;
        knowledgeId = id;
        accuracy = acc;
    }

    public String getKnowledgeId() {
        return knowledgeId!=null?knowledgeId:knowledgePointId;
    }

//    public void setKnowledgeId(String knowledgeId) {
//        this.knowledgePointId = knowledgeId;
//    }

    public String getKnowledgeName() {
        return knowledgeName!=null?knowledgeName:knowledgePointName;
    }

//    public void setKnowledgeName(String knowledgeName) {
//        this.knowledgePointName = knowledgeName;
//    }

    public float getAccuracy() {
        if( accuracy < 0 ) return 0;
        return accuracy;
    }

    public boolean isMastery() {
        return mastery;
    }

    public void setMastery(boolean mastery) {
        this.mastery = mastery;
    }

    public int getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(int errorTimes) {
        this.errorTimes = errorTimes;
    }
}
