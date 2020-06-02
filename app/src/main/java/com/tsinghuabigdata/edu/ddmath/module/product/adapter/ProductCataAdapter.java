package com.tsinghuabigdata.edu.ddmath.module.product.adapter;

import android.content.Context;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductCataBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;


/**
 * 商品分组适配器
 * Created by Administrator on 2018/3/2.
 */

public class ProductCataAdapter extends CommonAdapter<ProductCataBean> {


    public ProductCataAdapter(Context context, List mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_product_cata;
        } else {
            return R.layout.item_product_cata_phone;
        }
    }

    @Override
    protected void convert(ViewHolder helper, int position, ProductCataBean item) {
        TextView tvTitle = helper.getView(R.id.tv_cata_title);
        tvTitle.setText(item.getCatalogName());
        tvTitle.setSelected(item.isSelect());
    }

}
