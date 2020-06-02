package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;

/**
 * 错误知识点 错误率>50%  错误次数最多的Top3
 */

public class EKPiontBean extends KnowledgePiontBean implements Serializable {

    private static final long serialVersionUID = -417963040416989483L;

    private int totalCount;
    private int rightCount;
    //private float accuracy;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

//    public float getAccuracy() {
//        return accuracy;
//    }
//
//    public void setAccuracy(double accuracy) {
//        this.accuracy = accuracy;
//    }

}
