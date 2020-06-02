package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

/**
 * Created by Administrator on 2016/12/13.
 */

public class TimeSpinnerAdapter extends BaseAdapter {

    private int          seclectPosition;
    private Context      mContext;
    private List<String> mDatas;

    public TimeSpinnerAdapter(Context context, List<String> list, int position) {
        mDatas = list;
        mContext = context;
        seclectPosition = position;
        //        filtrateHandle = (FiltrateHandle) context;
    }

    public void setSelectPostion(int position) {
        seclectPosition = position;
    }

    @Override
    public int getCount() {
        if (mDatas != null) {
            return mDatas.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDatas != null) {
            return mDatas.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 没有复用
            if (GlobalData.isPad()) {
                convertView = View.inflate(mContext, R.layout.item_time_spinner, null);
            } else {
                convertView = View.inflate(mContext, R.layout.item_time_spinner_phone, null);
            }
            holder = new ViewHolder();
            convertView.setTag(holder);
            // 给holder找view
            holder.root = (RelativeLayout) convertView.findViewById(R.id.rl_time_item);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_time_title);
            holder.ivChoosed = (ImageView) convertView.findViewById(R.id.iv_time_choosed);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String data = mDatas.get(position);
        // 给holder中的view设置数据
        holder.tvTitle.setText(data);
        boolean select = seclectPosition == position;
        holder.root.setActivated(select);
        if (select) {
            holder.ivChoosed.setVisibility(View.VISIBLE);
        } else {
            holder.ivChoosed.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        RelativeLayout root;
        TextView       tvTitle;
        ImageView      ivChoosed;
    }
}
