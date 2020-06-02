package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.tsinghuabigdata.edu.commons.cache.CacheManager;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;


import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import uk.co.senab.photoview.PhotoViewAttacher;


public class ClassmateAnsImageActivity extends RoboActivity implements View.OnClickListener {
    @ViewInject(R.id.iv_content)
    private ImageView iv;

    private String imgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_classmateans_img);
        x.view().inject( this );
        imgurl = getIntent().getStringExtra("imgurl");
        //放大及监听
        iv.setScaleType(ImageView.ScaleType.MATRIX);
        // 这句代码之前必须要现有默认图片来撑开位置
        iv.setOnClickListener(this);
        new PhotoViewAttacher(iv); //NOSONAR
        loadImage(this, iv, imgurl);
    }
    private static void loadImage(Context context, ImageView imageView, String headImage) {
        if (TextUtils.isEmpty(headImage)) {
            return;
        }
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.ic_temporary_image, R.drawable.ic_broken_image);
        ImageLoader imageLoader = new ImageLoader(Volley.newRequestQueue(context), CacheManager.getBitmapLruCache());
        imageLoader.get(headImage, listener);
    }
    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
