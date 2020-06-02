package com.tsinghuabigdata.edu.ddmath.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.ScreenPreviewPageAdapter;
import com.tsinghuabigdata.edu.ddmath.view.BigPhotoViewPager;

import java.util.ArrayList;


/**
 * 照片全屏预览界面
 * Created by Administrator on 2017/2/17.
 */

public class ScreenPreviewActivity extends RoboActivity {

    private BigPhotoViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_preview);
        initView();
        initData();
    }

    private void initView() {
        mViewPager = (BigPhotoViewPager) findViewById(R.id.view_pager);
    }

    private void initData() {
        int index = getIntent().getIntExtra("showindex", 0);
        ArrayList<String> arrayList = getIntent().getStringArrayListExtra("list");
        ArrayList<String> correctList = getIntent().getStringArrayListExtra("corrects");
        ScreenPreviewPageAdapter adapter = new ScreenPreviewPageAdapter(this, arrayList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(index);
        adapter.setActivity( this );
        adapter.setCorrectInfo( correctList );
    }

    public void updateImage(final ImageView imageView, final Bitmap bitmap ){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap( bitmap );
            }
        });
    }
}
