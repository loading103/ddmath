package com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/28.
 */

public class AlipaySignBean implements Serializable {

    private String sign;
    private String tradeNo;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }
}
