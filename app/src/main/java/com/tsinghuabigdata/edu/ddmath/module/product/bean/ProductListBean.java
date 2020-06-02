package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/11/23.
 */

public class ProductListBean implements Serializable {

    private String            schoolId;
    private List<String>      classIds;
    private List<ProductBean> productResVo;

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public List<String> getClassIds() {
        return classIds;
    }

    public void setClassIds(List<String> classIds) {
        this.classIds = classIds;
    }

    public List<ProductBean> getProductResVo() {
        return productResVo;
    }

    public void setProductResVo(List<ProductBean> productResVo) {
        this.productResVo = productResVo;
    }
}
