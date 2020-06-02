package com.tsinghuabigdata.edu.ddmath.event;

import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;

/**
 * 批阅作业
 * Created by Administrator on 2018/3/19.
 */

public class CorrectWorkEvent {

    private MessageInfo mMessageInfo;

    public CorrectWorkEvent(MessageInfo messageInfo) {
        mMessageInfo = messageInfo;
    }

    public MessageInfo getMessageInfo() {
        return mMessageInfo;
    }

    public void setMessageInfo(MessageInfo messageInfo) {
        mMessageInfo = messageInfo;
    }
}
