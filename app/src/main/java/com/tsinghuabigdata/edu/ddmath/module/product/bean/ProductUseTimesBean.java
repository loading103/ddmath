package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * 商品使用次数
 */

public class ProductUseTimesBean implements Serializable {
    private static final long serialVersionUID = 3125348734136746511L;

    private String privilegeId;
    private int useTimes;

    public String getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(String privilegeId) {
        this.privilegeId = privilegeId;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(int useTimes) {
        this.useTimes = useTimes;
    }
}
