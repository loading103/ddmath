package com.tsinghuabigdata.edu.ddmath.module.studycheat.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/7.
 */
public class AbilityQueryBean implements Serializable {
    private static final long serialVersionUID = -292068170576914886L;

//    correct	Boolean	正确

//    str	object	力量
//    increase	float	提升
//    value	float	值
//    con		体力
//    increase	float	提升
//    value	float	值
//    agi		速度
//    increase	float	提升
//    value	float	值
//    crt		暴击
//    increase	float	提升
//    value	float	值
//    surplus	int	剩余题数
//    increase	float	增加

//    continue	int	点击再来一题做题则+1，其他为0
//    remainChance	int	剩余机会
//    totalChance	int	总机会
//    errorRate   float 答题错误率

    boolean correct;    //答题结果
    AbilityItem str;    //力量
    AbilityItem con;    //体力
    AbilityItem agi;    //速度
    AbilityItem crt;    //暴击

    int surplus;        //剩余题数

    int count;

    float increase;
    int remainChance;       //剩余机会

    int totalChance;        //总机会
    float errorRate;        //答题错误率


    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public AbilityItem getStr() {
        return str;
    }

    public void setStr(AbilityItem str) {
        this.str = str;
    }

    public AbilityItem getCon() {
        return con;
    }

    public void setCon(AbilityItem con) {
        this.con = con;
    }

    public AbilityItem getAgi() {
        return agi;
    }

    public void setAgi(AbilityItem agi) {
        this.agi = agi;
    }

    public AbilityItem getCrt() {
        return crt;
    }

    public void setCrt(AbilityItem crt) {
        this.crt = crt;
    }

    public int getSurplus() {
        return surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getIncrease() {
        return increase;
    }

    public void setIncrease(float increase) {
        this.increase = increase;
    }


    public int getRemainChance() {
        return remainChance;
    }

    public void setRemainChance(int remainChance) {
        this.remainChance = remainChance;
    }

    public int getTotalChance() {
        return totalChance;
    }

    public void setTotalChance(int totalChance) {
        this.totalChance = totalChance;
    }


    public float getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(float errorRate) {
        this.errorRate = errorRate;
    }

}
