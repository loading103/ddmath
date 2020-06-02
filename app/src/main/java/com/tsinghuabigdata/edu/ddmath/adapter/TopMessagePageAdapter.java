package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageDetailActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Administrator on 2017/9/6.
 */

public class TopMessagePageAdapter extends PagerAdapter {

    private Context            context;
    private List<MessageInfo>  mList;
    private TopMessageListener mTopMessageListener;

    public TopMessagePageAdapter(Context context, List<MessageInfo> list) {
        this.context = context;
        mList = new ArrayList<>();
        mList.addAll(list);
        //this.mList = list;
        //Log.i("sky", "mList.size()= " + mList.size());
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        String url = null;
        //Log.i("sky", "instantiateItem mList.size()= " + mList.size());
        final MessageInfo messageInfo = mList.get(position % mList.size());
        if (messageInfo != null) {
            url = messageInfo.getPicUrl();
        }
        PicassoUtil.displayImageIndetUrl(url, imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTopMessageListener != null) {
                    mTopMessageListener.clickMessage(position % mList.size());
                }
            }
        });
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setTopMessageListener(TopMessageListener topMessageListener) {
        mTopMessageListener = topMessageListener;
    }

    public interface TopMessageListener {
        void clickMessage(int position);
    }

}
