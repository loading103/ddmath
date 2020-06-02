package com.tsinghuabigdata.edu.ddmath.module.mycenter.adapter;

import android.content.Context;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * 我要购买——套餐下商品次数列表适配器
 * Created by Administrator on 2018/7/6.
 */

public class ProductCountAdapter extends CommonAdapter<ProductBean> {

    private boolean fromParent = false;
    public ProductCountAdapter(Context context, List<ProductBean> mDatas) {
        super(context, mDatas);
    }

    public void setFromParent(){
        fromParent = true;
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_suite_product_count : R.layout.item_suite_product_count_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, ProductBean item) {
        //使用次数
        if (item.getUseTimes() < 0) {
            helper.setText(R.id.tv_product_count, item.getName() + ":不限次");
        } else {
            helper.setText(R.id.tv_product_count, item.getName() + ":" + item.getUseTimes() + "次");
        }
        if( fromParent ){
            TextView textView = helper.getView(R.id.tv_product_count);
            textView.setTextSize( 14 );
        }
    }

}
