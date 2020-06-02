package com.tsinghuabigdata.edu.ddmath.module.message;

import android.os.SystemClock;

import com.tsinghuabigdata.edu.ddmath.bean.LocalMessage;

import java.util.Observable;

/**
 * 消息观察者
 * Created by Administrator on 2017/9/5.
 */

public class MessageObservable extends Observable {

    public void sendMessage(LocalMessage localMessage) {
        setChanged();
        notifyObservers(localMessage);
    }

    public void sendMessage(final LocalMessage localMessage, final int delaytime ) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                int time = delaytime;
                while( time>0 ){
                    SystemClock.sleep( 50 );
                    time -= 50;
                }
                setChanged();
                notifyObservers(localMessage);
            }
        }).start();
    }
}
