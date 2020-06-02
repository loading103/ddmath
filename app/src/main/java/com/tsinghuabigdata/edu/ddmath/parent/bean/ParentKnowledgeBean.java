package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/28.
 */

public class ParentKnowledgeBean implements Serializable {
    /**
     * knowledges : [{"correctRate":0,"knowledgePointName":""}]
     * totalKnowledgeCount : 0
     */

    private int                  totalKnowledgeCount;
    private List<KnowledgesBean> knowledges;

    public int getTotalKnowledgeCount() {
        return totalKnowledgeCount;
    }

    public void setTotalKnowledgeCount(int totalKnowledgeCount) {
        this.totalKnowledgeCount = totalKnowledgeCount;
    }

    public List<KnowledgesBean> getKnowledges() {
        return knowledges;
    }

    public void setKnowledges(List<KnowledgesBean> knowledges) {
        this.knowledges = knowledges;
    }

}
