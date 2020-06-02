package com.tsinghuabigdata.edu.ddmath.util;

import android.os.Handler;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Administrator on 2018/3/20.
 */

public class EventBusUtils {


    public static void postDelay(Object event, Handler handler) {
        postDelay(event, handler, 2000);
    }

    public static void postDelay(final Object event, Handler handler, int time) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(event);
            }
        }, time);
    }
}
