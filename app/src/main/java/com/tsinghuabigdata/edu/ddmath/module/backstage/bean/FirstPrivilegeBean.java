package com.tsinghuabigdata.edu.ddmath.module.backstage.bean;

import java.io.Serializable;

/**
 * 用户首次登陆免费获得的用户权益
 */

public class FirstPrivilegeBean implements Serializable{

    private static final long serialVersionUID = -8646542428686173692L;

    //是否是新用户
    private Boolean newUser;
    //是否已经展示过
    private Boolean alreadyShowed;
    //套餐名
    private String productSuiteName;
    private String prodductSuitId;
    private String accountId;
    private String  alterTitle;     //提示语

    public Boolean getNewUser() {
        return newUser;
    }

    public Boolean getAlreadyShowed() {
        return alreadyShowed;
    }

    public String getProductSuiteName() {
        return productSuiteName;
    }

    public String getProdductSuitId() {
        return prodductSuitId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAlterTitle() {
        return alterTitle;
    }
}
