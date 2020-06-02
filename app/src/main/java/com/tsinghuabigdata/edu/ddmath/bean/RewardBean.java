package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/25.
 */

public class RewardBean implements Serializable {

    private static final long serialVersionUID = 3597640102448921207L;
    private int rechargeMoney;
    private int returnDdAmt;
    private int effect;
    private String returnSettingId;

    public int getRechargeMoney() {
        return rechargeMoney;
    }

    public int getReturnDdAmt() {
        return returnDdAmt;
    }

    public int getEffect() {
        return effect;
    }

    public void setRechargeMoney(int rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public void setReturnDdAmt(int returnDdAmt) {
        this.returnDdAmt = returnDdAmt;
    }

    public void setEffect(int effect) {
        this.effect = effect;
    }

    public String getReturnSettingId() {
        return returnSettingId;
    }

    public void setReturnSettingId(String returnSettingId) {
        this.returnSettingId = returnSettingId;
    }
}
