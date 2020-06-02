package com.tsinghuabigdata.edu.ddmath.module.mylearn.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 我的作业 一份作业的数据消息
 */
public class WorkBean implements Serializable {

    public static final int ST_UNCORRECT = 1;   //:未批阅2：正在批阅3：已批阅
    //public static final int ST_CORRECTING = 2;   //:未批阅2：正在批阅3：已批阅
    public static final int ST_CORRECTED = 2;   //:未批阅2：正在批阅3：已批阅

    private static final long serialVersionUID = 8235898033455052378L;

//    recordId	String	ID
//    createTime	Date	创建时间(2017-12-12 10:12:30)
//    uploadTitle	String	上传标题
//    checkStatus	Int	批阅状态，参见数据库
//    rightCount		对
//    wrongCount		错
//    halfRightCount		半对
//    images	Object[]	上传图片列表
//    relationStatus     1:已关联0：未关联

    private String recordId;
    private String createTime;
    private String uploadTitle;
    private int checkStatus;
    private int rightCount;
    private int wrongCount;
    private int halfRightCount;
    private int symbolCount;
    private int relationCount;
    private ArrayList<WorkImageBean> images;

    private int questionCount;

    private String checkResult;     //对应的叛卷结果

    private int relationStatus;

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUploadTitle() {
        return uploadTitle;
    }

    public void setUploadTitle(String uploadTitle) {
        this.uploadTitle = uploadTitle;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
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

    public int getHalfRightCount() {
        return halfRightCount;
    }

    public void setHalfRightCount(int halfRightCount) {
        this.halfRightCount = halfRightCount;
    }

    public ArrayList<WorkImageBean> getImages() {
        return images;
    }

    public void setImages(ArrayList<WorkImageBean> images) {
        this.images = images;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getSymbolCount() {
        return symbolCount;
    }

    public void setSymbolCount(int symbolCount) {
        this.symbolCount = symbolCount;
    }

    public ArrayList<String> getImagesPath(String domain){
        ArrayList<String> arrayList = new ArrayList<String>();
        for( WorkImageBean image : images ){
            arrayList.add(domain+image.getPath());
        }
        return arrayList;
    }

    public  String getCorrectList(){
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for( WorkImageBean image : images ){
            if( !first ) sb.append("#");
            else first = false;
            sb.append( image.getCheckResult() );
        }
        return sb.toString();
    }

    public int getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(int relationStatus) {
        this.relationStatus = relationStatus;
    }

    public int getRelationCount() {
        return relationCount;
    }

    public void setRelationCount(int relationCount) {
        this.relationCount = relationCount;
    }
}
