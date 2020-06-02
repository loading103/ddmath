package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/16.
 */

public class KnowPointCount implements Serializable {

    private static final long serialVersionUID = -115588718850235671L;

    private int errorCount;         //错误次数

    private KnowledgePiontBean knowledge;

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int count) {
        this.errorCount = count;
    }

    public KnowledgePiontBean getKnowledge() {
        return knowledge;
    }
    public void setKnowledge(KnowledgePiontBean knowledge) {
        this.knowledge = knowledge;
    }

}
