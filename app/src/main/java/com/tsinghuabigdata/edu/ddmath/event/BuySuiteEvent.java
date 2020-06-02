package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 成功购买套餐
 * Created by Administrator on 2018/5/2.
 */

public class BuySuiteEvent {

    private String suiteId;

    public BuySuiteEvent(){}
    public BuySuiteEvent(String suiteId){
        this.suiteId = suiteId;
    }

    public String getSuiteId() {
        return suiteId;
    }
}
