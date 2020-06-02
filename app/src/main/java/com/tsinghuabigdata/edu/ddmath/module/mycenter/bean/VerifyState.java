package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/24.
 */

public class VerifyState implements Serializable {


    private static final long serialVersionUID = 7871049112132230719L;
    private boolean isConfirmed;

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setComfirmed(boolean comfirmed) {
        isConfirmed = comfirmed;
    }
}
