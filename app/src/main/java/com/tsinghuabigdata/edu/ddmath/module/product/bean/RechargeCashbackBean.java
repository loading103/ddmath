package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * 充值返现类
 */

public class RechargeCashbackBean implements Serializable{
    private static final long serialVersionUID = -5238865001659785908L;

    private float rechargeMoney;        //充值金额
    private  int  returnDdAmt;          //赠送豆豆数
    private  int  effect;               //0失效 1生效

    public float getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(float rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public int getReturnDdAmt() {
        return returnDdAmt;
    }

    public void setReturnDdAmt(int returnDdAmt) {
        this.returnDdAmt = returnDdAmt;
    }

    public int getEffect() {
        return effect;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }
}
