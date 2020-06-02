package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 *  兑换次数
 */

public class ExchangeTimeBean implements Serializable{
    private static final long serialVersionUID = -8088055965773320145L;

    private String exchangeType;
    private int changeNum;      //次数
    private int ddAmt;          //学豆个数

    //本地使用的变量
    private transient boolean select;

    public int getChangeNum() {
        return changeNum;
    }

    public void setChangeNum(int changeNum) {
        this.changeNum = changeNum;
    }

    public int getDdAmt() {
        return ddAmt;
    }

    public void setDdAmt(int ddAmt) {
        this.ddAmt = ddAmt;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }
}
