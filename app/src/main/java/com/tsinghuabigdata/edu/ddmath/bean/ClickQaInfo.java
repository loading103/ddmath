package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by 28205 on 2016/11/21.
 */
public class ClickQaInfo implements Serializable {
    private static final long serialVersionUID = -5048784032004419148L;

    private String date;
    private String count;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
