package com.tsinghuabigdata.edu.ddmath.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.WelcomeActivity;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.util.AssetsUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.PicassoUtil;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;

import java.util.List;


/**
 * Created by Administrator on 2018/3/26.
 */

public class WelcomePageAdapter extends PagerAdapter {

    private WelcomeActivity mWelcomeActivity;
    private List<Integer>   mList;

    public WelcomePageAdapter(WelcomeActivity activity, List<Integer> list) {
        mWelcomeActivity = activity;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        int res = GlobalData.isPad()?R.layout.view_welcome_item : R.layout.view_welcome_item_phone;
        View view = LayoutInflater.from(mWelcomeActivity).inflate(res, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_guide);
        TextView tvJump = (TextView) view.findViewById(R.id.tv_jump);
        TextView tvExperience = (TextView) view.findViewById(R.id.tv_experience);
        if (position < mList.size() - 1) {
            tvJump.setVisibility(View.VISIBLE);
            tvExperience.setVisibility(View.INVISIBLE);
            tvJump.setOnClickListener(new JumpListener());
            tvJump.setTypeface(AssetsUtils.getMyTypeface(mWelcomeActivity));
        } else {
            tvJump.setVisibility(View.INVISIBLE);
            tvExperience.setVisibility(View.VISIBLE);
            tvExperience.setOnClickListener(new JumpListener());
            tvExperience.setTypeface(AssetsUtils.getMyTypeface(mWelcomeActivity));
        }
        PicassoUtil.getPicasso().load(mList.get(position)).into(iv);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public class JumpListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            PreferencesUtils.putInt(mWelcomeActivity, AppConst.WELCOME_OPENED, 1);
            Intent intent = new Intent(mWelcomeActivity, MainActivity.class);
            mWelcomeActivity.startActivity(intent);
            mWelcomeActivity.finish();
        }
    }

}
