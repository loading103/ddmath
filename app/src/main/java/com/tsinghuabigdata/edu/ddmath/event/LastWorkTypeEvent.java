package com.tsinghuabigdata.edu.ddmath.event;

/**
 * Created by Administrator on 2018/3/27.
 */

public class LastWorkTypeEvent {

    private boolean isCheckWork;

    public LastWorkTypeEvent(boolean isCheckWork) {
        this.isCheckWork = isCheckWork;
    }

    public boolean isCheckWork() {
        return isCheckWork;
    }

    public void setCheckWork(boolean checkWork) {
        isCheckWork = checkWork;
    }
}
