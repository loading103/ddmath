package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowledgeMasterBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;


/**
 * 知识诊断
 */

public class KnowDiagnoseAdapter extends CommonAdapter<KnowledgeMasterBean> {

    private Context mContext;
    public KnowDiagnoseAdapter(Context context, List<KnowledgeMasterBean> mDatas) {
        super(context, mDatas);
        mContext = context;
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_glory_diagnose : R.layout.item_glory_diagnose_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, KnowledgeMasterBean item) {

        //背景
        LinearLayout root = helper.getView(R.id.ll_class_item);
        root.setActivated( item.isSelect() );
        if ( item.isSelect() ) {
            root.setBackgroundResource(R.drawable.bg_honourlist_myrank_item);
        } else{
            root.setBackgroundResource( position % 2 == 0? R.drawable.bg_honourlist_item_1:R.drawable.bg_honourlist_item_2 );
            root.setActivated(false);
        }

        TextView nameView   = helper.getView( R.id.tv_know_name );
        TextView rateView   = helper.getView( R.id.tv_know_rate );
        TextView adjustView = helper.getView( R.id.tv_know_adjustvalue );

        //背景设置
        nameView.setSelected(  item.isSelect() );
        rateView.setSelected(  item.isSelect() );
        adjustView.setSelected(  item.isSelect() );

        //
        nameView.setText( item.getKnowledgePointName() );
        rateView.setText( ""+ Math.round(item.getAccuracy() * 100) +"%" );

        float adujst = item.getAccuracy() - item.getLastAccuracy();
        if( adujst >= 0 ){
            adjustView.setText( "上升 "+Math.round(adujst * 100) + "%");
            adjustView.setTextColor(  mContext.getResources().getColor( R.color.knowdiagnose_adjusttext_add_color ) );
        }else{
            adjustView.setText("下降 "+Math.round(-adujst * 100) + "%");
            adjustView.setTextColor(  mContext.getResources().getColor( R.color.knowdiagnose_adjusttext_dec_color ) );
        }
    }
}
