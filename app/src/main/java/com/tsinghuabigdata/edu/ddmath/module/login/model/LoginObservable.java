package com.tsinghuabigdata.edu.ddmath.module.login.model;

import java.util.Observable;

/**
 * 登录观察者
 * Created by Administrator on 2017/9/5.
 */

public class LoginObservable extends Observable {

    public void login(boolean isLogin) {
        setChanged();
        notifyObservers(isLogin);
    }

}
