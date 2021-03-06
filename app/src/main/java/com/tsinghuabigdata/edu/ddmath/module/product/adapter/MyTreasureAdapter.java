package com.tsinghuabigdata.edu.ddmath.module.product.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.product.ProductUtil;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.ProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.List;



/**
 * Created by Administrator on 2017/12/19.
 */

public class MyTreasureAdapter extends CommonAdapter<ProductBean> {

    private String mClassId    = "";
    private String mReallyName = "";

    public MyTreasureAdapter(Context context, List mDatas) {
        super(context, mDatas);
        if (AccountUtils.getCurrentClassInfo() != null) {
            mClassId = AccountUtils.getCurrentClassInfo().getClassId();
        }
        if (AccountUtils.getUserdetailInfo() != null) {
            mReallyName = AccountUtils.getUserdetailInfo().getReallyName();
        }
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_all_treasure;
        } else {
            return R.layout.item_all_treasure_phone;
        }
    }

    @Override
    protected void convert(ViewHolder helper, int position, ProductBean item) {
        helper.setText(R.id.tv_treasure_title, item.getName());
        helper.setText(R.id.tv_treasure_subtitle, item.getSubName());
        ImageView ivTreasure = helper.getView(R.id.iv_treasure);
        TextView tvExclusive = helper.getView(R.id.tv_exclusive);
        ImageView ivIneffective = helper.getView(R.id.iv_ineffective);
        ImageView ivAvailable = helper.getView(R.id.iv_available);
        TextView tvIneffective = helper.getView(R.id.tv_ineffective);
        // 无效
        if (!ProductUtil.effective(item,mClassId)) {
            ivIneffective.setVisibility(View.VISIBLE);
            ivAvailable.setVisibility(View.INVISIBLE);
            // 有效但是不可使用
        } else if (ProductUtil.vailable(item,mClassId)) {
            ivIneffective.setVisibility(View.INVISIBLE);
            ivAvailable.setVisibility(View.VISIBLE);
        } else {
            ivIneffective.setVisibility(View.INVISIBLE);
            ivAvailable.setVisibility(View.INVISIBLE);
        }
        /*ImageView ivAdded = helper.getView(R.id.iv_added);
        List<PrivilegeVo> list = item.getPrivilegeVos();
        if (list == null || list.size() == 0 || list.get(0) == null || list.get(0).getUsePeople() <= 0) {
            ivAdded.setVisibility(View.INVISIBLE);
        } else {
            ivAdded.setVisibility(View.VISIBLE);
        }*/
        if (item.isShowIneffectiveText()) {
            tvIneffective.setVisibility(View.VISIBLE);
        } else {
            tvIneffective.setVisibility(View.INVISIBLE);
        }
        if (item.getAutoUserName() == ProductBean.IS_AUTOUSERNAME) {
            tvExclusive.setVisibility(View.VISIBLE);
            tvExclusive.setText(mReallyName + "专属");
        } else {
            tvExclusive.setVisibility(View.INVISIBLE);
        }
        PicassoUtil.displayImage(item.getImagePath(),ivTreasure);
    }




}
