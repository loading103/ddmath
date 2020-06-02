package com.tsinghuabigdata.edu.ddmath.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 确认取消错题本Dialog
 * Created by Administrator on 2017/10/27.
 */

public class CancelBookDialog extends Dialog implements View.OnClickListener {

    private Button     mBtnWait;
    private Button     mBtnSure;
    private cancelBook mCancelBook;

    public CancelBookDialog(@NonNull Context context) {
        this(context,R.style.dialog);
    }

    public CancelBookDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initData();
    }

    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_cancel_book);
        } else {
            setContentView(R.layout.dialog_cancel_book_phone);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            if( window!=null ){
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
        mBtnWait = (Button) findViewById(R.id.btn_wait);
        mBtnSure = (Button) findViewById(R.id.btn_sure);
        mBtnWait.setOnClickListener(this);
        mBtnSure.setOnClickListener(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void setCancelBook(cancelBook cancelBook) {
        mCancelBook = cancelBook;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wait:
                if (mCancelBook != null) {
                    mCancelBook.waitProduce();
                }
                dismiss();
                break;
            case R.id.btn_sure:
                if (mCancelBook != null) {
                    mCancelBook.sure();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface cancelBook {
        void waitProduce();

        void sure();
    }
}
