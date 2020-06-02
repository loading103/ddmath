package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

/**
 * Created by 28205 on 2016/8/22.
 */
public class KnowcognoViewImagePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private static final String TAG = "state-viewpage-know";
    private Context         context;
    private ViewPager       viewPager;
    private List<ImageView> views;

    private ThumbnailHorizontalLvAdapter thumbnailImgAdapter;

    public KnowcognoViewImagePagerAdapter(Context context, List<ImageView> viewpages, ViewPager viewPager, ThumbnailHorizontalLvAdapter thumbnailHorizontalLvAdapter) {
        this.context = context;
        this.viewPager = viewPager;
        thumbnailImgAdapter = thumbnailHorizontalLvAdapter;

        this.viewPager.addOnPageChangeListener(this);
        views = viewpages;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = (ImageView) views.get(position);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            //在滚动完成之后
            case ViewPager.SCROLL_STATE_IDLE:
                thumbnailImgAdapter.setSelectImg(viewPager.getCurrentItem());
                break;
            default:
                break;
        }
    }
}