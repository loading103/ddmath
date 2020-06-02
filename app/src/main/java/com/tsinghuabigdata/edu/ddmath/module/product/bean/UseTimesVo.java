package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/27.
 */

public class UseTimesVo implements Serializable {

    private String classId;
    private int useTimes;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(int useTimes) {
        this.useTimes = useTimes;
    }
}
