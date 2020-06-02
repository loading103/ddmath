package com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/28.
 */

public class QrcodeBean implements Serializable {

    /**
     * outTradeNo : 201712282057197225
     * qr_code : https://qr.alipay.com/bax02715x94punqpz9uy00ba
     * rechargeId : A504570E798745D197A8BF5A07F632CE
     */

    private String outTradeNo;
    private String qr_code;
    private String rechargeId;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getRechargeId() {
        return rechargeId;
    }

    public void setRechargeId(String rechargeId) {
        this.rechargeId = rechargeId;
    }
}
