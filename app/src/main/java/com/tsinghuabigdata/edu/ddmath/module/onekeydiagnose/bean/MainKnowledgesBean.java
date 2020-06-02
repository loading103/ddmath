package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 三级知识点
 * Created by Administrator on 2018/8/20.
 */

public class MainKnowledgesBean implements Serializable{

    private static final long serialVersionUID = 8353179271225131456L;
    private String                  knowledgePointName;
    private String                  knowledgePointId;
    private List<SubKnowledgesBean> subKnowledges;

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

    public List<SubKnowledgesBean> getSubKnowledges() {
        return subKnowledges;
    }

    public void setSubKnowledges(List<SubKnowledgesBean> subKnowledges) {
        this.subKnowledges = subKnowledges;
    }
}
