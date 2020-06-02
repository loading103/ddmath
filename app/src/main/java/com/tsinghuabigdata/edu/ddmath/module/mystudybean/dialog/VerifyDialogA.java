package com.tsinghuabigdata.edu.ddmath.module.mystudybean.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * Created by Administrator on 2017/12/22.
 * 兑换码 正常情况
 */

public class VerifyDialogA extends Dialog {

    private Button btCancel;
    private Button btKnow;

    public VerifyDialogA(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        setContentView(GlobalData.isPad() ? R.layout.dialog_verify_a_phone : R.layout.dialog_verify_a_phone);
        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        btKnow = (Button) findViewById(R.id.bt_know);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
