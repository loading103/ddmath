package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/25.
 */

public class UseCountBean implements Serializable {

    private static final long serialVersionUID = 3579735254187799544L;
    private int useCount;
    private int reviseCount;


    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public int getReviseCount() {
        return reviseCount;
    }

    public void setReviseCount(int reviseCount) {
        this.reviseCount = reviseCount;
    }
}
