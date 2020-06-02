package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 原版教辅答案区域 答题区 辅助图去
 */

public class AreaInfoBean implements Serializable{
    private static final long serialVersionUID = -4955124985836809628L;

    private ArrayList<AnswerAreaBean> answerAreas;  //答题区   多个
    private ArrayList<AnswerAreaBean> figureAreas;  //辅助图区 多个

    public ArrayList<AnswerAreaBean> getAnswerAreas() {
        return answerAreas;
    }

    public void setAnswerAreas(ArrayList<AnswerAreaBean> answerAreas) {
        this.answerAreas = answerAreas;
    }

    public ArrayList<AnswerAreaBean> getFigureAreas() {
        return figureAreas;
    }

    public void setFigureAreas(ArrayList<AnswerAreaBean> figureAreas) {
        this.figureAreas = figureAreas;
    }
}
