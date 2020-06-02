package com.tsinghuabigdata.edu.ddmath.module.errorbook.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.KnowledgePiontBean;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.Locale;

/**
 *  知识点掌握度
 */

public class KnowledgeMasteryAdapter extends ArrayAdapter<KnowledgePiontBean> {

    private Context mContext;

    public KnowledgeMasteryAdapter(Context context ) {
        super(context, 0);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(GlobalData.isPad()?R.layout.item_ebook_knowledgemastery : R.layout.item_ebook_knowledgemastery_phone, parent, false);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = ((ViewHolder) convertView.getTag());
        viewHolder.bindView( position );
        return convertView;
    }

    //------------------------------------------------------------------------------------------
    class ViewHolder/* implements View.OnClickListener*/{

        private View mainLayout;
        //private int position;

        private TextView nameView;
        private TextView masteryView;

        private ViewHolder(View convertView) {
            mainLayout = convertView;
            nameView = (TextView)convertView.findViewById( R.id.item_ebookknow_knowname );
            masteryView = (TextView)convertView.findViewById( R.id.item_ebookknow_mastery );
        }

        void bindView( int position ){
            //this.position = position;
            int color = getContext().getResources().getColor( position%2==0?R.color.color_F3FCF8:R.color.color_EAF8F2);
            mainLayout.setBackgroundColor( color );

            KnowledgePiontBean bean = getItem(position);
            if( bean == null ) return;

            nameView.setText( bean.getKnowledgeName() );
            if( bean.isMastery() ){
                Resources resources = mContext.getResources();
                Drawable drawable = resources.getDrawable( R.drawable.zhuyao );
                int w = DensityUtils.dp2px(mContext, GlobalData.isPad()?24:16);
                drawable.setBounds( 0, 0, w, w);
                nameView.setCompoundDrawables(null, null, drawable, null);
            }else{
                nameView.setCompoundDrawables(null, null, null, null);
            }

            if( bean.getErrorTimes() > 0 ){
                String data = String.format(Locale.getDefault(),"错误：%d次", bean.getErrorTimes() );
                masteryView.setText( data );
            }else{
                String data = String.format(Locale.getDefault(),"正确率：%d", Math.round(bean.getAccuracy()*100) )+"%";
                masteryView.setText( data );
            }
        }
    }
}
