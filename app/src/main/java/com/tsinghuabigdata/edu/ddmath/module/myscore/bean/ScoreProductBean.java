package com.tsinghuabigdata.edu.ddmath.module.myscore.bean;

import java.io.Serializable;

/**
 * 积分商品
 */

public class ScoreProductBean implements Serializable {


    private static final long serialVersionUID = -3068776144792819123L;

    public final static int TYPE_LIMIT_DAY = 1;
    public final static int TYPE_LIMIT_WEEK = 2;
    public final static int TYPE_LIMIT_MONTH = 3;

    //商品类型
    public final static int PRODUCT_TYPE_XUEDOU = 3;             //学豆
    public final static int PRODUCT_TYPE_PENDANT = 5;           //挂件

    private String productId;
    private String name;                //商品名称
    private String productName;         //商品名称
    private String imagePath;           //商品图片地址
    private int pointAmt;               //商品消耗积分数
    private int exchangeCount;          //兑换人次

    //
    private int useless;                 //0未兑换完 1 已兑换完
    private boolean userExchange;       //用户是否兑换该商品 true 已兑换 false 未兑换
    private int ddAmt;                   //商品兑换的学豆数量

    private long exchangeTime;          //兑换时间
    private long createTime;            //创建时间
    private String remark;              //商品描述
    private int remainCount;            //剩余次数
    private boolean haveExchangeChance;//是否有兑换机会
    private int limitUnit;              //商品使用限制单位，1：每日，2：每周， 3：每月
    private int limitCount;             //商品使用限制次数

    private boolean hasPendant;         //是否拥有此挂件

    private int catalogId;              //商品类型

    private int useStatus;              //使用状态: 0 未使用，1  已使用
    private String recordId;            //交易ID

    public ScoreProductBean(){}
    public ScoreProductBean(String recordId, int use){
        this.recordId = recordId;
        this.useStatus = use;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getPointAmt() {
        return pointAmt;
    }

    public int getExchangeCount() {
        return exchangeCount;
    }

    public void setExchangeCount(int exchangeCount) {
        this.exchangeCount = exchangeCount;
    }

    public void setRemainCount(int remainCount) {
        this.remainCount = remainCount;
    }

    public long getExchangeTime() {
        return exchangeTime;
    }

    public String getRemark() {
        return remark;
    }

    public int getRemainCount() {
        return remainCount;
    }

    public boolean isHaveExchangeChance() {
        return haveExchangeChance;
    }

    public int getLimitUnit() {
        return limitUnit;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public boolean isHasPendant() {
        return hasPendant;
    }

    public int getUseStatus() {
        return useStatus;
    }
    public void setUseStatus(int status){
        useStatus = status;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public int getUseless() {
        return useless;
    }

    public boolean isUserExchange() {
        return userExchange;
    }

    public void setUserExchange(boolean userExchange) {
        this.userExchange = userExchange;
    }

    public int getDdAmt() {
        return ddAmt;
    }

    public String getProductName() {
        return productName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getCatalogId() {
        return catalogId;
    }
}
