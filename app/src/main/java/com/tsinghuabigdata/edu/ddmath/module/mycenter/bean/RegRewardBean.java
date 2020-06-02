package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/1.
 */

public class RegRewardBean implements Serializable {


    private static final long serialVersionUID = 1784560785132305677L;
    private int regWithRecWard;
    private int regWithOutRecAward;
    private int recAwardNum;
    private int maxRecAwardNum;

    public int getRegWithRecWard() {
        return regWithRecWard;
    }

    public void setRegWithRecWard(int regWithRecWard) {
        this.regWithRecWard = regWithRecWard;
    }

    public int getRegWithOutRecAward() {
        return regWithOutRecAward;
    }

    public void setRegWithOutRecAward(int regWithOutRecAward) {
        this.regWithOutRecAward = regWithOutRecAward;
    }

    public int getRecAwardNum() {
        return recAwardNum;
    }

    public void setRecAwardNum(int recAwardNum) {
        this.recAwardNum = recAwardNum;
    }

    public int getMaxRecAwardNum() {
        return maxRecAwardNum;
    }

    public void setMaxRecAwardNum(int maxRecAwardNum) {
        this.maxRecAwardNum = maxRecAwardNum;
    }
}
