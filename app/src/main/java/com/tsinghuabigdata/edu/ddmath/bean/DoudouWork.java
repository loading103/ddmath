package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/14.
 */

public class DoudouWork implements Serializable {

//    "account":26
//            "exerhomes": [

    private int  account;
    private ArrayList<SubmitQuestion> exerhomes;

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public ArrayList<SubmitQuestion> getExerhomes() {
        return exerhomes;
    }

    public void setExerhomes(ArrayList<SubmitQuestion> exerhomes) {
        this.exerhomes = exerhomes;
    }
}
