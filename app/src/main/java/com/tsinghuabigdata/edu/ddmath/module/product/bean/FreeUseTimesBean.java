package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * 商品使用次数
 */

public class FreeUseTimesBean implements Serializable{
    private static final long serialVersionUID = -5926576501603293816L;

    //商品ID
    private String productId;
    //总使用次数
    private int totalUseTimes;
    //自己兑换的使用次数
    private int changeUseTimes;
    //免费使用次数
    private int freeUseTimes;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getTotalUseTimes() {
        return totalUseTimes;
    }

    public void setTotalUseTimes(int tatalUseTimes) {
        this.totalUseTimes = tatalUseTimes;
    }

    public int getChangeUseTimes() {
        return changeUseTimes;
    }

    public void setChangeUseTimes(int changeUseTimes) {
        this.changeUseTimes = changeUseTimes;
    }

    public int getFreeUseTimes() {
        return freeUseTimes;
    }

    public void setFreeUseTimes(int freeUseTimes) {
        this.freeUseTimes = freeUseTimes;
    }
}
