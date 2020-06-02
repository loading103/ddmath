package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/20.
 */

public class PrivilegeVo implements Serializable {

    //权限id,关联权限系统权限表id
    private String id;
    //使用人数
    private int usePeople;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUsePeople() {
        return usePeople;
    }

    public void setUsePeople(int usePeople) {
        this.usePeople = usePeople;
    }
}
