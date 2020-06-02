package com.tsinghuabigdata.edu.ddmath.module.product.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.product.adapter.ProductSetmealAdapter;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 商品套餐详情界面
 */
public class ProductSetMealView extends LinearLayout{


    private TextView productNameView;
    private TextView learnDouView;
    private ListView listView;


    public ProductSetMealView(Context context) {
        super(context);
        init();
    }

    public ProductSetMealView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProductSetMealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setData(String productName, int learnDou, ArrayList<ProductBean> list){

        productNameView.setText( productName );
        learnDouView.setText( String.format( Locale.getDefault(), "需学豆: %d（约%d元）", learnDou, learnDou/10 ) );

        //
        ProductSetmealAdapter adapter = new ProductSetmealAdapter( getContext(), list );
        listView.setAdapter( adapter );
    }

    //-----------------------------------------------------------------------------
    private void init() {
        inflate( getContext(), GlobalData.isPad()?R.layout.view_setmeal_layout:R.layout.view_setmeal_layout_phone, this );

        productNameView = (TextView)findViewById( R.id.item_setmeal_productname );
        learnDouView = (TextView)findViewById( R.id.item_setmeal_lerandou );

        listView = (ListView) findViewById( R.id.item_setmeal_listview );
    }

}
