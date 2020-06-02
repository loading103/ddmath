package com.tsinghuabigdata.edu.ddmath.module.login.model;

import java.util.Observer;

/**
 * 登录控制器
 * Created by Administrator on 2017/9/6.
 */

public class LoginController {

    private LoginObservable mLoginObservable;

    private LoginController() {
        mLoginObservable = new LoginObservable();
    }

    private static class InstanceHolder {

        private static final LoginController instance = new LoginController();
    }

    public static LoginController getInstance() {
        return InstanceHolder.instance;
    }

    public void Login(boolean isLogin) {
        mLoginObservable.login(isLogin);
    }

    public void addObserver(Observer observer) {
        mLoginObservable.addObserver(observer);
    }

    public void deleteObservers(Observer observer) {
        mLoginObservable.deleteObserver(observer);
    }

    public void deleteAllObservers() {
        mLoginObservable.deleteObservers();
    }


}
