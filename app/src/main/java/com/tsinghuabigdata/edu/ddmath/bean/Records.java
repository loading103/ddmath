package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class Records implements Serializable {


    private static final long serialVersionUID = 5878697692592867876L;
    private String         month;
    private ArrayList<MyCourse> courses;


    public Records() {

    }


    public Records(String month, ArrayList<MyCourse> courses) {
        this.month = month;
        this.courses = courses;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<MyCourse> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<MyCourse> courses) {
        this.courses = courses;
    }
}