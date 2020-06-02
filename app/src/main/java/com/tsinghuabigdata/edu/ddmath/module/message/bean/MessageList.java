package com.tsinghuabigdata.edu.ddmath.module.message.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageList implements Serializable {

    private static final long serialVersionUID = 6736639961374161706L;

    private boolean success;
    private ArrayList<MessageInfo> message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<MessageInfo> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<MessageInfo> message) {
        this.message = message;
    }
}
