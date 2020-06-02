package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * 入学评测Dialog
 */
public class EntranceDialog extends Dialog implements View.OnClickListener {


    private RelativeLayout mLlEntranceReturn;
    private TextView       mTvTitle;
    private Button         mBtnKnow;
    private ImageView      mIvClose;
    private ImageView      mIvTitle;

    public EntranceDialog(Context context) {
        super(context);
        initData();
    }

    public EntranceDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }

    /**
     * 普通提示框
     *
     * @param message   文本信息
     * @param poBtnName 确认回调
     */
    public void setData(String message, String poBtnName) {
        mTvTitle.setText(message);
        mBtnKnow.setText(poBtnName);
    }

    public void setData(String message) {
        mTvTitle.setText(message);
    }

    public void setDrawble(int res) {
        mIvTitle.setImageResource(res);
    }

    private void initData() {
        if (GlobalData.isPad()){
            setContentView(R.layout.dialog_entrance);
        }else {
            setContentView(R.layout.dialog_entrance_phone);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        mLlEntranceReturn = (RelativeLayout) findViewById(R.id.ll_entrance_return);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mBtnKnow = (Button) findViewById(R.id.btn_know);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvTitle = (ImageView) findViewById(R.id.iv_title);
        mBtnKnow.setOnClickListener(this);
        mIvClose.setOnClickListener(this);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.3f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_know:
                dismiss();
                break;
            case R.id.iv_close:
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

}
