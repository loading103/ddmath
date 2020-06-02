package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 作业页面信息
 */
public class WorkPageInfo implements Serializable {

    private static final long serialVersionUID = 2579609326749017125L;

    //protected int typeIndex;              //第一个题目的题型是否显示  0: 没有处理，-1：不显示  > 0:显示
    protected int pageNum;                //当页页码
    /*protected*/ int submitStatus;           //提交状态 0:未提交 1：已提交
    protected ArrayList<LocalQuestionInfo>  questions;     //页内题目信息
    /*protected*/ String chapterName;           //章节名称

    protected String title;     //作业教辅名称
    private transient boolean showBookName;  //
    private transient boolean showChapterName;

    public void init(){
        submitStatus = 0;

        if( questions!=null ){
            for( LocalQuestionInfo questionInfo: questions ){
                questionInfo.init();
            }
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public ArrayList<LocalQuestionInfo> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<LocalQuestionInfo> questions) {
        this.questions = questions;
    }

    public int getSubmitStatus() {
        return submitStatus;
    }

//    public void setSubmitStatus(int submitStatus) {
//        this.submitStatus = submitStatus;
//    }

    public String getLearnMaterialName() {
        return title;
    }

    public void setLearnMaterialName(String learnMaterialName) {
        this.title = learnMaterialName;
    }

    public boolean isShowBookName() {
        return showBookName;
    }

    public void setShowBookName(boolean showTitle) {
        this.showBookName = showTitle;
    }

    public boolean isShowChapterName() {
        return showChapterName;
    }

    public void setShowChapterName(boolean showChapterName) {
        this.showChapterName = showChapterName;
    }

    public String getChapterName() {
        return chapterName;
    }

    /*public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }*/

}
