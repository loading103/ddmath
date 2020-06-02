package com.tsinghuabigdata.edu.ddmath.module.product.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.List;

import static java.lang.String.format;


/**
 * 拍照套餐 列表
 */

public class ProductSetmealAdapter extends CommonAdapter<ProductBean> {

    public ProductSetmealAdapter(Context context, List mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_setmeal_product;
        } else {
            return R.layout.item_setmeal_product_phone;
        }
    }

    @Override
    protected void convert(ViewHolder helper, int position, ProductBean item) {

        //商品图片
        ImageView ivTreasure = helper.getView(R.id.item_product_image);
        String imageUri = AccountUtils.getFileServer() + item.getImagePath();
        PicassoUtil.getPicasso(mContext).load( imageUri ).into(ivTreasure);

        //商品名称
        helper.setText( R.id.item_product_name, item.getName());

        //使用期限
        helper.setText( R.id.item_product_subtitle, ProductUtil.getProductUseTime( item ) );
        //使用次数
        helper.setText( R.id.item_product_times, format("使用次数:%s", item.getUseTimes()>0?String.valueOf(item.getUseTimes()):"不限次") );
    }

}
