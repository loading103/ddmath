package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 跳转到我要购买界面并展示指定套餐
 * Created by Administrator on 2018/6/6.
 */

public class JumpSuiteEvent {

    private String privilegeId; //自定义参数

    public JumpSuiteEvent(String privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(String privilegeId) {
        this.privilegeId = privilegeId;
    }
}
