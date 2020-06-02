package com.tsinghuabigdata.edu.ddmath.module.myscore.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * 兑换记录
 */

public class ExchangeRecordAdapter extends ArrayAdapter<ScoreProductBean> {

    private Context mContext;

    public ExchangeRecordAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate( GlobalData.isPad()?R.layout.item_exchange_record:R.layout.item_exchange_record_mobile, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( getItem(position) );
        return convertView;
    }

    class ViewHolder{

        private ImageView imageView;

        private TextView nameTextView;
        private TextView timeTextView;
        private TextView scoreTextView;

        private ViewHolder(View convertView) {
            imageView  =  convertView.findViewById( R.id.iv_product_image );
            nameTextView=convertView.findViewById( R.id.tv_product_name );
            scoreTextView=convertView.findViewById( R.id.tv_product_price );
            timeTextView=convertView.findViewById( R.id.tv_product_time );
        }

        public void bindView(ScoreProductBean bean ){
            nameTextView.setText( bean.getProductName() );
            scoreTextView.setText( String.format(Locale.getDefault(),"-%d积分", bean.getPointAmt()) );
            timeTextView.setText(DateUtils.format(bean.getCreateTime(), DateUtils.FORMAT_DATA_TIME));

            String url = BitmapUtils.getUrlWithToken( bean.getImagePath() );
            PicassoUtil.getPicasso(mContext).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(imageView);
        }
    }
}
