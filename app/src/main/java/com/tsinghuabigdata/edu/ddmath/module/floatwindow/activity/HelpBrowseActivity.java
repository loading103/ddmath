package com.tsinghuabigdata.edu.ddmath.module.floatwindow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.view.WorkToolbar;
import com.tsinghuabigdata.edu.ddmath.module.floatwindow.adapter.HelpViewPagerAdapter;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能帮助图片浏览
 */

public class HelpBrowseActivity   extends RoboActivity implements ViewPager.OnPageChangeListener {

    public final static String PARAM_POSITION = "position";
    public final static String PARAM_IMGPATHS = "imagePath";

    public static void startHelpBrowseActivity(Context context, ArrayList<String> list){
        Intent intent = new Intent( context, HelpBrowseActivity.class );
        intent.putStringArrayListExtra( PARAM_IMGPATHS, list );
        context.startActivity( intent );
    }

    //
    @ViewInject( R.id.worktoolbar )
    private WorkToolbar    mWorktoolbar;

    @ViewInject( R.id.images_view )
    private ViewPager mViewPager;

    @ViewInject(R.id.indicator_container)
    private LinearLayout indicatorLayout;

    //传递的参数
    // 原图url路径List
    private List<String> imagePath;
    // 当前显示的位置
    private int position;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transparent = true;
        super.onCreate(savedInstanceState);

        //设置全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_help);
        // 获取参数

        mContext = this;
        x.view().inject(this);
        if( !parseIntent() ){
            ToastUtils.show( mContext, "参数错误" );
            return;
        }

        initView();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < imagePath.size(); i++) {
            ImageView imageView = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position % imagePath.size()) {
                imageView.setSelected(true);
            } else {
                imageView.setSelected(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //-----------------------------------------------------------------------------
    private boolean parseIntent(){
        this.position = getIntent().getIntExtra( PARAM_POSITION, 0);
        this.imagePath = getIntent().getStringArrayListExtra( PARAM_IMGPATHS );
        return imagePath!=null&&imagePath.size()>0;
    }

    private void initView(){

        mWorktoolbar.setTitle( "帮助" );
        mWorktoolbar.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        },null);

        // 设置左右两列缓存的数目
        mViewPager.setOffscreenPageLimit(2);
        // 添加Adapter
        PagerAdapter adapter = new HelpViewPagerAdapter(this, imagePath);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(position);
        mViewPager.addOnPageChangeListener( this );

        indicatorLayout = (LinearLayout)findViewById( R.id.indicator_container );
        for (int i = 0; i < imagePath.size(); i++) {
            ImageView indicator = new ImageView(mContext);
            int diameter;
            int margin;
            if (GlobalData.isPad()) {
                diameter = DensityUtils.dp2px(mContext, 10);
                margin = DensityUtils.dp2px(mContext, 6);
            } else {
                diameter = DensityUtils.dp2px(mContext, 5);
                margin = DensityUtils.dp2px(mContext, 3);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(diameter, diameter);
            params.leftMargin = margin;
            if (GlobalData.isPad()) {
                indicator.setImageResource(R.drawable.selector_banner_dot);
            } else {
                indicator.setImageResource(R.drawable.selector_banner_dot);
            }
            indicatorLayout.addView(indicator, params);
            // 默认第一个是选中的效果
            if (i == 0) {
                indicator.setSelected(true);
            } else {
                indicator.setSelected(false);
            }
        }
    }

}