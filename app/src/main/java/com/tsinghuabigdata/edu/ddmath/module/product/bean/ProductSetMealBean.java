package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 商品套餐信息
 */

public class ProductSetMealBean implements Serializable {

    private static final long serialVersionUID = -7666865970196928245L;

    private String                      setMealName;          //套餐名称
    private String                      setMealID;            //套餐ID
    private int                         setMealPrice;         //套餐价格
    private ArrayList<ProductBean>      productList;          //套餐包含的商品列表
    private long                        useStartTime;         //使用期限
    private long                        useEndTime;           //使用期限

    public String getSetMealName() {
        return setMealName;
    }

    public String getSetMealID() {
        return setMealID;
    }

    public int getSetMealPrice() {
        return setMealPrice;
    }

    public ArrayList<ProductBean> getProductList() {
        return productList;
    }

    public long getUseStartTime() {
        return useStartTime;
    }

    public long getUseEndTime() {
        return useEndTime;
    }


    public void setSetMealName(String setMealName) {
        this.setMealName = setMealName;
    }

    public void setSetMealID(String setMealID) {
        this.setMealID = setMealID;
    }

    public void setSetMealPrice(int setMealPrice) {
        this.setMealPrice = setMealPrice;
    }

    public void setProductList(ArrayList<ProductBean> productList) {
        this.productList = productList;
    }

    public void setUseStartTime(long useStartTime) {
        this.useStartTime = useStartTime;
    }

    public void setUseEndTime(long useEndTime) {
        this.useEndTime = useEndTime;
    }
}
