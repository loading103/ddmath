package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.RankBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.view.BaseHeadView;

import java.util.List;

/**
 * 年级榜适配器
 * Created by Administrator on 2018/8/6.
 */

public class GradeRankAdapter extends CommonAdapter<RankBean> {

    public GradeRankAdapter(Context context, List<RankBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_grade_rank : R.layout.item_grade_rank_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, RankBean item) {
        BaseHeadView imageView = helper.getView(R.id.iv_head_class);
        imageView.showHeadImage( item.getHeadImage(), item.getPendentImage(), item.getVipLevel() );
        //imageView.setPendentSize(DensityUtils.dp2px( mContext,GlobalData.isPad()? 40:26) );

        ImageView pendentImage = helper.getView(R.id.iv_user_pendent);
        pendentImage.setVisibility( item.getVipLevel() == AppConst.MEMBER_NORMAL?View.INVISIBLE:View.VISIBLE );
        pendentImage.setImageResource( item.getVipLevel() == AppConst.MEMBER_SVIP? R.drawable.svip_buy:R.drawable.vip_buy );

        helper.setText(R.id.tv_name_class, item.getStudentName());
        helper.setText(R.id.tv_class_name_grade, item.getClassName());
        helper.setText(R.id.tv_glory_class, Math.round(item.getGlory() * 100) + "%");

        ImageView ivRank = helper.getView(R.id.iv_rank_class);
        TextView tvRank = helper.getView(R.id.tv_rank_class);
        if (item.getRank() < 4) {
            ivRank.setVisibility(View.VISIBLE);
            tvRank.setVisibility(View.INVISIBLE);
            if (item.getRank() == 1) {
                ivRank.setImageResource(R.drawable.icon_01);
            } else if (item.getRank() == 2) {
                ivRank.setImageResource(R.drawable.icon_02);
            } else if (item.getRank() == 3) {
                ivRank.setImageResource(R.drawable.icon_03);
            }
        } else {
            ivRank.setVisibility(View.INVISIBLE);
            tvRank.setVisibility(View.VISIBLE);
            tvRank.setText(item.getRank() + "");
        }
    }
}
