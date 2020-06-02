package com.tsinghuabigdata.edu.ddmath.module.mystudybean;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/6.
 */

public class PayView extends RecyclerView {

    private ArrayList<PayItem> list = new ArrayList<>();

    private PayAdapter adapter;

    private class PayItem {
        RewardBean bean;
        boolean    selected;

        public PayItem(RewardBean bean, boolean selected) {
            this.bean = bean;
            this.selected = selected;
        }
    }

    public PayView(Context context) {
        super(context);
        init();
    }

    public PayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PayView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

   /* public void setMoney( int resid ){
        String[] payArray = getResources().getStringArray( resid );
        for( int i = 0; i < payArray.length; i++ ){
            list.add( new PayItem(payArray[i], payArray[++i], false) );
        }

        adapter.notifyDataSetChanged();
    }*/

    public void setMoney(List<RewardBean> rewardBeanList) {
        list.clear();
        for (int i = 0; i < rewardBeanList.size(); i++) {
            list.add(new PayItem(rewardBeanList.get(i), false));
        }
        adapter.notifyDataSetChanged();
        adapter.singleSelectMode(rewardBeanList.size() - 1);
    }

    public RewardBean getSelectRewardBean() {
        RewardBean rewardBean = null;
        for (PayItem item : list) {
            if (item.selected) {
                rewardBean = item.bean;
            }
        }
        return rewardBean;
    }

    private void init() {
        adapter = new PayView.PayAdapter();
        setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        setAdapter(adapter);
    }

    private class PayAdapter extends RecyclerView.Adapter<PayAdapter.ViewHolder> {

        class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layout;
            TextView     mTvMoney;
            TextView     mTvStudyBean;
            TextView     mTvSendBean;

            public ViewHolder(View itemView) {
                super(itemView);
                this.mTvMoney = (TextView) itemView.findViewById(R.id.tv_money);
                this.mTvStudyBean = (TextView) itemView.findViewById(R.id.tv_studybean);
                this.mTvSendBean = (TextView) itemView.findViewById(R.id.tv_send_bean);
                this.layout = (LinearLayout) itemView.findViewById(R.id.ll_layout);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(GlobalData.isPad() ?
                    R.layout.item_pay_view : R.layout.item_pay_view_phone, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final PayItem item = list.get(position);
            holder.mTvMoney.setText(item.bean.getRechargeMoney() + "元");
            if (item.bean.getReturnDdAmt() <= 0) {
                holder.mTvStudyBean.setText(item.bean.getRechargeMoney() * 10 + "学豆");
                holder.mTvSendBean.setText(null);
            } else {
                holder.mTvStudyBean.setText(item.bean.getRechargeMoney() * 10 + "学豆+");
                holder.mTvSendBean.setText("赠送" + item.bean.getReturnDdAmt() + "学豆");
            }
            if (item.selected) {
                holder.layout.setSelected(true);
                holder.mTvMoney.setTextColor(Color.WHITE);
                holder.mTvStudyBean.setTextColor(Color.WHITE);
                holder.mTvSendBean.setTextColor(Color.WHITE);
            } else {
                holder.layout.setSelected(false);
                holder.mTvMoney.setTextColor(getResources().getColor(R.color.color_666666));
                holder.mTvStudyBean.setTextColor(getResources().getColor(R.color.color_666666));
                holder.mTvSendBean.setTextColor(getResources().getColor(R.color.color_666666));
            }
            holder.layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    singleSelectMode(position);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private void singleSelectMode(int position) {
            for (int i = 0; i < list.size(); i++) {
                PayItem item = list.get(i);
                item.selected = (i == position) && !item.selected;
            }
        }
    }

}
