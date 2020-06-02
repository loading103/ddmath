/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.util;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by yanshen on 2016/3/24.
 */
public class AsyncTaskCancel {

    /**
     * 取消请求
     * @param task
     */
    public static void cancel(AsyncTask task) {
        if (task != null && !task.isCancelled()) {
            task.cancel(true);
        }
    }

    /**
     * 取消未完成的请求队列
     */
    public static void cancel(List<AsyncTask> tasks){
        for (AsyncTask task : tasks){
            cancel(task);
        }
    }
}
