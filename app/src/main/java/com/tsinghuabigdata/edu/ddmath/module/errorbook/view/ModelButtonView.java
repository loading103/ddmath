package com.tsinghuabigdata.edu.ddmath.module.errorbook.view;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 功能模块Btn
 */
@Deprecated
public class ModelButtonView extends RelativeLayout{

    //
    private View mainView;
    //小红点
    private ImageView redImageView;
    private TextView nameView;

    private int nameColor;

    public ModelButtonView(Context context) {
        super(context);
        init(context);
    }

    public ModelButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ModelButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    /**
     * 设置按钮背景图
     */
    public void setButtonIamge( int resid, String name, int color ){
        mainView.setBackgroundResource( resid );
        nameView.setText( name );
        nameView.setTextColor( color );
        nameColor = color;
    }

    /**
     * 设置小红点显示
     */
    public void showRedPoint( boolean show ){
        redImageView.setVisibility( show? View.VISIBLE:View.GONE );
    }

    @Override
    public void setActivated(boolean activated) {
        mainView.setActivated(activated);
        if( activated ){
            nameView.setTextColor(Color.WHITE );
        }else{
            nameView.setTextColor( nameColor );
        }
    }
    @Override
    public boolean isActivated() {
        return mainView.isActivated();
    }

    //-------------------------------------------------------------------------
    private void init(Context context){
        inflate( context, GlobalData.isPad()?R.layout.view_errbook_modelbutton :R.layout.view_errbook_modelbutton_phone, this );
        mainView = findViewById( R.id.rl_errbook_modelbtn );

        redImageView = (ImageView) findViewById( R.id.iv_errbook_redpoint ) ;
        nameView     = (TextView) findViewById( R.id.tv_errbook_name );

    }

}
