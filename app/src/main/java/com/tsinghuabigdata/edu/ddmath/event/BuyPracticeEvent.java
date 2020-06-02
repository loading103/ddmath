package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 成功购买精品套题
 * Created by Administrator on 2018/5/2.
 */

public class BuyPracticeEvent {

    private String practiceId;

    public BuyPracticeEvent(){}
    public BuyPracticeEvent(String id){
        this.practiceId = id;
    }

    public String getPracticeId() {
        return practiceId;
    }
}
