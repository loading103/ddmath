package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.bean.ExamNameBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 错题本——上传记录列表适配器
 * Created by Administrator on 2017/2/17.
 */

public class UploadInfoAdatper extends CommonAdapter<ExamNameBean> {


    public UploadInfoAdatper(Context context, List<ExamNameBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_upload_info;
    }

    @Override
    protected void convert(ViewHolder helper, int position, ExamNameBean item) {
        helper.setText(R.id.tv_upload_title, item.getExamName());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm上传");
        String time = sdf.format(item.getUploadTime());
        helper.setText(R.id.tv_upload_time, time);
        ImageView imageView = helper.getView(R.id.iv_upload_right);
        if (item.isCorrect()){
            imageView.setImageResource(R.drawable.ic_upload_yes);
        }else {
            imageView.setImageResource(R.drawable.ic_upload_no);
        }
    }
}
