package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

import android.content.Context;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * 学豆充值列表适配器.
 * <p>
 * Created by Administrator on 2018/8/9.
 */

public class PayBeanAdapter extends CommonAdapter<RewardBean> {

    private int mCurPosition;
    private boolean fromParent = false;
    public PayBeanAdapter(Context context, List<RewardBean> mDatas) {
        super(context, mDatas);
    }

    public void setFromParent(){
        fromParent = true;
    }
    @Override
    protected int getLayoutId() {
        return GlobalData.isPad() ? R.layout.item_pay_bean : R.layout.item_pay_bean_phone;
    }

    @Override
    protected void convert(ViewHolder helper, int position, RewardBean item) {
        RelativeLayout root = helper.getView(R.id.rr_root);
        root.setBackgroundResource( fromParent?R.drawable.selector_pay_bean_parent:R.drawable.selector_pay_bean );
        root.setActivated(position == mCurPosition);
        helper.setText(R.id.tv_money, item.getRechargeMoney() + "");
        if (item.getReturnDdAmt() > 0 && !fromParent ) {
            helper.setText(R.id.tv_bean, item.getRechargeMoney() * 10 + "学豆+赠送" + item.getReturnDdAmt() + "学豆");
        } else {
            helper.setText(R.id.tv_bean, item.getRechargeMoney() * 10 + "学豆");
        }
    }

    public int getCurPosition() {
        return mCurPosition;
    }

    public void setCurPosition(int curPosition) {
        mCurPosition = curPosition;
    }
}
