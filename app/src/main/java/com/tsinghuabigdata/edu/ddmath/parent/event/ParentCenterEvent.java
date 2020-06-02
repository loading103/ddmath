package com.tsinghuabigdata.edu.ddmath.parent.event;

/**
 * 家长端更换头像事件
 */

public class ParentCenterEvent {

    public static final int TYPE_UPDATE_INFO    = 1;        //更新用户信息
    public static final int TYPE_MSG_COUNT      = 2;        //设置未读消息数量
    public static final int TYPE_MSG_DEC        = 3;        //减少一个未读消息
    public static final int TYPE_MSG_ADD        = 4;        //增加一个未读消息

    private int type;               //事件类型
    private int unReadCount;        //未读消息数量

    public ParentCenterEvent( int type ){
        this.type = type;
    }

    public ParentCenterEvent( int type, int count ){
        this.type = type;
        this.unReadCount = count;
    }

    public int getType() {
        return type;
    }

    public int getUnReadCount() {
        return unReadCount;
    }
}
