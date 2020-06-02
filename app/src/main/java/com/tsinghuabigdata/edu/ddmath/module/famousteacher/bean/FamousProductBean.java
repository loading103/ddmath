package com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean;

import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.FreeDroitDetailBean;

import java.io.Serializable;
import java.util.List;

/**
 * 名师精讲商品实体类
 * Created by Administrator on 2018/3/2.
 */

public class FamousProductBean implements Serializable {

    private String                    productId;
    private String                    productName;
    private int                       price;
    private List<FreeDroitDetailBean> productRightSimpleVos;   //使用权 list


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<FreeDroitDetailBean> getProductRightSimpleVos() {
        return productRightSimpleVos;
    }

    public void setProductRightSimpleVos(List<FreeDroitDetailBean> productRightSimpleVos) {
        this.productRightSimpleVos = productRightSimpleVos;
    }

    public int getFreeUseTimes() {
        int count = 0;
        if( productRightSimpleVos!=null ){
            for(FreeDroitDetailBean bean : productRightSimpleVos){
                int time = bean.getSubTimes();
                if( time == -1 ) return -1;
                else if( time < -1 ) time = 0;
                count += time;
            }
        }
        return count;
    }
}
