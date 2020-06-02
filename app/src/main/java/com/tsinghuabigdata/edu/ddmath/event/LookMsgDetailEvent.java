package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 查看消息详情
 * Created by Administrator on 2018/3/19.
 */

public class LookMsgDetailEvent {

    private String rowKey;

    public LookMsgDetailEvent(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

}
