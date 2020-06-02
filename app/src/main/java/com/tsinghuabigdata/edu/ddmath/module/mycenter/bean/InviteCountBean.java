package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/4/26.
 */

public class InviteCountBean implements Serializable{

    private static final long serialVersionUID = -7316133626993848099L;
    private int recPersonNum;
    private int ddAwardNum;

    public int getRecPersonNum() {
        return recPersonNum;
    }

    public void setRecPersonNum(int recPersonNum) {
        this.recPersonNum = recPersonNum;
    }

    public int getDdAwardNum() {
        return ddAwardNum;
    }

    public void setDdAwardNum(int ddAwardNum) {
        this.ddAwardNum = ddAwardNum;
    }
}
