package com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter;

import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * vip卡对应——套餐下商品列表适配器
 * Created by Administrator on 2018/4/8.
 */

public class VipCardProductAdapter extends CommonAdapter<ProductBean> {

    public VipCardProductAdapter(Context context, List<ProductBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_card_product : R.layout.item_suite_card_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, ProductBean item) {
        helper.setText(R.id.tv_product_name, item.getName());
        //使用期限
        helper.setText(R.id.tv_product_usedate, ProductUtil.getUseTime(item));
        //使用次数
        helper.setText(R.id.tv_product_count, item.getUseTimes() > 0 ? item.getUseTimes() + "次" : "不限次");
    }


}
