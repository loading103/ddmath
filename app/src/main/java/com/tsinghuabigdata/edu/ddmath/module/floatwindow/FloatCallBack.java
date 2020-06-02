package com.tsinghuabigdata.edu.ddmath.module.floatwindow;


import com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean.BubbleBean;

/**
 * Author:xishuang
 * Date:2017.08.01
 * Des:暴露一些与悬浮窗交互的接口
 */
public interface FloatCallBack {

    //void guideUser(int type);

    void show();

    void hide();

    //增加动态气泡
    void addBubble( BubbleBean bean );

    //删除动态气泡
    //void removeBubble( String data );

    //设置当前显示界面
    void setCurrUiName(String name);

}
