package com.tsinghuabigdata.edu.ddmath.module.product.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 兑换时次数时 请求结果
 */

public class ExchangeDetail implements Serializable {

    private static final long serialVersionUID = 756853071831147216L;

    private int money;                              //
    private int learndou;                           //

    private ArrayList<ExchangeTimeBean> list;       //

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getLearndou() {
        return learndou;
    }

    public void setLearndou(int learndou) {
        this.learndou = learndou;
    }

    public ArrayList<ExchangeTimeBean> getList() {
        return list;
    }

    public void setList(ArrayList<ExchangeTimeBean> list) {
        this.list = list;
    }
}
