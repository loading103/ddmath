package com.tsinghuabigdata.edu.ddmath.module.floatwindow.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;

import java.util.List;

/**
 * 帮助图片适配器
 */

public class HelpViewPagerAdapter extends PagerAdapter {


    private Context           context;
    private List<String> mList;

    public HelpViewPagerAdapter(Context context, List<String> list) {
        this.context = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList!=null?mList.size():0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);

        LogUtils.i("sky", "instantiateItem mList.size()= " + mList.size());
        String url = mList.get(position % mList.size());
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

