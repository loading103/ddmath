package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.BitmapUtils;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 原版教辅 移到
 */

public class LmGuideView extends RelativeLayout implements View.OnClickListener, ScrollBottomScrollView.OnScrollBottomListener{

    //对象
    private TextView btnView;
    private ImageView imageView;
    private OnClickListener listener;

	public LmGuideView(Context context) {
		super(context);
		initData(context);
	}

	public LmGuideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	public LmGuideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData( context );
	}

    @Override
    public void srollToBottom() {
        btnView.setVisibility( VISIBLE );
    }

    @Override
    public void onClick(View v) {
        setVisibility( View.GONE );
        if(listener!=null) listener.onClick( v );
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setData( String name ){
        BitmapUtils.showAssetImage( getContext(), name, imageView );
    }
    //-------------------------------------------------------------------------
	private void initData(Context context){
        //
        inflate( context, GlobalData.isPad()?R.layout.view_lm_cameraguide:R.layout.view_lm_cameraguide_phone, this );

        ScrollBottomScrollView scrollView = (ScrollBottomScrollView) findViewById( R.id.view_lm_scroll );
        scrollView.setListener( this );
        imageView  = (ImageView) findViewById( R.id.view_lm_image );
        btnView    = (TextView) findViewById( R.id.view_lm_camerabtn );
        btnView.setOnClickListener( this );
	}


}

