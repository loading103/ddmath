package com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.exclusivepractice.bean.FreeDroitDetailBean;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;

/**
 * 免费使用权列表适配器
 */

public class FreeDroidAdapter extends ArrayAdapter<FreeDroitDetailBean> {

    private Context mContext;

    public FreeDroidAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_free_droit : R.layout.item_free_droit_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

    //------------------------------------------------------------------------------------------
    class ViewHolder{
        //private int position;
        private TextView detailView;
        private TextView timesView;
        private ViewHolder(View convertView) {
            detailView   = (TextView)convertView.findViewById( R.id.tv_droit_detail );
            timesView    = (TextView)convertView.findViewById( R.id.tv_droit_times );
        }
        void bindView( int position ){
            FreeDroitDetailBean bean = getItem( position );
            if( bean == null ) return;

            detailView.setText( String.format( Locale.getDefault(), "使用期限：%s至%s", DateUtils.format( bean.getStartDate() ), DateUtils.format( bean.getEndDate() ) ) );
            timesView.setText( String.format( Locale.getDefault(), "%d次", bean.getSubTimes() ) );
        }
    }
}
