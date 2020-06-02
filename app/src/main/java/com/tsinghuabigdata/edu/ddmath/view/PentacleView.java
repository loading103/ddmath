package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


public class PentacleView extends LinearLayout {

    private String Tag = "SortView";

    private LinearLayout mLlPentacle;


    public PentacleView(Context context) {
        this(context, null);
    }

    public PentacleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()) {
            View.inflate(context, R.layout.view_pentacle, this);
        } else {
            View.inflate(context, R.layout.view_pentacle_phone, this);
        }
        initView();
    }


    private void initView() {
        mLlPentacle = (LinearLayout) findViewById(R.id.ll_pentacle);
    }


    public void setLevel(int level) {
        int num = level;
        if (level < 0) {
            num = 0;
        } else if (level > 5) {
            num = 5;
        }
        updateView(num);
    }

    private void updateView(int num) {
        for (int i = 0; i < mLlPentacle.getChildCount(); i++) {
            ImageView imageView = (ImageView) mLlPentacle.getChildAt(i);
            if (i < num) {
                imageView.setImageResource(R.drawable.ic_star_full);
            } else {
                imageView.setImageResource(R.drawable.ic_star_empty);
            }
        }
    }
}
