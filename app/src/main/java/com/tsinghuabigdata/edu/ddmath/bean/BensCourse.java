package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/9.
 */

public class BensCourse implements Serializable {


    private String name;
    private String grade;
    private String time;
    private String kind;
    private String tag;

    private int count = 385;
    private int price = 518;

    public BensCourse() {
    }

    public BensCourse(BensCourse course) {
        this.grade = course.getGrade();
        this.time = course.getTime();
        this.kind = course.getKind();
        this.tag = course.getTag();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
