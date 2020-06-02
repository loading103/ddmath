package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;

/**
 * 知识点掌握情况
 */

public class KnowledgeMasterBean implements Serializable{
    private static final long serialVersionUID = 38984550719459116L;

    private float accuracy;                       //正确率
    private float lastAccuracy;                   //上次正确率,
    private String knowledgePointId;            //知识点ID
    private String knowledgePointName;          //知识点名称
    private boolean newLearn;                   //是否新增
    private String parentId;                    //父知识点ID,
    private String parentName;                  //父知识点ID
    private int rightCount;                     //正确题目数量,
    private int totalCount;                     //总题目数量

    private transient boolean select;

    public float getAccuracy() {
        return accuracy;
    }

    public float getLastAccuracy() {
        return lastAccuracy;
    }

    public String getKnowledgePointId() {
        return knowledgePointId;
    }

    public String getKnowledgePointName() {
        return knowledgePointName;
    }

    public boolean isNewLearn() {
        return newLearn;
    }

    public String getParentId() {
        return parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public int getRightCount() {
        return rightCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
