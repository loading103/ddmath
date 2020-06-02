package com.tsinghuabigdata.edu.ddmath.module.ddwork.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.util.GlobalData;

/**
 * 校内作业toolbar
 */
public class DDWorkToolbar extends LinearLayout implements View.OnClickListener{

    private TextView leftView;
    private TextView titleView;
    private TextView rightView;

    private View.OnClickListener leftListener;
    private View.OnClickListener rightListener;

    public DDWorkToolbar(Context context) {
        super(context);
        init();
    }

    public DDWorkToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DDWorkToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_lefttitle:
                if( leftListener!=null ) leftListener.onClick( v );
                break;
            case R.id.toolbar_righttitle:
                if( rightListener!=null ) rightListener.onClick( v );
                break;
            default:
                break;
        }
    }

    /**
     * 回调
     * @param llistener  左按钮回调
     * @param rListener  右按钮回调
     */
    public void setClickListener(View.OnClickListener llistener, View.OnClickListener rListener){
        leftListener = llistener;
        rightListener=rListener;
    }

    public void setTitle( String title ){
        titleView.setText( title );
    }

    public void setRightBtn( String name ){
        rightView.setText( name );
    }

    //-----------------------------------------------------------------------------
    private void init() {

        inflate( getContext(), GlobalData.isPad()?R.layout.ddwork_toolbar_layout:R.layout.ddwork_toolbar_layout, this );
        leftView = (TextView)findViewById( R.id.toolbar_lefttitle );
        titleView = (TextView)findViewById( R.id.toolbar_title );
        rightView = (TextView)findViewById( R.id.toolbar_righttitle );

        leftView.setOnClickListener( this );
        rightView.setOnClickListener( this );
    }

}
