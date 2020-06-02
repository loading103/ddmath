package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 城市知识点排名
 */

public class CityRankResult implements Serializable {


    private static final long serialVersionUID = 1065677247328927661L;

    private float accuracy;       //知识点平均正确率
    private int totalStudent;   //总学生数
    private ArrayList<CityRankBean> list;

//    public CityRankResult( int accuracy, int totalStudent, ArrayList<CityRankBean> list){
//        this.accuracy = accuracy;
//        this.totalStudent = totalStudent;
//        this.list = list;
//    }

    public float getAccuracy() {
        return accuracy;
    }

    public int getTotalStudent() {
        return totalStudent;
    }

    public ArrayList<CityRankBean> getList() {
        return list;
    }

}
