package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.WeekBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;

public class PersonWeekAdapter extends BaseAdapter{
    private List<WeekBean> weeks;
    private Context context;
    private LayoutInflater inflater = null;


    public PersonWeekAdapter(Context context, List<WeekBean> weeks) {
        this.weeks=initData(weeks);
        this.context=context;
        inflater = LayoutInflater.from(context);
    }

    private List<WeekBean>  initData(List<WeekBean> weeks) {
        for (int i = 0; i < weeks.size(); i++) {
            if(i==0){
                weeks.get(i).setSelected(true);
            }else{
                weeks.get(i).setSelected(false);
            }

        }
        return weeks;
    }

    @Override
    public int getCount() {
        return weeks.size();
    }

    @Override
    public WeekBean getItem(int position) {
        return weeks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(GlobalData.isPad()?R.layout.item_person_week:R.layout.item_person_week_phone, parent, false);
            holder.tv_date=convertView.findViewById(R.id.tv_data_day);
            holder.tv_week=convertView.findViewById(R.id.tv_week_day);
            holder.iv_flag=convertView.findViewById(R.id.iv_flag);
            holder.ll_item_list=convertView.findViewById(R.id.ll_item_list);
            holder.line=convertView.findViewById(R.id.iv_line);
            // 为view设置标签
            convertView.setTag(holder);
        }else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        WeekBean weekBean = getItem(position);

        if(!weekBean.getWeek().equals("周末")){
            if(weekBean.getHaveWrongQuestion()){
                holder.iv_flag.setVisibility(View.VISIBLE);
                if(weekBean.getStatus()==0){
                    holder.iv_flag.setImageResource(R.drawable.icon_green);
                }else {
                    holder.iv_flag.setImageResource(R.drawable.icon_red);
                }
            }else {
                holder.iv_flag.setVisibility(View.INVISIBLE);
            }
        }else {
            if(weekBean.getStatus()==0){
                holder.iv_flag.setImageResource(R.drawable.icon_green);
            }else if(weekBean.getStatus()==1){
                holder.iv_flag.setImageResource(R.drawable.icon_red);
            }else {
                holder.iv_flag.setVisibility(View.INVISIBLE);
            }
        }

        if(weekBean.getSelected()){
            holder.line.setVisibility(View.VISIBLE);
            holder.ll_item_list.setActivated(true);
            holder.tv_date.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_week.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            holder.line.setVisibility(View.INVISIBLE);
            holder.ll_item_list.setActivated(false);
            holder.tv_date.setTextColor(context.getResources().getColor(R.color.color_666666));
            holder.tv_week.setTextColor(context.getResources().getColor(R.color.color_333333));
        }
        holder.tv_date.setText(weekBean.getDayString());
        holder.tv_week.setText(weekBean.getWeek());
        return convertView;
    }
    public static class ViewHolder {
        public TextView tv_date;
        public TextView tv_week;
        public ImageView iv_flag;
        public LinearLayout ll_item_list;
        public View line;
    }
}
