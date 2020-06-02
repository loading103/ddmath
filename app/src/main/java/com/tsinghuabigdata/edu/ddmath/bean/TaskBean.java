package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

public class TaskBean implements Serializable {
    private  String name;
    private  int questionCount;      //题目数量
    private  int unReviseCount; //未订正数量
    private  int revisedCount;   //订正数量
    private  int revisedRightCount;//订正正确题目数
    private  int revisedWrongCount;//订正错误题目数
    private  String sourceType;     //dd_period_review（错题浏览本） dd_week_exercise（错题再练本） exclusive_paper （变式训练） videocourse（同步微课）dd_revise(错题订正)
    private  int overdue;            //是否逾期0 未逾期 1逾期
    private  int exerStatus;         //作业状态   0：未提交，1：已提交，4:已批阅，5:已统计可看报告
    private  String examId;
    private  long createTime;
    private  String title;
    private  int status;             //完成状态 1 已完成 2 已订正
    private  String contentId;       //   recordId /exclusiveId/
    private  Boolean hasPrivilege;  //true有使用权 false没有使用权
    private  long submitTime;        //提交时间
    private  int downloadStatus;        //0 未下载 1已下载
    private  int reviseStatus;        //0 未订正 1已订正
    private  int deadDays;        //1需要今日完成 2需要2天完成 3需要三天完成
    private int hasWatch;       //
    private boolean hasDownload; // 是否有一建下载 true 有一建下载 ，false没有一建下载
    private String factory;       //第三方公司名称
    private String  productId;
    private List<WeekDatailBean> taskList;
    private Boolean hasNewReport;       //周提分方案是否有更新
    private String suiteNumber;          //已包含第几部分套题，逗号隔开
    private String updateSuiteNumber;   //更新了几部分套题，逗号隔开

    private int periodReviewTotalPrivilegeTimes;  //错题浏览本剩余使用次数

    private int  weekType   ; //是不是周提分方案1周   2错题  3微课  4自定义(自加)
    private Boolean isVisible;    //左边的图标是否显示（自加）
    private Boolean isUpLineVisible;    //左边的虚线是否显示（自加）
    private Boolean isDowmLineVisible;    //左边的虚线是否显示（自加）

    public int getHasWatch() {
        return hasWatch;
    }

    public int getPeriodReviewTotalPrivilegeTimes() {
        return periodReviewTotalPrivilegeTimes;
    }

    public void setPeriodReviewTotalPrivilegeTimes(int periodReviewTotalPrivilegeTimes) {
        this.periodReviewTotalPrivilegeTimes = periodReviewTotalPrivilegeTimes;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getUnReviseCount() {
        return unReviseCount;
    }

    public void setUnReviseCount(int unReviseCount) {
        this.unReviseCount = unReviseCount;
    }

    public int getRevisedCount() {
        return revisedCount;
    }

    public void setRevisedCount(int revisedCount) {
        this.revisedCount = revisedCount;
    }

    public int getRevisedRightCount() {
        return revisedRightCount;
    }

    public void setRevisedRightCount(int revisedRightCount) {
        this.revisedRightCount = revisedRightCount;
    }

    public int getRevisedWrongCount() {
        return revisedWrongCount;
    }

    public void setRevisedWrongCount(int revisedWrongCount) {
        this.revisedWrongCount = revisedWrongCount;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public int getOverdue() {
        return overdue;
    }

    public void setOverdue(int overdue) {
        this.overdue = overdue;
    }

    public int getExerStatus() {
        return exerStatus;
    }

    public void setExerStatus(int exerStatus) {
        this.exerStatus = exerStatus;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Boolean getHasPrivilege() {
        return hasPrivilege;
    }

    public void setHasPrivilege(Boolean hasPrivilege) {
        this.hasPrivilege = hasPrivilege;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long submitTime) {
        this.submitTime = submitTime;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getReviseStatus() {
        return reviseStatus;
    }

    public void setReviseStatus(int reviseStatus) {
        this.reviseStatus = reviseStatus;
    }

    public int getDeadDays() {
        return deadDays;
    }

    public void setDeadDays(int deadDays) {
        this.deadDays = deadDays;
    }

    public int isHasWatch() {
        return hasWatch;
    }

    public void setHasWatch(int hasWatch) {
        this.hasWatch = hasWatch;
    }

    public int getWeekType() {
        return weekType;
    }

    public void setWeekType(int weekType) {
        this.weekType = weekType;
    }

    public Boolean getUpLineVisible() {
        return isUpLineVisible;
    }

    public void setUpLineVisible(Boolean upLineVisible) {
        isUpLineVisible = upLineVisible;
    }

    public Boolean getDowmLineVisible() {
        return isDowmLineVisible;
    }

    public void setDowmLineVisible(Boolean dowmLineVisible) {
        isDowmLineVisible = dowmLineVisible;
    }


    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }
    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isHasDownload() {
        return hasDownload;
    }

    public void setHasDownload(boolean hasDownload) {
        this.hasDownload = hasDownload;
    }

    public List<WeekDatailBean> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<WeekDatailBean> taskList) {
        this.taskList = taskList;
    }

    public Boolean getHasNewReport() {
        return hasNewReport;
    }

    public void setHasNewReport(Boolean hasNewReport) {
        this.hasNewReport = hasNewReport;
    }

    public String getSuiteNumber() {
        return suiteNumber;
    }

    public void setSuiteNumber(String suiteNumber) {
        this.suiteNumber = suiteNumber;
    }

    public String getUpdateSuiteNumber() {
        return updateSuiteNumber;
    }

    public void setUpdateSuiteNumber(String updateSuiteNumber) {
        this.updateSuiteNumber = updateSuiteNumber;
    }

    public TaskBean(String name, int questionCount, int unReviseCount, int revisedCount, int revisedRightCount, int revisedWrongCount, String sourceType, int overdue,
                          int exerStatus, String examId, long createTime, String title,int status, String contentId, Boolean hasPrivilege, long submitTime,int deadDays,
                          boolean hasDownload,List<WeekDatailBean>data,boolean hasNewReport, String suiteNumber, String updateSuiteNumber,int periodReviewTotalPrivilegeTimes) {
        this.name = name;
        this.questionCount = questionCount;
        this.unReviseCount = unReviseCount;
        this.revisedCount = revisedCount;
        this.revisedRightCount = revisedRightCount;
        this.revisedWrongCount = revisedWrongCount;
        this.sourceType = sourceType;
        this.overdue = overdue;
        this.exerStatus = exerStatus;
        this.examId = examId;
        this.createTime = createTime;
        this.title = title;
        this.status=status;
        this.contentId = contentId;
        this.hasPrivilege = hasPrivilege;
        this.submitTime=submitTime;
        this.hasDownload = hasDownload;
        this.deadDays = deadDays;
        this.taskList=data;
        this.hasNewReport=hasNewReport;
        this.suiteNumber=suiteNumber;
        this.updateSuiteNumber=updateSuiteNumber;
        this.periodReviewTotalPrivilegeTimes=periodReviewTotalPrivilegeTimes;
    }
}
