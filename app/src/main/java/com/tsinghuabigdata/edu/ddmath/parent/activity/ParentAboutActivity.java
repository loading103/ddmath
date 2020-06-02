package com.tsinghuabigdata.edu.ddmath.parent.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.parent.view.ParentToolbar;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Locale;

/**
 * 家长端--关于界面
 */

public class ParentAboutActivity extends RoboActivity implements View.OnClickListener {

    @ViewInject(R.id.worktoolbar)
    private ParentToolbar workToolbar;

    @ViewInject(R.id.iv_about_version)
    private TextView versionView;
    @ViewInject(R.id.iv_email_adress)
    private TextView mIvEmail;
    @ViewInject(R.id.iv_phone_number )
    private TextView  mIvPhoneNumber;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent_about);
        x.view().inject( this );
        mContext = this;

        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_email_adress:
                AppUtils.startEmail( mContext );
                break;
            case R.id.iv_phone_number:
                AppUtils.dial( mContext );
                break;
            default:
                break;
        }
    }

    @Override
    public String getUmEventName(){
        return "parent_mycenter_about";
    }
    //----------------------------------------------------------------------------------------
    private void initView(){

        workToolbar.setTitle( "关于我们" );
        workToolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String versionName = "14.0.0";
        PackageInfo packageInfo = AppUtils.getPackageInfo(mContext);
        if( packageInfo!=null ) versionName = packageInfo.versionName;

        versionView.setText( String.format( Locale.getDefault(), "v%s", versionName)  );
        mIvEmail.setOnClickListener( this );
        mIvPhoneNumber.setOnClickListener( this );
    }

}
