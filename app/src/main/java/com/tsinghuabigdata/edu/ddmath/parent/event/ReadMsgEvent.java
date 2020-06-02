package com.tsinghuabigdata.edu.ddmath.parent.event;

/**
 *
 */

public class ReadMsgEvent {

    private String msgId;               //事件类型

    public ReadMsgEvent(String msgId){
        this.msgId = msgId;
    }

    public String getMsgId() {
        return msgId;
    }

}
