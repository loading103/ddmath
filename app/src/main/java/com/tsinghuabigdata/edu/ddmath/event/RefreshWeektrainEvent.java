package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 刷新错题周提炼
 * Created by Administrator on 2018/3/20.
 */

public class RefreshWeektrainEvent {
    private String contentId;
    public RefreshWeektrainEvent(){}

    public RefreshWeektrainEvent( String contentId){
        this.contentId = contentId;
    }

    public String getContentId() {
        return contentId;
    }
}
