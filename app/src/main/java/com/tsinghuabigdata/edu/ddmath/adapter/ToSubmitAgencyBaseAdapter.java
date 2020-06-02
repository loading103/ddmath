package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDWorkUtil;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

import java.util.ArrayList;
import java.util.List;

import static com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo.WORK_COMMITFAIL;
import static com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalWorkInfo.WORK_COMMITING;


/**
 * Created by Administrator on 2017/3/3.
 */

public class ToSubmitAgencyBaseAdapter extends BaseAdapter {

    private Context              mContext;
    private List<SubmitQuestion> mDatas;
    private List<SubmitQuestion> mListShow = new ArrayList<>();
    private int                  mNum      = 2;


    public ToSubmitAgencyBaseAdapter(Context context, List<SubmitQuestion> list) {
        mDatas = list;
        mContext = context;
    }


    @Override
    public int getCount() {
        if (mListShow != null) {
            return mListShow.size();
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
                convertView = View.inflate(mContext, R.layout.item_to_submit, null);
            } else {
                convertView = View.inflate(mContext, R.layout.item_to_submit_phone, null);
            }
            holder = new ViewHolder();
            convertView.setTag(holder);
            // 给holder找view
            holder.statusImageView = convertView.findViewById(R.id.iv_work_statustype);
            holder.statusTextView = convertView.findViewById(R.id.tv_work_statustips);
//            holder.llPageCount =  convertView.findViewById(R.id.ll_page_count);
//            holder.llScanUpload =  convertView.findViewById(R.id.ll_scan_upload);
//            holder.llCorrecredUpload= convertView.findViewById(R.id.ll_corrected_upload);
//            holder.llSubmitting =  convertView.findViewById(R.id.ll_submitting);
//            holder.llSubmitFail =  convertView.findViewById(R.id.ll_fail);
//            holder.tvPageTaked =  convertView.findViewById(R.id.tv_page_count_taked);
//            holder.tvCorrectPageTaked=convertView.findViewById(R.id.tv_correct_count_taked);
            holder.tvTtile =  convertView.findViewById(R.id.tv_title);
            holder.tvEndTime =  convertView.findViewById(R.id.tv_end_time);
            holder.tvSubmitCount =  convertView.findViewById(R.id.tv_submit_count);
            holder.llOverdueTip =  convertView.findViewById(R.id.ll_overdue_tip);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        SubmitQuestion item = mDatas.get(position);
        ////  2017/10/20 模拟提交状态
        //item.setStatus(new Random().nextInt(4));
        // 给holder中的view设置数据
        holder.tvTtile.setText(item.getExamName());
        Drawable typeDrawable = DDWorkUtil.getWorkTypeDrawable(mContext,item.getResourceType());
        holder.tvTtile.setCompoundDrawables(null, null, typeDrawable, null);//作业类型 画在右边

        setWorkStatus( item, holder );          //作业状态

        holder.tvEndTime.setText(DDWorkUtil.getEndMinute(item.getEndTime()));
        String data = String.format(mContext.getResources().getString(R.string.work_submite_count), item.getSubmittedCount());
        holder.tvSubmitCount.setText(data);
        if (item.isOverdue()) {
            holder.llOverdueTip.setVisibility(View.VISIBLE);
        } else {
            holder.llOverdueTip.setVisibility(View.GONE);
        }
        return convertView;
    }

    private void setWorkStatus( SubmitQuestion item, ViewHolder holder ){
        //先根据提交状态分类处理
        if (item.getStatus() == WORK_COMMITING) { //提交中
            holder.statusImageView.setImageResource( R.drawable.submission );
            holder.statusTextView.setText("正在提交...");
            holder.statusTextView.setTextColor( mContext.getResources().getColor(R.color.color_666666));
        } else if (item.getStatus() == WORK_COMMITFAIL) { //提交失败
            holder.statusImageView.setImageResource( R.drawable.submission );
            holder.statusTextView.setText("提交失败！请重新提交");
            holder.statusTextView.setTextColor( mContext.getResources().getColor(R.color.color_FF675F));

        } else {        //还未提交

            //再根据是否拍照分类处理
            if(item.getUploadType() == AppConst.UPLOAD_TYPE_SCAN){      //扫描上传作业
                holder.statusImageView.setImageResource( R.drawable.scan );
                holder.statusTextView.setText( mContext.getText(R.string.teacher_scan_upload) );
                holder.statusTextView.setTextColor( mContext.getResources().getColor(R.color.color_666666));
            } else if( item.getUploadType() == AppConst.UPLOAD_TYPE_MARKED ){   //批阅上传
                holder.statusImageView.setImageResource( R.drawable.icon_correct );
                holder.statusTextView.setTextColor( mContext.getResources().getColor(R.color.color_666666));

                if( item.getTakedpageCount() > 0 ){
                    String count = String.format(mContext.getResources().getString(R.string.page_count_taked), item.getTakedpageCount(), item.getPageCount());
                    holder.statusTextView.setText(count);
                }else{
                    holder.statusTextView.setText( mContext.getText(R.string.corrected_upload));
                }
            }else/* if (item.getUploadType() == AppConst.UPLOAD_TYPE_CAMERA)*/ {  //拍照上传，混合上传， 默认行为
                String count = String.format(mContext.getResources().getString(R.string.page_count_taked), item.getTakedpageCount(), item.getPageCount());
                holder.statusImageView.setImageResource( R.drawable.photo );
                holder.statusTextView.setText(count);
                holder.statusTextView.setTextColor( mContext.getResources().getColor(R.color.color_666666));
            }
        }
    }

    class ViewHolder {
        TextView     tvTtile;
//        ImageView    ivJiaofu;
//        ImageView    ivZhoulian;
//        ImageView    ivJiaQi;

        //作业状态，简化处理
        ImageView statusImageView;
        TextView statusTextView;
//        LinearLayout llPageCount;
//        LinearLayout llScanUpload;
//        LinearLayout llCorrecredUpload;
//        LinearLayout llSubmitting;
//        LinearLayout llSubmitFail;


        //TextView     tvPageTaked;
        //TextView     tvCorrectPageTaked;
//        TextView     tvDay;
        TextView     tvEndTime;
        TextView     tvSubmitCount;
        LinearLayout llOverdueTip;
    }

    /*public void setAll(boolean showAll) {
        mListShow.clear();
        if (showAll) {
            mListShow.addAll(mDatas);
        } else {
            List<SubmitQuestion> submitQuestions = mDatas.subList(0, mNum);
            mListShow.addAll(submitQuestions);
        }
    }*/

    public void setFirst(boolean checked) {
        setData(checked, false);
    }

    public void setData(boolean overdue, boolean all) {
        mListShow.clear();
        List<SubmitQuestion> list;
        if (overdue) {
            list = mDatas;
        } else {
            list = getList();
        }
        //Log.i("sky", "list.size()=" + list.size() + " all=" + all);
        if (all) {
            mListShow.addAll(list);
        } else {
            if (mNum < list.size()) {
                List<SubmitQuestion> submitQuestions = list.subList(0, mNum);
                mListShow.addAll(submitQuestions);
            } else {
                mListShow.addAll(list);
            }

        }
    }

    private List<SubmitQuestion> getList() {
        List<SubmitQuestion> tempList = new ArrayList<>();
        for (int i = 0; i < mDatas.size(); i++) {
            SubmitQuestion vo = mDatas.get(i);
            if (System.currentTimeMillis() - vo.getEndTime() < AppConst.RECENT_15_DAY) {
                tempList.add(vo);
            }
        }
        return tempList;
    }

    public void clearShow() {
        mListShow.clear();
    }


}

