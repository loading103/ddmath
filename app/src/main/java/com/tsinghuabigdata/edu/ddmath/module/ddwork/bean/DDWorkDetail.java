package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 豆豆作业详情
 */
public class DDWorkDetail implements Serializable {
    private static final long serialVersionUID = -618204373519946298L;

    //作业状态 空数据(-1),未开始(0),待上传(1),待批阅(2),批阅中(3),已批阅(4);

    public static final int WORK_NODATA   = -1;
    private static final int WORK_NOSTART  = 0;
    public static final int WORK_UNSUBMIT = 1;      //未提交
    public static final int WORK_WAITCORRECT = 2;   //待批阅
    public static final int WORK_CORRECTING = 3;    //批阅中
    public static final int WORK_CORRECTED  = 4;    //已批阅
    public static final int WORK_STATED  = 5;       //已统计可看报告

    //作业是否购买
    public static final int ST_NOBUY = 0;
    public static final int ST_BUYED = 1;

    private String examId;
    private String workName;
    private int pageCount;
    private int exerStatus;
    private int wrongQuestionCount;
    private int rightQuestionCount;
    private String lastReviewer;
    private int revoked;
    private boolean overdue;
    private String parentExamId;

    private int allowLookAnswer;      //允许学生在提交作业后查看答案，0：允许 1：不允许
    private String sourceType;            //作业类型

    //周练得分与总分
    private float questionScore;        //总分
    private float studentScore;         //得分

    private int usedCount;           //错题订正使用人次

    private int usePrivilege;       //  0：免费，1：付费

    //原版教辅 使用的参数
    private String bookId;
    private String bookName;

    private ArrayList<EKPiontBean> topKnowledgeList;

    private ArrayList<LocalPageInfo> pageInfo;
    private ArrayList<LocalPageInfo> pages;

    //老师布置
    public final static int LMTPYE_DINGZHI = 1;
    private final static int LMTPYE_ORIGIN  = 2;
    private int customType;        //1:订制教辅， 2:原版教辅
    
    private int uploadType;                    //上传方式，默认值0拍照上传，1为扫描上传，2，批阅结果上传
    private int subUploadType;              //拍照上传时，没有极速批阅上传的特权的学生上传方式，1：老师纸上批阅，0，老师网阅
    private String paperSize;             //A3,A4
    private float widthHeightRate;       //原版教辅的宽高比例
    
    //分享连击 效果使用
    private int submitRank;         //提交排名
    private double accuracy;           //正确率 or 得分率
    private int classRank;          //班级排名
    private String shareBaseImageUrl;        //分享背景Url
    private long createTime;

    private String creatorId;        //作业布置的人ID
    //-------------------------------------------------------------------------------------
    public void init(){
        exerStatus = WORK_NOSTART;
        if( pageInfo != null){
            for( LocalPageInfo page : pageInfo ){
                page.init();
            }
        }
        if( pages != null){
            for( LocalPageInfo page : pages ){
                page.init();
            }
        }
    }

    //------------------------------------------------------------
    public ArrayList<EKPiontBean> getTopKnowledgeList() {
        return topKnowledgeList;
    }

    public int getExerStatus() {
        return exerStatus;
    }

    public int getWrongQuestionCount() {
        return wrongQuestionCount;
    }

    public int getRightQuestionCount() {
        return rightQuestionCount;
    }

    public ArrayList<LocalPageInfo> getPageInfo() {
        if( pageInfo!=null ) return pageInfo;
        else return pages;
    }

    public String getWorkId() {
        return examId;
    }

    public void setWorkId(String workId) {
        if(TextUtils.isEmpty(examId))
            this.examId = workId;
    }

    public int getIsRevoked() {
        return revoked;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public int getAllowLookAnswer() {
        return allowLookAnswer;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String exerType) {
        this.sourceType = exerType;
    }

    public String getParentExamId() {
        return parentExamId;
    }

    public float getQuestionScore() {
        return questionScore;
    }

    public float getStudentScore() {
        return studentScore;
    }

    public void setStudentScore(float studentScore) {
        this.studentScore = studentScore;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public boolean isOrginLearnMaterail() {
        return customType == LMTPYE_ORIGIN;
    }
    public int getCustomType(){
        return customType;
    }

    public void setCustomType(int type) {
        this.customType = type;
    }

    public int getUploadType() {
        return uploadType;
    }

    public void setUploadType(int uploadType) {
        this.uploadType = uploadType;
    }

    public int getSubmitRank() {
        return submitRank;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getClassRank() {
        return classRank;
    }

    public String getShareUrl() {
        return shareBaseImageUrl;
    }

    public float getWidthHeightRate() {
        return widthHeightRate;
    }

    public int getUsePrivilege() {
        return usePrivilege;
    }

    public void setUsePrivilege(int usePrivilege) {
        this.usePrivilege = usePrivilege;
    }

    public long getCreateTime(){
        return createTime;
    }

    public String getPaperSize() {
        return paperSize;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public int getSubUploadType() {
        return subUploadType;
    }
}
