package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.bean.ExamNameBean;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.QuestionInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/9.
 */

public class QuestionVo extends QuestionInfo implements Serializable {


    /**
     * questionId : 0008FA6E6B3C40CAA394781E17E529C3
     * questionType : choice
     * stemg : 下列各直线的表示法中,正确的是(   )
     * stemGraph : group2/M00/00/01/ChoYplguleyIZzPcAAAmnere260AAAAggAFyvsAACa1190.jpg
     * subStemg : 下列各勾股定理的表示法中,正确的是(   )
     * subStemGraph : group2/M00/00/01/ChoYplguleyIZzPcAAAmnere260AAAAggAFyvsAAC1230.jpg
     * stemLatexGraph : group2/M00/00/01/ChoYplguleyIZzPcAAAmnere260AAAAggAFyvsAAC1230.jpg
     * subStemLatexGraph : group2/M00/00/01/ChoYplguleyIZzPcAAAmnere260AAAAggAFyvsAAC1230.jpg
     * difficult : 0.5
     * difficultLevel : 2
     * totalCount : 10
     * wrongCount : 5
     * stdAnswers : [{"wayOfNum":1,"contentg":"aaaaa","graph":"aaaaa","contentLatextGraph":""}]
     */

    private List<ExamNameBean>  examNames;
    //private float               difficult;
    //private int                 difficultLevel;
    private int                 totalCount;
    private int                 wrongCount;

    private boolean show; //控制展开、收起
    private int isNowCorrect;

    public String getSubStemLatexGraph() {
        return subStemLatexGraph;
    }

    public void setSubStemLatexGraph(String subStemLatexGraph) {
        this.subStemLatexGraph = subStemLatexGraph;
    }

    public List<ExamNameBean> getExamNames() {
        return examNames;
    }

    public void setExamNames(List<ExamNameBean> examNames) {
        this.examNames = examNames;
    }

//    public float getDifficult() {
//        return difficult;
//    }
//
//    public void setDifficult(float difficult) {
//        this.difficult = difficult;
//    }
//
//    public int getDifficultLevel() {
//        return difficultLevel;
//    }
//
//    public void setDifficultLevel(int difficultLevel) {
//        this.difficultLevel = difficultLevel;
//    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public int getIsNowCorrect() {
        return isNowCorrect;
    }

    public void setIsNowCorrect(int isNowCorrect) {
        this.isNowCorrect = isNowCorrect;
    }
}
