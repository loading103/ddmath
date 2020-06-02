package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteSuccessEvent;
import com.tsinghuabigdata.edu.ddmath.event.EditHeaderEvent;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;


/**
 *  套餐购买成功提示
 */
public class BuySuccessDialog extends Dialog implements View.OnClickListener {

    private ImageView levelImageView;
    private TextView tipsTextView;


    public BuySuccessDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_custom_closebtn: {
                dismiss();
                break;
            }
            case R.id.dialog_custom_rightbtn: {
                dismiss();
                break;
            }
            default:
                break;
        }
    }

    public void setData(int level, int count) {
        if(AppConst.MEMBER_SVIP == level ){
            levelImageView.setImageResource( R.drawable.svip_big );
            tipsTextView.setText( String.format(Locale.getDefault(),"恭喜你已成为超级VIP会员，可享%d项特权！",count));
        }else if(AppConst.MEMBER_VIP == level ){
            levelImageView.setImageResource( R.drawable.vip_big );
            tipsTextView.setText( String.format(Locale.getDefault(),"恭喜你已成为VIP会员，可享%d项特权！",count));
        }else{
            levelImageView.setImageResource( R.drawable.normal_set );
            tipsTextView.setText( "可享受个性化提分服务！" );
        }

//        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
//        if( detailinfo!=null && level > detailinfo.getVipLevel() ){
//            detailinfo.setVipLevel( level );
//            AccountUtils.setUserdetailInfo( detailinfo );
//        }
        EventBus.getDefault().post( new BuySuiteSuccessEvent() );
        EventBus.getDefault().post( new EditHeaderEvent() );
    }

    //--------------------------------------------------------------------------
    private void initData() {

        setContentView(GlobalData.isPad()?R.layout.dialog_buysuccess_layout:R.layout.dialog_buysuccess_layout_phone);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        ImageView closeView = findViewById(R.id.dialog_custom_closebtn);
        closeView.setOnClickListener(this);

        Button enterBtn = findViewById(R.id.dialog_custom_rightbtn);
        enterBtn.setOnClickListener(this);

        levelImageView = findViewById(R.id.iv_membergrade);
        tipsTextView = findViewById(R.id.tv_dialog_tipstext);
    }
}
