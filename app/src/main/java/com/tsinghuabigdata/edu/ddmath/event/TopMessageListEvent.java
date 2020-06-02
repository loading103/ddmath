package com.tsinghuabigdata.edu.ddmath.event;

import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;

import java.util.List;

/**
 * 首页置顶消息
 * Created by Administrator on 2018/3/19.
 */

public class TopMessageListEvent {

    private List<MessageInfo> bannerList;

    public TopMessageListEvent(List<MessageInfo> bannerList) {
        this.bannerList = bannerList;
    }

    public List<MessageInfo> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<MessageInfo> bannerList) {
        this.bannerList = bannerList;
    }
}
