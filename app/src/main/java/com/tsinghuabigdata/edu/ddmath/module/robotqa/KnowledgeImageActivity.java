package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.adapter.KnowcognoViewImagePagerAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ThumbnailHorizontalLvAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.ZoomOutPageTransformer;
import com.tsinghuabigdata.edu.ddmath.bean.WebchatAdapter;
import com.tsinghuabigdata.edu.ddmath.view.HorizontalListView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


public class KnowledgeImageActivity extends RoboActivity implements View.OnClickListener {
    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;
    @ViewInject(R.id.horlv_thumbnail)
    private HorizontalListView horizontalListView;

    private int              position;
    private int              visibleCounts;
    private List<ImageView>  pageViews;
    private List<FlagBitmap> visibleBitmaps;

    private KnowcognoViewImagePagerAdapter pagerAdapter;
    private ThumbnailHorizontalLvAdapter   thumbnailHorizontalLvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowlegde_image);

        x.view().inject( this );
        position = getIntent().getIntExtra(KnowledgeView.KNOW_CONGNITION_VALUE, -1);
        visibleCounts = getIntent().getIntExtra(KnowledgeView.KNOW_CONGNITION_TOTAL, 1);

        loadVpView();

        //缩略图
        thumbnailHorizontalLvAdapter = new ThumbnailHorizontalLvAdapter(this);
        horizontalListView.setAdapter(thumbnailHorizontalLvAdapter);
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewPager.setCurrentItem(position);
                thumbnailHorizontalLvAdapter.setSelectImg(position);
            }
        });
        initThumbnailBitmap();

        //主浏览图片
        pagerAdapter = new KnowcognoViewImagePagerAdapter(this, pageViews, viewPager, thumbnailHorizontalLvAdapter);
        viewPager.setPageMargin(120);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(position);

    }


    private void loadVpView() {
        pageViews = new ArrayList<ImageView>();
        for (int i = 0; i < visibleCounts; i++) {
            ImageView iv = new ImageView(this);
            Bitmap bitmap = WebchatAdapter.getBitmaps().get(i);
            iv.setImageBitmap(bitmap);

            //放大及监听
            iv.setScaleType(ImageView.ScaleType.MATRIX);
            // 这句代码之前必须要现有默认图片来撑开位置
            iv.setOnClickListener(this);
            new PhotoViewAttacher(iv); //NOSONAR
            pageViews.add(iv);
        }
    }

    private void initThumbnailBitmap() {
        visibleBitmaps = new ArrayList<FlagBitmap>();
        for (int i = 0; i < visibleCounts; i++) {
            Bitmap bitmap = WebchatAdapter.getBitmaps().get(i);
            FlagBitmap flagBitmap = new FlagBitmap();
            flagBitmap.setBitmap(bitmap);
            if (i == position){
                flagBitmap.setSelected(true);
                thumbnailHorizontalLvAdapter.initSelectImgPosition(i);
            }
            visibleBitmaps.add(flagBitmap);
        }
        thumbnailHorizontalLvAdapter.set(visibleBitmaps);
    }

    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
