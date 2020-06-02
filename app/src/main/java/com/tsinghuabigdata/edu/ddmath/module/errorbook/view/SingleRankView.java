package com.tsinghuabigdata.edu.ddmath.module.errorbook.view;


import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;
import com.tsinghuabigdata.edu.ddmath.util.HeadImageUtils;
import com.tsinghuabigdata.edu.ddmath.view.CircleImageView;

/**
 * 单个用户Bank x
 */
public class SingleRankView extends LinearLayout{


    private UserHeaderView myHeadView;
    private CircleImageView otherHeadView;
    private ImageView fristView;

    private ImageView planeView;

    private TextView rateView;

    //private int nameColor;

    public SingleRankView(Context context) {
        super(context);
        init(context);
    }

    public SingleRankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SingleRankView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    /**
     * 设置数据
     */
    public void setData( String url, int rate, boolean my, boolean correct ){

        myHeadView.setVisibility(View.GONE);
        otherHeadView.setVisibility(View.GONE);
        fristView.setVisibility( View.GONE );
        rateView.setVisibility( View.GONE );
        planeView.setVisibility(View.GONE);

        if( url == null ) return;

        if(TextUtils.isEmpty(url))
            url = "http://www.iclassedu.com/tmp";

        rateView.setVisibility( View.VISIBLE );
        if( my ){
            myHeadView.setVisibility(View.VISIBLE);
            myHeadView.setHeaderImage( url );

            rateView.setText( String.valueOf(rate) );
            rateView.setTextColor( getResources().getColor(R.color.color_F3903F ) );

            planeView.setVisibility(View.VISIBLE);
        }else{
            otherHeadView.setVisibility(View.VISIBLE);
            HeadImageUtils.showHeadImage( getContext(), url, otherHeadView );

            rateView.setText( String.valueOf(rate) );
            rateView.setTextColor( getResources().getColor(R.color.color_0E8FC6 ) );
        }
    }

    public void setFristData(){
        myHeadView.setVisibility(View.GONE);
        otherHeadView.setVisibility(View.GONE);
        fristView.setVisibility( View.VISIBLE );
        planeView.setVisibility(View.GONE);
        rateView.setVisibility( View.VISIBLE );

        rateView.setText( "100" );
        rateView.setTextColor( getResources().getColor(R.color.color_0E8FC6 ) );
    }

    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_errbook_singlebank :R.layout.view_errbook_singlebank_phone, this );

        //mainView = findViewById( R.id.rl_errbook_modelbtn );

        myHeadView      = (UserHeaderView) findViewById( R.id.userbank_myheadimg );
        otherHeadView   = (CircleImageView) findViewById( R.id.userbank_otherheadimg );
        fristView       = (ImageView)findViewById( R.id.userbank_firstheadimg );
        planeView       = (ImageView) findViewById( R.id.userbank_plane );

        rateView        = (TextView)findViewById( R.id.userbank_rate );
    }

}
