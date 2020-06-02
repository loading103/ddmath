package com.tsinghuabigdata.edu.ddmath.module.studycheat.bean;

import java.io.Serializable;
import java.util.List;

/**
 *
 */

public class StudyAbilityInfo implements Serializable {

    private static final long serialVersionUID = -2396877108334443288L;
    /**
     * cheats : [{"score":32,"surplus":1,"time":120,"type":12},{"score":32,"surplus":3,"time":120,"type":11}]
     * currentValue : 0
     * endValue : 50
     * percentExceed : 1
     * power : [{"endValue":50,"increase":0,"level":0,"name":"agi","startValue":0,"upLevel":0,"value":0},{"endValue":50,"increase":0,"level":0,"name":"con","startValue":0,"upLevel":0,"value":0},{"endValue":50,"increase":0,"level":0,"name":"str","startValue":0,"upLevel":0,"value":0},{"endValue":50,"increase":0,"level":0,"name":"crt","startValue":0,"upLevel":0,"value":0}]
     * startValue : 0
     * surplus : true
     * todayValue : 0
     */

    private int completeCount;          //今日已完成任务人数
    private boolean surplusReview;      //今日是否有错题回顾推荐
    private boolean surplusStength;     //今日是否有强化训练推荐
    private int remainedReviewChance;   //今日错题回顾剩余个数
    private int remainStrengthChance;    //今日强化训练剩余个数
    private List<CheatsBean> cheats;    //提分秘籍

    public int getCompleteCount() {
        return completeCount;
    }

    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public boolean isSurplusReview() {
        return surplusReview;
    }

    public void setSurplusReview(boolean surplusReview) {
        this.surplusReview = surplusReview;
    }

    public boolean isSurplusStength() {
        return surplusStength;
    }

    public void setSurplusStength(boolean surplusStength) {
        this.surplusStength = surplusStength;
    }

    public int getRemainedReviewChance() {
        return remainedReviewChance;
    }

    public void setRemainedReviewChance(int remainedReviewChance) {
        this.remainedReviewChance = remainedReviewChance;
    }

    public List<CheatsBean> getCheats() {
        return cheats;
    }

    public void setCheats(List<CheatsBean> cheats) {
        this.cheats = cheats;
    }

    public int getRemainStrengthChanc() {
        return remainStrengthChance;
    }

    public void setRemainStrengthChanc(int remainedTrainChance) {
        this.remainStrengthChance = remainedTrainChance;
    }
}
