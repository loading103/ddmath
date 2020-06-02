package com.tsinghuabigdata.edu.ddmath.module.entrance.bean;

/**
 * 上传任务状态
 */
public class UploadStatusBean {
    //
    public static final int ST_UNUPLOAD = 0;   //:未上传
    public static final int ST_UPLOADING = 1;   //:上传中
    public static final int ST_UPLOADED = 2;   //:上传成功
    public static final int ST_UPLOADFAIL = 3;   //:上传失败
    public static final int ST_UPLOADEDIT = 4;   //:编辑状态
    public static final int ST_UPLOADOLD  = 5;  //已上传

    private int uploadStatus;   //上传状态
    //private long uploadTime;    //上传时间
    private int viewStatus;

    public int getViewStatus() {
        return viewStatus;
    }

    public void setViewStatus(int viewStatus) {
        this.viewStatus = viewStatus;
    }

    //是否编辑状态
    private boolean editMode;

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
