package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.List;
import java.util.Locale;


/**
 * 豆豆检查作业适配器
 * Created by Administrator on 2017/11/29.
 */

public class MonthSubmitedCheckWorkAdatper extends BaseAdapter {


    private Context              mContext;
    private List<SubmitQuestion> mDatas;


    /*public*/ MonthSubmitedCheckWorkAdatper(Context context, List<SubmitQuestion> datas) {
        mContext = context;
        mDatas = datas;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // 没有复用
            if (GlobalData.isPad()) {
                convertView = View.inflate(mContext, R.layout.item_check_work, null);
            } else {
                convertView = View.inflate(mContext, R.layout.item_check_work_phone, null);
            }
            holder = new ViewHolder();
            convertView.setTag(holder);
            // 给holder找view
            holder.llQuestionCount =  convertView.findViewById(R.id.ll_question_count);
            holder.llCorrecting =  convertView.findViewById(R.id.ll_correcting);
            holder.tvRightCount =  convertView.findViewById(R.id.tv_right_count);
            holder.tvWrongCount =  convertView.findViewById(R.id.tv_wrong_count);
            holder.tvDay =  convertView.findViewById(R.id.tv_day);
            holder.tvTitle =  convertView.findViewById(R.id.tv_title);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SubmitQuestion item = mDatas.get(position);
        // 给holder中的view设置数据
        ////  2017/10/20 模拟批阅状态
        //        item.setExerStatus(3 + new Random().nextInt(2));

        if( item.getUploadType() == AppConst.UPLOAD_TYPE_MARKED ){
            convertView.setBackgroundResource( R.drawable.bg_submited_correctedwork_item );
            holder.tvTitle.setTextColor( mContext.getResources().getColor(R.color.color_559CE8));
        }else{
            convertView.setBackgroundResource( R.drawable.bg_submited_work_item );
            holder.tvTitle.setTextColor( mContext.getResources().getColor(R.color.color_3DA4BD));
        }

        holder.tvTitle.setText(item.getExamName());     // 2018/12/10   名称变了
        Drawable typeDrawable = DDWorkUtil.getWorkTypeDrawable(mContext,item.getResourceType());
        holder.tvTitle.setCompoundDrawables(null, null, typeDrawable, null);//作业类型 画在右边

        if (item.getExerStatus() == SubmitQuestion.CORRECTED) {
            holder.llQuestionCount.setVisibility(View.VISIBLE);
            holder.llCorrecting.setVisibility(View.INVISIBLE);
            holder.tvRightCount.setText( String.format(Locale.getDefault(), "%d题",item.getRightQuestionCount()));
            holder.tvWrongCount.setText( String.format(Locale.getDefault(), "%d题",item.getWrongQuestionCount()));
        } else {
            holder.llQuestionCount.setVisibility(View.INVISIBLE);
            holder.llCorrecting.setVisibility(View.VISIBLE);
        }

        holder.tvDay.setText(DDWorkUtil.getStartDay(item));
        return convertView;
    }

    class ViewHolder {
        TextView     tvTitle;
//        ImageView    ivJiaofu;
//        ImageView    ivZhoulian;
//        ImageView    ivJiaQi;
        LinearLayout llQuestionCount;
        LinearLayout llCorrecting;
        TextView     tvRightCount;
        TextView     tvWrongCount;
        TextView     tvDay;
    }

    public void setData(List<SubmitQuestion> questions) {
        mDatas.clear();
        mDatas.addAll(questions);
    }

}
