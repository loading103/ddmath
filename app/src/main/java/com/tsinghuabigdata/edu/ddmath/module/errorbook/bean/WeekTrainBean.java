package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 错题周题练任务
 */

public class WeekTrainBean implements Serializable {

    private static final long serialVersionUID = -7970773658094600544L;

    public static final int ST_DETAIL       = 0;       //0, 查看详情，
    public static final int ST_WAITCAMERA   = 1;       //1，拍照上传
    public static final int ST_CORRECTING   = 2;       //2，批阅中
    public static final int ST_CORRECTED    = 3;       //3, 已批阅

    //显示时使用
    private transient int showtype = 0;     //0：显示Item, 非0：显示引导项

    private String recordId;            //套题编号
    private String examId;              //作业ID

    private int status;                 //套题状态：0，查看详情，1，拍照上传，2，批阅中，3，已批阅
    private long startDate;             //开始时间
    private long endDate;               //
    private long createTime;

    private String title;               //套题名

    //private int serial;                 //套题序号

    private String knowledgeName;       //知识点
    private String knowledgeId;         //知识点id

    //private transient double correctRate;          //正确率

    private String pdfUrl; 	     	    //为空，生成中

    private int questionCount;          //题目数

    private boolean hasPrivilege;       //是否已经有使用权

    private long limitTime;            //提交截止时间
    private int paperType;           //套题类型   1: 每周，2：自定义,3:每月
    private ArrayList<KnowledgePiontBean> knowlegeVos;

    private String privilegeId;         //对应的服务ID
    private float accuracy;     //正确率

    //套题单价
    private int chargeDdAmt;

    //private ArrayList<UserRankBean> studentInfo;        //学生列表

    public WeekTrainBean(){}
    public WeekTrainBean(int showtype){
        this.showtype = showtype;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }
//
//    public void setPdfUrl(String pdfUrl) {
//        this.pdfUrl = pdfUrl;
//    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public int getSerial() {
//        return serial;
//    }
//
//    public void setSerial(int serial) {
//        this.serial = serial;
//    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName;
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

//    public double getCorrectRate() {
//        return correctRate;
//    }
//
//    public void setCorrectRate(double correctRate) {
//        this.correctRate = correctRate;
//    }

//    public ArrayList<UserRankBean> getStudentInfo() {
//        return studentInfo;
//    }
//
//    public void setStudentInfo(ArrayList<UserRankBean> studentInfo) {
//        this.studentInfo = studentInfo;
//    }

    public boolean hasPrivilege() {
        return hasPrivilege;
    }

    public void setHasPrivilege(boolean hasPrivilege) {
        this.hasPrivilege = hasPrivilege;
    }

    public long getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(long limitTime) {
        this.limitTime = limitTime;
    }

    public int getPaperType() {
        return paperType;
    }

    public ArrayList<KnowledgePiontBean> getKnowlegeVos() {
        return knowlegeVos;
    }

//    public void setKnowlegeVos(ArrayList<KnowledgePiontBean> knowlegeVos) {
//        this.knowlegeVos = knowlegeVos;
//    }

    public int getChargeDdAmt() {
        return chargeDdAmt;
    }

    public String getPrivilegeId() {
        return privilegeId;
    }

//    public void setProductName(String productName) {
//        this.productName = productName;
//    }

    public float getAccuracy() {
        if( accuracy<0 ) return 0;
        return accuracy;
    }

    public long getCreateTime() {
        return createTime;
    }

    public int getShowtype() {
        return showtype;
    }
}
