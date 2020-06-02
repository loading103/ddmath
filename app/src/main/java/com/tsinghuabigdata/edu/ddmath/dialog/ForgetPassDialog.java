package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;


/**
 * 忘记密码 联系客服Dialog
 */
public class ForgetPassDialog extends Dialog implements View.OnClickListener {


    private TextView mTvTitle;
    private TextView mTvPhoneNumber;
    private TextView mTvCancel;

    private Context            mContext;
    private ForgetPassListener mForgetPassListener;

    public ForgetPassDialog(Context context) {
        super(context);
        mContext = context;
        initData();
    }

    public ForgetPassDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        initData();
    }


    private void initData() {
        setContentView(R.layout.dialog_forget_pass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvPhoneNumber = (TextView) findViewById(R.id.tv_phone_number);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvPhoneNumber.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int screenWidth = WindowUtils.getScreenWidth(mContext);
        lp.width = screenWidth;
        lp.gravity = Gravity.BOTTOM;
        lp.dimAmount = 0.3f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone_number:
                if (mForgetPassListener != null) {
                    mForgetPassListener.dial();
                }
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setForgetPassListener(ForgetPassListener forgetPassListener) {
        mForgetPassListener = forgetPassListener;
    }

    public interface ForgetPassListener {
        void dial();
    }

}
