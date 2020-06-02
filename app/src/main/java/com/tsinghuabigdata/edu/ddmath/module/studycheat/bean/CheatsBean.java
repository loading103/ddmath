package com.tsinghuabigdata.edu.ddmath.module.studycheat.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/14.
 */
public class CheatsBean implements Serializable {

    /**
     * score : 32
     * surplus : 1
     * time : 120
     * type : 12
     */
    private int score;          //学力
    private int surplus;        //还剩多少道
    private int time;           //预估时间
    private int type;           //类型（11：错题回顾，12：强化训练）

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

