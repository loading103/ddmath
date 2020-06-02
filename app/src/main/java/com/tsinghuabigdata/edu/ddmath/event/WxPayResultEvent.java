package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 微信结果事件
 * Created by Administrator on 2018/3/19.
 */

public class WxPayResultEvent {

    public int errCode;
    public String errStr;
    public String transaction;
    public String openId;

    public WxPayResultEvent(int errCode, String errStr) {
        this.errCode = errCode;
        this.errStr = errStr;
    }

}
