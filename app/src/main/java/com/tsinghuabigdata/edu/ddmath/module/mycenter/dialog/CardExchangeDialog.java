package com.tsinghuabigdata.edu.ddmath.module.mycenter.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter.VipCardProductAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * VIP卡兑换成功Dialog
 * Created by Administrator on 2018/5/16.
 */

public class CardExchangeDialog extends Dialog implements View.OnClickListener{

    private ImageView mIvClose;
    private TextView  mTvSuiteName;
    private TextView  mTvSuiteTime;
    private ListView  mLvProduct;
    private ImageView mIvKnow;


    public CardExchangeDialog(@NonNull Context context) {
        this(context, R.style.dialog);
    }

    public CardExchangeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initView();
        initData();
    }

    private void initView() {
        if (GlobalData.isPad()) {
            setContentView(R.layout.dialog_card_exchange);
        } else {
            setContentView(R.layout.dialog_card_exchange_phone);
        }
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mTvSuiteName = (TextView) findViewById(R.id.tv_suite_name);
        mTvSuiteTime = (TextView) findViewById(R.id.tv_suite_time);
        mLvProduct = (ListView) findViewById(R.id.lv_product);
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

    private void initData() {

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


    public void setSuit(ProductBean item) {
        mTvSuiteTime.setText(ProductUtil.getUseTime(item));
        VipCardProductAdapter adapter = new VipCardProductAdapter(getContext(), item.getProductVoList());
        mLvProduct.setAdapter(adapter);
    }


}
