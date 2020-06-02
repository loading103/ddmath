package com.tsinghuabigdata.edu.ddmath.module.mylearn.bean;

import java.io.Serializable;

/**
 * 我的作业 作业图片
 */
public class WorkImageBean implements Serializable {
    private static final long serialVersionUID = -6624617064230747347L;

//    imageId	String	ID
//    createTime	Date	创建时间
//    path	String	图片路径
//    checkStatus	Int	批阅状态，参见数据库

    private String imageId;
    private String createTime;
    private String path;
    private int checkStatus;
    private String checkResult;

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

}
