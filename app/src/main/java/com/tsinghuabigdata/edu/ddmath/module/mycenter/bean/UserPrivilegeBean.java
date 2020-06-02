package com.tsinghuabigdata.edu.ddmath.module.mycenter.bean;

import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 用户特权
 *
 */

public class UserPrivilegeBean implements Serializable{


    private static final long serialVersionUID = -8884052564945010233L;

    private int acquiredPrivilegesSize;
    private int totalPrivilegesSize;

    private ArrayList<ProductBean> productResVos;

    public int getUseCount() {
        return acquiredPrivilegesSize;
    }

    public void setUseCount(int useCount) {
        this.acquiredPrivilegesSize = useCount;
    }

    public int getAllCount() {
        return totalPrivilegesSize;
    }

    public void setAllCount(int allCount) {
        this.totalPrivilegesSize = allCount;
    }

    public ArrayList<ProductBean> getList() {
        return productResVos;
    }

    public void setList(ArrayList<ProductBean> list) {
        this.productResVos = list;
    }
}
