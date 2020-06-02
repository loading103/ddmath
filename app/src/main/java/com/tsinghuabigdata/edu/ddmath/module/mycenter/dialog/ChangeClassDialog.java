package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 切换班级Dialog
 * Created by Administrator on 2018/1/15.
 */
@Deprecated
public class ChangeClassDialog extends Dialog implements View.OnClickListener {

    private ImageView           mIvClose;
    private TextView            mChangeTitle;
    private ImageView           mIvCancel;
    private ImageView           mIvSure;
    private int                 mClassPosition;
    private ChangeClassListener mChangeClassListener;


    public ChangeClassDialog(Context context) {
        super(context, R.style.dialog);
        initData();
    }

    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_change_class);
        } else {
            setContentView(R.layout.dialog_change_class_phone);
        }
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mChangeTitle = (TextView) findViewById(R.id.tv_change_title);
        mIvCancel = (ImageView) findViewById(R.id.iv_cancel);
        mIvSure = (ImageView) findViewById(R.id.iv_sure);
        mIvClose.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mIvSure.setOnClickListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_cancel:
                dismiss();
                break;
            case R.id.iv_sure:
                if (mChangeClassListener != null) {
                    mChangeClassListener.change(mClassPosition);
                }
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }

    public void setTips(String tips) {
        mChangeTitle.setText(tips);
    }

    public void setPostion(int position) {
        mClassPosition = position;
    }

    public void setChangeClassListener(ChangeClassListener changeClassListener) {
        mChangeClassListener = changeClassListener;
    }

    public interface ChangeClassListener {
        void change(int position);
    }
}
