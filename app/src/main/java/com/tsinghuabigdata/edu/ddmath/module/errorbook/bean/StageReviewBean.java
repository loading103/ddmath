package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * 错题本下载 任务
 */
public class StageReviewBean implements Serializable {

    private static final long serialVersionUID = 3598073856835385314L;

    private long startDate;         //开始日期
    private long endDate;           //结束日期
    private int serialName;         //套题序号

    private int rightCount;         //已掌握
    private int wrongCount;         //未掌握

    private ArrayList<KnowPointCount> errorKnowledgeInfoList;  //知识点

    private String questionsId;        //记录ID
    private int downloadStatus;        //套题下载状态
    private String  from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    private long createTime;

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    private transient String title;
    //private transient int questionNum;          //题量

    private int paperType;          //1周，2自定义
    private String pdfUrl;          //pdf路径为空则在生中
    private int chargeDdAmt;        //商品单价
    private String privilegeId;
    private  String scoretitle;

    public String getScoretitle() {
        return scoretitle;
    }

    public void setScoretitle(String scoretitle) {
        this.scoretitle = scoretitle;
    }

    private boolean hasPrivilege;

    private transient int showType; //0:默认ITEM,!=0默认引导

    public StageReviewBean(){}
    public StageReviewBean(int type){
        showType = type;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getSerialName() {
        return serialName;
    }

    public void setSerialName(int serialName) {
        this.serialName = serialName;
    }

    public int getRightCount() {
        return rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public int getQuestionNum() {
        return rightCount + wrongCount;
    }

    public String getQuestionsId() {
        return questionsId;
    }

    public void setQuestionsId(String questionsId) {
        this.questionsId = questionsId;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public ArrayList<KnowPointCount> getErrorKnowledgeInfoList() {
        return errorKnowledgeInfoList;
    }

    public void setErrorKnowledgeInfoList(ArrayList<KnowPointCount> errorKnowledgeInfoList) {
        this.errorKnowledgeInfoList = errorKnowledgeInfoList;
    }

    public String getTitle(){
        if( title == null ){
            Date date = new Date();
            date.setTime( startDate );
            title = DateUtils.format( date, DateUtils.FORMAT_DATA );
            title += "至";
            date.setTime( endDate );
            title += DateUtils.format( date, DateUtils.FORMAT_DATA_MD );
            if( paperType == AppConst.PAPER_TYPE_WEEK ){
                title += "周";
            }
            title += "错题浏览本-套题"+serialName;
        }
        return title;
    }


    public boolean isHasPrivilege() {
        return hasPrivilege;
    }

    public void setHasPrivilege(boolean hasPrivilege) {
        this.hasPrivilege = hasPrivilege;
    }

    public int getPaperType() {
        return paperType;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public int getChargeDdAmt() {
        return chargeDdAmt;
    }

    public String getPrivilegeId(){
        return privilegeId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getShowType() {
        return showType;
    }
}
