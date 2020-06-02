package com.tsinghuabigdata.edu.ddmath.module.famousteacher.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.CommonAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ViewHolder;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.List;


/**
 * 名师精讲视频适配器
 * Created by Administrator on 2018/2/7.
 */

public class FamousVideoAdapter extends CommonAdapter<SingleVideoBean> {


    private String price;
    private int    mFreeUseTimes;

    public FamousVideoAdapter(Context context, List<SingleVideoBean> mDatas) {
        super(context, mDatas);
    }

    @Override
    protected int getLayoutId() {
        if (GlobalData.isPad()) {
            return R.layout.item_famous_video;
        } else {
            return R.layout.item_famous_video_phone;
        }
    }

    @Override
    protected void convert(ViewHolder helper, int position, final SingleVideoBean item) {
        View viewDefault = helper.getView(R.id.view_default);
        ImageView ivVideo = helper.getView(R.id.iv_video);
        ImageView ivPlayBig = helper.getView(R.id.iv_play_big);
        ImageView ivPlay = helper.getView(R.id.iv_play);
        TextView tvPrice = helper.getView(R.id.tv_price);
        //        if (position % 3 == 0) {
        //            item.setSection(null);
        //        }
        if (TextUtils.isEmpty(item.getImageUrl())) {
            viewDefault.setVisibility(View.VISIBLE);
            ivPlayBig.setVisibility(View.VISIBLE);
            ivVideo.setVisibility(View.INVISIBLE);
            ivPlay.setVisibility(View.INVISIBLE);
        } else {
            viewDefault.setVisibility(View.INVISIBLE);
            ivPlayBig.setVisibility(View.INVISIBLE);
            ivVideo.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
            PicassoUtil.displayImage(item.getImageUrl(), ivVideo);
        }
        ImageView ivExchanged = helper.getView(R.id.iv_exchanged);
        tvPrice.setText(price);
        tvPrice.setVisibility(View.INVISIBLE);
        if (item.getStatus() == SingleVideoBean.Exchanged) {
            ivExchanged.setVisibility(View.VISIBLE);
            //tvPrice.setVisibility(View.INVISIBLE);
        } else if (mFreeUseTimes < 0) {
            ivExchanged.setVisibility(View.INVISIBLE);
            //tvPrice.setVisibility(View.INVISIBLE);
        } else {
            ivExchanged.setVisibility(View.INVISIBLE);
            //tvPrice.setVisibility(View.INVISIBLE);
        }
        String times = String.format(mContext.getResources().getString(R.string.video_play_times), item.getBroadcastTimes());
        helper.setText(R.id.tv_play_times, times);
        helper.setText(R.id.tv_grade, "年级：" + item.getGrade());
        helper.setText(R.id.tv_time, "时长：" + getTime(item.getVideoLong()));
        helper.setText(R.id.tv_video_name, getName(item));
    }

    public static String getName(SingleVideoBean item) {
        if (TextUtils.isEmpty(item.getSection())) {
            return item.getChapter();
        }
        return item.getChapter() + " " + item.getSection();
    }


    private String getTime(int videoLong) {
        if (videoLong < 60) {
            return videoLong + "秒";
        } else {
            int min = videoLong / 60;
            int sec = videoLong % 60;
            return min + "分" + sec + "秒";
        }
    }


    public void setPrice(String price) {
        this.price = price;
    }

    public void setFreeUseTimes(int freeUseTimes) {
        mFreeUseTimes = freeUseTimes;
    }
}
