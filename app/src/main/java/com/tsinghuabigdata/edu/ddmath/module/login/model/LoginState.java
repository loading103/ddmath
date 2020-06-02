package com.tsinghuabigdata.edu.ddmath.module.login.model;

/**
 * Created by 28205 on 2017/4/7.
 */
public class LoginState {
    private boolean login = false;

    private LoginState() {

    }

    private static class InstanceHolder {

        private static final LoginState instance = new LoginState();
    }

    public static LoginState getInstance() {
        return InstanceHolder.instance;
    }

    private LoginChangeListener listener;

    public void setListener(LoginChangeListener listener) {
        this.listener = listener;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void setLoginListener(boolean login) {
        this.login = login;
        if (listener != null) {
            listener.onChange(login);
        }
    }



    public interface LoginChangeListener {
        void onChange(boolean isLogin);
    }
}
