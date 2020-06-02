package com.tsinghuabigdata.edu.ddmath.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.adapter.PreviewPageAdapter;
import com.tsinghuabigdata.edu.ddmath.adapter.PreviewThumbAdapter;
import com.tsinghuabigdata.edu.ddmath.inter.MyItemClickListener;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.WindowUtils;
import com.tsinghuabigdata.edu.ddmath.view.BigPhotoViewPager;

import java.util.ArrayList;



/**
 * 照片大图预览界面
 * Created by Administrator on 2017/2/17.
 */

public class PicturePreviewActivity extends RoboBaseActivity implements MyItemClickListener {

    //多个图片地址，用","分割
    public static final String PARMA_IMAGES  = "imagespath";
    public static final String PARMA_CORRECT = "correctinfos";
    //显示的是第几张图片
    public static final String PARMA_INDEX   = "showindex";


    private PreviewThumbAdapter mPreviewThumbAdapter;

    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private RecyclerView mRvPreview;
    private int          mScreenWidth;
    private int          mSingleWidth;
    private int          mContentWidth;
    private int          totalDx;
    private boolean show = true;
    private boolean only;

    /**
     * 启动图片预览
     *
     * @param context 上下文
     * @param images  //多个图片地址，用","分割, 网络图片是完整的url
     * @param index   //当前显示的序号
     */
    public static void startPicturePreviewActivity(Context context, ArrayList<String> images, int index) {
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putExtra(PARMA_IMAGES, images);
        intent.putExtra(PARMA_INDEX, index);
        context.startActivity(intent);
    }

    public static void startPicturePreviewActivity(Context context, ArrayList<String> images, String corrects, int index) {
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putExtra(PARMA_IMAGES, images);
        intent.putExtra(PARMA_INDEX, index);
        intent.putExtra(PARMA_CORRECT, corrects);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GlobalData.isPad()) {
            setContentView(R.layout.activity_picture_preview);
        } else {
            setContentView(R.layout.activity_picture_preview_phone);
        }
        initView();
        initData();
    }


    private void initView() {
        mViewPager = (BigPhotoViewPager) findViewById(R.id.view_pager);
        mRvPreview = (RecyclerView) findViewById(R.id.rv_preview);
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_preview);
    }

    private void initData() {
        ArrayList<String> arrayList = getIntent().getStringArrayListExtra(PARMA_IMAGES);
        final int index = getIntent().getIntExtra(PARMA_INDEX, 0);
        String corrects = getIntent().getStringExtra(PARMA_CORRECT);

        ArrayList<String> correctInfos = null;
        if (!TextUtils.isEmpty(corrects)) {
            String[] array = corrects.split("#");
            correctInfos = new ArrayList<>();
            for (String str : array) {
                correctInfos.add(str);
            }
        }

        int size = arrayList.size();
        if (size < 2) {
            only = true;
            mLinearLayout.setVisibility(View.INVISIBLE);
        }
        mScreenWidth = WindowUtils.getScreenWidth(this);
        mSingleWidth = DensityUtils.dp2px(this, 92);
        mContentWidth = DensityUtils.dp2px(this, 92 * size);
        if (mContentWidth < mScreenWidth) {
            int marginWidth = mScreenWidth - mContentWidth;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRvPreview.getLayoutParams();
            layoutParams.leftMargin = marginWidth / 2;
            mRvPreview.setLayoutParams(layoutParams);
        }
        PreviewPageAdapter previewPageAdapter = new PreviewPageAdapter(this, arrayList);
        previewPageAdapter.setActivity(this);
        previewPageAdapter.setCorrectInfo(correctInfos);
        //        mViewPager.setOffscreenPageLimit(size);

        mViewPager.setAdapter(previewPageAdapter);
        mViewPager.setCurrentItem(index);
        mPreviewThumbAdapter = new PreviewThumbAdapter(this, arrayList, index);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvPreview.setLayoutManager(layoutManager);
        mRvPreview.setAdapter(mPreviewThumbAdapter);
        mPreviewThumbAdapter.setOnItemClickListener(this);
        mRvPreview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                totalDx -= dx;
                Log.i("sky", "onScrolled totalDx=" + totalDx);
            }
        });
        mRvPreview.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll(index);
            }
        }, 100);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPreviewThumbAdapter.select(position);
                scroll(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void scroll(int position) {
        int dis = (int) ((position + 0.5) * mSingleWidth) - mScreenWidth / 2;
        Log.i("sky", "totalDx=" + totalDx + "dis=" + dis);
        mRvPreview.scrollBy(dis + totalDx, 0);
    }

    @Override
    public void onItemClick(View view, int position) {
        mPreviewThumbAdapter.select(position);
        mViewPager.setCurrentItem(position, false);
        scroll(position);
    }

    public void updateImage(final ImageView imageView, final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    public void showList() {
        if (only) {
            return;
        }
        if (show) {
            mLinearLayout.setVisibility(View.INVISIBLE);
            show = false;
        } else {
            mLinearLayout.setVisibility(View.VISIBLE);
            show = true;
        }
    }
}
