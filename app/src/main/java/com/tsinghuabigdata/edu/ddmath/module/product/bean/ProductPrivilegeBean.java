package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 按Privilege查询到的商品信息
 */

public class ProductPrivilegeBean implements Serializable {

    private static final long serialVersionUID = -47107369155115378L;

    private ProductBean                     product;                    //商品
    private ArrayList<ExchangeTimeBean>     priseList;                  //学豆兑换次数列表 <次数, 学豆数>
    private String[]                        bookNameList;               //教辅名字列表
    private String[]                        paperSetNameList;           //套题名字列表

    public ProductBean getProduct() {
        return product;
    }

    public void setProduct(ProductBean product) {
        this.product = product;
    }

    public ArrayList<ExchangeTimeBean> getPriseList() {
        return priseList;
    }

    public void setPriseList(ArrayList<ExchangeTimeBean> priseMap) {
        this.priseList = priseMap;
    }

    public String[] getBookNameList() {
        return bookNameList;
    }

    public void setBookNameList(String[] bookNameList) {
        this.bookNameList = bookNameList;
    }

    public String[] getPaperSetNameList() {
        return paperSetNameList;
    }

    public void setPaperSetNameList(String[] paperSetNameList) {
        this.paperSetNameList = paperSetNameList;
    }
}
