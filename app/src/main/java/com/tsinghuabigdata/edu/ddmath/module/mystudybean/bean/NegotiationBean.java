package com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/29.
 */

public class NegotiationBean implements Serializable {

    private String rechargeId;
    private String rechargeNum;
    private String loginName;
    private String studentName;
    private double rechargeMoney;
    private int totalDdAmt;
    private int rechargeDdAmt;
    private int returnDdAmt;
    private int rechargeType;
    private int rechargeStatus;
    private String createTime;

    public String getRechargeId() {
        return rechargeId;
    }

    public void setRechargeId(String rechargeId) {
        this.rechargeId = rechargeId;
    }

    public String getRechargeNum() {
        return rechargeNum;
    }

    public void setRechargeNum(String rechargeNum) {
        this.rechargeNum = rechargeNum;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public double getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(double rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public int getTotalDdAmt() {
        return totalDdAmt;
    }

    public void setTotalDdAmt(int totalDdAmt) {
        this.totalDdAmt = totalDdAmt;
    }

    public int getRechargeDdAmt() {
        return rechargeDdAmt;
    }

    public void setRechargeDdAmt(int rechargeDdAmt) {
        this.rechargeDdAmt = rechargeDdAmt;
    }

    public int getReturnDdAmt() {
        return returnDdAmt;
    }

    public void setReturnDdAmt(int returnDdAmt) {
        this.returnDdAmt = returnDdAmt;
    }

    public int getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(int rechargeType) {
        this.rechargeType = rechargeType;
    }

    public int getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(int rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
