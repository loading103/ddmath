package com.tsinghuabigdata.edu.ddmath.bean;

/**
 * Created by Administrator on 2017/9/11.
 */

public class LocalMessage<T> {

    private String action;
    private T data;

    public LocalMessage(String action) {
        this.action = action;
    }

    public LocalMessage(String action, T data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
