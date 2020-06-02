package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;


import java.io.Serializable;
import java.util.List;

import static com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.KnowledgeUtils.getSubKnowledges;

/**
 * Created by Administrator on 2018/7/31.
 */

public class KnowlegeDiagnoseBean implements Serializable {

    private static final long serialVersionUID = -7028954345449916700L;
    private LevelKnowledgesBean knowledges;
    private LevelKnowledgesBean firstSubKnowledges;
    private LevelKnowledgesBean fourthSubKnowledges;
    private LevelKnowledgesBean secondSubKnowledges;
    private LevelKnowledgesBean thirdSubKnowledges;

    /*public float getAvgAccuracy() {
        return avgAccuracy;
    }

    public int getTotalKnowledgeCount() {
        return totalKnowledgeCount;
    }

    public int getTotalQuestionCount() {
        return totalQuestionCount;
    }

    public int getTotalWrongCount() {
        return totalWrongCount;
    }*/

    public LevelKnowledgesBean getKnowledges() {
        return knowledges;
    }

    public void setKnowledges(LevelKnowledgesBean knowledges) {
        this.knowledges = knowledges;
    }

    public LevelKnowledgesBean getFirstSubKnowledges() {
        return firstSubKnowledges;
    }

    public void setFirstSubKnowledges(LevelKnowledgesBean firstSubKnowledges) {
        this.firstSubKnowledges = firstSubKnowledges;
    }

    public LevelKnowledgesBean getFourthSubKnowledges() {
        return fourthSubKnowledges;
    }

    public void setFourthSubKnowledges(LevelKnowledgesBean fourthSubKnowledges) {
        this.fourthSubKnowledges = fourthSubKnowledges;
    }

    public LevelKnowledgesBean getSecondSubKnowledges() {
        return secondSubKnowledges;
    }

    public void setSecondSubKnowledges(LevelKnowledgesBean secondSubKnowledges) {
        this.secondSubKnowledges = secondSubKnowledges;
    }

    public LevelKnowledgesBean getThirdSubKnowledges() {
        return thirdSubKnowledges;
    }

    public void setThirdSubKnowledges(LevelKnowledgesBean thirdSubKnowledges) {
        this.thirdSubKnowledges = thirdSubKnowledges;
    }

    public List<SubKnowledgesBean> getFirstKnowledges() {
        return getSubKnowledges(firstSubKnowledges);
    }


    public List<SubKnowledgesBean> getSecondKnowledges() {
        return getSubKnowledges(secondSubKnowledges);

    }

    public List<SubKnowledgesBean> getThirdKnowledges() {
        return getSubKnowledges(thirdSubKnowledges);

    }

    public List<SubKnowledgesBean> getFourthKnowledges() {
        return getSubKnowledges(fourthSubKnowledges);
    }

    public int getFirstSize() {
        if (firstSubKnowledges != null) {
            return firstSubKnowledges.getTotalKnowledgeCount();
        }
        return 0;
    }

    public int getSecondSSize() {
        if (secondSubKnowledges != null) {
            return secondSubKnowledges.getTotalKnowledgeCount();
        }
        return 0;
    }

    public int getThirdSize() {
        if (thirdSubKnowledges != null) {
            return thirdSubKnowledges.getTotalKnowledgeCount();
        }
        return 0;
    }

    public int getFourthSize() {
        if (fourthSubKnowledges != null) {
            return fourthSubKnowledges.getTotalKnowledgeCount();
        }
        return 0;
    }

    public int getTotalSize() {
        return getFirstSize() + getSecondSSize() + getThirdSize() + getFourthSize();
    }




}
