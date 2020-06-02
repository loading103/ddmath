package com.tsinghuabigdata.edu.ddmath.event;

/**
 * （个人中心首页）同步展示我的学豆
 * Created by Administrator on 2018/3/20.
 */

public class SyncShowStudybeanFromDialogEvent {

    private int totalBean;

    public SyncShowStudybeanFromDialogEvent(int totalBean) {
        this.totalBean = totalBean;
    }

    public int getTotalBean() {
        return totalBean;
    }

    public void setTotalBean(int totalBean) {
        this.totalBean = totalBean;
    }
}
