package com.tsinghuabigdata.edu.ddmath.module.product.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;

/**
 * 假期作业介绍
 */
public class HolidayWorkView extends LinearLayout{


    private TextView productNameView;
    private TextView learnDouView;
    private ListView listView;


    public HolidayWorkView(Context context) {
        super(context);
        init();
    }

    public HolidayWorkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HolidayWorkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setData(String productName, int learnDou, String[] materials, String practices[] ){

        productNameView.setText( productName );
        learnDouView.setText( String.format( Locale.getDefault(), "%d学豆", learnDou ) );

        //
        int count = (materials != null?materials.length:0) + (practices != null?practices.length:0) ;
        if( count == 0 ) return;

        String buffer[] = new String[count];

        int index = 0;
        if( materials != null && materials.length > 0 ){
            index = materials.length;
            System.arraycopy( materials, 0, buffer, 0, materials.length );
        }
        if( practices != null && practices.length > 0 ){
            System.arraycopy( practices, 0, buffer, index, practices.length );
        }
        listView.setAdapter(new ArrayAdapter<>( getContext(), android.R.layout.simple_list_item_1, buffer));
    }

    //-----------------------------------------------------------------------------
    private void init() {

        inflate( getContext(), GlobalData.isPad()?R.layout.view_holidaywork_layout:R.layout.view_holidaywork_layout_phone, this );

        productNameView = (TextView)findViewById( R.id.item_holiday_productname );
        learnDouView = (TextView)findViewById( R.id.item_holiday_learndou );
        listView = (ListView) findViewById( R.id.listview );
    }

}
