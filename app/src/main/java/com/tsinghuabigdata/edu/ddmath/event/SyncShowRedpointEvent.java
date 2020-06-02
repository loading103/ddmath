package com.tsinghuabigdata.edu.ddmath.event;

/**
 * （个人中心首页）同步展示消息红点
 * Created by Administrator on 2018/3/20.
 */

public class SyncShowRedpointEvent {

    private boolean hasNew;

    public SyncShowRedpointEvent(boolean hasNew) {
        this.hasNew = hasNew;
    }

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }
}
