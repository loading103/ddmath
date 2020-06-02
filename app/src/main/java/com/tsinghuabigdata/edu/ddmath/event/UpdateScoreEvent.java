package com.tsinghuabigdata.edu.ddmath.event;

/**
 * 刷新积分，需要请求网络
 * Created by Administrator on 2018/3/20.
 */

public class UpdateScoreEvent {
    private boolean bAdd = false;       //false,减少积分, true: 添加积分
    public UpdateScoreEvent( boolean add ){
        this.bAdd = add;
    }

    public boolean isAdd() {
        return bAdd;
    }
}
