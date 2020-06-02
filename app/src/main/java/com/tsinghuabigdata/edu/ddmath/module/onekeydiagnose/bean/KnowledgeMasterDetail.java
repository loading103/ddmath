package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 知识点掌握详情
 */

public class KnowledgeMasterDetail implements Serializable{

    private static final long serialVersionUID = 8462141671665672619L;

    //bestTop10
    //private ArrayList<KnowledgeMasterBean> bestTop10;
    //private ArrayList<KnowledgeMasterBean> firstLevelKnMastery;
    //private ArrayList<KnowledgeMasterBean> secondLevelKnMastery;
    //private ArrayList<KnowledgeMasterBean> worstTop10;
    private ArrayList<KnowledgeMasterBean> thirdLevelKnMastery;

    private String createTime;                  //报告更新时间
    private String reportName;                  //报告名称

    private float totalScore;                   //总得分
    //private float totalScoreStar;               //

    public ArrayList<KnowledgeMasterBean> getThirdLevelKnMastery() {
        return thirdLevelKnMastery;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getReportName() {
        return reportName;
    }

    public float getTotalScore() {
        return totalScore;
    }

}
