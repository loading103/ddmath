package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 详情子项title
 */
public class ItemTitleView extends LinearLayout{

    //对象
    private TextView titleView;

    public ItemTitleView(Context context) {
        super(context);
        initData(context);
    }

    public ItemTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public ItemTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData( context);
    }

    /**
     * 设置数据
     * @param title      标题
     */
    public void setTitle( String title ){
        titleView.setText( title );
    }

    //-------------------------------------------------------------------------
    private void initData(Context context){

        inflate( context, GlobalData.isPad()?R.layout.view_ddwork_itemtitle:R.layout.view_ddwork_itemtitle_phone, this );

        titleView = (TextView) findViewById( R.id.item_ddwork_titleview ) ;
    }

}
