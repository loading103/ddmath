package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 首页跳转事件
 * Created by Administrator on 2018/3/19.
 */

public class JumpEvent {

    private int mainModule;
    private int subModule;


    public JumpEvent(int mainModule) {
        this.mainModule = mainModule;
    }

    public JumpEvent(int mainModule, int subModule) {
        this.mainModule = mainModule;
        this.subModule = subModule;
    }

    public int getMainModule() {
        return mainModule;
    }

    public void setMainModule(int mainModule) {
        this.mainModule = mainModule;
    }

    public int getSubModule() {
        return subModule;
    }

    public void setSubModule(int subModule) {
        this.subModule = subModule;
    }
}
