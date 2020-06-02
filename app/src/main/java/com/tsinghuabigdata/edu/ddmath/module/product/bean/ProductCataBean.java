package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/1.
 */

public class ProductCataBean implements Serializable {

    private String catalogId;
    private String catalogName;
    private boolean select;

    public ProductCataBean() {
    }

    public ProductCataBean(String catalogId, String catalogName, boolean select) {
        this.catalogId = catalogId;
        this.catalogName = catalogName;
        this.select = select;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
