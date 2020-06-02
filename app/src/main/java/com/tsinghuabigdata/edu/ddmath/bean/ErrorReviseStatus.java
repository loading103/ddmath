package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * 每日错题订正状态查询
 * Created by Administrator on 2018/6/19.
 */

public class ErrorReviseStatus implements Serializable{


    private static final long serialVersionUID = -870688886967700045L;
    private int hasReviseCount;
    private int tobeReviseCount;


    public int getHasReviseCount() {
        return hasReviseCount;
    }

    public void setHasReviseCount(int hasReviseCount) {
        this.hasReviseCount = hasReviseCount;
    }

    public int getTobeReviseCount() {
        return tobeReviseCount;
    }

    public void setTobeReviseCount(int tobeReviseCount) {
        this.tobeReviseCount = tobeReviseCount;
    }
}
