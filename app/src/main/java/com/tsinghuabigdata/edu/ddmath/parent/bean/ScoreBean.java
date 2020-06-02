package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/2.
 */

public class ScoreBean implements Serializable {

    /**
     * avgScore : 0
     * firstFlag : false
     * increaseCorrect : 0
     * questionCount : 0
     * rank : 0
     * rightCount : 0
     * studentScore : 0
     * totalScore : 0
     */

    private int avgScore;
    private boolean firstFlag;
    private int     increaseCorrect;
    private int     questionCount;
    private int     rank;
    private int     rightCount;
    private int     studentScore;
    private int     totalScore;

    public int getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(int avgScore) {
        this.avgScore = avgScore;
    }

    public boolean isFirstFlag() {
        return firstFlag;
    }

    public void setFirstFlag(boolean firstFlag) {
        this.firstFlag = firstFlag;
    }

    public int getIncreaseCorrect() {
        return increaseCorrect;
    }

    public void setIncreaseCorrect(int increaseCorrect) {
        this.increaseCorrect = increaseCorrect;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public int getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(int studentScore) {
        this.studentScore = studentScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
