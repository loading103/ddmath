package com.tsinghuabigdata.edu.ddmath.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.DensityUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;


/**
 * 头像——自定义控件
 * Created by Administrator on 2018/3/28.
 */

public class HeadView extends RelativeLayout implements View.OnClickListener {


//    private ImageView       mIvSheadBgSmall;
//    private ImageView       mIvSheadBgBig;
//    private CircleImageView mIvHeadSmall;
//    private CircleImageView mIvHeadBig;
    private BaseHeadView baseHeadView;
    private ImageView       mIvNewMessage;


    public HeadView(Context context) {
        this(context, null);
    }

    public HeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (GlobalData.isPad()) {
            View.inflate(context, R.layout.view_head, this);
        } else {
            View.inflate(context, R.layout.view_head_phone, this);
        }
        initView();
    }

    /**
     * 初始化界面元素
     */
    private void initView() {
        baseHeadView = findViewById( R.id.iv_basehead_img );
        mIvNewMessage = findViewById(R.id.iv_new_message);
    }


    @Override
    public void onClick(View v) {
        AccountUtils.checkJionSchoolClass(getContext());
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        baseHeadView.setActivated( activated );

        boolean isPad = GlobalData.isPad();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mIvNewMessage.getLayoutParams();
        if( activated ){
            layoutParams.width = DensityUtils.dp2px( getContext(), isPad?24:16 );
            layoutParams.height = DensityUtils.dp2px( getContext(), isPad?24:16 );
            layoutParams.topMargin = DensityUtils.dp2px( getContext(), isPad?6:4 );
            layoutParams.rightMargin = DensityUtils.dp2px( getContext(), isPad?3:2 );
        }else{
            layoutParams.width = DensityUtils.dp2px( getContext(), isPad?15:10 );
            layoutParams.height = DensityUtils.dp2px( getContext(), isPad?15:10 );
            layoutParams.topMargin = DensityUtils.dp2px( getContext(), isPad?18:12 );
            layoutParams.rightMargin = DensityUtils.dp2px( getContext(), isPad?12:8 );
        }
        mIvNewMessage.setLayoutParams( layoutParams );
    }

    public void setRedpoint(boolean show) {
        if (show) {
            mIvNewMessage.setVisibility(VISIBLE);
        } else {
            mIvNewMessage.setVisibility(INVISIBLE);
        }
    }

    public void refreshHeadImage() {
        baseHeadView.showHeadImage();
//        HeadImageUtils.setHeadImage(mIvHeadSmall);
//        HeadImageUtils.setHeadImage(mIvHeadBig);
    }
}
