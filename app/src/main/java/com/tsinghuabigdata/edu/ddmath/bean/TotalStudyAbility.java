package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * 我的学力
 * Created by Administrator on 2017/9/11.
 */

public class TotalStudyAbility implements Serializable {

    /**
     * todayStudyAbilityValue : 123123123
     * totalStudyAbilityValue : 12154545
     * examSubmitTimes : 10
     * homeworkSubmitTimes : 11
     * wrongQuestionReviewCount : 1
     * strengthExerciseCount : 12
     * exceedPercent : 0.555
     */

    private int todayStudyAbilityValue;
    private int    totalStudyAbilityValue;
    private int    examSubmitTimes;
    private int    homeworkSubmitTimes;
    private int    wrongQuestionReviewCount;
    private int    strengthExerciseCount;
    private float exceedPercent;
    private int startValue;
    private int endValue;

    private int reviseCount;


    public int getTodayStudyAbilityValue() {
        return todayStudyAbilityValue;
    }

    public void setTodayStudyAbilityValue(int todayStudyAbilityValue) {
        this.todayStudyAbilityValue = todayStudyAbilityValue;
    }

    public int getTotalStudyAbilityValue() {
        return totalStudyAbilityValue;
    }

    public void setTotalStudyAbilityValue(int totalStudyAbilityValue) {
        this.totalStudyAbilityValue = totalStudyAbilityValue;
    }

    public int getExamSubmitTimes() {
        return examSubmitTimes;
    }

    public void setExamSubmitTimes(int examSubmitTimes) {
        this.examSubmitTimes = examSubmitTimes;
    }

    public int getHomeworkSubmitTimes() {
        return homeworkSubmitTimes;
    }

    public void setHomeworkSubmitTimes(int homeworkSubmitTimes) {
        this.homeworkSubmitTimes = homeworkSubmitTimes;
    }

    public int getWrongQuestionReviewCount() {
        return wrongQuestionReviewCount;
    }

    public void setWrongQuestionReviewCount(int wrongQuestionReviewCount) {
        this.wrongQuestionReviewCount = wrongQuestionReviewCount;
    }

    public int getStrengthExerciseCount() {
        return strengthExerciseCount;
    }

    public void setStrengthExerciseCount(int strengthExerciseCount) {
        this.strengthExerciseCount = strengthExerciseCount;
    }

    public float getExceedPercent() {
        return exceedPercent;
    }

    public void setExceedPercent(float exceedPercent) {
        this.exceedPercent = exceedPercent;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

    public int getEndValue() {
        return endValue;
    }

    public void setEndValue(int endValue) {
        this.endValue = endValue;
    }

    public int getReviseCount() {
        return reviseCount;
    }

    public void setReviseCount(int reviseCount) {
        this.reviseCount = reviseCount;
    }
}
