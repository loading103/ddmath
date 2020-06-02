package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/7/31.
 */

public class KnowledgesBean implements Serializable{

    private static final long serialVersionUID = -6524147471616545419L;
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
