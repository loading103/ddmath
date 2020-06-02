package com.tsinghuabigdata.edu.ddmath.module.myscore.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.dialog.ProductDetailDialog;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.Locale;

import javax.annotation.Nonnull;

/**
 * 积分商品
 */

public class ScoreProductAdapter extends ArrayAdapter<ScoreProductBean> {

    private Context mContext;

    public ScoreProductAdapter(Context context) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @Nonnull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate( GlobalData.isPad()?R.layout.item_score_product:R.layout.item_score_product_mobile, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( getItem(position), position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener{

        private int position;

        private LinearLayout mainLayout;

        private ImageView defaultHeadView;
        private ImageView imageView;
        private ImageView exchangedView;
        private RelativeLayout coverLayout;

        private TextView nameTextView;
        private TextView scoreTextView;
        private TextView countTextView;

        private ViewHolder(View convertView) {
            mainLayout = (LinearLayout) convertView;
            mainLayout.setOnClickListener( this );
            defaultHeadView = convertView.findViewById(R.id.iv_default_headimg);
            exchangedView = convertView.findViewById(R.id.iv_finished_exchange);
            imageView  =  convertView.findViewById( R.id.iv_product_image );
            coverLayout=  convertView.findViewById( R.id.layout_no_product );
            nameTextView=convertView.findViewById( R.id.tv_product_name );
            scoreTextView=convertView.findViewById( R.id.tv_price_score );
            countTextView=convertView.findViewById( R.id.tv_product_excount );

        }

        public void bindView(ScoreProductBean bean, int position ){
            this.position = position;

            //增加已兑换显示，
            // 1、只可兑换一次的显示已兑换的标志（兑换挂件需要显示），
            exchangedView.setVisibility( (bean.isUserExchange()&&bean.getCatalogId() == ScoreProductBean.PRODUCT_TYPE_PENDANT )?View.VISIBLE:View.GONE );

            nameTextView.setText( bean.getName() );
            scoreTextView.setText( String.format(Locale.getDefault(),"%d积分", bean.getPointAmt()) );
            countTextView.setText( String.format( Locale.getDefault(), "%d人次兑换", bean.getExchangeCount()));
            
            defaultHeadView.setVisibility( bean.getCatalogId()==ScoreProductBean.PRODUCT_TYPE_PENDANT? View.VISIBLE:View.GONE);

            coverLayout.setVisibility( bean.getRemainCount()<=0?View.VISIBLE:View.GONE );

            String url = BitmapUtils.getUrlWithToken(bean.getImagePath());
            PicassoUtil.getPicasso(mContext).load(url).error(R.drawable.ic_broken_image).placeholder(R.drawable.ic_temporary_image).into(imageView);
        }

        @Override
        public void onClick(View v) {
            final ScoreProductBean bean = getItem(position);
            if( bean == null ) return;

            ProductDetailDialog dialog = new ProductDetailDialog( mContext, bean.getProductId() );
            dialog.setListener(new ProductDetailDialog.ExchangeSuccessListener() {
                @Override
                public void onSuccess(String pruductId) {
                    //
                    bean.setUserExchange( true );
                    bean.setExchangeCount( bean.getExchangeCount()+1 );
                    bean.setRemainCount( bean.getRemainCount()-1 );
                    notifyDataSetChanged();
                }
            });
            dialog.show();
        }
    }

}
