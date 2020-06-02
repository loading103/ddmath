package com.tsinghuabigdata.edu.ddmath.module.message;


import com.tsinghuabigdata.edu.ddmath.bean.LocalMessage;

import java.util.Observer;

/**
 * 本地消息控制器
 * Created by Administrator on 2017/9/6.
 */

public class MessageController {

    private MessageObservable mObservable;

    private MessageController() {
        mObservable = new MessageObservable();
    }

    private static class InstanceHolder {

        private static final MessageController instance = new MessageController();
    }

    public static MessageController getInstance() {
        return InstanceHolder.instance;
    }

    public void sendMessage(LocalMessage localMessage) {
        mObservable.sendMessage(localMessage);
    }
    public void sendMessageDelay(LocalMessage localMessage, int time) {
        mObservable.sendMessage(localMessage, time);
    }

    public void addObserver(Observer observer) {
        mObservable.addObserver(observer);
    }

    public void deleteObserver(Observer observer) {
        mObservable.deleteObserver(observer);
    }

    public void deleteAllObservers() {
        mObservable.deleteObservers();
    }


}
