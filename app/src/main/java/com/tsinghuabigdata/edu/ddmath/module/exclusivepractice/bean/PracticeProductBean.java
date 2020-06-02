package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean;

import android.text.TextUtils;

import com.tsinghuabigdata.edu.utils.NumberUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 专属习题商品
 */
public class PracticeProductBean implements Serializable {
    private static final long serialVersionUID = -8154299841090374652L;

    //商品信息
    private String productId;             //商品ID
    private String productType;           //商品类型1：每周变式训练，2：自定义；3：每月；4：考前
    private String name;                  //商品名称
    private String subName;               //辅助名称
    private String catalogId;             //商品二级分类ID 商品大类型5：专属套题 4：精选套题 3：假期作业 2：日清-周练-月结 1：提分之源
    private String privilegeId;           //

//    data[].introImagePath 	String 	商品介绍图片地址
//    data[].introImageWidth 	int 	商品介绍图片宽度
//    data[].introImageHeight 	int 	商品介绍图片高度
//    data[].userGuideImagePath 	String 	使用方法图片地址
//    data[].userGuideImageWidth 	int 	使用方法图片宽度
//    data[].userGuideImageHeight 	int 	使用方法图片高度

    private ArrayList<PracticeBean> productList;      //套题列表

    private FreeDroitBean useTimes;       //免费使用权

    //private boolean showRealName;       //显示用户真实名称

    private int price;                  //价格，用户学豆表示, 单题价格

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public ArrayList<PracticeBean> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<PracticeBean> productList) {
        this.productList = productList;
    }

//    public boolean isShowRealName() {
//        return showRealName;
//    }
//
//    public void setShowRealName(boolean showRealName) {
//        this.showRealName = showRealName;
//    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public FreeDroitBean getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(FreeDroitBean useTimes) {
        this.useTimes = useTimes;
    }

    public int getProductType() {
        if(TextUtils.isEmpty(productType)) return 0;
        return NumberUtils.toInt(productType,0);
    }
//
//    public void setProductType(String productType) {
//        this.productType = productType;
//    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getPrivilegeId() {
        return privilegeId;
    }
}
