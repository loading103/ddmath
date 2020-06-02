package com.tsinghuabigdata.edu.ddmath.module.myscore.bean;

import java.io.Serializable;

/**
 * 我的积分
 * Created by Administrator on 2017/9/11.
 */

public class UserScoreBean implements Serializable {


    private static final long serialVersionUID = -2091370178157353465L;
    private int todayCredit;        //今日积分
    private int totalCredit;        //总积分

    public int getTodayCredit() {
        return todayCredit;
    }

    public int getTotalCredit() {
        return totalCredit;
    }

    public void useScore( int score ){
        totalCredit -= score;
        if(totalCredit<0) totalCredit = 0;
    }

}
