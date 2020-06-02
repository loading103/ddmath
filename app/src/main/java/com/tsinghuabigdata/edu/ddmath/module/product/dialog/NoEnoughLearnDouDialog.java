package com.tsinghuabigdata.edu.ddmath.module.product.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.RechargeCashbackBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;


/**
 * 学豆不足界面
 */
public class NoEnoughLearnDouDialog extends Dialog implements View.OnClickListener {

    //提示
    private TextView textView;

    private TextView rechargeView;

    private ImageView rechargeBtn;

    private OnClickListener btnListener;

//    public NoEnoughLearnDouDialog(Context context) {
//        super(context);
//        initData();
//    }

    public NoEnoughLearnDouDialog(Context context, int theme) {
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
            case R.id.dialog_recharge_btn: {
                if (btnListener != null) btnListener.onClick(this, 0);
                dismiss();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 两个按钮
     * @param message          文本信息
     */
    public void setData(String message, int btnResId,  OnClickListener btnListener ) {

        textView.setText(message);

        RechargeCashbackBean detail = AccountUtils.getRechargeCashback();
        if( detail != null ){
            String rechargeText = String.format( Locale.getDefault(), "充%d元送%d学豆",  (int)detail.getRechargeMoney(), detail.getReturnDdAmt() );
            rechargeView.setText( rechargeText );
        }else{
            rechargeView.setVisibility( View.GONE );
        }

        this.btnListener = btnListener;

        rechargeBtn.setImageResource( btnResId );
    }

    //--------------------------------------------------------------------------
    private void initData() {

        setContentView(GlobalData.isPad()?R.layout.dialog_noenough_layout:R.layout.dialog_noenough_layout_phone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        ImageView closeView = (ImageView) findViewById(R.id.dialog_custom_closebtn);
        closeView.setOnClickListener(this);

        textView     = (TextView) findViewById(R.id.dialog_custom_text);
        rechargeBtn  = (ImageView) findViewById(R.id.dialog_recharge_btn);
        rechargeView = (TextView) findViewById(R.id.dialog_recharge_text);

        rechargeBtn.setOnClickListener(this);
    }

}
