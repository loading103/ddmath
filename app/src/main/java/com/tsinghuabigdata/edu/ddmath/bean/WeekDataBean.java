package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14.
 */

public class WeekDataBean implements Serializable {

    private int  code;
    private ArrayList<WeekBean> datas;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<WeekBean> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<WeekBean> datas) {
        this.datas = datas;
    }
}
