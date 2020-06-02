package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;

/**
 * 商品使用次数汇总
 */

public class ProductUseTimesDetail implements Serializable {

    private static final long serialVersionUID = -8986321089278833484L;

    private int errReviseCount;             //错题订正次数
    private int practiceUploadCount;        //套题上传
    private int materialUploadCount;        //教辅上传

    //充值推荐的
    private int rechargeMoney;              //充值价钱
    private int giveLearnDou;               //赠送学豆

    public int getErrReviseCount() {
        return errReviseCount;
    }

    public void setErrReviseCount(int errReviseCount) {
        this.errReviseCount = errReviseCount;
    }

    public int getPracticeUploadCount() {
        return practiceUploadCount;
    }

    public void setPracticeUploadCount(int practiceUploadCount) {
        this.practiceUploadCount = practiceUploadCount;
    }

    public int getMaterialUploadCount() {
        return materialUploadCount;
    }

    public void setMaterialUploadCount(int materialUploadCount) {
        this.materialUploadCount = materialUploadCount;
    }

    public int getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(int rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public int getGiveLearnDou() {
        return giveLearnDou;
    }

    public void setGiveLearnDou(int giveLearnDou) {
        this.giveLearnDou = giveLearnDou;
    }
}
