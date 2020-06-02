/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.bean;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;

public class IsExistNewMsgResult implements Serializable{
    private static final long serialVersionUID = -1153744785040160817L;

    /**
     *  {"zxtj":false,"wdxx":false}
     */
    /**
     * 信息
     */
    private boolean zxtj;

    private boolean wdxx;

    public boolean isZxtj() {
        return zxtj;
    }

    public void setZxtj(boolean zxtj) {
        this.zxtj = zxtj;
    }

    public boolean isWdxx() {
        return wdxx;
    }

    public void setWdxx(boolean wdxx) {
        this.wdxx = wdxx;
    }

    public static TypeToken<IsExistNewMsgResult> getIsExistNewMsgTypeToken() {
        return new TypeToken<IsExistNewMsgResult>() {
        };
    }
}
