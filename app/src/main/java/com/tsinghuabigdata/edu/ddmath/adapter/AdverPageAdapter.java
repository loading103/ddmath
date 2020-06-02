package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.view.RoundImageView;

import java.util.List;



/**
 * Created by fish on 2016/10/24.
 */

public class AdverPageAdapter extends PagerAdapter {


    private Context           context;
    private List<MessageInfo> mList;

    public AdverPageAdapter(Context context, List<MessageInfo> list) {
        this.context = context;
        mList = list;
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
    public Object instantiateItem(final ViewGroup container, int position) {
        RoundImageView imageView = new RoundImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setType( RoundImageView.TYPE_ROUND );
        imageView.setBorderRadius( 10 );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        String url = null;
        LogUtils.i("sky", "instantiateItem mList.size()= " + mList.size());
        MessageInfo messageInfo = mList.get(position % mList.size());
        if (messageInfo != null) {
            url = messageInfo.getPicUrl();
        }
        PicassoUtil.displayImageIndetUrl(url, imageView);
        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort(context,"AdverPageAdapter onClick");
            }
        });*/
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
