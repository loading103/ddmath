package com.tsinghuabigdata.edu.ddmath.event;

import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;

/**
 * 布置新作业
 * Created by Administrator on 2018/3/19.
 */

public class AssignNewWorkEvent {

    private MessageInfo mMessageInfo;

    public AssignNewWorkEvent(MessageInfo messageInfo) {
        mMessageInfo = messageInfo;
    }

    public MessageInfo getMessageInfo() {
        return mMessageInfo;
    }

    public void setMessageInfo(MessageInfo messageInfo) {
        mMessageInfo = messageInfo;
    }

}
