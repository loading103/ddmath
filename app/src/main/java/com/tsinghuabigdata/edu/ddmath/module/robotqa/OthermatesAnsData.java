package com.tsinghuabigdata.edu.ddmath.module.robotqa;


import com.tsinghuabigdata.edu.ddmath.bean.QuestionAns;

import java.util.List;

public class OthermatesAnsData {

    private int displayIndex;

    private List<QuestionAns> questionAnses;

    public OthermatesAnsData(List<QuestionAns> topicList) {
        questionAnses = topicList;
    }

    public int getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(int displayIndex) {
        this.displayIndex = displayIndex;
    }

    public QuestionAns getDisplayAns() {
        return questionAnses.get(displayIndex);
    }

    public void nextDisplay() {
        this.displayIndex++;
        if (this.displayIndex == questionAnses.size()) {
            this.displayIndex = 0;
        }
    }
}
