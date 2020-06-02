package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * 意见反馈Dialog
 */
public class FeedbackDialog extends Dialog implements View.OnClickListener {


    private TextView  mTvDialogTitle;
    private Button    mBtnKnow;
    private ImageView mIvClose;

    public FeedbackDialog(Context context) {
        super(context);
        initData();
    }

    public FeedbackDialog(Context context, int theme) {
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
        mTvDialogTitle.setText(message);
        mBtnKnow.setText(poBtnName);
    }


    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_feedback);
        } else {
            setContentView(R.layout.dialog_feedback_phone);
        }
        mTvDialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        mBtnKnow = (Button) findViewById(R.id.btn_know);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mBtnKnow.setOnClickListener(this);
        mIvClose.setOnClickListener(this);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
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
