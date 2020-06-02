package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.view.View;

import com.tsinghuabigdata.edu.ddmath.bean.ChatMessage;


/**
 * Created by 28205 on 2016/10/18.
 */
public interface RecycleViewItemListener {
    public void onRecycleViewItemClick(View v, ChatMessage chatmsg);
    public void onRecycleViewItemDoubleClick(View v, ChatMessage chatmsg);
}
