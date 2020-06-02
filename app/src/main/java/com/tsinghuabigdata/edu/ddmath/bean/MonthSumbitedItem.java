package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class MonthSumbitedItem implements Serializable {


    private String         month;
    private ArrayList<SubmitQuestion> questions;

    public MonthSumbitedItem() {
    }

    public MonthSumbitedItem(String month, ArrayList<SubmitQuestion> questions) {
        this.month = month;
        this.questions = questions;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<SubmitQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<SubmitQuestion> questions) {
        this.questions = questions;
    }
}