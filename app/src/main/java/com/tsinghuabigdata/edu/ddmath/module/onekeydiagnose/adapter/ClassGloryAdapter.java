package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.RankBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;


/**
 *
 */
@Deprecated
public class ClassGloryAdapter extends CommonAdapter<RankBean> {

    private String studentId;

    public ClassGloryAdapter(Context context, List<RankBean> mDatas) {
        super(context, mDatas);
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null) {
            studentId = detailinfo.getStudentId();
        }

    }

    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_glory_classlist : R.layout.item_glory_classlist_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, RankBean item) {
        LinearLayout root = helper.getView(R.id.ll_class_item);
        //排行榜如果是自己显示高亮 背景：FFC42C 字：B68300
        if (studentId.equals(item.getStudentId())) {
            root.setActivated(true);
//            root.setBackgroundColor(mContext.getResources().getColor(R.color.color_FFC42C));
            root.setBackgroundResource(R.drawable.bg_honourlist_myrank_item);
        } else if (position % 2 == 0) {
            root.setActivated(false);
//            root.setBackgroundColor(mContext.getResources().getColor(R.color.color_69BBC5));
            root.setBackgroundResource(R.drawable.bg_honourlist_item_1);
        } else {
            root.setActivated(false);
//            root.setBackgroundColor(mContext.getResources().getColor(R.color.color_309BA8));
            root.setBackgroundResource(R.drawable.bg_honourlist_item_2);
        }

        ImageView imageView = helper.getView(R.id.iv_head_class);
        HeadImageUtils.setHeadImage(imageView, item.getHeadImage(), R.drawable.doudou_portrait_default);
        helper.setText(R.id.tv_name_class, item.getStudentName());
        helper.setText(R.id.tv_glory_class, Math.round(item.getGlory() * 1000) + "");
        helper.setText(R.id.tv_rank_class, item.getRank() + "");
        if (position > 0) {
            RankBean lastItem = mDatas.get(position - 1);
            if (lastItem.getRank() == item.getRank()) {
                helper.setText(R.id.tv_rank_class, "");
            }
        }
    }
}
