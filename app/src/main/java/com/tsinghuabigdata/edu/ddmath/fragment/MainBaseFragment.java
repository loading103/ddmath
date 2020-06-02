package com.tsinghuabigdata.edu.ddmath.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginController;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Administrator on 2017/9/6.
 */

public abstract class MainBaseFragment extends MyBaseFragment implements Observer {

    protected Handler mHandler = new Handler();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginController.getInstance().addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        final boolean isLogin = (boolean) data;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                login(isLogin);
            }
        });
    }

    protected abstract void login(boolean isLogin);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LoginController.getInstance().deleteObservers(this);
    }
}
