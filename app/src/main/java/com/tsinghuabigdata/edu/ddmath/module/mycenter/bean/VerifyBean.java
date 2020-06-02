package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/24.
 */

public class VerifyBean implements Serializable {

    private static final long serialVersionUID = 2682436067153488913L;
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
