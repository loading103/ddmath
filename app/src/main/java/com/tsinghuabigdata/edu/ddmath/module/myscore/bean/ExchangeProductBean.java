package com.tsinghuabigdata.edu.ddmath.module.myscore.bean;

import java.io.Serializable;

/**
 * 兑换商品
 */

public class ExchangeProductBean implements Serializable {

    private static final long serialVersionUID = 4544100440186253834L;

    private int success;                //0 成功 1失败 2 积分不足
    private int pointAmt;               //所需积分数
    private String imagePath;           //图片地址信息
    private int catelogId;              //所属分类
    private String catelogName;         //分类名称
    private String recordId;

    public int getSuccess() {
        return success;
    }

    public int getPointAmt() {
        return pointAmt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getCatelogId() {
        return catelogId;
    }

    public String getCatelogName() {
        return catelogName;
    }

    public String getRecordId() {
        return recordId;
    }
}
