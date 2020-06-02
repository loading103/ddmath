package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * 错题订正提交
 */

public class SubmitReviseBean implements Serializable {

    private static final long serialVersionUID = 7466761556093920958L;

    private String reviseId;        //错题订正ID
    private int score;            //学力值

    public String getReviseId() {
        return reviseId;
    }

    public void setReviseId(String reviseId) {
        this.reviseId = reviseId;
    }

    public int getAbility() {
        return score;
    }

    public void setAbility(int ability) {
        this.score = ability;
    }
}
