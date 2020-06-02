package com.tsinghuabigdata.edu.ddmath.module.studycheat.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/14.
 */
public class PowerBean implements Serializable {

    /**
     * endValue : 50
     * increase : 0
     * level : 0
     * name : agi
     * startValue : 0
     * upLevel : 0
     * value : 0
     */

    private int endValue;
    private int    increase;
    private int    level;
    private String name;
    private int    startValue;
    private int    upLevel;
    private int    value;

    public int getEndValue() {
        return endValue;
    }

    public void setEndValue(int endValue) {
        this.endValue = endValue;
    }

    public int getIncrease() {
        return increase;
    }

    public void setIncrease(int increase) {
        this.increase = increase;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public int getUpLevel() {
        return upLevel;
    }

    public void setUpLevel(int upLevel) {
        this.upLevel = upLevel;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
