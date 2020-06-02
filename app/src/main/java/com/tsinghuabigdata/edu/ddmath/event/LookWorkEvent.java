package com.tsinghuabigdata.edu.ddmath.event;


/**
 * 查看作业详情
 * Created by Administrator on 2018/3/19.
 */

public class LookWorkEvent {

    private String workId;

    public LookWorkEvent(String workId) {
        this.workId = workId;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }
}
