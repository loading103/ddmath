package com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.fragment.MyBaseFragment;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;

/**
 * 关于
 * Created by Administrator on 2017/10/16.
 */

public class AboutFragment extends MyBaseFragment implements View.OnClickListener {

    private TextView  mIvEmailAdress;
    private ImageView mIvPhone;
    private TextView  mIvPhoneNumber;
    private TextView  mAppVersion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        if (GlobalData.isPad()) {
            root = inflater.inflate(R.layout.fragment_about, container, false);
        } else {
            root = inflater.inflate(R.layout.fragment_about_phone, container, false);
        }
        initView(root);
        setPrepared();
        initData();
        return root;
    }

    public String getUmEventName() {
        return "mycenter_about";
    }
    private void initView(View root) {
        mIvEmailAdress = (TextView) root.findViewById(R.id.iv_email_adress);
        mIvPhone = (ImageView) root.findViewById(R.id.iv_phone);
        mIvPhoneNumber = (TextView) root.findViewById(R.id.iv_phone_number);
        mIvEmailAdress.setOnClickListener(this);
        mIvPhone.setOnClickListener(this);
        mIvPhoneNumber.setOnClickListener(this);

        mAppVersion = (TextView)root.findViewById( R.id.iv_about_version );
        mAppVersion.setText( String.format(Locale.getDefault(),"版本：%s v%s",AppUtils.getAppName(),AppUtils.getPackageInfo(getContext()).versionName) );
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_email_adress:
                AppUtils.startEmail( getContext() );
                break;
            case R.id.iv_phone:
            case R.id.iv_phone_number:
                AppUtils.dial( getContext() );
                break;
            default:
                break;
        }
    }

}
