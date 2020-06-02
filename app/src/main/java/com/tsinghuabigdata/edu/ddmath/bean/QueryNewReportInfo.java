package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by 28205 on 2017/6/13.
 */
public class QueryNewReportInfo implements Serializable {
    private static final long serialVersionUID = 1960130906157895376L;
    /**
     * newExamReport : false
     * newExerhomeReport : false
     * newIntegratedReport : false
     * newKnowledgeReport : false
     * newReport : true
     * newWeekExamReport : true
     */

    private boolean newExamReport;              //考试
    private boolean newExerhomeReport;          //作业
    private boolean newIntegratedReport;        //全部
    private boolean newKnowledgeReport;         //知识图谱
    private boolean newReport;
    private boolean newWeekExamReport;          //周练

    //private boolean newReviseReport;            //错题订正
    private boolean newWeekExerciseReport;      //周提炼
    private boolean newExclusiveReport;         //专属
    private boolean newWeekReport;              //周学习分析

    public boolean isNewExamReport() {
        return newExamReport;
    }

    public void setNewExamReport(boolean newExamReport) {
        this.newExamReport = newExamReport;
    }

    public boolean isNewExerhomeReport() {
        return newExerhomeReport;
    }

    public void setNewExerhomeReport(boolean newExerhomeReport) {
        this.newExerhomeReport = newExerhomeReport;
    }

    public boolean isNewIntegratedReport() {
        return newIntegratedReport;
    }

    public void setNewIntegratedReport(boolean newIntegratedReport) {
        this.newIntegratedReport = newIntegratedReport;
    }

    public boolean isNewKnowledgeReport() {
        return newKnowledgeReport;
    }

    public void setNewKnowledgeReport(boolean newKnowledgeReport) {
        this.newKnowledgeReport = newKnowledgeReport;
    }

    public boolean isNewReport() {
        return newReport;
    }

    public void setNewReport(boolean newReport) {
        this.newReport = newReport;
    }

    public boolean isNewWeekExamReport() {
        return newWeekExamReport;
    }

    public void setNewWeekExamReport(boolean newWeekExamReport) {
        this.newWeekExamReport = newWeekExamReport;
    }

//    public boolean isNewReviseReport() {
//        return newReviseReport;
//    }
//
//    public void setNewReviseReport(boolean newReviseReport) {
//        this.newReviseReport = newReviseReport;
//    }

    public boolean isNewWeekExerciseReport() {
        return newWeekExerciseReport;
    }

    public void setNewWeekExerciseReport(boolean newWeekExerciseReport) {
        this.newWeekExerciseReport = newWeekExerciseReport;
    }

    public boolean isNewExclusiveReport() {
        return newExclusiveReport;
    }

    public void setNewExclusiveReport(boolean newExclusiveReport) {
        this.newExclusiveReport = newExclusiveReport;
    }

    public boolean isNewWeekReport() {
        return newWeekReport;
    }

    public void setNewWeekReport(boolean newWeekReport) {
        this.newWeekReport = newWeekReport;
    }
}
