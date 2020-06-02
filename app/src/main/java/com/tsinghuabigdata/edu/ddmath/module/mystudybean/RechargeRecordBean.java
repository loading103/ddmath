package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

/**
 * Created by Administrator on 2017/12/4.
 */

public class RechargeRecordBean {
    int id;
    String time;
    int money;
    String payWay;
    int inStudyBean;
    int buyStudyBean;
    int sendStudyBean;
    String rechargeStatus;

    public RechargeRecordBean(int id, String time, int money, String payWay, int inStudyBean, int buyStudyBean, int sendStudyBean, String rechargeStatus) {
        this.id = id;
        this.time = time;
        this.money = money;
        this.payWay = payWay;
        this.inStudyBean = inStudyBean;
        this.buyStudyBean = buyStudyBean;
        this.sendStudyBean = sendStudyBean;
        this.rechargeStatus = rechargeStatus;
    }

    public int getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public int getMoney() {
        return money;
    }

    public String getPayWay() {
        return payWay;
    }

    public int getInStudyBean() {
        return inStudyBean;
    }

    public int getBuyStudyBean() {
        return buyStudyBean;
    }

    public int getSendStudyBean() {
        return sendStudyBean;
    }

    public String getRechargeStatus() {
        return rechargeStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay;
    }

    public void setInStudyBean(int inStudyBean) {
        this.inStudyBean = inStudyBean;
    }

    public void setBuyStudyBean(int buyStudyBean) {
        this.buyStudyBean = buyStudyBean;
    }

    public void setSendStudyBean(int sendStudyBean) {
        this.sendStudyBean = sendStudyBean;
    }

    public void setRechargeStatus(String rechargeStatus) {
        this.rechargeStatus = rechargeStatus;
    }
}
