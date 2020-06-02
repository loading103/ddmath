package com.tsinghuabigdata.edu.ddmath.module.message.bean;

import java.io.IOException;
import java.io.Serializable;


public class MessageRecord implements Serializable {
    private static final long serialVersionUID = -6126489996626563757L;

    public static final String TYPE_ZXRECOMMAND = "准星推荐";
    public static final String TYPE_MYMSG = "我的消息";
    private String type;

    private int zxMsgNum;
    private int myMsgNum;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getZxMsgNum() {
        return zxMsgNum;
    }

    public void setZxMsgNum(int zxMsgNum) {
        this.zxMsgNum = zxMsgNum;
    }

    public int getMyMsgNum() {
        return myMsgNum;
    }

    public void setMyMsgNum(int myMsgNum) {
        this.myMsgNum = myMsgNum;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(type);
        out.writeObject(zxMsgNum);
        out.writeObject(myMsgNum);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.type = (String) in.readObject();
        this.zxMsgNum = (int) in.readObject();
        this.myMsgNum = (int) in.readObject();
    }
}
