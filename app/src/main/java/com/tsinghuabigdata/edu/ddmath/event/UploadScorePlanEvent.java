package com.tsinghuabigdata.edu.ddmath.event;


/**
 * 上传学校作业
 * Created by Administrator on 2018/3/19.
 */

public class UploadScorePlanEvent {
    private String contentId;

    public UploadScorePlanEvent(String contentId) {
        this.contentId = contentId;
    }
}
