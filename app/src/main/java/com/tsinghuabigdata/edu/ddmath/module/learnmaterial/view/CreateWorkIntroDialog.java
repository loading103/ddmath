package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 创建一次作业--使用说明Dialog
 * Created by Administrator on 2018/1/15.
 */

public class CreateWorkIntroDialog extends Dialog implements View.OnClickListener{

    private ImageView mIvClose;
    private ImageView mIvKnow;

    public CreateWorkIntroDialog(Context context) {
        super(context, R.style.dialog);
        initData();
    }



    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_create_work_intro);
        } else {
            setContentView(R.layout.dialog_create_work_intro_phone);
        }
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvKnow = (ImageView) findViewById(R.id.iv_know);
        mIvClose.setOnClickListener(this);
        mIvKnow.setOnClickListener(this);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

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
            case R.id.iv_know:
                dismiss();
                break;
            default:
                dismiss();
                break;
        }
    }
}
