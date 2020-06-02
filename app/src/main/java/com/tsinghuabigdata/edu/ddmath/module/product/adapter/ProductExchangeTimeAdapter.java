package com.tsinghuabigdata.edu.ddmath.module.product.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ExchangeTimeBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;

/**
 * 商品兑换次数适配器
 */

public class ProductExchangeTimeAdapter extends ArrayAdapter<ExchangeTimeBean> {

    private Context mContext;
//    private ExchangeDetail mExchangeDetail;

    public ProductExchangeTimeAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_product_exchangetime : R.layout.item_product_exchangetime_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }
    public ExchangeTimeBean getSelectProductBean(){

        int count = getCount();
        for( int i=0; i<count; i++ ){
            ExchangeTimeBean bean = getItem( i );
            if( bean!=null && bean.isSelect() ) return bean;
        }
        return null;
    }
    //---------------------------------------------------------------------------------------

    private void setSelectItem( int position ){
        int count = getCount();
        for( int i=0; i<count; i++ ){
            ExchangeTimeBean bean = getItem(i);
            if( bean == null ) continue;
            bean.setSelect( i == position );
        }
    }

    //------------------------------------------------------------------------------------------
    class ViewHolder implements View.OnClickListener{

        //private LinearLayout mainLayout;
        private int position;

        private View mainView;
        private TextView timesView;
        private TextView learndouView;

        private ViewHolder(View convertView) {

            mainView = convertView.findViewById( R.id.item_product_mainlayout );
            mainView.setOnClickListener( this );

            timesView       = (TextView)convertView.findViewById( R.id.item_product_times );
            learndouView    = (TextView)convertView.findViewById( R.id.item_product_learndou );
        }

        @Override
        public void onClick(View v) {
            if( R.id.item_product_mainlayout == v.getId() ){
                setSelectItem( position );
                notifyDataSetChanged();
            }
        }


        void bindView( int position ){
            this.position = position;

            ExchangeTimeBean bean = getItem( position );
            if( bean == null ) return;

            Resources res = getContext().getResources();

            timesView.setText( String.format(Locale.getDefault(), "%d次", bean.getChangeNum() ) );
            learndouView.setText( String.format(Locale.getDefault(), "%d学豆", bean.getDdAmt())  );

            if( bean.isSelect() ){
                mainView.setBackgroundResource( R.drawable.bg_rect_product_exchange_selected );
                timesView.setTextColor( Color.WHITE );
                learndouView.setTextColor( Color.WHITE );
            }else{
                mainView.setBackgroundResource( R.drawable.bg_rect_product_exchange );
                timesView.setTextColor( res.getColor(R.color.color_6A7382) );
                learndouView.setTextColor( res.getColor(R.color.color_6A7382) );
            }
        }

    }
}
