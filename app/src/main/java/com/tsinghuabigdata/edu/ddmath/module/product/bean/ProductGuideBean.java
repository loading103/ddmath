package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/23.
 */

public class ProductGuideBean implements Serializable {

    //    userGuideImageHeight=563
    //    userGuideImageWidth=1509

    private String privilegeId;
    private String productId;
    private String userGuideImagePath;
    private int    userGuideImageHeight;
    private int    userGuideImageWidth;
    private int    totalCount;

    public String getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(String privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserGuideImagePath() {
        return userGuideImagePath;
    }

    public void setUserGuideImagePath(String userGuideImagePath) {
        this.userGuideImagePath = userGuideImagePath;
    }

    public int getUserGuideImageHeight() {
        return userGuideImageHeight;
    }

    public void setUserGuideImageHeight(int userGuideImageHeight) {
        this.userGuideImageHeight = userGuideImageHeight;
    }

    public int getUserGuideImageWidth() {
        return userGuideImageWidth;
    }

    public void setUserGuideImageWidth(int userGuideImageWidth) {
        this.userGuideImageWidth = userGuideImageWidth;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
