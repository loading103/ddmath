package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 变式训练
 * Created by Administrator on 2018/3/20.
 */

public class RefreshVariantEvent {
    private String contentId;
    public RefreshVariantEvent(){}
    public RefreshVariantEvent(String contentId){
        this.contentId = contentId;
    }
    public String getContentId() {
        return contentId;
    }
}
