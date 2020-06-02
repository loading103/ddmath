package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * 套题结构
 */

public class PaperSetBean implements Serializable {
    private static final long serialVersionUID = -1997777372815103829L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
