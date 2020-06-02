package com.tsinghuabigdata.edu.ddmath.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


public class UnLoginView extends LinearLayout implements View.OnClickListener {

    private Button      mBtnLogin;
    private TextView     mBtnRegister;

    public UnLoginView(Context context) {
        super(context);

        init();
    }

    public UnLoginView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public UnLoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    //-----------------------------------------------------------
    private void init(){
        inflate( getContext(), GlobalData.isPad()?R.layout.view_unlogin:R.layout.view_unlogin_phone, this);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                //goActivity(RegisterActivity.class);
                ToastUtils.showLong( getContext(), getResources().getString(R.string.regisiter_tips));
                break;
            case R.id.btn_login:
                goActivity(LoginActivity.class);
                break;
            default:
                break;
        }
    }

    private void goActivity(Class clazz) {
        Context context = getContext();
        if( context instanceof Activity ){
            Activity activity = (Activity)context;
            Intent intent = new Intent( getContext(), clazz);
            activity.startActivity(intent);
        }
    }
}
