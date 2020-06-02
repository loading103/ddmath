package com.tsinghuabigdata.edu.ddmath.bean;

/**
 * Created by Administrator on 2018/3/19.
 */

public class MessageEvent<T> {

    private String action;
    private T data;

    public MessageEvent(String action) {
        this.action = action;
    }

    public MessageEvent(String action, T data) {
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
