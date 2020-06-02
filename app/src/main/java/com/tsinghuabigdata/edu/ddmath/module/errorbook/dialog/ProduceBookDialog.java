package com.tsinghuabigdata.edu.ddmath.module.errorbook.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.MyProgessLine;

/**
 * 生成错题本Dialog
 * Created by Administrator on 2017/10/19.
 */

public class ProduceBookDialog extends Dialog {

    private ImageView     mIvClose;
    private TextView      mTvDialogTitle;
    private MyProgessLine mMyProgessLine;

    private produceBook mProduceBook;


    public ProduceBookDialog(@NonNull Context context) {
        this(context,R.style.dialog);
    }

    public ProduceBookDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initData();
    }

    private void initData() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_produce_book);
        } else {
            setContentView(R.layout.dialog_produce_book_phone);
        }
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mTvDialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        mMyProgessLine = (MyProgessLine) findViewById(R.id.myProgessLine);

        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProduceBook != null) {
                    mProduceBook.cancel();
                }
            }
        });

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.dimAmount = 0.5f;
        dialogWindow.setAttributes(lp);
        //        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void setProduceBook(produceBook produceBook) {
        mProduceBook = produceBook;
    }

    public interface produceBook {
        void cancel();
    }

    public void setProduceTitle(String title) {
        mTvDialogTitle.setText(title);
    }

    public MyProgessLine getMyProgessLine() {
        return mMyProgessLine;
    }
}
