package com.tsinghuabigdata.edu.ddmath.commons;

import android.os.Looper;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/18.
 * </p>
 *
 * 消息循环，运行在一个单独的子线程里面
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.commons
 * @createTime: 2015/11/18 11:09
 */
public class MessageLooper extends Thread {

    private Looper looper;

    @Override
    public void run() {
        Looper.prepare();
        looper = Looper.myLooper();
        Looper.loop();
    }

    public Looper getLooper() {
        return looper;
    }

    public void quit(){
        getLooper().quit();
    }

}
