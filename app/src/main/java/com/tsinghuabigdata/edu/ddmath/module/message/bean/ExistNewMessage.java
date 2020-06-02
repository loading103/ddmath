package com.tsinghuabigdata.edu.ddmath.module.message.bean;

import java.io.Serializable;


public class ExistNewMessage implements Serializable {

    private static final long serialVersionUID = 6385941839714404180L;

    private boolean zxtj;
    private boolean wdxx;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isZxtj() {
        return zxtj;
    }

    public void setZxtj(boolean zxtj) {
        this.zxtj = zxtj;
    }

    public boolean isWdxx() {
        return wdxx;
    }

    public void setWdxx(boolean wdxx) {
        this.wdxx = wdxx;
    }
}
